// Copyright © 2017-2020 Andy Goryachev <andy@goryachev.com>
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
	private final int charIndex;
	
	
	public TextPos(int line, int charIndex)
	{
		this.line = line;
		this.charIndex = charIndex;
	}
	

	public int getLine()
	{
		return line;
	}
	
	
	public int getCharIndex()
	{
		return charIndex;
	}
	
	
	public String toString()
	{
		SB sb = new SB(32);
		sb.a("TextPos[");
		sb.a(line);
		sb.a(':');
		sb.a(getCharIndex());
		sb.a("]");
		return sb.toString();
	}
}
