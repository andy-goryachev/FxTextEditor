// Copyright © 2020-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.fx.TextCellStyle;
import javafx.scene.paint.Color;


/**
 * Simple ITextLine: one style per line.
 */
public class SimpleTextLine
	implements ITextLine
{
	private final int lineNumber;
	private final TextCellStyle style;
	private final String text;


	public SimpleTextLine(int lineNumber, TextCellStyle style, String text)
	{
		this.lineNumber = lineNumber;
		this.style = style;
		this.text = text;
	}


	@Override
	public int getLineNumber()
	{
		return lineNumber;
	}


	@Override
	public int getModelIndex()
	{
		return lineNumber;
	}


	@Override
	public String getPlainText()
	{
		return text;
	}


	@Override
	public int getTextLength()
	{
		return text.length();
	}


	@Override
	public TextCellStyle getCellStyle(int charOffset)
	{
		return style;
	}


	@Override
	public Color getLineColor()
	{
		return null;
	}
}
