// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.log.Log;
import goryachev.common.util.CList;
import goryachev.common.util.ElasticIntArray;
import goryachev.fxtexteditor.GlyphType;
import goryachev.fxtexteditor.ITabPolicy;
import java.util.Arrays;


/**
 * Complex WrapInfo encapsulates mapping between
 * text characters, glyphs, and screen cells, for the given 
 * screen configuration (tab policy, width, wrapping mode).
 */
public class ComplexWrapInfo 
	extends WrapInfo
{
	protected final FlowLine fline;
	protected final ITabPolicy tabPolicy;
	protected final int width;
	protected final boolean wrapLines;
	/**
	 * maps cells to glyphIndexes:
	 * the values provides glyphIndex for regular glyphs (>0), 
	 * or -glyphIndex if inside a tab.
	 */ 
	protected final int[][] cells;
	
	
	public ComplexWrapInfo(FlowLine fline, ITabPolicy tabPolicy, int width, boolean wrapLines, int[][] cells)
	{
		this.fline = fline;
		this.tabPolicy = tabPolicy;
		this.width = width;
		this.wrapLines = wrapLines;
		this.cells = cells;
	}
	
	
	public int getWrapRowCount()
	{
		return cells.length;
	}


	public int getGlyphIndexForRow_DELETE(int row)
	{
		try
		{
			// TODO check
			int[] cs = cells[row];
			return cs[0];
		}
		catch(Exception e)
		{
			// FIX
			Log.err(e); // FIX
			return 0;
		}
	}
	
	
	protected int getGlyphIndex(int wrapRow, int column)
	{
		if(wrapRow >= cells.length)
		{
			return GlyphIndex.EOF_INDEX;
		}
		
		int[] cs = cells[wrapRow];
		if(column >= cs.length)
		{
			return GlyphIndex.EOL_INDEX;
		}
		
		return cs[column];
	}
	
	
	public int findRowForGlyphIndex(int glyphIndex)
	{
		// TODO binary search would be better
		int sz = getWrapRowCount();
		for(int i=0; i<sz; i++)
		{
			int ix = getGlyphIndexForRow_DELETE(i);
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
		int gix = fline.getGlyphIndex(charIndex);
		return getWrapRowForGlyphIndex(gix);
	}
	
	
	public int getWrapRowForGlyphIndex(int gix)
	{
		int row = 0;
		for( ; row<cells.length; row++)
		{
			int start = cells[row][0];
			if(gix < start)
			{
				return row - 1;
			}
		}
		return row - 1;
	}
	
	
	public int getGlyphCountAtRow(int wrapRow)
	{
		return cells[wrapRow].length;
	}


	public int getColumnForCharIndex(int charIndex)
	{
		int gix = fline.getGlyphIndex(charIndex);
		int row = getWrapRowForGlyphIndex(gix);
		int[] cs = cells[row];
		
		// TODO binary search, keep in mind negative values for tabs
		for(int i=0; i<cs.length; i++)
		{
			int ix = cs[i];
			if(ix < 0)
			{
				ix = -ix - 1;
			}
			
			if(gix <= ix)
			{
				return i;
			}
		}
		
		return cs.length;
	}


	public int getCharIndexForColumn(int wrapRow, int column)
	{
		if((wrapRow < 0) || (wrapRow >= cells.length))
		{
			throw new Error("wrapRow=" + wrapRow);
		}
		
		int[] cs = cells[wrapRow];
		if(column < 0)
		{
			column = 0;
		}
		
		if(column >= cs.length)
		{
			column = cs.length - 1;
		}
		
		int gix = findNearestInsertPoint(cs, column);

		return fline.getCharIndex(gix);
	}
	
	
	// TODO this might belong to WrapInfo
	protected int findNearestInsertPoint(int[] cs, int column)
	{
		int gix = cs[column];
		if(gix >= 0)
		{
			return gix;
		}
		
		// in the middle of a tab: either tab start or tab end will be the closest point
		for(int i=1; i<cs.length; i++)
		{
			// step to the righth
			int ix = column + i;
			if(ix >= cs.length)
			{
				return cs.length;
			}
			else if(cs[ix] != gix)
			{
				return ix;
			}
			
			// step to the left
			ix = column - i;
			if(ix <= 0)
			{
				return 0;
			}
			else if(cs[ix] != gix)
			{
				return ix;
			}
		}
		
		// should never get here
		throw new Error();
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
						return new ComplexWrapInfo(fline, tabPolicy, width, wrapLines, rs);
						
					case TAB:
						tabSpan = tabPolicy.nextTabStop(x) - x;
						cells.add(-glyphIndex-1);
						--tabSpan;
						x++;
						break;
						
					case REG:
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