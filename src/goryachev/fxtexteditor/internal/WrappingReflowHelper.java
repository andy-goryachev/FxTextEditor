// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.GlyptType;
import goryachev.fxtexteditor.ITabPolicy;
import goryachev.fxtexteditor.ITextLine;
import goryachev.fxtexteditor.VFlow;


/**
 * Wrapping Reflow Helper.
 */
public class WrappingReflowHelper
{
	private ScreenBuffer buffer;
	private VFlow flow;
	private int xmax;
	private int ymax;
	private int lineIndex;
	private int x;
	private int y;
	private ScreenRow r;
	private ITextLine tline;
	private int glyphIndex;
	private int tabDistance;
	private boolean complex;
	private int[] offsets;
	private int startGlyphIndex;
	private int cellIndex;
	
	
	public WrappingReflowHelper()
	{
	}
	
	
	protected void reset(VFlow flow, ScreenBuffer buffer, int xmax, int ymax)
	{
		// TODO check if local vars are sufficient
		this.flow = flow;
		this.buffer = buffer;
		this.xmax = xmax;
		this.ymax = ymax;

		lineIndex = flow.getTopLine();
		cellIndex = 0;
		x = 0;
		y = 0;
		startGlyphIndex = 0;
		r = null;
		tline = null;
		offsets = null;
		glyphIndex = 0;
		tabDistance = 0;
		complex = false;
	}

	
	// FIX update top cell index if different
	public void reflow(VFlow flow, ScreenBuffer buffer, int xmax, int ymax, ITabPolicy tabPolicy)
	{
		reset(flow, buffer, xmax, ymax);
		
		while(y < ymax)
		{
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
				}
				
				glyphIndex = 0;
				cellIndex = 0;
				startGlyphIndex = 0;
			}
			
			if(r == null)
			{
				r = buffer.getRow(y);
				r.setComplex(complex);

				x = 0;
				
				if(complex)
				{
					offsets = r.prepareOffsetsForWidth(xmax);
				}
			}
			
			if(x == 0)
			{
				r.setTextLine(tline);
				r.setStartGlyphIndex(startGlyphIndex);
			}
			
			// main FSM loop
			
			if(x >= xmax)
			{
				x = (x + 0);
			}
				
			if(tline == null)
			{
				// next line
				r.setSize(0);
				r = null;
				x = 0;
				cellIndex = 0;
				y++;
				lineIndex++;
			}
			else if(tabDistance > 0)
			{
				if(x >= xmax)
				{
					// carry on to next line, resetting tab distance
					r.setSize(x);
					startGlyphIndex = glyphIndex;
					tabDistance = 0;
					// FIX line disappears
					r = null;
					x = 0;
					y++;
				}
				else
				{
					offsets[x] = -tabDistance;
					--tabDistance;
					x++;
				}
				cellIndex++;
			}
			else if(complex)
			{
				if(x >= xmax)
				{
					// next row
					r.setSize(x);
					startGlyphIndex = glyphIndex;
					tabDistance = 0;
					x = 0;
					y++;
					r = null;
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
						cellIndex = 0;
						tline = null;
						break;
					case TAB:
						tabDistance = tabPolicy.nextTabStop(x) - x;
						offsets[x] = -tabDistance;
						--tabDistance;
						glyphIndex++;
						cellIndex++;
						x++;
						break;
					case NORMAL:
						offsets[x] = glyphIndex;
						glyphIndex++;
						cellIndex++;
						x++;
						break;
					default:
						throw new Error("?" + gt);
					}
				}
			}
			else
			{
				// simple case, cell indexes coincide with glyph indexes
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
					glyphIndex += xmax;
					startGlyphIndex = glyphIndex;
				}
				
				y++;
				x = 0;
				r = null;
			}
		}
		
//		D.print(buffer.dump());
	}
}
