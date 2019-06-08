// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import goryachev.common.util.CKit;
import goryachev.fxtexteditor.Edit;
import goryachev.fxtexteditor.FxTextEditorModel;
import goryachev.fxtexteditor.LoadInfo;
import goryachev.fxtexteditor.TextDecor;


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


	public String getPlainText(int line)
	{
		if(line < getLineCount())
		{
			return lines[line];
		}
		return null;
	}


	public TextDecor getTextLine(int line, String text, TextDecor d)
	{
		if(line < getLineCount())
		{
			for(Segment seg: new DemoSyntax(text).generateSegments())
			{
				d.setTextColor(seg.textColor);
				d.setBackground(seg.backgroundColor);
				d.setBold(seg.bold);
				d.addSegment(seg.text.length());	
			}
			return d;
		}
		return null;
	}
}
