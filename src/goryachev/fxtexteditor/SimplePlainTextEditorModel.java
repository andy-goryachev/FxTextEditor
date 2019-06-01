// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.util.CKit;


/**
 * Simple Plain Text FxTextEditorModel.
 */
public class SimplePlainTextEditorModel
	extends FxTextEditorModel
{
	protected final String[] lines;


	public SimplePlainTextEditorModel(String text)
	{
		lines = CKit.split(text, '\n');
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
		return lines.length;
	}


	public int getCellCount(int line)
	{
		String s = getPlainText(line);
		return s == null ? 0 : s.length();
	}
	

	public String getPlainText(int line)
	{
		if(line < getLineCount())
		{
			return lines[line];
		}
		return null;
	}


	public ITextLine getTextCells(int line)
	{
		if(line < getLineCount())
		{
			String text = getPlainText(line);
			return new PlainTextCells(text);
		}
		return null;
	}
}
