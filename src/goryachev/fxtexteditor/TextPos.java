// Copyright © 2017-2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.util.Assert;
import goryachev.common.util.FH;
import goryachev.common.util.SB;
import goryachev.fxtexteditor.internal.Markers;


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
	private final int position;
	private final int caret;
	
	
	public TextPos(int line, int pos, int caret)
	{
		this.line = line;
		this.position = pos;
		this.caret = caret;
	}
	

	public int hashCode()
	{
		int h = FH.hash(TextPos.class);
		h = FH.hash(h, line);
		h = FH.hash(h, position);
		return FH.hash(h, caret);
	}

	
	public boolean equals(Object x)
	{
		if(x == this)
		{
			return true;
		}
		else if(x instanceof TextPos)
		{
			TextPos m = (TextPos)x;
			return
				(line == m.line) && 
				(position == m.position) &&
				(caret == m.caret);
		}
		else
		{
			return false;
		}
	}

	
	public int compareTo(TextPos m)
	{
		int d = line - m.line;
		if(d == 0)
		{
			return getPosition() - m.getPosition();
		}
		return d;
	}
	

	public int getLine()
	{
		return line;
	}
	
	
	public int getPosition()
	{
		return position;
	}
	
	
	public int getCaret()
	{
		return caret;
	}
	
	
	public boolean isValidCaret()
	{
		return (caret == position);
	}
	
	
	public String toString()
	{
		SB sb = new SB(16);
		sb.a(line);
		sb.a(':');
		sb.a(getPosition());
		return sb.toString();
	}


	public boolean isBefore(TextPos m)
	{
		if(line < m.line)
		{
			return true;
		}
		else if(line == m.line)
		{
			if(position < m.position)
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
			if(getPosition() < pos)
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
			if(getPosition() <= pos)
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
			if(getPosition() > pos)
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
			if(getPosition() >= pos)
			{
				return true;
			}
		}
		
		return false;
	}
}
