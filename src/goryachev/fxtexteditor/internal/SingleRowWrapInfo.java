// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.ITabPolicy;


/**
 * Simple WrapInfo with 1:1 mapping between 
 * characters, glyphs, and screen cells, 
 * represents a single non-wrapped line.
 */
public class SingleRowWrapInfo
	extends WrapInfo
{
	private final int length;
	
	
	public SingleRowWrapInfo(int length)
	{
		this.length = length;
	}


	public int getWrapRowCount()
	{
		return 1;
	}


	public int getGlyphIndexForRow_DELETE(int row)
	{
		return 0;
	}
	

	public int findRowForGlyphIndex(int glyphIndex)
	{
		return 0;
	}


	public boolean isCompatible(ITabPolicy tabPolicy, int width, boolean wrapLines)
	{
		return (wrapLines == false);
	}


	public int getWrapRowForCharIndex(int charIndex)
	{
		return 0;
	}
	
	
	public int getWrapRowForGlyphIndex(int glyphIndex)
	{
		return 0;
	}


	public int getColumnForCharIndex(int charIndex)
	{
		if(charIndex > length)
		{
			return length;
		}
		
		return charIndex;
	}


	public int getCharIndexForColumn(int wrapRow, int column)
	{
		if(wrapRow != 0)
		{
			throw new Error("wrapRow=" + wrapRow);
		}
		
		if(column > length)
		{
			return length;
		}
		
		return column;
	}
	
	
	protected int getGlyphIndex(int wrapRow, int column)
	{
		if(wrapRow != 0)
		{
			throw new Error("wrapRow=" + wrapRow);
		}
		
		if(column >= length)
		{
			return GlyphIndex.EOL_INDEX;
		}
		
		return column;
	}
}