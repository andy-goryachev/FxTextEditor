// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.fxtexteditor.internal.TextCell;
import goryachev.fxtexteditor.internal.WrapInfo;


/**
 * Encapsulates the starting points of each wrap line:
 * line index (and its WrapInfo), and a wrap row within. 
 */
public class WrapPos
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
	
	
	public int getStartGlyphIndex()
	{
		return wrap.getGlyphIndexForRow(row);
	}
	
	
	public String toString()
	{
		return
			"WrapPos[" + line + 
			":" + row +
			"]";
	}
}
