// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import goryachev.common.util.CKit;
import goryachev.fxtexteditor.Edit;
import goryachev.fxtexteditor.FxTextEditorModel;
import goryachev.fxtexteditor.ITextCells;
import goryachev.fxtexteditor.LoadInfo;
import goryachev.fxtexteditor.PlainTextCells;


/**
 * Simple FxTextEditorModel.
 */
public class PlainTextEditorModel
	extends FxTextEditorModel
{
	protected final String[] lines;


	public PlainTextEditorModel(String text)
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


	public ITextCells getTextCells(int line)
	{
		if(line < getLineCount())
		{
			String text = getPlainText(line);
			return new PlainTextCells(text);
		}
		return null;
	}
}
