// Copyright © 2017-2021 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import goryachev.fxtexteditor.CellStyle;


/**
 * Text Attribute Segment.
 */
public class TSegment
{
	public final String text;
	public final int start;
	public final int end;
	public final CellStyle style;
	
	
	public TSegment(String text, int start, int end, CellStyle style)
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