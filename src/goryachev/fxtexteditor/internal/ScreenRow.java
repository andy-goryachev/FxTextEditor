// Copyright © 2019-2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.util.CKit;
import goryachev.common.util.SB;
import goryachev.fxtexteditor.CellStyle;
import goryachev.fxtexteditor.GlyphType;
import goryachev.fxtexteditor.ITextLine;


/**
 * Screen Row translates sequence of glyphs obtained from the model (ITextLine) 
 * to the cells on screen.
 */
public class ScreenRow
{
	private static final int CARET = 0x0000_0001;
	private static final int SELECTED = 0x0000_0002;
	
	private FlowLine fline = FlowLine.BLANK;
	private WrapInfo wrap;
	private int lineNumber;
	private int wrapRow;
	private int startGlyphIndex;
	
	// TODO is this needed?
	private boolean eof;
	private boolean bol;
	
	@Deprecated // FIX remove
	private int appendIndex;
	
	
	public ScreenRow()
	{
	}
	
	
	public FlowLine getFlowLine()
	{
		return fline;
	}
	
	
	public boolean isBOL()
	{
		return bol;
	}
	
	
	/**
	 * returns a glyph index for the given x screen coordinate.
	 * if the x coordinate falls inside a tab, a special GlyphIndex
	 * provides information about the tab end position.
	 * or GlyphIndex.EOL if past the end of given line,
	 * of GlyphIndex.BOL if before the beginning of line,
	 * or GlyphIndex.EOF if past the end of file.
	 */
//	@Deprecated // FIX
//	public GlyphIndex getGlyphIndex(int x)
//	{
//		if(complex)
//		{
//			if(x < 0)
//			{
//				return GlyphIndex.BOL;
//			}
//			else if(x < cellCount)
//			{
//				return glyphOffsets[x];
//			}
//			else
//			{
//				return GlyphIndex.atEOL(x == cellCount);
//			}
//		}
//		else
//		{
//			if(eof)
//			{
//				return GlyphIndex.EOF;
//			}
//			
//			int ct = getGlyphCount();
//
//			if(startGlyphIndex_OLD == null)
//			{
//				return GlyphIndex.BOL;
//			}
//			
//			int ix = startGlyphIndex_OLD.intValue() + x;
//			if(ix < ct)
//			{
//				return GlyphIndex.of(ix);
//			}
//			else
//			{
//				return GlyphIndex.atEOL(ix == ct);
//			}
//		}
//	}
	
	
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
//	@Deprecated // FIX
//	public int getNearestInsertPosition(int x)
//	{
//		if(complex)
//		{
//			// look for the nearest insertion point before or after the specified screen coordinate.
//			// this should not take more than the tabsize iterations, 
//			// but let's introduce the upper limit anyway.
//			for(int i=1; i<10000; i++)
//			{
//				// backward
//				GlyphIndex gix = getGlyphIndex(x - i);
//				if(gix.isRegular())
//				{
//					return getCharIndex(gix) + 1;
//				}
//				else if(gix.isInsideTab())
//				{
//					int ix = gix.getLeadingCharIndex();
//					if(ix >= 0)
//					{
//						return ix; 
//					}
//				}
//				else if(gix.isBOL())
//				{
//					return getCharIndex(GlyphIndex.ZERO);
//				}
//				
//				// forward
//				gix = getGlyphIndex(x + i);
//				if(gix.isRegular())
//				{
//					return getCharIndex(gix);
//				}
//				else if(gix.isInsideTab())
//				{
//					int ix = gix.getLeadingCharIndex();
//					if(ix >= 0)
//					{
//						return ix; 
//					}
//				}
//			}
//			throw new Error();
//		}
//		else
//		{
//			// TODO
//			return x;
//		}
//	}


	public ITextLine getTextLine()
	{
		return fline.getTextLine();
	}
	
	
	public int getTextLength()
	{
		return fline.getTextLength();
	}
	
	
	/** returns model.getRowCount() index if this row immediately follows the last row with non-null textLine, or -1 */
	public int getAppendModelIndex()
	{
		return appendIndex;
	}


	public String dump()
	{
		SB sb = new SB();
		
		sb.append("(");
		sb.append(lineNumber);
		sb.append(",");
		sb.append(wrapRow);
		sb.append(") ");
		
		return sb.toString();
	}
	
	
	// TODO new interface


	public void init(FlowLine fline, WrapInfo wrap, int lineNumber, int wrapRow, int startGlyphIndex)
	{
		this.fline = fline;
		this.wrap = wrap;
		this.lineNumber = lineNumber;
		this.wrapRow = wrapRow;
		this.startGlyphIndex = startGlyphIndex;
	}
	

	/** returns line number (starts at 0) or -1 if line number should not be displayed */
	public int getLineNumber()
	{
		return lineNumber;
	}
	
	
	/** returns the type of a glyph at the specified column */
	public GlyphType getGlyphTypeAtColumn(int column)
	{
		return wrap.getGlyphType(wrapRow, column);
	}
	
	
	/** 
	 * returns tab span (distance to the next glyph), or throws an Error if it is not a tab.
	 * must always be preceded by a call to getGlyphTypeAtColumn() and a chech against GlyphType.TAB.
	 */
	public int getTabSpan(int column)
	{
		return wrap.getTabSpan(wrapRow, column);
	}
	
	
	/** 
	 * returns the text to be rendered in one cell
	 */
	public String getCellText(int col)
	{
		int gix = wrap.getGlyphIndex(wrapRow, col);
		if(gix < 0)
		{
			return null;
		}
		
		return getGlyphText(gix);
	}
	

	public CellStyle getCellStyles(int col)
	{
		int gix = wrap.getGlyphIndex(wrapRow, col);
		if(gix < 0)
		{
			if(GlyphIndex.isTab(gix))
			{
				gix = -gix;
			}
			else
			{
				return null;
			}
		}
		
		int charIndex = getCharIndex(gix);
		return fline.getCellStyle(charIndex);
	}
	

	/** returns the offest into plain text string for the given glyph index */
	protected int getCharIndex(int glyphIndex)
	{
		return fline.glyphInfo().getCharIndex(glyphIndex);
	}
	
	
	public int getCharIndexForColumn(int column)
	{
		return wrap.getCharIndexForColumn(wrapRow, column);
	}
	
	
	protected String getGlyphText(int glyphIndex)
	{
		return fline.glyphInfo().getGlyphText(glyphIndex);
	}
	
	
	public int getCellCount()
	{
		return wrap.getGlyphCountAtRow(wrapRow);
	}
	
	
	public WrapInfo getWrapInfo()
	{
		return wrap;
	}
	
	
	public int getWrapRow()
	{
		return wrapRow;
	}
	
	
	public int getWrapRowCount()
	{
		return wrap.getWrapRowCount();
	}
}
