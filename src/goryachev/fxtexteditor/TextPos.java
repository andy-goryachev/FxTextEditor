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
	private final boolean leading;
	private final boolean synthetic;
	
	
	public TextPos(int line, int offset, boolean leading, boolean synthetic)
	{
		this.line = line;
		this.offset = offset;
		this.leading = leading;
		this.synthetic = synthetic;
	}
	

	public int getLine()
	{
		return line;
	}
	
	
	public int getOffset()
	{
		return offset;
	}
	
	
	public boolean isLeading()
	{
		return leading;
	}
	
	
	public boolean isTrailing()
	{
		return !leading;
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
		if(leading)
		{
			sb.a("L:");
		}
		sb.a(line);
		sb.a(':');
		sb.a(getOffset());
		sb.a("]");
		return sb.toString();
	}
}
