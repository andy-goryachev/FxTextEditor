// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.util.SB;
import goryachev.fxtexteditor.CellStyles;
import goryachev.fxtexteditor.ITextLine;


/**
 * Screen Row translates chain of glyphs obtain from the model (ITextLine) 
 * to the cells on screen.
 */
public class ScreenRow
{
	private ITextLine textLine;
	private int startGlyphIndex;
	private int[] offsets;
	private int size;
	private boolean complex;
	
	
	public ScreenRow()
	{
	}
	
	
	public void setSize(int sz)
	{
		size = sz;
	}
	
	
	public void setComplex(boolean on)
	{
		complex = on;;
	}
	
	
	public void setTextLine(ITextLine t)
	{
		textLine = t;
	}
	
	
	public void setStartGlyphIndex(int ix)
	{
		this.startGlyphIndex = ix;
	}
	
	
	public int[] prepareOffsetsForWidth(int width)
	{
		if((offsets == null) || (offsets.length < width))
		{
			offsets = new int[width];
		}
		return offsets;
	}
	
	
	/**
	 * returns a glyph index for a given x screen coordinate.
	 * or a negative offset to the position after a tab (if inside a tab),
	 * or ScreenBuffer.EOL if past the end of given line,
	 * or ScreenBuffer.EOF if past the end of file
	 */
	public int getGlyphIndex(int x)
	{
		if(complex)
		{
			if(x < size)
			{
				return offsets[x];
			}
			else
			{
				return ScreenBuffer.EOL;
			}
		}
		else
		{
			int ix = startGlyphIndex + x;
			if(ix > getGlyphCount())
			{
				return getGlyphCount();
			}
			return ix;
		}
	}


	public ITextLine getTextLine()
	{
		return textLine;
	}


	public int getStartOffset()
	{
		return startGlyphIndex;
	}


	public void updateStyle(int x, CellStyles style)
	{
		if(textLine != null)
		{
			textLine.updateStyle(x, style);
		}
	}


	public int getModelIndex()
	{
		if(textLine == null)
		{
			return -1;
		}
		return textLine.getModelIndex();
	}


	public String getCellText(int x)
	{
		if(textLine == null)
		{
			return "";
		}
		int ix = getGlyphIndex(x);
		return textLine.getCellText(ix);
	}


	public int getGlyphCount()
	{
		return textLine == null ? 0 : textLine.getGlyphCount();
	}


	public String dump()
	{
		SB sb = new SB();
		
		if(complex)
		{
			sb.append("C");
		}
		else
		{
			sb.append("S");
		}
		
		sb.append("(").append(startGlyphIndex).append(") ");
		
		if(offsets != null)
		{
			int mx = Math.min(size, offsets.length);
			for(int i=0; i<mx; i++)
			{
				if(i > 0)
				{
					sb.append(',');
				}
				sb.append(offsets[i]);
			}
		}
		return sb.toString();
	}
}
