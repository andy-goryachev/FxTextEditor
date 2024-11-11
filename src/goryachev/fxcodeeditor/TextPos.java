// Copyright © 2024-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxcodeeditor;
import goryachev.common.util.FH;


/**
 * Position within the text, corresponding to the insertion point between the characters.
 */
public final class TextPos
	implements Comparable<TextPos>
{
	public static final TextPos ZERO = new TextPos(0, 0);
	
	private final int index;
	private final int offset;


	public TextPos(int index, int offset)
	{
		this.index = index;
		this.offset = offset;
	}


	/**
	 * Returns 0-based model index.
	 */
	public int index()
	{
		return index;
	}


	/**
	 * Returns 0-based character offset.
	 */
	public int offset()
	{
		return offset;
	}
	
	
	/**
	 * Returns 1-based line number, equaling to {@link #index} + 1.
	 */
	public int getLineNumber()
	{
		return index + 1;
	}
	
	
	/**
	 * Returns 1-based visual column number, equaling to {@link #offset} + 1.
	 */
	public int getColumn()
	{
		return offset + 1;
	}


	@Override
	public boolean equals(Object x)
	{
		if(x == this)
		{
			return true;
		}
		else if(x instanceof TextPos p)
		{
			return (index == p.index) && (offset == p.offset);
		}
		return false;
	}


	@Override
	public int hashCode()
	{
		int h = FH.hash(TextPos.class);
		h = FH.hash(h, index);
		return FH.hash(h, offset);
	}


	@Override
	public int compareTo(TextPos p)
	{
		int d = index - p.index;
		if(d == 0)
		{
			return offset - p.offset;
		}
		return d;
	}


	@Override
	public String toString()
	{
		return "TextPos{index=" + index + ", offset=" + offset + "}";
	}
}
