// Copyright © 2019-2020 Andy Goryachev <andy@goryachev.com>
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
	protected final int lineCount;


	public DemoTextEditorModel(String text, int lineCount)
	{
		lines = CKit.split(text, '\n');
		this.lineCount = lineCount;
	}
	
	
	public DemoTextEditorModel(String text)
	{
		lines = CKit.split(text, '\n');
		this.lineCount = lines.length;
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
		return lineCount;
	}

	
	protected String plainText(int line)
	{
		if(line < getLineCount())
		{
			int ix = line % lines.length;
			String s = lines[ix];
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
