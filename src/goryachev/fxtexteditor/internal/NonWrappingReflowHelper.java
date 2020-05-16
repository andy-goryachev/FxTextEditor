// Copyright © 2019-2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.GlyphType;
import goryachev.fxtexteditor.ITabPolicy;
import goryachev.fxtexteditor.VFlow;


/**
 * Non-Wrapping Reflow Helper.
 */
public class NonWrappingReflowHelper
{
	public static void reflow(VFlow flow, ScreenBuffer buffer, int xmax, int ymax, ITabPolicy tabPolicy)
	{
		int lineIndex = flow.getTopLine();
		int modelLineCount = flow.getModelLineCount();
		int topCellIndex = flow.getTopCellIndex();
		
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

			r.initLine(fline, lineIndex, modelLineCount, true);
			r.setComplex(complex);
			r.setAppendModelIndex(-1);
			
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
						boolean leading = true;
						for( ; ct>0; ct--)
						{
							if((cellIndex >= topCellIndex) && (cellIndex < maxCellIndex))
							{
								if(cellIndex == topCellIndex)
								{
									startGlyphIndex = glyphIndex;
								}
								
								glyphOffsets[cellIndex - topCellIndex] = GlyphIndex.inTab(ct, leading, glyphIndex.intValue());
								size++;
								leading = false;
							}
							cellIndex++;
						}
						glyphIndex = glyphIndex.increment();
						break;
					case REG:
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
			}
			
			lineIndex++;
		}
	}

	public static int computeCellCount(FlowLine fline, ITabPolicy tabPolicy)
	{
		boolean complex = fline.hasComplexGlyphs();
		if(!complex)
		{
			if(!tabPolicy.isSimple())
			{
				complex |= fline.hasTabs();
			}
		}

		if(complex)
		{
			int glyphCount = fline.getGlyphCount();
			int size = 0;
			int cellIndex = 0;
			GlyphIndex glyphIndex = GlyphIndex.ZERO;
			
			for(;;)
			{
				GlyphType gt = fline.getGlyphType(glyphIndex);
				switch(gt)
				{
				case EOL:
					return size;
				case TAB:
					int d = tabPolicy.nextTabStop(cellIndex);
					int ct = d - cellIndex;
					for( ; ct>0; ct--)
					{
						size++;
						cellIndex++;
					}
					glyphIndex = glyphIndex.increment();
					break;
				case REG:
					size++;
					glyphIndex = glyphIndex.increment();
					cellIndex++;
					break;
				default:
					throw new Error("?" + gt);
				}
			}
		}
		else
		{
			return fline.getTextLength();
		}
	}
}
