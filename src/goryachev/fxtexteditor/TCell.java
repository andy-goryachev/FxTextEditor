// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import javafx.scene.paint.Color;


/**
 * Text Cell.
 */
public class TCell
{
	private String text;
	private boolean rtl;
	private Color backgroundColor;
	private Color textColor;
	
	public TCell(String text, boolean rtl, Color bg, Color fg)
	{
		this.text = text;
		this.rtl = rtl;
		this.backgroundColor = bg;
		this.textColor = fg;
	}
	
	
	public String getText()
	{
		return text;
	}
	
	
	public Color getBackgroundColor()
	{
		return backgroundColor;
	}
	
	
	public Color getTextColor()
	{
		return textColor;
	}
}
