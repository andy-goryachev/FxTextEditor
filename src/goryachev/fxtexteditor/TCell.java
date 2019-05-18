// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import javafx.scene.paint.Color;


/**
 * Text Cell.
 */
public class TCell
{
	private static final int BOLD = 0x00000001;
	private static final int ITALIC = 0x00000002;
	private static final int STRIKETHROUGH = 0x00000004;
	private static final int UNDERSCORE = 0x00000008;
	private String text;
	private Color backgroundColor;
	private Color textColor;
	private int flags;
	
	
	public TCell(String text, Color bg, Color fg, boolean bold, boolean italic, boolean strikethrough, boolean underscore)
	{
		this.text = text;
		this.backgroundColor = bg;
		this.textColor = fg;
		this.flags = flags(bold, italic, strikethrough, underscore);
	}
	
	
	public TCell(String text)
	{
		this.text = text;
	}
	
	
	protected static int flags(boolean bold, boolean italic, boolean strikethrough, boolean underscore)
	{
		int f = 0;
		if(bold)
		{
			f |= BOLD;
		}
		if(italic)
		{
			f |= ITALIC;
		}
		if(strikethrough)
		{
			f |= STRIKETHROUGH;
		}
		if(underscore)
		{
			f |= UNDERSCORE;
		}
		return f;
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
	
	
	protected boolean getFlag(int f)
	{
		return (flags & f) != 0;
	}
	
	
	public boolean isBold()
	{
		return getFlag(BOLD);
	}
	
	
	public boolean isItalic()
	{
		return getFlag(ITALIC);
	}
	
	
	public boolean isStrikeThrough()
	{
		return getFlag(STRIKETHROUGH);
	}
	
	
	public boolean isUnderscore()
	{
		return getFlag(UNDERSCORE);
	}
}
