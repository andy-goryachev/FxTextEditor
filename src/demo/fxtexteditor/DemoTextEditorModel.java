// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import goryachev.common.util.CKit;
import goryachev.fxtexteditor.Edit;
import goryachev.fxtexteditor.FxTextEditorModel;
import goryachev.fxtexteditor.ITextCells;
import goryachev.fxtexteditor.LoadInfo;
import goryachev.fxtexteditor.SimpleTextCells;


/**
 * Demo FxTextEditorModel.
 */
public class DemoTextEditorModel
	extends FxTextEditorModel
{
	protected final String[] lines;


	public DemoTextEditorModel(String text)
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
			
			SimpleTextCells tc = new SimpleTextCells();
			for(Segment seg: new DemoSyntax(text).generateSegments())
			{
				tc.setTextColor(seg.textColor);
				tc.setBackground(seg.backgroundColor);
				tc.setBold(seg.bold);
				tc.addText(seg.text);	
			}
			return tc;
		}
		return null;
	}
}
