// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.util.CList;
import goryachev.common.util.ElasticIntArray;
import goryachev.fxtexteditor.GlyphType;
import goryachev.fxtexteditor.ITabPolicy;


/**
 * Complex WrapInfo encapsulates mapping between
 * text characters, glyphs, and screen cells, for the given 
 * screen configuration (tab policy, width, wrapping mode).
 */
public class ComplexWrapInfo 
	extends WrapInfo
{
	protected final ITabPolicy tabPolicy;
	protected final int width;
	protected final boolean wrapLines;
	/**
	 * maps cells to glyphIndexes:
	 * the values provides glyphIndex for regular glyphs (>0), 
	 * or -glyphIndex if inside a tab.
	 */ 
	protected final int[][] cells;
	
	
	public ComplexWrapInfo(ITabPolicy tabPolicy, int width, boolean wrapLines, int[][] cells)
	{
		this.tabPolicy = tabPolicy;
		this.width = width;
		this.wrapLines = wrapLines;
		this.cells = cells;
	}
	
	
	public int getWrapRowCount()
	{
		return cells.length;
	}


	public int getGlyphIndexForRow(int row)
	{
		return cells[row][0];
	}
	
	
	public int findRowForGlyphIndex(int glyphIndex)
	{
		// TODO binary search would be better
		int sz = getWrapRowCount();
		for(int i=0; i<sz; i++)
		{
			int ix = getGlyphIndexForRow(i);
			if(ix > glyphIndex)
			{
				return i - 1;
			}
		}
		return sz - 1;
	}
	
	
	public boolean isCompatible(ITabPolicy tabPolicy, int width, boolean wrapLines)
	{
		return 
			(this.wrapLines == wrapLines) &&
			(this.width == width) &&
			(this.tabPolicy == tabPolicy);
	}


	public int getWrapRowForCharIndex(int charIndex)
	{
//		int gix = charToGlyphIndex(charIndex);
//		return 0;
		throw new Error(); // TODO
	}


	public int getColumnForCharIndex(int charIndex)
	{
//		return 0;
		throw new Error(); // TODO
	}


	public int getCharIndexForColumn(int wrapRow, int column)
	{
		int gix = cells[wrapRow][column];
		if(gix < 0)
		{
			// TODO backtrack to first, then add 
		}
		return gix;
	}
	
	
	public static ComplexWrapInfo createComplexWrapInfo(FlowLine fline, ITabPolicy tabPolicy, int width, boolean wrapLines)
	{
		CList<int[]> rows = new CList(4);
		ElasticIntArray cells = null;
		int glyphIndex = 0;
		int x = 0;
		int tabSpan = -1;
		
		for(;;)
		{
			if(cells == null)
			{
				cells = new ElasticIntArray();
			}
			
			// main finite state machine loop
			
			if(tabSpan > 0)
			{
				if(wrapLines && (x >= width))
				{
					// carry on to next line, resetting tab span
					tabSpan = -1;
					rows.add(cells.toArray());
					cells = null;
					x = 0;
					glyphIndex++;
				}
				else
				{
					cells.add(-glyphIndex-1);
					--tabSpan;
					if(tabSpan == 0)
					{
						glyphIndex++;
					}
					x++;
				}
			}
			else
			{
				if(wrapLines && (x >= width))
				{
					// next row
					if(tabSpan == 0)
					{
						glyphIndex++;
					}
					tabSpan = -1;
					x = 0;
					rows.add(cells.toArray());
					cells = null;
				}
				else
				{
					tabSpan = -1;
					
					GlyphType gt = fline.getGlyphType(glyphIndex);
					switch(gt)
					{
					case EOL:
						// we are done
						if(cells.size() > 0)
						{
							rows.add(cells.toArray());
						}
						int[][] rs = new int[rows.size()][];
						rows.toArray(rs);
						return new ComplexWrapInfo(tabPolicy, width, wrapLines, rs);
						
					case TAB:
						tabSpan = tabPolicy.nextTabStop(x) - x;
						cells.add(-glyphIndex-1);
						--tabSpan;
						x++;
						break;
						
					case NORMAL:
						cells.add(glyphIndex);
						glyphIndex++;
						x++;
						break;
						
					default:
						throw new Error("?" + gt);
					}
				}
			}
		}
	}
}