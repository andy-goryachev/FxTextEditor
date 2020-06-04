// Copyright © 2019-2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.util.SB;
import goryachev.fxtexteditor.CellStyle;
import goryachev.fxtexteditor.ITextLine;
import javafx.scene.paint.Color;


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
	
	
	public Color getLineColor()
	{
		ITextLine t = getTextLine();
		return t == null ? null : t.getLineColor();
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
	
	
	public int getStartGlyphIndex()
	{
		return startGlyphIndex;
	}
	
	
	/** returns the text cell at the specified column */
	public TextCell getCell(int column)
	{
		return wrap.getCell(TextCell.globalInstance(), wrapRow, column);
	}
	
	
	public String getCellText(TextCell cell)
	{
		switch(cell.getGlyphType())
		{
		case EOF:
		case EOL:
		case TAB:
			return null;
		}
		
		int gix = cell.getGlyphIndex();
		return fline.glyphInfo().getGlyphText(gix);
	}
	

	public CellStyle getCellStyles(TextCell cell)
	{
		switch(cell.getGlyphType())
		{
		case EOF:
		case EOL:
			return null;
		}
		
		int gix = cell.getGlyphIndex();
		int charIndex = fline.glyphInfo().getCharIndex(gix);
		return fline.getCellStyle(charIndex);
	}
	
	
	public int getGlyphCount()
	{
		return wrap.getGlyphCountAtRow(wrapRow);
	}
	
	
	public int getWrapRow()
	{
		return wrapRow;
	}
}
