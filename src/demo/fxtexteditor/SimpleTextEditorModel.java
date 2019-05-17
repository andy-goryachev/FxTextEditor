// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import goryachev.common.util.CKit;
import goryachev.fxtexteditor.Edit;
import goryachev.fxtexteditor.FxTextEditorModel;
import goryachev.fxtexteditor.LoadInfo;
import goryachev.fxtexteditor.TextCells;

/**
 * Simple FxTextEditorModel.
 */
public class SimpleTextEditorModel
	extends FxTextEditorModel
{
	protected final String[] lines;


	public SimpleTextEditorModel(String text)
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


	public String getPlainText(int line)
	{
		return lines[line];
	}


	public TextCells getTextCells(int line)
	{
		if(line < getLineCount())
		{
			TextCells cells = new TextCells();
			cells.addText(getPlainText(line));
			return cells;
		}
		return null;
	}
}
