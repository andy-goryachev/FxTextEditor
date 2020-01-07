// Copyright © 2017-2020 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import goryachev.common.util.CList;
import java.util.List;
import javafx.scene.paint.Color;


/**
 * Demo Syntax.
 */
public class DemoSyntax
{
	private final String text;
	private final CList<Segment> segments = new CList();
	private int start;
	private Color color = Color.BLACK;
	private Color bg;
	private boolean bold; // means nothing, random logic
	
	
	public DemoSyntax(String text)
	{
		this.text = text;
	}
	
	
	public List<Segment> generateSegments()
	{
		for(int i=0; i<text.length(); i++)
		{
			char c = text.charAt(i);
			
			int close = getClosingChar(c);
			if(close >= 0)
			{
				int ix = text.indexOf(close, i);
				if(ix >= 0)
				{
					addSegment(i);
					color = Color.BLUE;
					bg = Color.YELLOW;
					i = ix;
					
					addSegment(i);
					continue;
				}
			}
			
			Color col = getColor(c);
			if(!col.equals(color))
			{
				addSegment(i);
				color = col;
				bg = null;
			}
		}
		
		addSegment(text.length());

		return segments;
	}
	
	
	protected int getClosingChar(char c)
	{
		switch(c)
		{
		case '(':
			return ')';
		case '[':
			return ']';
		case '{':
			return '}';
		}
		return -1;
	}


	protected void addSegment(int end)
	{
		if(end > start)
		{
			segments.add(new Segment(text, start, end, color, bg, bold));
			start = end;
			bold = false;
		}
	}
	
	
	protected Color getColor(char c)
	{
		if(Character.isDigit(c))
		{
			bold = true;
			return Color.RED;
		}
		
		byte dir = Character.getDirectionality(c);
		switch(dir)
		{
		case Character.DIRECTIONALITY_ARABIC_NUMBER:
		case Character.DIRECTIONALITY_RIGHT_TO_LEFT:
		case Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC:
		case Character.DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING:
		case Character.DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE:
			return Color.OLIVEDRAB;
		}
		
		return Color.BLACK;
	}
}
