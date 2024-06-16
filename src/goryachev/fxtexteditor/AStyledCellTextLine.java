// Copyright © 2020-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.fx.TextCellStyle;
import javafx.scene.paint.Color;


/**
 * An ITextLine implementation based on CellStyleArray.
 */
public abstract class AStyledCellTextLine
	implements ITextLine
{
	/** creates cell style array based on the plain text */
	protected abstract CellStyleArray createCellStyleArray(String text);
	
	//
	
	protected final int line;
	protected final String plainText;
	private CellStyleArray styles;
	
	
	public AStyledCellTextLine(int line, String text)
	{
		this.line = line;
		this.plainText = text;
	}
	
	
	@Override
	public int getTextLength()
	{
		return plainText.length();
	}


	@Override
	public String getPlainText()
	{
		return plainText;
	}


	@Override
	public int getModelIndex()
	{
		return line;
	}


	@Override
	public int getLineNumber()
	{
		return line;
	}


	@Override
	public TextCellStyle getCellStyle(int charOffset)
	{
		return styles().get(charOffset);
	}
	
	
	@Override
	public Color getLineColor()
	{
		return styles().getLineColor();
	}
	
	
	protected CellStyleArray styles()
	{
		if(styles == null)
		{
			styles = createCellStyleArray(plainText);
		}

		return styles;
	}
}
