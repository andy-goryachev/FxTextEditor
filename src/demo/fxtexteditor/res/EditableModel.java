// Copyright © 2020-2023 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor.res;
import goryachev.common.util.CList;
import goryachev.common.util.text.IBreakIterator;
import goryachev.fxtexteditor.Edit;
import goryachev.fxtexteditor.FxTextEditorModel;
import goryachev.fxtexteditor.ITextLine;
import goryachev.fxtexteditor.Marker;
import goryachev.fxtexteditor.PlainTextLine;
import goryachev.fxtexteditor.SelectionSegment;
import goryachev.fxtexteditor.TextPos;


/**
 * Editable Plain Text Model.
 */
public class EditableModel
	extends FxTextEditorModel
{
	protected final CList<String> lines = new CList();
	
	
	public EditableModel()
	{
		setEditable(true);
	}


	public int getLineCount()
	{
		return lines.size();
	}


	public ITextLine getTextLine(int line)
	{
		String text = lines.get(line);
		return new PlainTextLine(line, text);
	}
	
	
	public IBreakIterator getBreakIterator()
	{
		return null;
	}
	
	
	protected void setText(int ix, String text)
	{
		if(ix < lines.size())
		{
			lines.set(ix, text);
		}
		else if(ix == lines.size())
		{
			lines.add(text);
		}
		else
		{
			throw new Error("line=" + ix + " lineCount=" + getLineCount());
		}
	}
	
	
	// new version
	public Edit edit(Edit edit) throws Exception
	{
		int line0 = edit.getMinLine();
		int pos0 = edit.getMinCharIndex();
		String text0 = getPlainText(line0);
		if(text0 == null)
		{
			text0 = "";
		}
		
		String head = text0.substring(0, pos0);
		
		int line2 = edit.getMaxLine();
		int pos2 = edit.getMaxCharIndex();
		
		String tail;
		if(line0 == line2)
		{
			tail = text0.substring(pos2); 
		}
		else
		{
			String text2 = getPlainText(line2);
			if(text2 == null)
			{
				text2 = "";
			}
			
			tail = text2.substring(pos2);
		}
		
		if(edit.isText())
		{
			String added = edit.getText();
			String s = head + added + tail;
			setText(line0, s);
			
			int mx = Math.min(line2, getLineCount()-1);
			for(int i=mx; i>line0; i--)
			{
				lines.remove(i);
			}
			
			fireTextAltered(line0, pos0, line2, pos2, added.length(), line0-line2, 0);
			// TODO reverse Edit
			return null;
		}
		else
		{
			String[] added = edit.getTextLines();
			int last = added.length - 1;
			int ix = line0;
			
			for(int i=0; i<=last; i++)
			{
				String text;
				if(i == 0)
				{
					text = head + added[i];
				}
				else if(i == last)
				{
					text = added[i] + tail;
				}
				else
				{
					text = added[i];
				}
				
				if(ix <= line2)
				{
					setText(ix, text);
				}
				else
				{
					lines.add(ix, text);
				}
				
				ix++;
			}
			
			int mx = ix;
			for(int i=line2; i>mx; i--)
			{
				lines.remove(i);
			}
			
			fireTextAltered(line0, pos0, line2, pos2, added[0].length(), line0-line2+added.length-1, added[last].length());
			// TODO reverse Edit
			return null;
		}
	}


	// FIX remove
	@Deprecated
	public Edit edit_old(Edit edit) throws Exception
	{
		int line0 = edit.getMinLine();
		
		// TODO rework to do in one pass
		if(edit.isOnSameLine())
		{
			if(edit.isText())
			{
				String add = edit.getText();
				int p0 = edit.getMinCharIndex();
				int p1 = edit.getMaxCharIndex();
				
				String old = getPlainText(line0);
				if(old == null)
				{
					old = "";
				}
				
				String text;
				String cut;
	
				if(p0 == p1)
				{
					// simple insert
					if(p0 < old.length())
					{
						text = old.substring(0, p0) + add + old.substring(p0);
					}
					else
					{
						text = old + add;
					}
					cut = "";
				}
				else
				{
					if(p1 < old.length())
					{
						text = old.substring(0, p0) + add + old.substring(p1);
					}
					else
					{
						text = old.substring(0, p0) + add;
					}
					cut = old.substring(p0, p1);
				}
	
				if(line0 < getLineCount())
				{
					lines.set(line0, text);
				}
				else if(line0 == getLineCount())
				{
					lines.add(text);
				}
				else
				{
					throw new Error("line=" + line0 + " lineCount=" + getLineCount());
				}
				
				fireTextAltered(line0, p0, p1, add.length());
				return null;
			}
			else
			{
				String[] add = edit.getTextLines();
				throw new Error("todo");
			}
		}
		else
		{
			throw new Error("todo");
		}
	}
}
