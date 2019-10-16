// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.CellStyles;
import goryachev.fxtexteditor.GlyptType;
import goryachev.fxtexteditor.ITabPolicy;
import goryachev.fxtexteditor.ITextLine;


/**
 * Screen Row translates chain of glyphs obtain from the model (ITextLine) 
 * to the cells on screen.
 */
public class ScreenRow
{
	private ITextLine textLine;
	private int startOffset;
	private int[] offsets;
	private int size;
	private boolean complex;
	
	
	public ScreenRow()
	{
	}
	
	
	public void setStart(ITextLine t, int startCellOffset, ITabPolicy tabPolicy, int width)
	{
		textLine = t;
		startOffset = startCellOffset;
		
		complex = t.hasComplexGlyphs();
		if(!tabPolicy.isSimple())
		{
			complex |= t.hasTabs();
		}
		
		if(complex)
		{
			if((offsets == null) || (offsets.length < width))
			{
				offsets = new int[width];
			}
			
			for(int i=0; i<width; i++)
			{
				int off = startCellOffset + i;
				GlyptType gt = t.getGlyphType(off);
				switch(gt)
				{
				case EOL:
					size = i;
					return;
				case TAB:
					int d = tabPolicy.nextTabStop(off);
					int ct = d - off;
					for( ; ct>0; ct--,i++)
					{
						offsets[i] = ct;
					}
					continue;
				case NORMAL:
					offsets[i] = off;
					size = i;
					break;
				default:
					throw new Error("?" + gt);
				}
			}
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
