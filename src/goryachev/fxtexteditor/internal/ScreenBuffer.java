// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.util.Dump;
import goryachev.common.util.SB;
import goryachev.fxtexteditor.ITextLine;
import goryachev.fxtexteditor.TextPos;


/**
 * Screen Buffer.
 */
public class ScreenBuffer
{
	public static final int EOF = Integer.MIN_VALUE;
	public static final int EOL = Integer.MIN_VALUE + 1;
	private int height;
	private int width;
	private ScreenRow[] rows;
	
	
	public ScreenBuffer()
	{
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
	
	
	public ScreenRow getRow(int ix)
	{
		return rows[ix];
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
		
		return r.getGlyphIndex(x);
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
					// can't happen
					throw new Error();
				}
				else if(off == EOL)
				{
					off = row.getCellCount();
				}
				else if(off < 0)
				{
					// inside a tab
					off = -off;
				}
			}
		}
		
		return new TextPos(line, off);
	}
	
	
	public String dump()
	{
		SB sb = new SB();
		for(int i=0; i<getHeight(); i++)
		{
			ScreenRow r = rows[i];
			ITextLine tline = r.getTextLine();
			String text = (tline == null ? "" : Dump.toPrintable(tline.getPlainText()));
			sb.format("%02d %s %s\n", i, r.dump(), text);
		}
		return sb.toString();
	}
}
