// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;


/**
 * FxTextEditor Layout.
 */
public class FxTextEditorLayout
{
	protected final ITextCells[] cells;
	protected final int[] offsets;
	protected final int maxColumns;
	
	
	public FxTextEditorLayout(ITextCells[] cells, int[] offsets, int maxColumns)
	{
		this.cells = cells;
		this.offsets = offsets;
		this.maxColumns = maxColumns;
	}
	
	
	public TCell getCell(int x, int y)
	{
		if(y < cells.length)
		{
			ITextCells tc = cells[y];
			if(tc != null)
			{
				int off = offsets[y];
				int ix = x + off;
				return tc.getCell(ix);
			}
		}
		return null;
	}


	public int getMaxColumnCount()
	{
		return maxColumns;
	}


	public int getTextPos(int x, int y)
	{
		ITextCells tc;
		
		for(;;)
		{
			tc = cells[y];
			if(tc == null)
			{
				// find
				y--;
				if(y < 0)
				{
					// should not happen
					throw new Error();
				}
			}
			else
			{
				int off = offsets[y];
				int pos = x + off;
				if(pos > tc.getCellCount())
				{
					return tc.getCellCount() - off;
				}
				return pos;
			}
		}
	}
}
