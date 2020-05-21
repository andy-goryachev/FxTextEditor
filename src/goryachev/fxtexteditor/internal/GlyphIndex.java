// Copyright © 2019-2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.util.SB;


/**
 * Glyph Index is different from screen cell index and character index.
 */
public class GlyphIndex
{
	/** end of file glyph index value */
	public static final int EOF = Integer.MIN_VALUE;
	/** end of line glyph index value */
	public static final int EOL = Integer.MIN_VALUE + 1;

	private final int index;
	
	
	protected GlyphIndex(int ix)
	{
		this.index = ix;
	}
	
	
	public static GlyphIndex of(int ix)
	{
		return new GlyphIndex(ix);
	}
	
	
	public static GlyphIndex atEOL(boolean atEOL)
	{
		return new GlyphIndex(EOL)
		{
			@Override
			public boolean isAtEOL()
			{
				return atEOL;
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
		return index == EOF;
	}
	
	
	/** is end of a line */
	public boolean isEOL()
	{
		return index == EOL;
	}
	
	
	public boolean isInsideTab()
	{
		return isTab(index);
	}
	
	
	public static boolean isTab(int ix)
	{
		if(ix < 0)
		{
			switch(ix)
			{
			case EOF:
			case EOL:
				return false;
			default:
				return true;
			}
		}
		return false;
	}
	
	
	public boolean isRegular()
	{
		switch(index)
		{
		case EOF:
		case EOL:
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
		case EOF:
			sb.append("EOF");
			break;
		case EOL:
			sb.append("EOL");
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
	
	
	public GlyphIndex decrement()
	{
		return add(-1);
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
			throw new Error("cannot increment non-regular index:" + index);
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
	
	
	/** is end of file */
	public static boolean isEOF(int glyphIndex)
	{
		return glyphIndex == EOF;
	}
	
	
	/** is end of a line */
	public  static boolean isEOL(int glyphIndex)
	{
		return glyphIndex == EOL;
	}
	
	
	/** 
	 * converts glyph index to a positie value if inside a tab, 
	 * returns a positive value as is,
	 * throws an Error for EOF or EOL
	 */
	public static int fixGlypIndex(int gix)
	{
		switch(gix)
		{
		case EOF:
		case EOL:
			throw new Error("gix=" + gix);
		}
		
		if(gix < 0)
		{
			return -gix - 1;
		}
		return gix;
	}
}
