// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import javafx.scene.paint.Color;


/**
 * Screen Buffer Cell.
 */
public class ScreenCell
{
	private Color backgroundColor;
	private Color textColor;
	private boolean caret;
	private int line;
	private int offset;
	private Grapheme grapheme;
	
	
	public ScreenCell()
	{
	}
	
	
	public Color getBackgroundColor()
	{
		return backgroundColor;
	}
	
	
	public void setBackgroundColor(Color c)
	{
		backgroundColor = c;
	}
	
	
	public String getText()
	{
		return grapheme == null ? null : grapheme.getText();
	}
	
	
	public Color getTextColor()
	{
		return textColor;
	}
	
	
	public void setTextColor(Color c)
	{
		textColor = c;
	}
	
	
	public boolean isCaret()
	{
		return caret;
	}
	
	
	public void setCaret(boolean on)
	{
		caret = on;
	}
	
	
	public int getLine()
	{
		return line;
	}
	
	
	public void setLine(int line)
	{
		this.line = line;
	}
	
	
	public int getOffset()
	{
		return offset;
	}
	
	
	public void setOffset(int offset)
	{
		this.offset = offset;
	}
	

	public void setCell(Grapheme gr)
	{
		this.grapheme = gr;
	}
	
	
	public boolean isBold()
	{
		return grapheme.isBold();
	}
	
	
	public boolean isItalic()
	{
		return grapheme.isItalic();
	}
	
	
	public boolean isStrikeThrough()
	{
		return grapheme.isStrikeThrough();
	}
	
	
	public boolean isUnderscore()
	{
		return grapheme.isUnderscore();
	}
}
