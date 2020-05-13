// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.ITabPolicy;


/**
 * Simple WrapInfo with 1:1 mapping between 
 * characters, glyphs, and screen cells.
 */
public class SimpleWrapInfo
	extends WrapInfo
{
	private final int width;
	private final int length;
	
	
	public SimpleWrapInfo(int length, int width)
	{
		this.length = length;
		this.width = width;
	}


	public int getWrapRowCount()
	{
		if(width == 0)
		{
			// TODO is this possible?
			return length;
		}
		return 1 + (length - 1) / width;
	}


	public int getGlyphIndexForRow(int row)
	{
		return row * width;
	}
	

	public int findRowForGlyphIndex(int glyphIndex)
	{
		return glyphIndex / width;
	}


	public boolean isCompatible(ITabPolicy tabPolicy, int width, boolean wrapLines)
	{
		return
			(true == wrapLines) &&
			(this.width == width);
	}


	public int getWrapRowForCharIndex(int charIndex)
	{
		return 0;
	}


	public int getColumnForCharIndex(int charIndex)
	{
		return charIndex % width;
	}


	public int getCharIndexForColumn(int wrapRow, int column)
	{
		return (wrapRow * width) + column;
	}
}