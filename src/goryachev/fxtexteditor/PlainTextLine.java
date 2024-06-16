// Copyright © 2019-2024 Andy Goryachev <andy@goryachev.com>
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


	@Override
	public int getLineNumber()
	{
		return line;
	}
	
	
	@Override
	public int getModelIndex()
	{
		return line;
	}


	@Override
	public String getPlainText()
	{
		return text;
	}
	
	
	@Override
	public int getTextLength()
	{
		return text == null ? 0 : text.length();
	}
	

	@Override
	public TextCellStyle getCellStyle(int offset)
	{
		return null;
	}


	@Override
	public Color getLineColor()
	{
		return null;
	}
}
