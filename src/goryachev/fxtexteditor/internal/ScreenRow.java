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
	private ITextLine textLine;
	private int startOffset;
	
	
	public ScreenRow()
	{
	}
	
	
	public void setStart(ITextLine t, int off)
	{
		textLine = t;
		startOffset = off;
		// TODO populate?
	}


	public ITextLine getTextLine()
	{
		return textLine;
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


	public void updateStyle(int x, ScreenCell style)
	{
		textLine.updateStyle(x, style);
	}


	public int getModelIndex()
	{
		return textLine.getModelIndex();
	}
}
