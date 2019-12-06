// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import goryachev.common.util.CKit;
import goryachev.fxtexteditor.Edit;
import goryachev.fxtexteditor.FxTextEditorModel;
import goryachev.fxtexteditor.ITextLine;
import goryachev.fxtexteditor.LoadInfo;


/**
 * Demo FxTextEditorModel.
 */
public class DemoTextEditorModel
	extends FxTextEditorModel
{
	protected final String[] lines;
	protected final int repeats;


	public DemoTextEditorModel(String text, int repeats)
	{
		lines = CKit.split(text, '\n');
		this.repeats = repeats;
	}
	
	
	public LoadInfo getLoadInfo()
	{
		return null;
	}
	
	
	public Edit edit(Edit ed) throws Exception
	{
		throw new Exception("not supported");
	}


	public int getLineCount()
	{
		return lines.length * repeats;
	}

	
	protected String plainText(int line)
	{
		if(line < getLineCount())
		{
			String s = lines[line % lines.length];
			if(s.length() > 0)
			{
				switch(s.charAt(s.length() - 1))
				{
				case '\r':
				case '\n':
					return s.substring(0, s.length() - 1);
				}
			}
			return s;
		}
		return null;
	}
	
	
	public ITextLine getTextLine(int line)
	{
		String text = plainText(line);
		if(text != null)
		{
			return new DemoTextLine(text, line);
		}
		return null;
	}
}
