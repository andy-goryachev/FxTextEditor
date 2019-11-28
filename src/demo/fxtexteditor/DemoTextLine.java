// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import goryachev.fxtexteditor.CellStyles;
import goryachev.fxtexteditor.PlainTextLine;


/**
 * Demo TextLine.
 */
public class DemoTextLine
	extends PlainTextLine
{
	public DemoTextLine(String text, int line)
	{
		super(line, text);
	}


	// TODO
//	public TextDecor getTextDecor(int line, String text, TextDecor d)
//	{
//		if(line < getLineCount())
//		{
//			for(Segment seg: new DemoSyntax(text).generateSegments())
//			{
//				d.setTextColor(seg.textColor);
//				d.setBackground(seg.backgroundColor);
//				d.setBold(seg.bold);
//				d.addSegment(seg.text.length());	
//			}
//			return d;
//		}
//		return null;
//	}
	

	public void updateStyle(int off, CellStyles styles)
	{
		// TODO syntax
		styles.update
		(
			null,
			null,
			false,
			false, 
			false,
			false
		);
	}
}