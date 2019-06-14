// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


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
	private Grapheme cell;
	
	
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
		return cell == null ? null : cell.getText();
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
	

	public void setCell(Grapheme cell)
	{
		this.cell = cell;
	}
	
	
	public boolean isBold()
	{
		return false; // TODO
	}
	
	
	public boolean isItalic()
	{
		return false; // TODO
	}
}
