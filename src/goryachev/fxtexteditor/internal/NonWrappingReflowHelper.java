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
				r.setSize(0);
				r.setTextLine(null);
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

				if(complex)
				{
					int[] offsets = r.prepareOffsetsForWidth(xmax);
					
					int glyphCount = tline.getGlyphCount();
					int maxCellIndex = topCellIndex + xmax;
					int size = 0;
					int glyphIndex = 0;
					int cellIndex = 0;
					boolean run = true;
					
					// TODO
					r.setStartGlyphIndex(topCellIndex);
					
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
								if(cellIndex >= topCellIndex)
								{
									offsets[cellIndex - topCellIndex] = -ct;
									size++;
								}
								cellIndex++;
							}
							glyphIndex++;
							break;
						case NORMAL:
							if(cellIndex >= topCellIndex)
							{
								offsets[cellIndex - topCellIndex] = glyphIndex;
								size++;
							}
							glyphIndex++;
							cellIndex++;
							break;
						default:
							throw new Error("?" + gt);
						}
					}
					
					r.setSize(size);
					
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
