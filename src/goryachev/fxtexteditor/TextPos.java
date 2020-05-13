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
	private final boolean synthetic;
	
	
	public TextPos(int line, int charIndex, boolean synthetic)
	{
		this.line = line;
		this.charIndex = charIndex;
		this.synthetic = synthetic;
	}
	

	public int getLine()
	{
		return line;
	}
	
	
	public int getCharIndex()
	{
		return charIndex;
	}
	
	
	/** 
	 * returns true if this insert position differs from a position which otherwise would exist
	 * at the requested coordinates, for example, when the user clicks beyond the end of line
	 * or end of file
	 */ 
	public boolean isSynthetic()
	{
		return synthetic;
	}
	
	
	public String toString()
	{
		SB sb = new SB(32);
		sb.a("TextPos[");
		if(synthetic)
		{
			sb.a("S:");
		}
		sb.a(line);
		sb.a(':');
		sb.a(getCharIndex());
		sb.a("]");
		return sb.toString();
	}
}
