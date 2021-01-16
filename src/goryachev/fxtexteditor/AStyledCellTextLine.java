// Copyright © 2020-2021 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
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
	
	
	public int getTextLength()
	{
		return plainText.length();
	}


	public String getPlainText()
	{
		return plainText;
	}


	public int getModelIndex()
	{
		return line;
	}


	public int getLineNumber()
	{
		return line;
	}


	public CellStyle getCellStyle(int charOffset)
	{
		return styles().get(charOffset);
	}
	
	
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
