// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.TextPos;


/**
 * Screen Buffer.
 */
public class ScreenBuffer
{
	private ScreenCell[] cells;
	private int height;
	private int width;
	
	
	public ScreenBuffer()
	{
	}
	
	
	public void setSize(int w, int h)
	{
		if((w != width) || (h != height))
		{
			int sz = w * h;
			
			if((cells == null) || (cells.length < sz))
			{
				cells = new ScreenCell[sz];
				for(int i=0; i<sz; i++)
				{
					cells[i] = new ScreenCell();
				}
			}
			
			width = w;
			height = h;
		}
	}
	
	
	public int getHeight()
	{
		return height;
	}
	
	
	public int getWidth()
	{
		return width;
	}
	

	public ScreenCell getCell(int x, int y)
	{
		int ix = y * width + x;
		return cells[ix];
	}
	
	
	public ScreenCell getCell(int index)
	{
		return cells[index];
	}


	public TextPos getInsertPosition(int x, int y)
	{
		ScreenCell c = getCell(x, y);
		int line = c.getLine();
		int off = c.getOffset();
		return new TextPos(line, off);
	}
}
