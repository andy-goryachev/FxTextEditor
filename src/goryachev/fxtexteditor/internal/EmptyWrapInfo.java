// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.ITabPolicy;


/**
 * Empty Line Wrap Info.
 */
public class EmptyWrapInfo
	extends WrapInfo
{
	public EmptyWrapInfo()
	{
	}
	
	
	public int getWrapRowCount()
	{
		return 1;
	}


	public int getGlyphIndexForRow(int row)
	{
		return 0;
	}


	public boolean isCompatible(ITabPolicy tabPolicy, int width, boolean wrapLines)
	{
		return true;
	}


	public int findRowForGlyphIndex(int glyphIndex)
	{
		return 0;
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
		return 0;
	}


	public int getCharIndexForColumn(int wrapRow, int column)
	{
		return 0;
	}
}