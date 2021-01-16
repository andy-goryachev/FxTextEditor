// Copyright © 2020-2021 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
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
	private final CellStyle[] styles;
	
	
	public SimpleStyledTextLine(int lineNumber, String text, Color lineColor, CellStyle[] styles)
	{
		this.lineNumber = lineNumber;
		this.text = text;
		this.lineColor = lineColor;
		this.styles = styles;
	}


	public int getTextLength()
	{
		return text.length();
	}
	
	
	public String getPlainText()
	{
		return text;
	}
	
	
	public int getModelIndex()
	{
		return lineNumber;
	}
	
	
	public int getLineNumber()
	{
		return lineNumber;
	}
	
	
	public CellStyle getCellStyle(int charOffset)
	{
		return styles[charOffset];
	}


	public Color getLineColor()
	{
		return lineColor;
	}
}