// Copyright © 2017-2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.util.SB;


/**
 * Text position is represented by model line and insert position within the line.
 * 
 * Unlike Marker, the text position is a simple holder and does not move after an 
 * insert or delete operation.
 */
public class TextPos
{
	private final int line;
	private final int offset;
	
	
	public TextPos(int line, int offset)
	{
		this.line = line;
		this.offset = offset;
	}
	

	public int getLine()
	{
		return line;
	}
	
	
	public int getOffset()
	{
		return offset;
	}
	
	
	public String toString()
	{
		SB sb = new SB(16);
		sb.a(line);
		sb.a(':');
		sb.a(getOffset());
		return sb.toString();
	}
}
