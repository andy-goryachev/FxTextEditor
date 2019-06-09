// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import javafx.scene.paint.Color;


/**
 * Text Cell.  TODO rename: grapheme cluster
 */
public class TextCell
{
	public final int start;
	public final int end;
	public final String text;
	private TAttrs attrs;
	
	
	public TextCell(int start, int end, String text)
	{
		this.start = start;
		this.end = end;
		this.text = text;
	}


	public String getText()
	{
		return text;
	}


	public Color getBackgroundColor()
	{
		return attrs == null ? null : attrs.getBackgroundColor();
	}


	public void setStyle(TAttrs a)
	{
		attrs = a;
	}
}