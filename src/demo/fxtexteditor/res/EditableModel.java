// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
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
			if(line0 < lines.size())
			{
				lines.set(line0, s);
			}
			else if(line0 == lines.size())
			{
				lines.add(s);
			}
			else
			{
				throw new Error("line=" + line0 + " lineCount=" + getLineCount());
			}
			
			int mx = line0;
			for(int i=line2; i>mx; i--)
			{
				lines.remove(i);
			}
			
			fireTextUpdated(line0, pos0, added.length(), line0-line2, line2, pos2, 0);
			// TODO reverse Edit
			return null;
		}
		else
		{
			throw new Error("todo");
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
				
				fireTextUpdated(line0, p0, add.length(), 0, line0, p1, 0);
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
