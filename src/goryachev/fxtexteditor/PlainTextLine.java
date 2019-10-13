// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;


/**
 * Plain Text Line.
 */
public class PlainTextLine
	implements ITextLine
{
	private final int line;
	private final String text;
	
	
	public PlainTextLine(int line, String text)
	{
		this.line = line;
		this.text = text;
	}


	public int getLineNumber()
	{
		return line;
	}
	
	
	public int getModelIndex()
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


	public void updateStyle(int offset, CellStyles styles)
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
}
