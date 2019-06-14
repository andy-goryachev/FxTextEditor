// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.util.CList;


/**
 * Collection of Text Cells representing one line of text in the model.
 */
public class TextCells
{
	protected final CList<Grapheme> cells = new CList();
	
	
	public TextCells()
	{
	}
	
	
	public int getLength()
	{
		return cells.size();
	}
	
	
	public void addCell(int start, int end, String s)
	{
		cells.add(new Grapheme(start, end, s));
	}
	
	
	public Grapheme getCell(int off)
	{
		if(off < cells.size())
		{
			return cells.get(off);
		}
		return null;
	}
}
