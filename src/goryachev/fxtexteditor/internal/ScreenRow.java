// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.util.CKit;
import goryachev.common.util.SB;
import goryachev.fxtexteditor.CellStyles;
import goryachev.fxtexteditor.GlyphType;
import goryachev.fxtexteditor.ITextLine;
import java.util.Arrays;


/**
 * Screen Row translates sequence of glyphs obtained from the model (ITextLine) 
 * to the cells on screen.
 */
public class ScreenRow
{
	private static final int CARET = 0x0000_0001;
	private static final int SELECTED = 0x0000_0002;
	
	private FlowLine fline = FlowLine.BLANK;
	private int startGlyphIndex;
	private int[] glyphOffsets;
	private byte[] flags;
	private int size;
	private boolean complex;
	private int appendIndex;
	private boolean caretLine;
	
	
	public ScreenRow()
	{
	}
	
	
	public void setCellCount(int sz)
	{
		size = sz;
	}
	
	
	public void setComplex(boolean on)
	{
		complex = on;
	}
	
	
	public void initLine(FlowLine f)
	{
		if(f == null)
		{
			throw new Error();
		}
		fline = f;
	}
	
	
	/** returns the type of a glyph at the specified cell index. */
	public GlyphType getGlyphType(int glyphIndex)
	{
		String s = getGlyphText(glyphIndex);
		if(s == null)
		{
			return GlyphType.EOL;
		}
		else if("\t".equals(s))
		{
			return GlyphType.TAB;
		}
		else
		{
			return GlyphType.NORMAL;
		}
	}
	
	
	public void setStartGlyphIndex(int ix)
	{
		this.startGlyphIndex = ix;
	}
	
	
	public int[] prepareGlyphOffsetsForWidth(int width)
	{
		if((glyphOffsets == null) || (glyphOffsets.length < width))
		{
			glyphOffsets = new int[CKit.toNeatSize(width)];
		}
		return glyphOffsets;
	}
	
	
	public byte[] prepareFlagsForWidth(int width)
	{
		if((flags == null) || (flags.length < width))
		{
			flags = new byte[CKit.toNeatSize(width)];
		}
		Arrays.fill(flags, (byte)0);
		return flags;
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
	 * returns the nearest insert position inside of a tab.
	 * For example, when the user clicks over the tab space the text "A\tB"
	 * this method might return, depending on where exactly the mouse click hit, 
	 * 
	 * either
	 * 2:   a - - -|b
	 * or
	 * 1:  a|- - - b
	 */ 
	public int getNearestInsertPosition(int x)
	{
		if(complex)
		{
			// look for the nearest insertion point before or after the specified screen coordinate.
			// this should not take more than the tabsize iterations, 
			// but let's introduce the upper limit anyway.
			for(int i=1; i<10000; i++)
			{
				int glyphIndex = getGlyphIndex(x - i);
				if(glyphIndex >= 0)
				{
					// FIX need to handle BOL, without +1 
					return getCharIndex(glyphIndex) + 1;
				}
				
				glyphIndex = getGlyphIndex(x + i);
				if(glyphIndex >= 0)
				{
					return getCharIndex(glyphIndex);
				}
			}
			throw new Error();
		}
		else
		{
			// TODO
			return x;
		}
	}


	public ITextLine getTextLine()
	{
		return fline.getTextLine();
	}
	
	
	public int getTextLength()
	{
		return fline.getTextLength();
	}
	
	
	public int getStartOffset()
	{
		return startGlyphIndex;
	}


	public void updateStyle(int x, CellStyles style)
	{
		fline.updateStyle(x, style);
	}


	public int getModelIndex()
	{
		return fline.getModelIndex();
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
	
	
	/** 
	 * returns the number of glyphs in the text line.  
	 * one glyph is rendered in one fixed width cell (even full width CJK)
	 * A tab is one glyph.
	 */
	public int getGlyphCount()
	{
		return fline.info().getGlyphCount();
	}
	
	
	/** returns the offest into plain text string for the given glyph index */
	protected int getCharIndex(int glyphIndex)
	{
		return fline.info().getCharIndex(glyphIndex); //startGlyphIndex + glyphIndex);
	}
	
	
	/** 
	 * returns the text to be rendered in one cell
	 */
	public String getCellText(int cellIndex)
	{
		int glyphIndex = getGlyphIndex(cellIndex);
		return getGlyphText(glyphIndex);
	}
	
	
	protected String getGlyphText(int glyphIndex)
	{
		return fline.info().getGlyphText(glyphIndex);
	}


	public void setCaret(int x)
	{
		if((x >= 0) && (x < flags.length))
		{
			flags[x] |= CARET;
		}
	}
	
	
//	public boolean isCaret(int x)
//	{
//		// TODO remove once all wrap modes are implemented
//		if(flags == null)
//		{
//			return false;
//		}
//		return ((flags[x] & CARET) != 0);
//	}


	public void setCaretLine(boolean on)
	{
		caretLine = on;
	}
	
	
//	public boolean isCaretLine()
//	{
//		return caretLine;
//	}
}
