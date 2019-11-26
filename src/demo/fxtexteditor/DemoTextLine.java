// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import goryachev.fxtexteditor.CellStyles;
import goryachev.fxtexteditor.GlyptType;
import goryachev.fxtexteditor.ITextLine;


/**
 * Demo TextLine.
 */
public class DemoTextLine
	implements ITextLine
{
	private final String text;
	private final int line;
	private final boolean hasTabs;
	
	
	public DemoTextLine(String text, int line)
	{
		this.text = text;
		this.line = line;
		this.hasTabs = (text == null ? false : (text.indexOf('\t') >= 0));
	}

	
	public int getModelIndex()
	{
		return line;
	}
	
	
	public int getLineNumber()
	{
		return line;
	}


	public String getPlainText()
	{
		return text;
	}
	
	
	public int getTextLength()
	{
		return text == null ? 0 : text.length();
	}


	public int getGlyphCount()
	{
		return text.length();
	}


	public String getCellText(int offset)
	{
		int len = text.length();
		if((offset >= 0) && (offset + 1 <= len))
		{
			return text.substring(offset, offset + 1);
		}
		return null;
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


	public boolean hasComplexGlyphs()
	{
		return false;
	}
	
	
	public GlyptType getGlyphType(int cellIndex)
	{
		if((cellIndex >= 0) && (cellIndex < text.length()))
		{
			char c = text.charAt(cellIndex);
			if(c == '\t')
			{
				return GlyptType.TAB;
			}
			else
			{
				return GlyptType.NORMAL;
			}
		}
		return GlyptType.EOL;
	}


	public boolean hasTabs()
	{
		return hasTabs;
	}


	public int getCharIndex(int glyphIndex)
	{
		return glyphIndex;
	}
}
