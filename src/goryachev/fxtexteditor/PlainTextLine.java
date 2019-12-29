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
	
	
	public int getTextLength()
	{
		return text == null ? 0 : text.length();
	}
	

	public void updateStyles(CellStyles styles, int offset)
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
