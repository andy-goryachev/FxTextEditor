// Copyright © 2019-2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import javafx.scene.paint.Color;


/**
 * Captures text cell style.
 */
public class CellStyle
{
	private Color backgroundColor;
	private Color textColor;
	private boolean bold;
	private boolean italic;
	private boolean strikeThrough;
	private boolean underscore;
	
	
	public CellStyle(Color fg, Color bg, boolean bold, boolean italic, boolean strikeThrough, boolean underscore)
	{
		this.backgroundColor = bg;
		this.textColor = fg;
		this.bold = bold;
		this.italic = italic;
		this.strikeThrough = strikeThrough;
		this.underscore = underscore;
	}
	
	
	public CellStyle()
	{
	}
	
	
	public void clear()
	{
		this.backgroundColor = null;
		this.textColor = null;
		this.bold = false;
		this.italic = false;
		this.strikeThrough = false;
		this.underscore = false;
	}
	
	
	public Color getBackgroundColor()
	{
		return backgroundColor;
	}
	
	
//	public void setBackgroundColor(Color c)
//	{
//		backgroundColor = c;
//	}
	
	
	public Color getTextColor()
	{
		return textColor;
	}
	
	
//	public void setTextColor(Color c)
//	{
//		textColor = c;
//	}


	public boolean isBold()
	{
		return bold;
	}
	
	
//	public void setBold(boolean on)
//	{
//		bold = on;
//	}


	public boolean isItalic()
	{
		return italic;
	}
	
	
//	public void setItalic(boolean on)
//	{
//		italic = on;
//	}
	
	
	public boolean isStrikeThrough()
	{
		return strikeThrough;
	}
	
	
//	public void setStrikeThrough(boolean on)
//	{
//		strikeThrough = on;
//	}
	
	
	public boolean isUnderscore()
	{
		return underscore;
	}
	
	
//	public void setUnderscore(boolean on)
//	{
//		underscore = on;
//	}
}