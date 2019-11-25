// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.GlyptType;
import goryachev.fxtexteditor.ITabPolicy;
import goryachev.fxtexteditor.ITextLine;
import goryachev.fxtexteditor.VFlow;


/**
 * Non-Wrapping Reflow Helper.
 */
public class NonWrappingReflowHelper
{
	public static void reflow(VFlow flow, ScreenBuffer buffer, int xmax, int ymax, ITabPolicy tabPolicy)
	{
		int lineIndex = flow.getTopLine();
		int topCellIndex = flow.getTopCellIndex();
		
		for(int y=0; y<ymax; y++)
		{
			ScreenRow r = buffer.getRow(y);
			
			ITextLine tline = flow.getTextLine(lineIndex);
			if(tline == null)
			{
				r.setCellCount(0);
				r.setTextLine(null);
				
				int mx = flow.getEditor().getModel().getLineCount();
				r.setAppendModelIndex(mx == lineIndex ? mx : -1);
			}
			else
			{
				boolean complex = tline.hasComplexGlyphs();
				if(!complex)
				{
					if(!tabPolicy.isSimple())
					{
						complex |= tline.hasTabs();
					}
				}

				r.setTextLine(tline);
				r.setComplex(complex);
				r.setAppendModelIndex(-1);

				if(complex)
				{
					int[] glyphOffsets = r.prepareGlyphOffsetsForWidth(xmax);
					int glyphCount = tline.getGlyphCount();
					int maxCellIndex = topCellIndex + xmax;
					int size = 0;
					int glyphIndex = 0;
					int cellIndex = 0;
					boolean run = true;
					int startGlyphIndex = 0;
					
					while(run)
					{
						GlyptType gt = tline.getGlyphType(glyphIndex);
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
									
									glyphOffsets[cellIndex - topCellIndex] = -ct;
									size++;
								}
								cellIndex++;
							}
							glyphIndex++;
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
							glyphIndex++;
							cellIndex++;
							break;
						default:
							throw new Error("?" + gt);
						}
					}
					
					r.setCellCount(size);
					r.setStartGlyphIndex(startGlyphIndex);

					if(glyphIndex >= glyphCount)
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
					r.setStartGlyphIndex(topCellIndex);
				}
			}
			
			lineIndex++;
		}
	}
}