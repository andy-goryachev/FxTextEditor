// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;


/**
 * FxTextEditor Layout.
 */
public class FxTextEditorLayout
{
	protected final TextCells[] cells;
	protected final int[] offsets;
	
	
	public FxTextEditorLayout(TextCells[] cells, int[] offsets)
	{
		this.cells = cells;
		this.offsets = offsets;
	}
	
	
	public TCell getCell(int x, int y)
	{
		if(y < cells.length)
		{
			TextCells tc = cells[y];
			if(tc != null)
			{
				int off = offsets[y];
				int ix = x + off;
				return tc.getCell(ix);
			}
		}
		return null;
	}
}
