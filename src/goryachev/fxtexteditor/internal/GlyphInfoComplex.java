// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import java.util.Arrays;


/**
 * Complex AGlyphInfo when text does not have 1:1 correspondence between
 * glyph and characters (i.e. contains combining characters, emoji, etc.)
 */
public class GlyphInfoComplex
	extends AGlyphInfo
{
	private final int[] charOffsets;
	
	
	public GlyphInfoComplex(String text, boolean hasTabs, int[] charOffsets)
	{
		super(text, hasTabs);
		this.charOffsets = charOffsets;
	}


	public boolean hasComplexGlyphs()
	{
		return true;
	}
	

	public int getCharIndex(int glyphIndex)
	{
		return charOffsets[glyphIndex];
	}
	
	
	public int getGlyphIndex(int charIndex)
	{
		// this can be replaced either by a separate array
		// possibly created on demand.
		// but for now, let's assume the binary search should be faster and easier on memory
		int ix = Arrays.binarySearch(charOffsets, charIndex);
		if(ix < 0)
		{
			ix = -ix;
		}
		return ix;
	}


	public String getGlyphText(int ix)
	{
		if(ix > charOffsets.length)
		{
			return null;
		}

		int start = charOffsets[ix];
		if(ix == charOffsets.length)
		{
			return text.substring(start);
		}
		else
		{
			int end = charOffsets[ix + 1];
			return text.substring(start);
		}
	}


	public int getGlyphCount()
	{
		return charOffsets.length;
	}
}