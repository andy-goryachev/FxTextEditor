// Copyright © 2020-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.util.CList;
import goryachev.common.util.text.IBreakIterator;


/**
 * Simple List-based FxTextEditorModel.
 */
public class SimpleListTextEditorModel
	extends FxTextEditorModel
{
	private final CList<ITextLine> lines = new CList();
	
	
	public SimpleListTextEditorModel()
	{
	}
	
	
	/** 
	 * Adds a text line to the model.
	 * TODO send events.  For now, you must add() before the first use of this model.
	 */ 
	public void add(ITextLine line)
	{
		lines.add(line);
		
		// TODO send update events
	}
	
	
	public void add(String text)
	{
		int line = lines.size();
		lines.add(new PlainTextLine(line, text));
	}


	public int getLineCount()
	{
		return lines.size();
	}


	public ITextLine getTextLine(int line)
	{
		return lines.get(line);
	}


	public Edit edit(Edit ed) throws Exception
	{
		throw new Exception();
	}


	public IBreakIterator getBreakIterator()
	{
		return null;
	}
}
