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


	public Edit edit(Edit edit) throws Exception
	{
		SelectionSegment seg = edit.getSelection();
		int line0 = seg.getMinLine();
		
		if(seg.isSameLine())
		{
			CharSequence add = edit.getReplaceText();
			int p0 = seg.getMinCharIndex();
			int p1 = seg.getMaxCharIndex();
			
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
			
			// FIX problem: markers are a part of the editor, not the model!  use TextPos?
//			TextPos m0 = new TextPos();
//			TextPos m1 = new TextPos();
//			SelectionSegment newSeg = new SelectionSegment(m0, m1, seg.isCaretAtMin());
//			return new Edit(newSeg, old);
		}
		else
		{
			throw new Error("todo");
		}
	}
}
