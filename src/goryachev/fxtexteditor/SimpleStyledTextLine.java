// Copyright © 2020-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.fx.TextCellStyle;
import javafx.scene.paint.Color;


/**
 * Simple Styled ITextLine.
 */
public class SimpleStyledTextLine
	implements ITextLine
{
	private final int lineNumber;
	private final String text;
	private final Color lineColor;
	private final TextCellStyle[] styles;
	
	
	public SimpleStyledTextLine(int lineNumber, String text, Color lineColor, TextCellStyle[] styles)
	{
		this.lineNumber = lineNumber;
		this.text = text;
		this.lineColor = lineColor;
		this.styles = styles;
	}


	@Override
	public int getTextLength()
	{
		return text.length();
	}
	
	
	@Override
	public String getPlainText()
	{
		return text;
	}
	
	
	@Override
	public int getModelIndex()
	{
		return lineNumber;
	}
	
	
	@Override
	public int getLineNumber()
	{
		return lineNumber;
	}
	
	
	@Override
	public TextCellStyle getCellStyle(int charOffset)
	{
		return styles[charOffset];
	}


	@Override
	public Color getLineColor()
	{
		return lineColor;
	}
}