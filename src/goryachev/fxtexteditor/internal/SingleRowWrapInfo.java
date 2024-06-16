// Copyright © 2020-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.GlyphType;
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


	@Override
	public int getWrapRowCount()
	{
		return 1;
	}


	@Override
	public int getGlyphIndexForRow(int row)
	{
		return 0;
	}
	

	@Override
	public boolean isCompatible(ITabPolicy tabPolicy, int width, boolean wrapLines)
	{
		return (wrapLines == false);
	}


	@Override
	public int getWrapRowForCharIndex(int charIndex)
	{
		return 0;
	}
	
	
	@Override
	public int getWrapRowForGlyphIndex(int glyphIndex)
	{
		return 0;
	}


	@Override
	public int getColumnForCharIndex(int charIndex)
	{
		if(charIndex > length)
		{
			return length;
		}
		
		return charIndex;
	}
	
	
	protected void checkRow(int wrapRow)
	{
		if(wrapRow != 0)
		{
			throw new Error("wrapRow=" + wrapRow);
		}
	}


	@Override
	public int getCharIndexForColumn(int wrapRow, int column)
	{
		checkRow(wrapRow);
		
		if(column > length)
		{
			return length;
		}
		
		return column;
	}
	
	
	@Override
	public int getGlyphCountAtRow(int wrapRow)
	{
		checkRow(wrapRow);
		
		return length;
	}


	@Override
	public TextCell getCell(TextCell cell, int wrapRow, int column)
	{
		checkRow(wrapRow);
		
		GlyphType type;
		int ix;
		
		if(column < length)
		{
			type = GlyphType.REG;
			ix = column;
		}
		else if(column == length)
		{
			type = GlyphType.EOL;
			ix = column;
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