// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.DELETE;
import javafx.scene.paint.Color;


/**
 * Text Attributes.
 */
public class TAttrs
{
	private static final int BOLD = 0x00000001;
	private static final int ITALIC = 0x00000002;
	private static final int STRIKETHROUGH = 0x00000004;
	private static final int UNDERSCORE = 0x00000008;
	private Color lingBackground;
	private Color backgroundColor;
	private Color textColor;
	private int flags;
	
	
	public TAttrs(Color lingBackground, Color bg, Color textColor, boolean bold, boolean italic, boolean strikethrough, boolean underscore)
	{
		this.lingBackground = lingBackground;
		this.backgroundColor = bg;
		this.textColor = textColor;
		this.flags = flags(bold, italic, strikethrough, underscore);
	}
	
	
	public TAttrs()
	{
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
	
	
	public Color getLineBackground()
	{
		return lingBackground;
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
