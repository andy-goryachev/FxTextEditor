// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import javafx.scene.paint.Color;


/**
 * A single instance of Screen Cell holds cell values during paintCell().
 */
public class ScreenCell
{
	private String text;
	private Color backgroundColor;
	private Color textColor;
	private boolean bold;
	private boolean italic;
	private boolean strikeThrough;
	private boolean underscore;
	
	
	public ScreenCell()
	{
	}
	
	
	public void reset()
	{
		text = null;
		backgroundColor = null;
		textColor = null;
		bold = false;
		italic = false;
		strikeThrough = false;
		underscore = false;
	}
	
	
	public String getText()
	{
		return text;
	}
	
	
	public void setText(String s)
	{
		text = s;
	}
	
	
	public Color getBackgroundColor()
	{
		return backgroundColor;
	}
	
	
	public void setBackgroundColor(Color c)
	{
		backgroundColor = c;
	}
	
	
	public Color getTextColor()
	{
		return textColor;
	}
	
	
	public void setTextColor(Color c)
	{
		textColor = c;
	}


	public boolean isBold()
	{
		return bold;
	}
	
	
	public void setBold(boolean on)
	{
		bold = on;
	}


	public boolean isItalic()
	{
		return italic;
	}
	
	
	public void setItalic(boolean on)
	{
		italic = on;
	}
	
	
	public boolean isStrikeThrough()
	{
		return strikeThrough;
	}
	
	
	public void setStrikeThrough(boolean on)
	{
		strikeThrough = on;
	}
	
	
	public boolean isUnderscore()
	{
		return underscore;
	}
	
	
	public void setUnderscore(boolean on)
	{
		underscore = on;
	}
}