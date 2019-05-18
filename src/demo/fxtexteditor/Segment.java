// Copyright © 2017-2019 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import javafx.scene.paint.Color;


/**
 * Segment.
 */
public class Segment
{
	public final String text;
	public final Color color;
	public final boolean bold;
	
	
	public Segment(Color color, String text, boolean bold)
	{
		this.text = text;
		this.color = color;
		this.bold = bold;
	}
}