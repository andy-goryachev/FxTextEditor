// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.ITabPolicy;
import goryachev.fxtexteditor.ITextLine;
import goryachev.fxtexteditor.TextPos;


/**
 * Screen Buffer.
 */
public class ScreenBuffer
{
	public static final int EOL = -1;
	public static final int EOF = -2;
	private int height;
	private int width;
	private ScreenRow[] rows;
	private ITabPolicy tabPolicy;
	
	
	public ScreenBuffer()
	{
	}
	
	
	public void setTabPolicy(ITabPolicy p)
	{
		tabPolicy = p;
	}
	
	
	public void setSize(int w, int h)
	{
		if(h > height)
		{
			rows = new ScreenRow[h];
			for(int i=0; i<h; i++)
			{
				rows[i] = new ScreenRow();
			}
		}
		
		width = w;
		height = h;
	}
	
	
	public int getHeight()
	{
		return height;
	}
	
	
	public int getWidth()
	{
		return width;
	}
	
	
	public void addRow(int ix, ITextLine textLine, int off)
	{
		rows[ix].setStart(textLine, off, tabPolicy, width);
	}
	

	public int getOffset(int x, int y)
	{
		if(x < 0)
		{
			x = 0;
		}
		if(y < 0)
		{
			y = 0;
		}
		
		ScreenRow r = getScreenRow(y);
		if(r == null)
		{
			return EOF;
		}
		
		return r.getCellOffset(x);
	}
	
	
	public ScreenRow getScreenRow(int y)
	{
		if(y < height)
		{
			return rows[y];
		}
		return null;
	}
	

	/** 
	 * returns an insert position for the given screen coordinates.
	 * might contain negative values for line or offset TODO explain. 
	 */
	public TextPos getInsertPosition(int x, int y)
	{
		int line;
		int off;
		
		ScreenRow row = getScreenRow(y);
		if(row == null)
		{
			// TODO scan back to find the end of last text line
			// FIX
			line = 0;
			off = 0;
		}
		else
		{
			line = row.getModelIndex();
			off = getOffset(x, y);
			if(off < 0)
			{
				if(off == EOF)
				{
					
				}
				else if(off == EOL)
				{
					
				}
				// TODO tab, eol, eof
				// FIX
				line = 0;
				off = 0;
			}
		}
		
		return new TextPos(line, off);
	}
}
