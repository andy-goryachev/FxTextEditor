// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import goryachev.common.util.CKit;
import goryachev.fxtexteditor.CellStyles;
import goryachev.fxtexteditor.PlainTextLine;


/**
 * Demo TextLine.
 */
public class DemoTextLine
	extends PlainTextLine
{
	private TextAttributes attributes;
	private static TextAttributes NONE = new TextAttributes(0);
	
	
	public DemoTextLine(String text, int line)
	{
		super(line, text);
	}


	public void updateStyles(CellStyles styles, int off)
	{
		if(attributes == null)
		{
			String text = getPlainText();
			attributes = applySyntax(text); 
		}
		
		attributes.update(styles, off);
	}
	
	
	protected TextAttributes applySyntax(String text)
	{
		if(CKit.isBlank(text))
		{
			return NONE;
		}
		
		TextAttributes a = new TextAttributes(text.length());
		int start = 0;
		for(Segment seg: new DemoSyntax(text).generateSegments())
		{
			a.addSegment(start, seg);
			start += seg.length();
		}
		return a;
	}
}