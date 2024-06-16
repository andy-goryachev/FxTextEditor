// Copyright © 2017-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.util.Assert;
import goryachev.common.util.FH;
import goryachev.common.util.SB;
import goryachev.fxtexteditor.internal.Markers;


/**
 * Marker represents a position in the text model maintained 
 * in the presence of insertion and removals.
 */
public class Marker
	implements Comparable<Marker>
{
	public static final Marker ZERO = new Marker();
	private int line;
	private int charIndex;
	
	
	public Marker(Markers owner, int line, int charIndex)
	{
		Assert.notNull(owner, "owner");
		
		this.line = line;
		this.charIndex = charIndex;
	}
	
	
	private Marker()
	{
		this.line = 0;
		this.charIndex = 0;
	}
	

	@Override
	public int hashCode()
	{
		int h = FH.hash(Marker.class);
		h = FH.hash(h, line);
		return FH.hash(h, charIndex);
	}

	
	@Override
	public boolean equals(Object x)
	{
		if(x == this)
		{
			return true;
		}
		else if(x instanceof Marker)
		{
			Marker m = (Marker)x;
			return (line == m.line) && (charIndex == m.charIndex);
		}
		else
		{
			return false;
		}
	}

	
	@Override
	public int compareTo(Marker m)
	{
		int d = line - m.line;
		if(d == 0)
		{
			return getCharIndex() - m.getCharIndex();
		}
		return d;
	}
	
	
	public void reset(int line, int pos)
	{
		this.line = line;
		this.charIndex = pos;
	}
	

	/** returns the line index */
	public int getLine()
	{
		return line;
	}
	
	
	/** returns the character index */
	public int getCharIndex()
	{
		return charIndex;
	}
	
	
	@Override
	public String toString()
	{
		SB sb = new SB(16);
		sb.a(line);
		sb.a(':');
		sb.a(getCharIndex());
		return sb.toString();
	}


	public boolean isBefore(Marker m)
	{
		if(line < m.line)
		{
			return true;
		}
		else if(line == m.line)
		{
			if(charIndex < m.charIndex)
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
			if(this.charIndex > pos)
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
	
	
	public void moveLine(int delta)
	{
		line += delta;
	}
	
	
	public void movePosition(int delta)
	{
		// TODO validate
		charIndex += delta;
	}
}
