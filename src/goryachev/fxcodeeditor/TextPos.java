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


	public int index()
	{
		return index;
	}


	public int offset()
	{
		return offset;
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
