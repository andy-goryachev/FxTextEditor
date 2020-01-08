// Copyright © 2017-2020 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import javafx.scene.paint.Color;


/**
 * Text Attribute Segment.
 */
public class TSegment
{
	public final String text;
	public final int start;
	public final int end;
	public final Color textColor;
	public final Color backgroundColor;
	public final boolean bold;
	
	
	public TSegment(String text, int start, int end, Color textColor, Color bg, boolean bold)
	{
		this.text = text;
		this.start = start;
		this.end = end;
		this.textColor = textColor;
		this.backgroundColor = bg;
		this.bold = bold;
	}


	public int length()
	{
		return end - start;
	}
	
	
	public String getText()
	{
		return text.substring(start, end);
	}
}