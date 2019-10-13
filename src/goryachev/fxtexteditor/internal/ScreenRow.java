// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.ITextLine;
import goryachev.fxtexteditor.ScreenCell;


/**
 * Screen Row translates chain of glyphs obtain from the model (ITextLine) 
 * to the cells on screen.
 */
public class ScreenRow
{
	private TextCells cells;
	private int startOffset;
	
	
	public ScreenRow()
	{
	}
	
	
	public void setStart(TextCells t, int off)
	{
		cells = t;
		startOffset = off;
		// TODO populate?
	}


	public TextCells getTextLine()
	{
		return cells;
	}


	public int getStartOffset()
	{
		return startOffset;
	}


	public boolean isEOL(int x)
	{
		// TODO
		return false;
	}


	public int getTabSpan(int x)
	{
		// TODO
		return 0;
	}


	// TODO maybe include text as well?
	public void updateStyle(ScreenCell style, int x)
	{
		// TODO need screen buffer to get offset
	}


	public int getModelIndex()
	{
		return cells.getModelIndex();
	}
}
