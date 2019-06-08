// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.util.CList;
import javafx.scene.paint.Color;


/**
 * Text Decorations.
 */
public class TextDecor
{
	protected final CList<Segment> segments = new CList();
	private Color lineBackground;
	private Color backgroundColor;
	private Color textColor;
	private boolean hasRTL;
	private boolean bold;
	private boolean italic;
	private boolean strikeThrough;
	private boolean underscore;
	
	
	public TextDecor()
	{
	}
	
	
	public void setLineBackground(Color c)
	{
		lineBackground = c;
	}
	
	
	public void setBackground(Color c)
	{
		backgroundColor = c;
	}
	
	
	public void setTextColor(Color c)
	{
		textColor = c;
	}
	
	
	public void setBold(boolean on)
	{
		bold = on;
	}
	
	
	public void setItalic(boolean on)
	{
		italic = on;
	}
	
	
	public void setStrikeThrough(boolean on)
	{
		strikeThrough = on;
	}
	
	
	public void setUnderscore(boolean on)
	{
		underscore = on;
	}
	
	
	public void addSegment(int length)
	{
		Segment s = new Segment();
//		s.setStyle(); // TODO
		s.length = length;
		segments.add(s);
	}
	
	
	//
	
	
	protected static class Segment
	{
		public int length;
	}
}
