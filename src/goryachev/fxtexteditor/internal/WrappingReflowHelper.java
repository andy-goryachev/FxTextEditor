// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.GlyphType;
import goryachev.fxtexteditor.ITabPolicy;
import goryachev.fxtexteditor.ITextLine;
import goryachev.fxtexteditor.VFlow;


/**
 * Wrapping Reflow Helper.
 */
public class WrappingReflowHelper
{
	private static final int[] AVOID_COMPILER_WARNING = { };
	
	
	// FIX update top cell index if different
	public static void reflow(VFlow flow, ScreenBuffer buffer, int xmax, int ymax, ITabPolicy tabPolicy)
	{
		int lineIndex = flow.getTopLine();
		int cellIndex = 0;
		int x = 0;
		int y = 0;
		int startGlyphIndex = 0;
		ScreenRow r = null;
		FlowLine fline = null;
		int[] glyphOffsets = AVOID_COMPILER_WARNING;
		int glyphIndex = 0;
		int tabDistance = 0;
		boolean complex = false;
		
		while(y < ymax)
		{
			if(fline == null)
			{
				fline = flow.getTextLine(lineIndex);
				if(fline == null)
				{
					complex = false;
				}
				else
				{
					complex = fline.hasComplexGlyphs();
					if(!complex)
					{
						if(!tabPolicy.isSimple())
						{
							complex |= fline.hasTabs();
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
					glyphOffsets = r.prepareGlyphOffsetsForWidth(xmax);
				}
			}
			
			if(x == 0)
			{
				r.initLine(fline);
				r.setStartGlyphIndex(startGlyphIndex);
				int mx = flow.getEditor().getModel().getLineCount();
				r.setAppendModelIndex(mx == lineIndex ? mx : -1);
			}
			
			// main FSM loop
			
			if(fline == null)
			{
				// next line
				r.setCellCount(0);
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
					r.setCellCount(x);
					startGlyphIndex = glyphIndex;
					tabDistance = 0;
					// FIX line disappears
					r = null;
					x = 0;
					y++;
				}
				else
				{
					glyphOffsets[x] = -tabDistance;
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
					r.setCellCount(x);
					startGlyphIndex = glyphIndex;
					tabDistance = 0;
					x = 0;
					y++;
					r = null;
				}
				else
				{
					GlyphType gt = r.getGlyphType(glyphIndex);
					switch(gt)
					{
					case EOL:
						r.setCellCount(x);
						r = null;
						fline = null;
						lineIndex++;
						y++;
						cellIndex = 0;
						break;
					case TAB:
						tabDistance = tabPolicy.nextTabStop(x) - x;
						glyphOffsets[x] = -tabDistance;
						--tabDistance;
						glyphIndex++;
						cellIndex++;
						x++;
						break;
					case NORMAL:
						glyphOffsets[x] = glyphIndex;
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
				if(cellIndex + xmax >= r.getGlyphCount())
				{
					// end of line
					int sz = r.getGlyphCount() - cellIndex;
					r.setCellCount(sz);
					
					fline = null;
					r = null;
					lineIndex++;
				}
				else
				{
					// middle of line
					r.setCellCount(xmax);
					glyphIndex += xmax;
					cellIndex += xmax;
					startGlyphIndex = glyphIndex;
				}
				
				y++;
				x = 0;
				r = null;
			}
		}
	}
}
