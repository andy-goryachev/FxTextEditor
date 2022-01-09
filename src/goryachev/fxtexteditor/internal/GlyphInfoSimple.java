// Copyright © 2020-2022 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;


/**
 * AGlyphInfo when there is 1:1 correspondence between characters and glyphs.
 */
public class GlyphInfoSimple
	extends AGlyphInfo
{
	public GlyphInfoSimple(String text, boolean hasTabs)
	{
		super(text, hasTabs);
	}


	public String getGlyphText(int ix)
	{
		if((ix >= 0) && (ix < text.length()))
		{
			return text.substring(ix, ix + 1);
		}
		return null;
	}


	public int getCharIndex(int glyphIndex)
	{
		if(glyphIndex < 0)
		{
			return 0;
		}
		else if(glyphIndex > text.length())
		{
			return text.length();
		}
		return glyphIndex;
	}
	
	
	public int getGlyphIndex(int charIndex)
	{
		return charIndex;
	}


	public int getGlyphCount()
	{
		return text.length();
	}


	public boolean hasComplexGlyphs()
	{
		return false;
	}
}