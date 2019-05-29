// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;


/**
 * FxTextEditor Layout.
 * 
 * TODO rename, move to internal pkg
 * TODO reuse instance
 */
public class FxTextEditorLayout
{
	protected final ITextCells[] cells;
	protected final int[] lines;
	protected final int[] offsets;
	protected final int maxRows;
	protected final int maxColumns;
	
	
	public FxTextEditorLayout(ITextCells[] cells, int[] lines, int[] offsets, int maxRows, int maxColumns)
	{
		this.cells = cells;
		this.lines = lines;
		this.offsets = offsets;
		this.maxRows = maxRows;
		this.maxColumns = maxColumns;
	}
	

	public TextPos getPosition(int x, int y)
	{
		int line;
		int off;
		int caret;
		ITextCells cell = cells[y];
		if(cell == null)
		{
			line = -1;
			off = -2;
			caret = -3;
		}
		else
		{
			line = lines[y];
			off = offsets[y] + x;
			if(off > cell.getCellCount())
			{
				caret = cell.getCellCount();
			}
			else
			{
				caret = off;
			}
		}
		return new TextPos(line, off, caret);
	}
	
	
	public int getVisibleLineCount()
	{
		return maxRows;
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
	
	
	public int getLineOffset(int y)
	{
		if(y < offsets.length)
		{
			return offsets[y];
		}
		return 0;
	}


	public int getMaxColumnCount()
	{
		return maxColumns;
	}
}
