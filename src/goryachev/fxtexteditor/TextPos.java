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
	private final boolean validCaretLine;
	private final boolean validCaretOffset;
	
	
	public TextPos(int line, int offset, boolean validCaretLine, boolean validCaretOffset)
	{
		this.line = line;
		this.offset = offset;
		this.validCaretLine = validCaretLine;
		this.validCaretOffset = validCaretOffset;
	}
	

	public int getLine()
	{
		return line;
	}
	
	
	public int getOffset()
	{
		return offset;
	}
	
	
	public boolean isValidCaretLine()
	{
		return validCaretLine;
	}
	
	
	public boolean isValidCaretOffset()
	{
		return validCaretOffset;
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
