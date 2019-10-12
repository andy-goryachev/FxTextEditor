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


	public void getStyle(StyleInfo s, int offset)
	{
	}
}
