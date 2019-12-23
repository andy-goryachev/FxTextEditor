// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.util.SB;


/**
 * Glyph Index is different from screen cell index and character index.
 */
public class GlyphIndex
{
	private static final int EOF_INDEX = Integer.MIN_VALUE;
	private static final int EOL_INDEX = Integer.MIN_VALUE + 1;
	private static final int BOL_INDEX = Integer.MIN_VALUE + 2;
	
	public static final GlyphIndex EOF = new GlyphIndex(EOF_INDEX);
	public static final GlyphIndex EOL = new GlyphIndex(EOL_INDEX); // TODO needs leading char off
	public static final GlyphIndex BOL = new GlyphIndex(BOL_INDEX);
	public static final GlyphIndex ZERO = new GlyphIndex(0);
	
	private final int index;
	
	
	protected GlyphIndex(int ix)
	{
		this.index = ix;
	}
	
	
	public static GlyphIndex of(int ix)
	{
		return new GlyphIndex(ix);
	}
	
	
	public static GlyphIndex atEOL()
	{
		return new GlyphIndex(EOL_INDEX)
		{
			@Override
			public boolean isAtEOL()
			{
				return true;
			}
		};
	}
	
	
	public static GlyphIndex inTab(int tabSpan, boolean leading, int off)
	{
		return new GlyphIndex(-tabSpan)
		{
			@Override
			public int getLeadingCharIndex()
			{
				return leading ? off : -1;
			}
			
			
			@Override
			public int getTabCharIndex()
			{
				return off;
			}
		};
	}
	
	
	public int intValue()
	{
		return index;
	}
	
	
	public boolean isEOF()
	{
		return index == EOF.index;
	}
	
	
	public boolean isEOL()
	{
		return index == EOL.index;
	}
	
	
	public boolean isBOL()
	{
		return index == BOL.index;
	}
	
	
	public boolean isInsideTab()
	{
		switch(index)
		{
		case EOF_INDEX:
		case EOL_INDEX:
		case BOL_INDEX:
			return false;
		}
		
		return (index < 0);
	}
	
	
	public boolean isRegular()
	{
		switch(index)
		{
		case EOF_INDEX:
		case EOL_INDEX:
		case BOL_INDEX:
			return false;
		}
		
		return (index >= 0);
	}
	
	
	public String toString()
	{
		SB sb = new SB();
		sb.a("GlyphIndex.");
		
		switch(index)
		{
		case EOF_INDEX:
			sb.append("EOF");
			break;
		case EOL_INDEX:
			sb.append("EOL");
			break;
		case BOL_INDEX:
			sb.append("BOL");
			break;
		default:
			if(index < 0)
			{
				sb.a("TAB(").a(index).a(",");
				sb.a(getLeadingCharIndex()).a(",");
				sb.a(getTabCharIndex()).a(")");
			}
			else
			{
				sb.a(index);
			}
		}
		
		return sb.toString();
	}
	
	
	public GlyphIndex increment()
	{
		return add(1);
	}
	
	
	public GlyphIndex add(int delta)
	{
		if(isRegular())
		{
			// strictly speaking, we must use long here
			// but it's unlikely that both index and delta are huge
			int ix = index + delta;
			
			GlyphIndex rv = of(ix);
			if(rv.isRegular())
			{
				return rv;
			}
			throw new Error("incremented index out of allowed range: " + ix);
		}
		else
		{
			throw new Error("cannot increment non-regular index");
		}
	}


	/** returns a char offset only if the first cell within a tab, otherwise -1 */
	public int getLeadingCharIndex()
	{
		return -1;
	}
	
	
	/** returns a char offset only if within the tab, otherwise -1 */
	public int getTabCharIndex()
	{
		return -1;
	}
	
	
	/** returns true if this glyph is at EOL exacly, false otherwise */
	public boolean isAtEOL()
	{
		return false;
	}
}
