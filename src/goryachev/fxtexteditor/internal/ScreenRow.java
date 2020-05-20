// Copyright © 2019-2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.util.SB;
import goryachev.fxtexteditor.CellStyle;
import goryachev.fxtexteditor.ITextLine;


/**
 * Screen Row translates sequence of glyphs obtained from the model (ITextLine) 
 * to the cells on screen.
 */
public class ScreenRow
{
	private FlowLine fline = FlowLine.BLANK;
	private WrapInfo wrap;
	private int lineNumber;
	private int wrapRow;
	private int startGlyphIndex;
	
	
	public ScreenRow()
	{
	}
	
	
	public FlowLine getFlowLine()
	{
		return fline;
	}
	
	
	public boolean isBOL()
	{
		return wrapRow == 0;
	}
	

	public ITextLine getTextLine()
	{
		return fline.getTextLine();
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
	
	
	/** returns the text cell at the specified column */
	public TextCell getCell(int column)
	{
		return wrap.getCell(wrapRow, column);
	}
	
	
	/** 
	 * returns the text to be rendered in one cell
	 */
	@Deprecated // TODO use TextCell
	public String getCellText(int col)
	{
		int gix = wrap.getGlyphIndex(wrapRow, col);
		if(gix < 0)
		{
			return null;
		}
		
		return fline.glyphInfo().getGlyphText(gix);
	}
	

	@Deprecated // TODO use TextCell
	public CellStyle getCellStyles(int col)
	{
		int gix = wrap.getGlyphIndex(wrapRow, col);
		if(gix < 0)
		{
			if(GlyphIndex.isTab(gix))
			{
				gix = GlyphIndex.fixGlypIndex(gix);
			}
			else
			{
				return null;
			}
		}
		
		int charIndex = fline.glyphInfo().getCharIndex(gix);
		return fline.getCellStyle(charIndex);
	}
	
	
	public int getCellCount()
	{
		return wrap.getGlyphCountAtRow(wrapRow);
	}
	
	
	public int getWrapRow()
	{
		return wrapRow;
	}
}
