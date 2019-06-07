// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.FxTextEditor;
import goryachev.fxtexteditor.FxTextEditorModel;
import goryachev.fxtexteditor.ITextLine;
import goryachev.fxtexteditor.TCell;
import goryachev.fxtexteditor.TextPos;
import goryachev.fxtexteditor.VTextFlow;
import javafx.scene.paint.Color;


/**
 * Screen Buffer.
 */
public class ScreenBuffer
{
	private Cell[] cells;
	private boolean valid;
	private int height;
	private int width;
	
	
	public ScreenBuffer()
	{
	}
	
	
	public void invalidate()
	{
		valid = false;
	}
	
	
	public boolean isValid()
	{
		return valid;
	}
	
	
	public int getHeight()
	{
		return height;
	}
	
	
	public int getWidth()
	{
		return width;
	}


	public Cell getCell(int x, int y)
	{
		int ix = y * width + x;
		return cells[ix];
	}


	public TextPos getInsertPosition(int x, int y)
	{
		Cell c = getCell(x, y);
		int line = c.getLine();
		int off = c.getOffset();
		return new TextPos(line, off);
	}


	public void validate(VTextFlow vflow)
	{
		int w = vflow.getColumnCount() + 1;
		int h = vflow.getLineCount();
		int sz = w * h;
		
		if((w != width) || (h != height))
		{
			if((cells == null) || (cells.length < sz))
			{
				cells = new Cell[sz];
				for(int i=0; i<sz; i++)
				{
					cells[i] = new Cell();
				}
			}
			
			width = w;
			height = h;
		}
		
		FxTextEditor ed = vflow.getEditor();
		boolean wrap = ed.isWrapLines();
		FxTextEditorModel m = ed.getModel();
		int lineIndex = vflow.getTopLine();
		int topOffset = vflow.getTopOffset();
		int y = 0;
		int x = 0;
		int off = topOffset;
		boolean eof = false;
		boolean eol = false;
		boolean caretLine = false;
		Color bg = null;
		Color fg = null;
		Color textColor = null;
		ITextLine textLine = null;
		TCell cell = null;
		
		for(int ix=0; ix<sz; ix++)
		{
			Cell c = cells[ix];
			
			String text;
			if(eof)
			{
				text = null;
			}
			else if(eol)
			{
				text = null;
			}
			else
			{
				if(cell == null)
				{
					if(textLine == null)
					{
						textLine = m.getTextLine(lineIndex);
					}
					
					if(textLine == null)
					{
						eof = true;
					}
					else
					{
						cell = textLine.getCell(off);
					}
				}
			}
			
			c.setBackgroundColor(bg);
//			c.setText(text);
//			
//			int cellWidth;
//			if("\t".equals(text))
//			{
//				int toNextTab = ed.getTabPolicy().distanceToNextTabStop(topOffset + x);
//				cellWidth = toNextTab;
//				text = null;
//				
//				for(int i=0; i<toNextTab; i++)
//				{
//					// TODO move to main loop instead
//				}
//			}
//				
//			// text width
//			c.setWidth(cellWidth);
				
			if(x > width)
			{
				x = 0;
				y++;
				
				if(y > height)
				{
					return;
				}
			}
		}
	}
}
