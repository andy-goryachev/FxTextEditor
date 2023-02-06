// Copyright © 2019-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.fx.TextCellStyle;
import javafx.scene.paint.Color;


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
	

	public TextCellStyle getCellStyle(int offset)
	{
		return null;
	}


	public Color getLineColor()
	{
		return null;
	}
}
