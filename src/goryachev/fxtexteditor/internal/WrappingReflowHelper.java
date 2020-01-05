// Copyright © 2019-2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.FxTextEditorModel;
import goryachev.fxtexteditor.GlyphType;
import goryachev.fxtexteditor.ITabPolicy;
import goryachev.fxtexteditor.VFlow;
import java.util.function.BiConsumer;


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
		GlyphIndex startGlyphIndex = flow.getTopGlyphIndex();
		boolean useStartGlyphIndex = true;
			
		int modelLineCount = flow.getModelLineCount();
		int cellIndex = 0;
		int x = 0;
		int y = 0;
		ScreenRow r = null;
		FlowLine fline = null;
		GlyphIndex[] glyphOffsets = AVOID_COMPILER_WARNING;
		GlyphIndex glyphIndex = GlyphIndex.ZERO;
		int tabDistance = 0;
		boolean complex = false;
		boolean bol = true;
		
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
				bol = true;
				if(useStartGlyphIndex)
				{
					glyphIndex = startGlyphIndex;
					useStartGlyphIndex = false;
					// FIX cellIndex?
				}
				else
				{
					glyphIndex = GlyphIndex.ZERO;
					startGlyphIndex = GlyphIndex.ZERO;
				}
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
				r.initLine(fline, lineIndex, modelLineCount, bol);
				r.setStartGlyphIndex(startGlyphIndex);
				bol = false;
				
				FxTextEditorModel m = flow.getEditor().getModel();
				int mx = m == null ? 0 : m.getLineCount();
				r.setAppendModelIndex(mx == lineIndex ? mx : -1);
			}
			
			// main finite state machine loop
			
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
					bol = false;
				}
				else
				{
					// TODO check
					glyphOffsets[x] = GlyphIndex.inTab(tabDistance, false, glyphIndex.intValue());
					--tabDistance;
					x++;
				}
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
						glyphOffsets[x] = GlyphIndex.inTab(tabDistance, true, cellIndex);
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
				// simple case: cell indexes coincide with glyph indexes
				int ix = glyphIndex.intValue();
				if(ix + xmax >= r.getGlyphCount())
				{
					// end of line
					int sz = r.getGlyphCount() - ix;
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


	public static int computeBreaks(VerticalScrollHelper helper, ITabPolicy tabPolicy, FlowLine fline, int xmax)
	{
		int lineIndex = fline.getModelIndex();
		if(lineIndex < 0)
		{
			return 0;
		}
		
		int cellIndex = 0;
		int x = 0;
		GlyphIndex startGlyphIndex = GlyphIndex.ZERO;
		GlyphIndex glyphIndex = GlyphIndex.ZERO;
		int tabDistance = 0;
		boolean bol = true;
		
		boolean complex = fline.hasComplexGlyphs();
		if(!complex)
		{
			if(!tabPolicy.isSimple())
			{
				complex |= fline.hasTabs();
			}
		}
		
		helper.addEntry(lineIndex, startGlyphIndex);
		
		int y = 1;
		
		for(;;)
		{
			if(tabDistance > 0)
			{
				if(x >= xmax)
				{
					// carry on to next line, resetting tab distance
					startGlyphIndex = glyphIndex;
					tabDistance = 0;
					x = 0;
					y++;
					bol = false;
				}
				else
				{
					--tabDistance;
					x++;
				}
			}
			else if(complex)
			{
				if(x >= xmax)
				{
					// next row
					startGlyphIndex = glyphIndex;
					tabDistance = 0;
					x = 0;
					y++;
					helper.addEntry(lineIndex, startGlyphIndex);
				}
				else
				{
					GlyphType gt = fline.getGlyphType(glyphIndex);
					switch(gt)
					{
					case EOL:
						return y;
					case TAB:
						tabDistance = tabPolicy.nextTabStop(x) - x;
						--tabDistance;
						glyphIndex = glyphIndex.increment();
						cellIndex++;
						x++;
						break;
					case NORMAL:
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
				if(cellIndex + xmax >= fline.info().getGlyphCount())
				{
					// end of line
					return y;
				}
				else
				{
					// middle of line
					glyphIndex = glyphIndex.add(xmax);
					cellIndex += xmax;
					startGlyphIndex = glyphIndex;
				}
				
				x = 0;
				y++;

				helper.addEntry(lineIndex, startGlyphIndex);
			}
		}
	}
}
