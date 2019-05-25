// Copyright © 2017-2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.util.Assert;
import goryachev.common.util.FH;
import goryachev.common.util.SB;
import goryachev.fxtexteditor.internal.Markers;


/**
 * Marker represents a position in the text model maintained 
 * in the presence of insertion and removals.
 * 
 * TODO perhaps it should refer to insert position instead of (char index / leading)
 */
public class Marker
	implements Comparable<Marker>
{
	public static final Marker ZERO = new Marker();
	private int line;
	private int position;
	
	
	public Marker(Markers owner, int line, int pos)
	{
		Assert.notNull(owner, "owner");
		
		this.line = line;
		this.position = pos;
	}
	
	
	private Marker()
	{
		this.line = 0;
		this.position = 0;
	}
	

	public int hashCode()
	{
		int h = FH.hash(Marker.class);
		h = FH.hash(h, line);
		return FH.hash(h, position);
	}

	
	public boolean equals(Object x)
	{
		if(x == this)
		{
			return true;
		}
		else if(x instanceof Marker)
		{
			Marker m = (Marker)x;
			return (line == m.line) && (position == m.position);
		}
		else
		{
			return false;
		}
	}

	
	public int compareTo(Marker m)
	{
		int d = line - m.line;
		if(d == 0)
		{
			return getPosition() - m.getPosition();
		}
		return d;
	}
	
	
	public void reset(int line, int pos)
	{
		this.line = line;
		this.position = pos;
	}
	

	/** returns the line index */
	public int getLine()
	{
		return line;
	}
	
	
	public String toString()
	{
		SB sb = new SB(16);
		sb.a(line);
		sb.a(':');
		sb.a(getPosition());
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
	
	
	public void moveLine(int delta)
	{
		line += delta;
	}
	
	
	public void movePosition(int delta)
	{
		// TODO validate
		position += delta;
	}
	
	
	public int getPosition()
	{
		return position;
	}
}
