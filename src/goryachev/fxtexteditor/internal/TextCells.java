// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.util.CList;


/**
 * Collection of Text Cells representing one line of text in the model.
 */
public class TextCells
{
	protected final CList<TextCell> cells = new CList();
	
	
	public TextCells()
	{
	}
	
	
	public int getLength()
	{
		return cells.size();
	}
	
	
	public void addCell(int start, int end, String s)
	{
		cells.add(new TextCell(start, end, s));
	}
	
	
	public TextCell getCell(int off)
	{
		if(off < cells.size())
		{
			return cells.get(off);
		}
		return null;
	}
}
