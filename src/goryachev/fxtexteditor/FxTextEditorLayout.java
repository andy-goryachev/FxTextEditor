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
	protected final ITextLine[] cells;
	protected final int[] lines;
	protected final int[] offsets;
	protected final int maxRows;
	protected final int maxColumns;
	
	
	public FxTextEditorLayout(ITextLine[] cells, int[] lines, int[] offsets, int maxRows, int maxColumns)
	{
		this.cells = cells;
		this.lines = lines;
		this.offsets = offsets;
		this.maxRows = maxRows;
		this.maxColumns = maxColumns;
	}
	

	/** 
	 * returns a non-null insert position in model coordinates (line,offset) corresponding to the specified cell location.
	 * the actual location may be different due to end of line, end of file, or a tab.
	 */
	public TextPos getInsertPosition(int x, int y)
	{
		// TODO horizontal scrolling?
		int line;
		int off;
		boolean validCaretLine = true;
		boolean validCaretOffset = true;
		
		ITextLine cell = cells[y];
		if(cell == null)
		{
			line = lastLine();
			off = 0;
			validCaretLine = false; // FIX
		}
		else
		{
			line = lines[y];
			off = offsets[y] + x; // TODO tabs
			if(off > cell.getCellCount())
			{
				off = cell.getCellCount();
				validCaretOffset = false;
			}
		}
		return new TextPos(line, off, validCaretLine, validCaretOffset);
	}
	
	
	protected int lastLine()
	{
		for(int i=cells.length-1; i>=0; i--)
		{
			if(cells[i] != null)
			{
				return lines[i] + 1; 
			}
		}
		return -1;
	}
	
	
	public int getVisibleLineCount()
	{
		return maxRows;
	}
	
	
	public TCell getCell(int x, int y)
	{
		if(y < cells.length)
		{
			ITextLine tc = cells[y];
			if(tc != null)
			{
				int off = offsets[y];
				int ix = x + off; // TODO hor scroll, tabs
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
