// Copyright © 2020-2022 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import javafx.scene.paint.Color;


/**
 * Simple ITextLine: one style per line.
 */
public class SimpleTextLine
	implements ITextLine
{
	private final int lineNumber;
	private final CellStyle style;
	private final String text;


	public SimpleTextLine(int lineNumber, CellStyle style, String text)
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


	public CellStyle getCellStyle(int charOffset)
	{
		return style;
	}


	public Color getLineColor()
	{
		return null;
	}
}
