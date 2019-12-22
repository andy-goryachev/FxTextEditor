// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;


/**
 * Glyph Index is different from screen cell index and character index.
 */
public class GlyphIndex
{
	private static final int EOF = Integer.MIN_VALUE;
	private static final int EOL = Integer.MIN_VALUE + 1;
	private static final int BOL = Integer.MIN_VALUE + 2;
	
	private final int index;
	
	
	private GlyphIndex(int ix)
	{
		this.index = ix;
	}
	
	
	public static GlyphIndex of(int ix)
	{
		return new GlyphIndex(ix);
	}
	
	
	public int toInt()
	{
		return index;
	}
	
	
	public boolean isEOF()
	{
		return index == EOF;
	}
	
	
	public boolean isEOL()
	{
		return index == EOL;
	}
	
	
	public boolean isBOL()
	{
		return index == BOL;
	}
	
	
	public boolean isInsideTab()
	{
		switch(index)
		{
		case EOF:
		case EOL:
		case BOL:
			return false;
		}
		
		return (index < 0);
	}
	
	
	public boolean isRegular()
	{
		switch(index)
		{
		case EOF:
		case EOL:
		case BOL:
			return false;
		}
		
		return (index >= 0);
	}
}
