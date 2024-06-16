// Copyright © 2020-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.fxtexteditor.internal.TextCell;
import goryachev.fxtexteditor.internal.WrapInfo;


/**
 * Provides information about position within the array
 * of (possibly wrapped) text rows.
 */
public class WrapPos
	implements Comparable<WrapPos>
{
	private final int line;
	private final int row;
	private final WrapInfo wrap;
	
	
	public WrapPos(int line, int row, WrapInfo wrap)
	{
		this.line = line;
		this.row = row;
		this.wrap = wrap;
	}
	
	
	public int getLine()
	{
		return line;
	}
	
	
	public int getRow()
	{
		return row;
	}
	
	
	public WrapInfo getWrapInfo()
	{
		return wrap;
	}
	
	
	public boolean isAfter(WrapPos p)
	{
		return compareTo(p) > 0;
	}
	
	
	@Override
	public int compareTo(WrapPos p)
	{
		int d = line - p.line;
		if(d == 0)
		{
			d = row - p.row;
		}
		return d;
	}
	
	
	public int getStartGlyphIndex()
	{
		return wrap.getGlyphIndexForRow(row);
	}
	
	
	@Override
	public String toString()
	{
		return
			"WrapPos[" + line + 
			":" + row +
			"]";
	}
}
