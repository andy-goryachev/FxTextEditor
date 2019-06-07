// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


/**
 * Screen Buffer Cell.
 * 
 * may take more than one screen cell rectangle (tab, end of line)
 */
public class Cell
{
	private int width;
	private String text;
	private Font font;
	private Color backgroundColor;
	private Color textColor;
	private boolean caret;
	private int line;
	private int offset;
	
	
	public Cell()
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
	
	
	public Font getFont()
	{
		return font;
	}
	
	
	public void setFont(Font f)
	{
		font = f;
	}
	
	
	public String getText()
	{
		return text;
	}
	
	
	public void setText(String text)
	{
		this.text = text;
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
	
	
	public int getWidth()
	{
		return width;
	}
	
	
	public void setWidth(int w)
	{
		width = w;
	}


	public boolean isTab()
	{
		return false;
	}
}
