// Copyright © 2020-2022 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.GlyphType;
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
	

	public boolean isCompatible(ITabPolicy tabPolicy, int width, boolean wrapLines)
	{
		return
			(true == wrapLines) &&
			(this.width == width);
	}


	public int getWrapRowForCharIndex(int charIndex)
	{
		if(charIndex < 0)
		{
			return 0;
		}
		else if(width == 0)
		{
			return 0;
		}
		
		if(charIndex > length)
		{
			charIndex = length;
		}
		
		return charIndex / width;
	}
	
	
	public int getWrapRowForGlyphIndex(int glyphIndex)
	{
		return getWrapRowForCharIndex(glyphIndex);
	}


	public int getColumnForCharIndex(int charIndex)
	{
		if(charIndex < 0)
		{
			return 0;
		}
		
		if(charIndex > length)
		{
			charIndex = length;
		}
		
		return charIndex % width;
	}
	

	public int getCharIndexForColumn(int wrapRow, int column)
	{
		int ix = (wrapRow * width) + column;
		if(ix < length)
		{
			return ix;
		}
		return length;
	}
	

	public int getGlyphCountAtRow(int wrapRow)
	{
		int rows = getWrapRowCount();
		if(wrapRow < (rows - 1))
		{
			return width;
		}
		
		return length % width;
	}
	
	
	public TextCell getCell(TextCell cell, int wrapRow, int column)
	{
		GlyphType type;
		
		int ix = (wrapRow * width) + column;
		if(ix < length)
		{
			type = GlyphType.REG;
		}
		else if(ix == length)
		{
			type = GlyphType.EOL;
		}
		else
		{
			type = GlyphType.EOL;
			ix = -1;
		}
		
		cell.set(type, ix, ix, ix, ix);
		return cell;
	}
}