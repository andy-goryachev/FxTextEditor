// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.SelectionSegment;
import java.util.List;


/**
 * Selection Helper.
 */
public class SelectionHelper
{
	private static final int CARET = 1;
	private static final int CARET_LINE = 2;
	private static final int SELECTED = 4;
	
	
	public static int getFlags(List<SelectionSegment> segments, ScreenRow row, int x)
	{
		if(row == null)
		{
			// except when last line
			return 0;
		}
		
		int line = row.getModelIndex();
		if(line < 0)
		{
			line = row.getAppendModelIndex();
		}
		
		GlyphIndex gix = row.getGlyphIndex(x);
		int off;
		if(gix.isRegular())
		{
			off = row.getCharIndex(gix);
		}
		else if(gix.isEOL())
		{
			off = gix.isAtEOL() ? row.getTextLength() : -1;
		}
		else if(gix.isEOF())
		{
			off = -1;
		}
		else if(gix.isBOL())
		{
			off = 0; // TODO check
		}
		else if(gix.isInsideTab())
		{
			off = gix.getLeadingCharIndex(); 
		}
		else
		{
			throw new Error(gix.toString());
		}
		
		int flags = 0;
		for(SelectionSegment ss: segments)
		{
			if(ss.isCaretLine(line))
			{
				flags |= CARET_LINE;
				
				if(off >= 0)
				{
					if(ss.isCaret(line, off))
					{
						flags |= CARET;
					}
				}
			}
			
			if(ss.contains(line, off))
			{
				flags |= SELECTED;
			}
		}
		
		return flags;
	}


	public static boolean isCaret(int flags)
	{
		return (flags & CARET) != 0;
	}
	
	
	public static boolean isCaretLine(int flags)
	{
		return (flags & CARET_LINE) != 0;
	}
	
	
	public static boolean isSelected(int flags)
	{
		return (flags & SELECTED) != 0;
	}
}
