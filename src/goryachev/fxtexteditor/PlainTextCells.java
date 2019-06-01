// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;


/**
 * Plain (no colors, no font attributes) ITextCells.
 */
public class PlainTextCells
	implements ITextLine
{
	protected final String text;
	
	
	public PlainTextCells(String text)
	{
		this.text = text;
	}
	
	
	public int getCellCount()
	{
		return text.length();
	}
	
	
	public TCell getCell(int ix)
	{
		if(ix < text.length())
		{
			String s = String.valueOf(text.charAt(ix));
			return new TCell(s);
		}
		return null;
	}
}
