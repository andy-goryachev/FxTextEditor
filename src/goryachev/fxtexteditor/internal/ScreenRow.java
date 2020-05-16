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
	private int lineNumber;
	private boolean eof;
	private boolean bol;
	
	@Deprecated // FIX remove
	private FlowLine fline_OLD = FlowLine.BLANK;
	@Deprecated // FIX remove
	private GlyphIndex startGlyphIndex_OLD;
	@Deprecated // FIX remove
	private GlyphIndex[] glyphOffsets;
	@Deprecated // FIX remove
	private int cellCount;
	@Deprecated // FIX remove
	private boolean complex;
	@Deprecated // FIX remove
	private int appendIndex;
	@Deprecated
	private int lineIndex_OLD;
	
	
	public ScreenRow()
	{
	}
	
	
	public void setCellCount(int sz)
	{
		cellCount = sz;
	}
	
	
	public void setComplex(boolean on)
	{
		complex = on;
	}
	
	
	public void initLine(FlowLine f, int lineIndex, int modelLineCount, boolean bol)
	{
		if(f == null)
		{
			throw new Error();
		}
		
		this.fline_OLD = f;
		this.lineIndex_OLD = (lineIndex <= modelLineCount ? lineIndex : -1);
		this.eof = (lineIndex >= modelLineCount);
		this.bol = bol;
	}
	
	
	public FlowLine getFlowLine()
	{
		return fline_OLD;
	}
	
	
	public boolean isBOL()
	{
		return bol;
	}
	
	
	/** returns the type of a glyph at the specified glyph (cell) index. */
	@Deprecated // TODO remove
	public GlyphType getGlyphType(GlyphIndex glyphIndex)
	{
		return fline_OLD.getGlyphType(glyphIndex);
	}
	
	
	/** returns the type of a glyph at the specified glyph (cell) index. */
	public GlyphType getGlyphType(int glyphIndex)
	{
		return fline_OLD.getGlyphType(glyphIndex);
	}
	
	
	public void setStartGlyphIndex(GlyphIndex gix)
	{
		this.startGlyphIndex_OLD = gix;
	}
	
	
	
	@Deprecated // FIX
	public GlyphIndex[] prepareGlyphOffsetsForWidth(int width)
	{
		if((glyphOffsets == null) || (glyphOffsets.length < width))
		{
			glyphOffsets = new GlyphIndex[CKit.toNeatSize(width)];
		}
		return glyphOffsets;
	}
	
	
	/**
	 * returns a glyph index for the given x screen coordinate.
	 * if the x coordinate falls inside a tab, a special GlyphIndex
	 * provides information about the tab end position.
	 * or GlyphIndex.EOL if past the end of given line,
	 * of GlyphIndex.BOL if before the beginning of line,
	 * or GlyphIndex.EOF if past the end of file.
	 */
	@Deprecated // FIX
	public GlyphIndex getGlyphIndex(int x)
	{
		if(complex)
		{
			if(x < 0)
			{
				return GlyphIndex.BOL;
			}
			else if(x < cellCount)
			{
				return glyphOffsets[x];
			}
			else
			{
				return GlyphIndex.atEOL(x == cellCount);
			}
		}
		else
		{
			if(eof)
			{
				return GlyphIndex.EOF;
			}
			
			int ct = getGlyphCount();

			if(startGlyphIndex_OLD == null)
			{
				return GlyphIndex.BOL;
			}
			
			int ix = startGlyphIndex_OLD.intValue() + x;
			if(ix < ct)
			{
				return GlyphIndex.of(ix);
			}
			else
			{
				return GlyphIndex.atEOL(ix == ct);
			}
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
	@Deprecated // FIX
	public int getNearestInsertPosition(int x)
	{
		if(complex)
		{
			// look for the nearest insertion point before or after the specified screen coordinate.
			// this should not take more than the tabsize iterations, 
			// but let's introduce the upper limit anyway.
			for(int i=1; i<10000; i++)
			{
				// backward
				GlyphIndex gix = getGlyphIndex(x - i);
				if(gix.isRegular())
				{
					return getCharIndex(gix) + 1;
				}
				else if(gix.isInsideTab())
				{
					int ix = gix.getLeadingCharIndex();
					if(ix >= 0)
					{
						return ix; 
					}
				}
				else if(gix.isBOL())
				{
					return getCharIndex(GlyphIndex.ZERO);
				}
				
				// forward
				gix = getGlyphIndex(x + i);
				if(gix.isRegular())
				{
					return getCharIndex(gix);
				}
				else if(gix.isInsideTab())
				{
					int ix = gix.getLeadingCharIndex();
					if(ix >= 0)
					{
						return ix; 
					}
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
		return fline_OLD.getTextLine();
	}
	
	
	public int getTextLength()
	{
		return fline_OLD.getTextLength();
	}
	
	
	public GlyphIndex getStartGlyphIndex()
	{
		return startGlyphIndex_OLD;
	}


	public CellStyle getCellStyles(int cellIndex)
	{
		GlyphIndex glyphIndex = getGlyphIndex(cellIndex);
		int off = glyphIndex.intValue();
		if(off >= 0)
		{
			return fline_OLD.getCellStyle(off);
		}
		return null;
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
		
		sb.append("(");
		sb.append(lineIndex_OLD);
		sb.append(",");
		sb.append(startGlyphIndex_OLD);
		sb.append(") ");
		
//		if(glyphOffsets != null)
//		{
//			int mx = Math.min(cellCount, glyphOffsets.length);
//			for(int i=0; i<mx; i++)
//			{
//				if(i > 0)
//				{
//					sb.append(',');
//				}
//				sb.append(glyphOffsets[i]);
//			}
//		}
		
		return sb.toString();
	}
	
	
	/** 
	 * returns the number of glyphs in the text line.  
	 * one glyph is rendered in one fixed width cell (even full width CJK)
	 * A tab is one glyph.
	 */
	public int getGlyphCount()
	{
		return fline_OLD.glyphInfo().getGlyphCount();
	}
	
	
	/** returns the offest into plain text string for the given glyph index */
	protected int getCharIndex(GlyphIndex glyphIndex)
	{
		return fline_OLD.glyphInfo().getCharIndex(glyphIndex); //startGlyphIndex + glyphIndex);
	}
	
	
	/** 
	 * returns the text to be rendered in one cell
	 */
	public String getCellText(int cellIndex)
	{
		GlyphIndex glyphIndex = getGlyphIndex(cellIndex);
		return getGlyphText(glyphIndex);
	}
	
	
	protected String getGlyphText(GlyphIndex glyphIndex)
	{
		return fline_OLD.glyphInfo().getGlyphText(glyphIndex);
	}

	
	// TODO new interface


	public void init(FlowLine fline, int lineNumber, int row, int startGlyphIndex)
	{
		this.fline = fline;
		this.lineNumber = lineNumber;
	}
	

	/** returns line number (starts at 0) or -1 if line number should not be displayed */
	public int getLineNumber()
	{
		return lineNumber;
	}
}
