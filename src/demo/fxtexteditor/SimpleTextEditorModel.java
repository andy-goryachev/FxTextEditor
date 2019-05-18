// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import goryachev.common.util.CKit;
import goryachev.fxtexteditor.Edit;
import goryachev.fxtexteditor.FxTextEditorModel;
import goryachev.fxtexteditor.ITextCells;
import goryachev.fxtexteditor.LoadInfo;
import goryachev.fxtexteditor.SimpleTextCells;


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


	public ITextCells getTextCells(int line)
	{
		if(line < getLineCount())
		{
			String text = getPlainText(line);
			
			SimpleTextCells tc = new SimpleTextCells();
			for(Segment seg: new DemoSyntax(text).generateSegments())
			{
				tc.setTextColor(seg.color);
				tc.addText(seg.text);				
			}
			return tc;
		}
		return null;
	}
}
