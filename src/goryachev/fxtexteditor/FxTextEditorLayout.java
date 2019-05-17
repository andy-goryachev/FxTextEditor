// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;

/**
 * FxTextEditor Layout.
 */
public class FxTextEditorLayout
{
	protected final VTextFlow vflow;
	protected final TextCells[] cells;
	protected final int[] offsets;
	
	
	public FxTextEditorLayout(VTextFlow v)
	{
		this.vflow = v;
		
		int sz = v.getVisibleRowCount() + 1;
		offsets = new int[sz];
		cells = new TextCells[sz];
		
		FxTextEditor ed = v.getEditor();
		FxTextEditorModel m = ed.getModel();
		
		int ix = v.getTopLine();
		int colCount = v.getVisibleColumnCount();
		int y = 0;
		
		for(;;)
		{
			TextCells tc = m.getTextCells(ix);
			if(tc == null)
			{
				break;
			}
			
			int len = tc.size();
			int off = v.getTopOffset();
			while(off < len)
			{
				cells[y] = tc;
				offsets[y] = off;
				
				off += colCount;
				y++;
				
				if(y >= sz)
				{
					break;
				}
			}
			
			ix++;
		}
	}
	
	
	public TCell getCell(int x, int y)
	{
		TextCells tc = cells[y];
		if(tc != null)
		{
			int off = offsets[y];
			int ix = x + off;
			return tc.getCell(ix);
		}
		return null;
	}
}
