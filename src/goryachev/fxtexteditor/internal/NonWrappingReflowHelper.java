// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.GlyphType;
import goryachev.fxtexteditor.ITabPolicy;
import goryachev.fxtexteditor.ITextLine;
import goryachev.fxtexteditor.SelectionSegment;
import goryachev.fxtexteditor.VFlow;
import java.util.List;


/**
 * Non-Wrapping Reflow Helper.
 */
public class NonWrappingReflowHelper
{
	public static void reflow(VFlow flow, ScreenBuffer buffer, int xmax, int ymax, ITabPolicy tabPolicy)
	{
		int lineIndex = flow.getTopLine();
		int topCellIndex = flow.getTopCellIndex();
		SelectionSegment[] selection = flow.getSelectionSegments();
		int selectedSegmentIndex;
		
		for(int y=0; y<ymax; y++)
		{
			ScreenRow r = buffer.getRow(y);
			FlowLine fline = flow.getTextLine(lineIndex);
			boolean complex = fline.hasComplexGlyphs();
			if(!complex)
			{
				if(!tabPolicy.isSimple())
				{
					complex |= fline.hasTabs();
				}
			}

			r.initLine(fline);
			r.setComplex(complex);
			r.setAppendModelIndex(-1);
			
			byte[] flags = r.prepareFlagsForWidth(xmax);

			if(complex)
			{
				GlyphIndex[] glyphOffsets = r.prepareGlyphOffsetsForWidth(xmax);
				int glyphCount = fline.getGlyphCount();
				int maxCellIndex = topCellIndex + xmax;
				int size = 0;
				int cellIndex = 0;
				boolean run = true;
				GlyphIndex glyphIndex = GlyphIndex.ZERO;
				GlyphIndex startGlyphIndex = GlyphIndex.ZERO;
				
				while(run)
				{
					GlyphType gt = r.getGlyphType(glyphIndex);
					switch(gt)
					{
					case EOL:
						run = false;
						break;
					case TAB:
						int d = tabPolicy.nextTabStop(cellIndex);
						int ct = d - cellIndex;
						for( ; ct>0; ct--)
						{
							if((cellIndex >= topCellIndex) && (cellIndex < maxCellIndex))
							{
								if(cellIndex == topCellIndex)
								{
									startGlyphIndex = glyphIndex;
								}
								
								glyphOffsets[cellIndex - topCellIndex] = GlyphIndex.of(-ct);
								size++;
							}
							cellIndex++;
						}
						glyphIndex = glyphIndex.increment();
						break;
					case NORMAL:
						if((cellIndex >= topCellIndex) && (cellIndex < maxCellIndex))
						{
							if(cellIndex == topCellIndex)
							{
								startGlyphIndex = glyphIndex;
							}
							
							glyphOffsets[cellIndex - topCellIndex] = glyphIndex;
							size++;
						}
						glyphIndex = glyphIndex.increment();
						cellIndex++;
						break;
					default:
						throw new Error("?" + gt);
					}
				}
				
				r.setCellCount(size);
				r.setStartGlyphIndex(startGlyphIndex);

				if(glyphIndex.intValue() >= glyphCount)
				{
					run = false;
				}
				else if(cellIndex >= maxCellIndex)
				{
					run = false;
				}
			}
			else
			{
				// cell index coincides with glyph index
				r.setStartGlyphIndex(GlyphIndex.of(topCellIndex));
				
				// TODO selectedBefore, selectedAfter
				boolean caretLine = false;
				for(SelectionSegment seg: selection)
				{
					if(lineIndex < seg.getMinLine())
					{
						break;
					}
					
					if((lineIndex >= seg.getMinLine()) && (lineIndex <= seg.getMaxLine()))
					{
						if(seg.getCaretLine() == lineIndex)
						{
							caretLine = true;
							r.setCaret(seg.getCaretCharIndex() - topCellIndex);
						}
					}
				}
				
				r.setCaretLine(caretLine);
			}
			
			lineIndex++;
		}
	}
}
