// Copyright © 2020-2022 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fx.internal.GlyphCache;
import goryachev.fxtexteditor.GlyphType;


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
			char c = text.charAt(ix);
			return GlyphCache.get(c);
		}
		return null;
	}
	
	
	/** returns the type of a glyph at the specified glyph index. */
	public GlyphType getGlyphType(int ix)
	{
		if(ix < 0)
		{
			return GlyphType.EOL;
		}
		else if(ix >= text.length())
		{
			return GlyphType.EOL;
		}
		else
		{
			char c = text.charAt(ix);
			if(c == '\t')
			{
				return GlyphType.TAB;
			}
		}
		return GlyphType.REG;
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