// Copyright © 2019-2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.log.Log;
import goryachev.fxtexteditor.FxTextEditorModel;
import goryachev.fxtexteditor.GlyphType;
import goryachev.fxtexteditor.ITabPolicy;
import goryachev.fxtexteditor.VFlow;


/**
 * Wrapping Reflow Helper.
 */
public class WrappingReflowHelper
{
	protected static final Log log = Log.get("WrappingReflowHelper");
	private static final GlyphIndex[] AVOID_COMPILER_WARNING = { };
	
	
	// FIX update top cell index if different
	public static void reflow(VFlow flow, ScreenBuffer buffer, int width, int height, ITabPolicy tabPolicy)
	{
		int lineIndex = flow.getTopLine();
		int startGlyphIndex = flow.getTopGlyphIndex().intValue();
		boolean useStartGlyphIndex = true;
		
		log.trace("line=%d start=%d", lineIndex, startGlyphIndex);
			
		int modelLineCount = flow.getModelLineCount();
		int cellIndex = 0;
		int x = 0;
		int y = 0;
		ScreenRow r = null;
		FlowLine fline = null;
		GlyphIndex[] glyphOffsets = AVOID_COMPILER_WARNING;
		int glyphIndex = 0;
		int tabDistance = 0;
		boolean complex = false;
		boolean bol = true;
		
		while(y < height)
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
				}
				else
				{
					glyphIndex = 0;
					startGlyphIndex = 0;
				}
			}
			
			if(r == null)
			{
				r = buffer.getRow(y);
				r.setComplex(complex);

				x = 0;
				
				if(complex)
				{					
					glyphOffsets = r.prepareGlyphOffsetsForWidth(width);
				}
			}
			
			if(x == 0)
			{
				r.initLine(fline, lineIndex, modelLineCount, bol);
				r.setStartGlyphIndex(new GlyphIndex(startGlyphIndex));
				bol = false;
				
				FxTextEditorModel m = flow.getEditor().getModel();
				int mx = m == null ? 0 : m.getLineCount();
				r.setAppendModelIndex(mx == lineIndex ? mx : -1);
			}
			
			// main finite state machine loop
			
			if(tabDistance > 0)
			{
				if(x >= width)
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
					glyphOffsets[x] = GlyphIndex.inTab(tabDistance, false, glyphIndex);
					--tabDistance;
					x++;
				}
			}
			else if(complex)
			{
				if(x >= width)
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
						glyphIndex++;
						cellIndex++;
						x++;
						break;
					case NORMAL:
						glyphOffsets[x] = new GlyphIndex(glyphIndex);
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
				// simple case: cell indexes coincide with glyph indexes
				int ix = glyphIndex;
				if(ix + width >= r.getGlyphCount())
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
					r.setCellCount(width);
					glyphIndex += width;
					cellIndex += width;
					startGlyphIndex = glyphIndex;
				}
				
				y++;
				x = 0;
				r = null;
			}
		}
	}
}
