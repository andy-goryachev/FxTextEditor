// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.util.D;
import goryachev.fxtexteditor.GlyptType;
import goryachev.fxtexteditor.ITabPolicy;
import goryachev.fxtexteditor.ITextLine;
import goryachev.fxtexteditor.VFlow;


/**
 * Wrapped Reflow Helper.
 */
public class WrappedReflowHelper
{
	private ScreenBuffer buffer;
	private VFlow flow;
	private int xmax;
	private int ymax;
	private ITabPolicy tabPolicy;
	private int lineIndex;
	private int topCellIndex;
	private int x;
	private int y;
	private ScreenRow r;
	private ITextLine tline;
	private int glyphIndex;
	private int glyphCount;
	private int tabDistance;
	private boolean complex;
	private int[] offsets;
	private int startOffset;
	
	
	public WrappedReflowHelper()
	{
	}
	
	
	protected void reset(VFlow flow, ScreenBuffer buffer, int xmax, int ymax, ITabPolicy tabPolicy)
	{
		// TODO check if local vars are sufficient (tabPolicy?)
		this.flow = flow;
		this.buffer = buffer;
		this.xmax = xmax;
		this.ymax = ymax;
		this.tabPolicy = tabPolicy;

		lineIndex = flow.getTopLine();
		topCellIndex = flow.getTopCellIndex();
		x = 0;
		y = 0;
		startOffset = 0;
		r = null;
		tline = null;
		offsets = null;
		glyphIndex = 0;
		glyphCount = 0;
		tabDistance = 0;
		complex = false;
	}

	
	public void reflow(VFlow flow, ScreenBuffer buffer, int xmax, int ymax, ITabPolicy tabPolicy)
	{
		reset(flow, buffer, xmax, ymax, tabPolicy);
		
		while(y < ymax)
		{
			if(r == null)
			{
				r = buffer.getRow(y);
				x = 0;
			}
			
			if(tline == null)
			{
				tline = flow.getTextLine(lineIndex);
				if(tline == null)
				{
					complex = false;
				}
				else
				{
					complex = tline.hasComplexGlyphs();
					if(!complex)
					{
						if(!tabPolicy.isSimple())
						{
							complex |= tline.hasTabs();
						}
					}
					
					if(complex)
					{
						offsets = r.prepareOffsetsForWidth(xmax);
						glyphCount = tline.getGlyphCount();
					}
				}
				
				glyphIndex = 0;
				startOffset = 0;
				r.setComplex(complex);
			}
			
			if(x == 0)
			{
				r.setTextLine(tline, startOffset);
			}
			
			int cellIndex = startOffset + x;
			
			// main FSM loop
				
			if(tline == null)
			{
				// next line
				r.setSize(0);
				r = null;
				x = 0;
				y++;
				lineIndex++;
			}
			else if(tabDistance > 0)
			{
				if(x >= xmax)
				{
					// next line
					r.setSize(x);
					startOffset = cellIndex + tabDistance;
					tabDistance = 0;
					x = 0;
					// FIX line disappears
					r = null;
					y++;
				}
				else
				{
					offsets[x] = -tabDistance;
					--tabDistance;
					x++;
				}
			}
			else if(complex)
			{
				if(x >= xmax)
				{
					// next line
					r.setSize(x);
					startOffset = 0;
					tabDistance = 0;
					x = 0;
					y++;
				}
				else
				{
					GlyptType gt = tline.getGlyphType(glyphIndex);
					switch(gt)
					{
					case EOL:
						r.setSize(x);
						r = null;
						tline = null;
						lineIndex++;
						y++;
						break;
					case TAB:
						tabDistance = tabPolicy.nextTabStop(cellIndex) - cellIndex;
						offsets[x] = -tabDistance;
						--tabDistance;
						glyphIndex++;
						x++;
						break;
					case NORMAL:
						offsets[x] = glyphIndex;
						glyphIndex++;
						x++;
						break;
					default:
						throw new Error("?" + gt);
					}
				}
			}
			else
			{
				if(cellIndex + xmax >= tline.getGlyphCount())
				{
					// end of line
					int sz = tline.getGlyphCount() - cellIndex;
					r.setSize(sz);
					
					tline = null;
					lineIndex++;
				}
				else
				{
					// middle of line
					r.setSize(xmax);
					startOffset += xmax;
				}
				
				y++;
				x = 0;
				r = null;
			}
		}
		
		D.print(buffer.dump());
//		System.exit(0); // FIX
	}
}
