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
	private static final GlyphIndex[] AVOID_COMPILER_WARNING = { };
	
	
	// FIX update top cell index if different
	public static void reflow(VFlow flow, ScreenBuffer buffer, int xmax, int ymax, ITabPolicy tabPolicy)
	{
		int lineIndex = flow.getTopLine();
		int cellIndex = 0;
		int x = 0;
		int y = 0;
		GlyphIndex startGlyphIndex = GlyphIndex.ZERO;
		ScreenRow r = null;
		FlowLine fline = null;
		GlyphIndex[] glyphOffsets = AVOID_COMPILER_WARNING;
		GlyphIndex glyphIndex = GlyphIndex.ZERO;
		int tabDistance = 0;
		boolean complex = false;
		
		while(y < ymax)
		{
			if(fline == null)
			{
				fline = flow.getTextLine(lineIndex);
				complex = fline.hasComplexGlyphs();
				if(!complex)
				{
					if(!tabPolicy.isSimple())
					{
						complex |= fline.hasTabs();
					}
				}
				
				cellIndex = 0;
				glyphIndex = GlyphIndex.ZERO;
				startGlyphIndex = GlyphIndex.ZERO;
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
			
			if(tabDistance > 0)
			{
				if(x >= xmax)
				{
					// carry on to next line, resetting tab distance
					r.setCellCount(x);
					startGlyphIndex = glyphIndex;
					tabDistance = 0;
					r = null;
					x = 0;
					y++;
				}
				else
				{
					glyphOffsets[x] = GlyphIndex.of(-tabDistance);
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
						glyphOffsets[x] = GlyphIndex.of(-tabDistance);
						--tabDistance;
						glyphIndex = glyphIndex.increment();
						cellIndex++;
						x++;
						break;
					case NORMAL:
						glyphOffsets[x] = glyphIndex;
						glyphIndex = glyphIndex.increment();
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
					glyphIndex = glyphIndex.add(xmax);
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
