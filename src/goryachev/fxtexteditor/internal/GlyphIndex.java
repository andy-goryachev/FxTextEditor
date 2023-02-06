// Copyright © 2019-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;

/**
 * Glyph Index is different from screen cell index and character index.
 */
public class GlyphIndex
{
	/** end of file glyph index value */
	public static final int EOF = Integer.MIN_VALUE;
	/** end of line glyph index value */
	public static final int EOL = Integer.MIN_VALUE + 1;

	
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
	
	
	/** is end of file */
	public static boolean isEOF(int glyphIndex)
	{
		return glyphIndex == EOF;
	}
	
	
	/** is end of a line */
	public static boolean isEOL(int glyphIndex)
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
