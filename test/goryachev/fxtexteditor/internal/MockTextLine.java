// Copyright © 2020-2024 Andy Goryachev <andy@goryachev.com>
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
	
	
	@Override
	public int getLineNumber()
	{
		return 0;
	}


	@Override
	public int getModelIndex()
	{
		return 0;
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
		return null;
	}


	@Override
	public Color getLineColor()
	{
		return null;
	}
}
