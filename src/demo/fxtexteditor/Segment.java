// Copyright © 2017-2020 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import javafx.scene.paint.Color;


/**
 * Segment.
 */
public class Segment
{
	public final String text;
	public final Color textColor;
	public final Color backgroundColor;
	public final boolean bold;
	
	
	public Segment(Color textColor, Color bg, String text, boolean bold)
	{
		this.text = text;
		this.textColor = textColor;
		this.backgroundColor = bg;
		this.bold = bold;
	}


	public int length()
	{
		return text.length();
	}
}