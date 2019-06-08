// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.FxTextEditor;
import goryachev.fxtexteditor.FxTextEditorModel;
import goryachev.fxtexteditor.TextDecor;
import goryachev.fxtexteditor.TextPos;
import goryachev.fxtexteditor.VTextFlow;
import java.util.Locale;
import com.ibm.icu.text.BreakIterator;
import javafx.scene.paint.Color;


/**
 * Screen Buffer.
 */
public class ScreenBuffer
{
	private ScreenCell[] cells;
	private boolean valid;
	private int height;
	private int width;
	private BreakIterator breakIterator;
	protected final TextDecor decor = new TextDecor();
	
	
	public ScreenBuffer()
	{
		// TODO get locale from the model
		breakIterator = BreakIterator.getCharacterInstance(Locale.US);
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


	public ScreenCell getCell(int x, int y)
	{
		int ix = y * width + x;
		return cells[ix];
	}


	public TextPos getInsertPosition(int x, int y)
	{
		ScreenCell c = getCell(x, y);
		int line = c.getLine();
		int off = c.getOffset();
		return new TextPos(line, off);
	}
	
	
	protected TextCells createTextLine(int lineIndex, String text, TextDecor d)
	{
		TextCells cs = new TextCells();
		breakIterator.setText(text);

		int start = breakIterator.first();
		for(int end=breakIterator.next(); end!=BreakIterator.DONE; start=end, end=breakIterator.next())
		{
			String s = text.substring(start,end);
			cs.addCell(start, end, s);
		}
		
		if(d != null)
		{
			// TODO populate styles
		}
		
		return cs;
	}


	public void reflow(VTextFlow vflow)
	{
		int w = vflow.getColumnCount() + 1;
		int h = vflow.getLineCount() + 1;
		int sz = w * h;
		
		if((w != width) || (h != height))
		{
			if((cells == null) || (cells.length < sz))
			{
				cells = new ScreenCell[sz];
				for(int i=0; i<sz; i++)
				{
					cells[i] = new ScreenCell();
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
		Color bg = Color.WHITE; // TODO null;
		Color fg = Color.BLACK; // TODO
		Color textColor = Color.BLACK; // FIX null
		TextCells textLine = null;
		TextCells.LCell cell = null;
		
		for(int ix=0; ix<sz; ix++)
		{
			ScreenCell screenCell = cells[ix];
			
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
				if(textLine == null)
				{
					if(lineIndex >= m.getLineCount())
					{
						eof = true;
					}
					else
					{
						String s = m.getPlainText(lineIndex);
						TextDecor d = m.getTextLine(lineIndex, s, decor);
						textLine = createTextLine(lineIndex, s, d);
					}
				}
				
				if(eof || eol || (textLine == null))
				{
					cell = null;
				}
				else 
				{
					cell = textLine.getCell(off);
					off++;
					
					// TODO tabs
				}
			}
			
			screenCell.setText(cell == null ? null : cell.getText());
			screenCell.setBackgroundColor(bg);
			screenCell.setTextColor(textColor);
			x++;
				
			if(x > width)
			{
				x = 0;
				y++;
				lineIndex++;
				textLine = null;
				
				if(y > height)
				{
					break;
				}
			}
		}
		
		valid = true;
	}
}
