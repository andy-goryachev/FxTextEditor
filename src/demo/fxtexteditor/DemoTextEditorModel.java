// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import goryachev.common.util.CKit;
import goryachev.fxtexteditor.Edit;
import goryachev.fxtexteditor.FxTextEditorModel;
import goryachev.fxtexteditor.ITextLine;
import goryachev.fxtexteditor.LoadInfo;
import goryachev.fxtexteditor.ScreenCell;
import goryachev.fxtexteditor.DELETE.TextDecor;


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
			return lines[line % lines.length];
		}
		return null;
	}


//	public TextDecor getTextDecor(int line, String text, TextDecor d)
//	{
//		if(line < getLineCount())
//		{
//			for(Segment seg: new DemoSyntax(text).generateSegments())
//			{
//				d.setTextColor(seg.textColor);
//				d.setBackground(seg.backgroundColor);
//				d.setBold(seg.bold);
//				d.addSegment(seg.text.length());	
//			}
//			return d;
//		}
//		return null;
//	}
	
	
	public ITextLine getTextLine(int line)
	{
		String text = plainText(line);
		if(text == null)
		{
			return null;
		}
		
		return new ITextLine()
		{
			public int getModelIndex()
			{
				return line;
			}
			
			
			public int getLineNumber()
			{
				return line;
			}


			public String getPlainText()
			{
				return text;
			}


			public int getCellCount()
			{
				return text.length();
			}


			public String getCellText(int offset)
			{
				return text.substring(offset, offset + 1);
			}


			public void getStyle(ScreenCell s, int offset)
			{
				// TODO style
			}
		};
	}
}
