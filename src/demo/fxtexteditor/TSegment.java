// Copyright © 2017-2023 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import goryachev.fx.TextCellStyle;


/**
 * Text Attribute Segment.
 */
public class TSegment
{
	public final String text;
	public final int start;
	public final int end;
	public final TextCellStyle style;
	
	
	public TSegment(String text, int start, int end, TextCellStyle style)
	{
		this.text = text;
		this.start = start;
		this.end = end;
		this.style = style;
	}


	public int length()
	{
		return end - start;
	}
	
	
	public String getText()
	{
		return text.substring(start, end);
	}


	public boolean contains(int off)
	{
		return (off >= start) && (off < end);
	}
}