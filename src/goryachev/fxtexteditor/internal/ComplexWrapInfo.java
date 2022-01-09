// Copyright © 2020-2022 Andy Goryachev <andy@goryachev.com>
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


	public int getGlyphIndexForRow(int row)
	{
		int[] cs = cells[row];
		return GlyphIndex.fixGlypIndex(cs[0]);
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
		
		int gix = findNearestInsertPoint(cs, column);

		return fline.getCharIndex(gix);
	}
	
	
	public TextCell getCell(TextCell cell, int wrapRow, int column)
	{
		if(wrapRow >= cells.length)
		{
			throw new Error("wrapRow=" + wrapRow);
		}
		
		int[] cs = cells[wrapRow];

		GlyphType type;
		int caretIndex;
		int leadingCharIndex;
		int insertCharIndex;
		int glyphIndex;
		
		if(column < cs.length)
		{
			int gix = cs[column];
			if(gix < 0)
			{
				type = GlyphType.TAB;
				int gx = GlyphIndex.fixGlypIndex(gix);
				
				int ix = fline.getCharIndex(gx);
				int gi = backtrackToLeadingTabEdge(cs, column, gx);
				int leadIndex = fline.getCharIndex(gi);
				boolean leading = isLeading(cs, column, gix);
				int caret = leading ? ix : -1;
				
				// delaying lead/insert position computations to speed up painting
				// as these may not be always required
				return new TextCell(type, caret, ix, ix, gx)
				{
					public int getLeadingEdgeCharIndex()
					{
						return leadIndex;
					}
					
					
					public int getInsertCharIndex()
					{
						int gi = findNearestInsertPoint(cs, column);
						return fline.getCharIndex(gi);
					}
					
					
					public int getTabSpan()
					{
						return computeTabSpan(cs, column);
					}
				};
			}
			else
			{
				type = GlyphType.REG;
				int ix = fline.getCharIndex(gix);
				
				caretIndex = ix;
				leadingCharIndex = ix;
				insertCharIndex = ix;
				glyphIndex = ix;
			}
		}
		else if(column == cs.length)
		{
			type = GlyphType.EOL;
			int ix = fline.getTextLength();
			
			caretIndex = ix;
			leadingCharIndex = ix;
			insertCharIndex = ix;
			glyphIndex = ix;
		}
		else
		{
			type = GlyphType.EOL;
			int ix = fline.getTextLength();
			
			caretIndex = -1;
			leadingCharIndex = -1;
			insertCharIndex = ix;
			glyphIndex = -1;
		}
		
		cell.set(type, caretIndex, leadingCharIndex, insertCharIndex, glyphIndex);
		return cell;
	}
	
	
	protected static int computeTabSpan(int[] cs, int ix)
	{
		int span = 1;
		int val = cs[ix];
		for(int i=ix+1; i<cs.length; i++)
		{
			if(cs[i] != val)
			{
				return span; 
			}
			
			span++;
		}
		
		return span;
	}
	
	
	/** returns true of glyph at index ix is the leading tab index */
	protected static boolean isLeading(int[] cs, int ix, int gix)
	{
		if(ix == 0)
		{
			return true;
		}
		return (cs[ix - 1] != gix);
	}

	
	/** backtracks from the cell at index ix (value gix) to find the leading egde.  returns glyph index */
	protected static int backtrackToLeadingTabEdge(int[] cs, int ix, int gix)
	{
		for(int i=ix; i>=0; --i)
		{
			int j = i - 1;
			if(j < 0)
			{
				int v = cs[0];
				return GlyphIndex.fixGlypIndex(v);
			}
			
			int gv = cs[j];
			if(gv != gix)
			{
				int v = cs[i];
				return GlyphIndex.fixGlypIndex(v);
			}
		}
		
		throw new Error();
	}
	
	
	/** returns glyph index */
	protected static int findNearestInsertPoint(int[] cs, int column)
	{
		if(column >= cs.length)
		{
			int gix = cs[cs.length - 1];
			++gix;
			return gix;
		}
		
		int gix = cs[column];
		if(gix >= 0)
		{
			return gix;
		}
		
		// in the middle of a tab: either tab start or tab end will be the closest point
		for(int i=1; i<cs.length; i++)
		{
			// step to the right
			int ix = column + i;
			if(ix >= cs.length)
			{
				gix = cs[cs.length - 1];
				break;
			}
			else if(cs[ix] != gix)
			{
				gix = cs[ix];
				break;
			}
			
			// step to the left
			ix = column - i;
			if(ix <= 0)
			{
				gix = cs[0];
				break;
			}
			else if(cs[ix] != gix)
			{
				gix = cs[ix + 1];
				break;
			}
		}
		
		return GlyphIndex.fixGlypIndex(gix);
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