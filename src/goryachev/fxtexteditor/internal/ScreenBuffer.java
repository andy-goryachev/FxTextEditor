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
		if(x < 0)
		{
			x = 0;
		}
		if(y < 0)
		{
			y = 0;
		}
		
		int ix = y * width + x;
		return cells[ix];
	}
	
	
	public ScreenCell getCell(int index)
	{
		return cells[index];
	}
	

	/** returns insert position.  might contain negative values for line or offset. */
	public TextPos getInsertPosition(int x, int y)
	{
		ScreenCell c = getCell(x, y);
		
		int line;
		int off;
		
		if(c.isValidLine())
		{
			line = c.getLine();
		}
		else
		{
			// beyond eof
			line = -1;
		}
		
		if(c.isValidCaret())
		{
			off = c.getOffset();
		}
		else
		{
			// beyond eol
			off = -1;
		}
		
		return new TextPos(line, off);
	}
}
