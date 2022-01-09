// Copyright © 2019-2022 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.util.text.IBreakIterator;


/**
 * In-memory Plain Text FxTextEditorModel.
 */
public class InMemoryPlainTextEditorModel
	extends FxTextEditorModel
{
	protected final String[] lines;


	public InMemoryPlainTextEditorModel(String[] lines)
	{
		this.lines = lines;
	}
	
	
	public IBreakIterator getBreakIterator()
	{
		return null;
	}
	
	
	public Edit edit(Edit ed) throws Exception
	{
		throw new Exception("not supported");
	}


	public int getLineCount()
	{
		return lines.length;
	}
	

	public ITextLine getTextLine(int line)
	{
		if(line < getLineCount())
		{
			String text = lines[line];
			if(text != null)
			{
				if(text.endsWith("\r"))
				{
					text = text.substring(0, text.length() - 1);
				}
				return new PlainTextLine(line, text);
			}
		}
		return null;
	}
}
