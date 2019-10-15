// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import goryachev.common.util.CKit;
import goryachev.fxtexteditor.CellStyles;
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
			return lines[line % lines.length];
		}
		return null;
	}


	// TODO
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
				int len = text.length();
				if((offset >= 0) && (offset + 1 <= len))
				{
					return text.substring(offset, offset + 1);
				}
				return null;
			}


			public void updateStyle(int off, CellStyles styles)
			{
				styles.update
				(
					null,
					null,
					false,
					false, 
					false,
					false
				);
			}


			public boolean hasComplexGlyphLogic()
			{
				return false;
			}
		};
	}
}
