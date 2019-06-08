// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.util.CList;


/**
 * Collection of Text Cells representing one line of text in the model.
 */
public class TextCells
{
	protected final CList<LCell> cells = new CList();
	
	
	public TextCells()
	{
	}
	
	
	public void addCell(int start, int end, String s)
	{
		cells.add(new LCell(start, end, s));
	}
	
	
	public LCell getCell(int off)
	{
		if(off < cells.size())
		{
			return cells.get(off);
		}
		return null;
	}
	
	
	//
	
	
	public static class LCell
	{
		public final int start;
		public final int end;
		public final String text;
		
		
		public LCell(int start, int end, String text)
		{
			this.start = start;
			this.end = end;
			this.text = text;
		}


		public String getText()
		{
			return text;
		}
	}
}
