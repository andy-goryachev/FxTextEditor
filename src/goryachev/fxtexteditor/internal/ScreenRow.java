// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.ITextLine;
import goryachev.fxtexteditor.CellStyles;
import goryachev.fxtexteditor.ITabPolicy;


/**
 * Screen Row translates chain of glyphs obtain from the model (ITextLine) 
 * to the cells on screen.
 */
public class ScreenRow
{
	private ITextLine textLine;
	private int startOffset;
	private int[] offsets;
	private boolean complex;
	
	
	public ScreenRow()
	{
	}
	
	
	public void setStart(ITextLine t, int startCellOffset, ITabPolicy tabs, int width)
	{
		textLine = t;
		startOffset = startCellOffset;
		
		// TODO if line has no tabs, no double chars -> set simple
		complex = t.hasComplexGlyphLogic();
		
		if(complex)
		{
			if((offsets == null) || (offsets.length < width))
			{
				offsets = new int[width];
			}
			
			// TODO populate using start offset, tab policy
			for(int i=0; i<width; i++)
			{
				
				
			}
			
			throw new Error(); // TODO
		}
	}
	
	
	/**
	 * returns a cell offset, 
	 * or a negative offset to the position after a tab (if inside a tab),
	 * or ScreenBuffer.EOL if past the end of given line,
	 * or ScreenBuffer.EOF if past the end of file
	 */
	public int getCellOffset(int x)
	{
		if(complex)
		{
			return offsets[x];
		}
		else
		{
			return startOffset + x; 
		}
	}


	public ITextLine getTextLine()
	{
		return textLine;
	}


	public int getStartOffset()
	{
		return startOffset;
	}


	// TODO perhaps merge the two?
//	public boolean isEOL(int x)
//	{
//		// TODO
//		return false;
//	}
//
//
//	public int getTabSpan(int x)
//	{
//		// TODO
//		return 0;
//	}


	public void updateStyle(int x, CellStyles style)
	{
		textLine.updateStyle(x, style);
	}


	public int getModelIndex()
	{
		return textLine.getModelIndex();
	}


	public String getCellText(int x)
	{
		return textLine.getCellText(startOffset + x);
	}


	public int getCellCount()
	{
		return textLine.getCellCount();
	}
}
