// Copyright © 2020-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fx.TextCellStyle;
import goryachev.fxtexteditor.ITextLine;
import javafx.scene.paint.Color;


/**
 * Mock TextLine.
 */
public class MockTextLine
	implements ITextLine
{
	private final String text;
	
	
	public MockTextLine(String text)
	{
		this.text = text;
	}
	
	
	public int getLineNumber()
	{
		return 0;
	}


	public int getModelIndex()
	{
		return 0;
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
		return null;
	}


	public Color getLineColor()
	{
		return null;
	}
}
