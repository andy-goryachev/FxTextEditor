// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
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
	
	
	public String toString()
	{
		return
			"WrapPos[" + line + 
			":" + row +
			"]";
	}


	/** returns insert position (char index) for the given column */  
	public int getInsertPosition(int column)
	{
		return wrap.getCharIndexForColumn(row, column);
	}
}
