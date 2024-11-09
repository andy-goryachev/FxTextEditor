// Copyright © 2017-2024 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import goryachev.common.util.CList;
import goryachev.fx.TextCellStyle;
import java.util.List;
import javafx.scene.paint.Color;


/**
 * Demo Syntax.
 */
public class DemoSyntax
{
	private static final TextCellStyle STYLE_TEXT = new TextCellStyle();
	private static final TextCellStyle STYLE_ENCLOSED = new TextCellStyle(null, Color.rgb(0, 255, 0, 0.3), false, false, false, false);
	private static final TextCellStyle STYLE_NUMBER = new TextCellStyle(Color.BLUE, null, true, false, false, false);
	private static final TextCellStyle STYLE_RTF = new TextCellStyle(Color.OLIVEDRAB, null, true, false, false, false);

	private final String text;
	private final CList<TSegment> segments = new CList();
	private int start;
	private TextCellStyle style = STYLE_TEXT;
	
	
	public DemoSyntax(String text)
	{
		this.text = text;
	}
	
	
	public List<TSegment> generateSegments()
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
					style = STYLE_ENCLOSED;
					i = ix;
					
					addSegment(i);
					continue;
				}
			}
			
			TextCellStyle st = getCellStyle(c);
			if(!st.equals(style))
			{
				addSegment(i);
				style = st;
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
			segments.add(new TSegment(text, start, end, style));
			start = end;
		}
	}
	
	
	protected TextCellStyle getCellStyle(char c)
	{
		if(Character.isDigit(c))
		{
			return STYLE_NUMBER;
		}
		
		byte dir = Character.getDirectionality(c);
		switch(dir)
		{
		case Character.DIRECTIONALITY_ARABIC_NUMBER:
		case Character.DIRECTIONALITY_RIGHT_TO_LEFT:
		case Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC:
		case Character.DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING:
		case Character.DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE:
			return STYLE_RTF;
		}
		
		return STYLE_TEXT;
	}
}
