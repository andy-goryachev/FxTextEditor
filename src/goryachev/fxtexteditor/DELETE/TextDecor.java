// Copyright © 2019-2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.DELETE;
import goryachev.common.util.CList;
import javafx.scene.paint.Color;


/**
 * Text Decorations.
 */
@Deprecated // FIX remove
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
	
	
	public void reset()
	{
		segments.clear();
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
	
	
	protected TAttrs getAttributes()
	{
		return new TAttrs(lineBackground, backgroundColor, textColor, bold, italic, strikeThrough, underscore);
	}
	
	
	public void addSegment(int length)
	{
		TAttrs a = getAttributes();
		
		Segment s = new Segment();
		s.attrs = a;
		s.length = length;
		segments.add(s);
	}
	
	
//	public void applyStyles(ITextCells cs)
//	{
//		int pos = 0;
//		for(Segment s: segments)
//		{
//			TAttrs a = s.attrs;
//			for(int i=0; i<s.length; i++)
//			{
//				Grapheme c = cs.getCell(pos);
//				c.setStyle(a);
//				pos++;
//			}
//		}
//	}
	
	
	//
	
	
	protected static class Segment
	{
		public int length;
		public TAttrs attrs;
	}
}
