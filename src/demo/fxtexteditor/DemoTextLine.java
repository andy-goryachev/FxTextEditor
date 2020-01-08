// Copyright © 2019-2020 Andy Goryachev <andy@goryachev.com>
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
	private TAttributes attributes;
	private static TAttributes NONE = new TAttributes();
	
	
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
	
	
	protected TAttributes applySyntax(String text)
	{
		if(CKit.isBlank(text))
		{
			return NONE;
		}
		
		TAttributes a = new TAttributes();
		int start = 0;
		for(TSegment seg: new DemoSyntax(text).generateSegments())
		{
			a.addSegment(start, seg);
			start += seg.length();
		}
		return a;
	}
}