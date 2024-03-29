// Copyright © 2020-2023 Andy Goryachev <andy@goryachev.com>
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


	public int getLineNumber()
	{
		return lineNumber;
	}


	public int getModelIndex()
	{
		return lineNumber;
	}


	public String getPlainText()
	{
		return text;
	}


	public int getTextLength()
	{
		return text.length();
	}


	public TextCellStyle getCellStyle(int charOffset)
	{
		return style;
	}


	public Color getLineColor()
	{
		return null;
	}
}
