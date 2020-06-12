// Copyright © 2019-2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.util.CKit;
import goryachev.common.util.FH;
import javafx.scene.paint.Color;


/**
 * Captures text cell style.
 */
public class CellStyle
	implements Cloneable
{
	private Color textColor;
	private Color backgroundColor;
	private boolean bold;
	private boolean italic;
	private boolean strikeThrough;
	private boolean underscore;
	
	
	public CellStyle(Color fg, Color bg, boolean bold, boolean italic, boolean strikeThrough, boolean underscore)
	{
		this.textColor = fg;
		this.backgroundColor = bg;
		this.bold = bold;
		this.italic = italic;
		this.strikeThrough = strikeThrough;
		this.underscore = underscore;
	}
	
	
	public CellStyle(Color fg)
	{
		this.textColor = fg;
	}
	
	
	public CellStyle()
	{
	}
	
	
	public CellStyle copy()
	{
		return (CellStyle)clone();
	}
	
	
	public Object clone()
	{
		return new CellStyle(textColor, backgroundColor, bold, italic, strikeThrough, underscore);
	}
	
	
	public boolean isEmpty()
	{
		return
			!bold &&
			!italic &&
			!strikeThrough &&
			!underscore &&
			(backgroundColor == null) &&
			(textColor == null);
	}
	
	
	public void init(CellStyle x)
	{
		this.backgroundColor = x.backgroundColor;
		this.textColor = x.textColor;
		this.bold = x.bold;
		this.italic = x.italic;
		this.strikeThrough = x.strikeThrough;
		this.underscore = x.underscore;
	}
	
	
	public boolean equals(Object x)
	{
		if(x == this)
		{
			return true;
		}
		else if(x instanceof CellStyle)
		{
			CellStyle c = (CellStyle)x;
			return
				(bold == c.bold) &&
				(italic == c.italic) &&
				(strikeThrough == c.strikeThrough) &&
				(underscore == c.underscore) &&
				CKit.equals(textColor, c.textColor) &&
				CKit.equals(backgroundColor, c.backgroundColor);
		}
		return false;
	}
	
	
	public int hashCode()
	{
		int h = FH.hash(CellStyle.class);
		h = FH.hash(h, backgroundColor);
		h = FH.hash(h, textColor);
		h = FH.hash(h, bold);
		h = FH.hash(h, italic);
		h = FH.hash(h, strikeThrough);
		h = FH.hash(h, underscore);
		return h;
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