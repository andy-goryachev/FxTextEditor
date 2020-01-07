// Copyright © 2017-2020 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import javafx.scene.paint.Color;


/**
 * Segment.
 */
public class Segment
{
	private final String text;
	private final int start;
	private final int end;
	public final Color textColor;
	public final Color backgroundColor;
	public final boolean bold;
	
	
	public Segment(String text, int start, int end, Color textColor, Color bg, boolean bold)
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