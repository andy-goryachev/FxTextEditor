// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.util.Dump;
import goryachev.common.util.SB;
import goryachev.fxtexteditor.ITabPolicy;
import goryachev.fxtexteditor.ITextLine;
import goryachev.fxtexteditor.TextPos;


/**
 * Screen Buffer.
 */
public class ScreenBuffer
{
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
	
	
	public ScreenRow getScreenRow(int y)
	{
		if(y < height)
		{
			return rows[y];
		}
		return null;
	}
	
	
	/** 
	 * returns the maximum number of horizontal screen cells required to display the 
	 * visible text in the screen buffer.
	 * this method makes sense only in non-wrapping mode
	 */
	public int getMaxCellCount(ITabPolicy tabPolicy)
	{
		int w = 0;
		for(int i=0; i<height; i++)
		{
			int len = NonWrappingReflowHelper.computeCellCount(rows[i].getFlowLine(), tabPolicy);
			if(len > w)
			{
				w = len;
			}
		}
		
		if(w > 0)
		{
			w++;
		}
		
		return w;
	}
	

	/** 
	 * returns an insert position for the given screen coordinates,
	 * or null if beyond the end of file.
	 * 
	 * TODO might contain negative values for line or offset TODO explain. 
	 */
	public TextPos getInsertPosition(int x, int y)
	{
		int line;
		int charIndex;
		boolean synthetic = false;
		
		ScreenRow row = getScreenRow(y);
		if(row == null)
		{
			return null;
		}
		else
		{
			line = row.getModelIndex();
			if(line < 0)
			{
				return null;
			}
			else
			{
				GlyphIndex glyphIndex = row.getGlyphIndex(x);
				if(glyphIndex.isRegular())
				{
					charIndex = row.getCharIndex(glyphIndex);
				}
				else
				{
					synthetic = true;
				
					if(glyphIndex.isEOF())
					{
						// can't happen
						throw new Error();
					}
					else if(glyphIndex.isEOL())
					{
						// at or after end of line
						charIndex = row.getTextLength();
					}
					else
					{
						// inside a tab
						charIndex = row.getNearestInsertPosition(x);
					}
				}
				
				// if eof, eol
				
				// FIX incorrect in case of a tab
//				charIndex = row.getCharIndex(glyphIndex);
//				if(charIndex < 0)
//				{
//					synthetic = true;
//					
//					if(charIndex == EOF)
//					{
//						// can't happen
//						throw new Error();
//					}
//					else if(charIndex == EOL)
//					{
//						charIndex = row.getTextLength() + 1;
//					}
//					else if(charIndex < 0)
//					{
//						charIndex = row.getNearestInsertPosition(x);
//					}
//				}
			}
		}
		
		return new TextPos(line, charIndex, synthetic);
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


	public void reset()
	{
		height = 0;
		width = 0;
	}
}
