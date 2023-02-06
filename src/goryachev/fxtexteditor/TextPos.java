// Copyright © 2017-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.util.FH;
import goryachev.common.util.SB;


/**
 * Text position is represented by model line and insert position within the line.
 * 
 * Unlike Marker, the text position is a simple holder and does not move after an 
 * insert or delete operation.
 */
public class TextPos
	implements Comparable<TextPos>
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
	

	public int hashCode()
	{
		int h = FH.hash(TextPos.class);
		h = FH.hash(h, line);
		return FH.hash(h, charIndex);
	}

	
	public boolean equals(Object x)
	{
		if(x == this)
		{
			return true;
		}
		else if(x instanceof TextPos)
		{
			TextPos p = (TextPos)x;
			return (line == p.line) && (charIndex == p.charIndex);
		}
		else
		{
			return false;
		}
	}

	
	public int compareTo(TextPos p)
	{
		int d = line - p.line;
		if(d == 0)
		{
			return getCharIndex() - p.getCharIndex();
		}
		return d;
	}
	
	

	public boolean isBefore(TextPos p)
	{
		if(line < p.line)
		{
			return true;
		}
		else if(line == p.line)
		{
			if(charIndex < p.charIndex)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	
	public boolean isBefore(int line, int pos)
	{
		if(this.line < line)
		{
			return true;
		}
		else if(this.line == line)
		{
			if(getCharIndex() < pos)
			{
				return true;
			}
		}
		return false;
	}
	
	
	public boolean isAtOrBefore(int line, int pos)
	{
		if(this.line < line)
		{
			return true;
		}
		else if(this.line == line)
		{
			if(getCharIndex() <= pos)
			{
				return true;
			}
		}
		return false;
	}
	
	
	public boolean isAfter(int line, int pos)
	{
		if(this.line > line)
		{
			return true;
		}
		else if(this.line == line)
		{
			if(getCharIndex() > pos)
			{
				return true;
			}
		}
		
		return false;
	}
	
	
	public boolean isAtOrAfter(int line, int pos)
	{
		if(this.line > line)
		{
			return true;
		}
		else if(this.line == line)
		{
			if(getCharIndex() >= pos)
			{
				return true;
			}
		}
		
		return false;
	}
	
	
	public String toString()
	{
		SB sb = new SB(32);
		sb.a(line);
		sb.a(':');
		sb.a(getCharIndex());
		return sb.toString();
	}
}
