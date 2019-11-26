// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.util.SB;
import goryachev.fxtexteditor.CellStyles;
import goryachev.fxtexteditor.ITextLine;


/**
 * Screen Row translates sequence of glyphs obtained from the model (ITextLine) 
 * to the cells on screen.
 */
public class ScreenRow
{
	private ITextLine textLine;
	private int startGlyphIndex;
	private int[] glyphOffsets;
	private int size;
	private boolean complex;
	private int appendIndex;
	
	
	public ScreenRow()
	{
	}
	
	
	public void setCellCount(int sz)
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
	
	
	public int[] prepareGlyphOffsetsForWidth(int width)
	{
		if((glyphOffsets == null) || (glyphOffsets.length < width))
		{
			glyphOffsets = new int[width];
		}
		return glyphOffsets;
	}
	
	
	/**
	 * returns a glyph index for the given x screen coordinate.
	 * or a negative offset to the next tab position (if inside a tab),
	 * or ScreenBuffer.EOL if past the end of given line,
	 * or ScreenBuffer.EOF if past the end of file
	 */
	public int getGlyphIndex(int x)
	{
		if(complex)
		{
			if(x < 0)
			{
				return 0;
			}
			else if(x < size)
			{
				return glyphOffsets[x];
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
				return ScreenBuffer.EOL;
			}
			return ix;
		}
	}
	

	/**
	 * returns a character index (into plain text) for the given glyph index.
	 */
	public int getCharIndex(int glyphIndex)
	{
		if(textLine == null)
		{
			return 0;
		}
		else
		{
			return textLine.getCharIndex(glyphIndex);
		}
	}
	
	
	/** 
	 * returns the nearest insert position inside of a tab.
	 * For example, when the user clicks over the tab space the text "A\tB"
	 * this method might return, depending on where exactly the mouse click hit, 
	 * 
	 * either
	 * (offset=2, leading):   a - - -|b
	 * or
	 * (offset=0, trailing):  a|- - - b
	 */ 
	public NearestPos getNearestInsertPosition(int x)
	{
		if(complex)
		{
			// look for the nearest insertion point before or after the specified screen coordinate.
			// this should not take more than the tabsize iterations, 
			// but let's introduce the upper limit anyway.
			for(int i=0; i<10000; i++)
			{
				int ix = getGlyphIndex(x - i);
				if(ix >= 0)
				{
					return new NearestPos(getCharIndex(ix), false);
				}
				
				ix = getGlyphIndex(x + i);
				if(ix >= 0)
				{
					return new NearestPos(getCharIndex(ix), true);
				}
			}
			throw new Error();
		}
		else
		{
			// not used in this mode
			throw new Error();
		}
	}


	public ITextLine getTextLine()
	{
		return textLine;
	}
	
	
	public int getTextLength()
	{
		return textLine == null ? 0 : textLine.getTextLength();
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
	
	
	public void setAppendModelIndex(int ix)
	{
		appendIndex = ix;
	}
	
	
	/** returns model.getRowCount() index if this row immediately follows the last row with non-null textLine, or -1 */
	public int getAppendModelIndex()
	{
		return appendIndex;
	}


	public String getCellText(int x)
	{
		if(textLine != null)
		{
			int ix = getGlyphIndex(x);
			if(ix >= 0)
			{
				return textLine.getCellText(ix);
			}
		}
		return "";
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
		
		if(glyphOffsets != null)
		{
			int mx = Math.min(size, glyphOffsets.length);
			for(int i=0; i<mx; i++)
			{
				if(i > 0)
				{
					sb.append(',');
				}
				sb.append(glyphOffsets[i]);
			}
		}
		return sb.toString();
	}
}
