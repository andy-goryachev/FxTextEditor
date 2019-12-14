// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.util.D;
import goryachev.fxtexteditor.SelectionSegment;
import java.util.List;


/**
 * Selection Helper.
 */
@Deprecated // TODO remove, prepare flags in reflow helper
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
		
		// FIX glyph index?  or char in
		// TODO returns negative values here
		int glyphIndex = row.getGlyphIndex(x);
		int off;
		if(glyphIndex < 0)
		{
			// FIX this might be complicated due to trailing text pos
			if(glyphIndex == ScreenBuffer.EOL)
			{
				off = -1;
			}
			else if(glyphIndex == ScreenBuffer.EOF)
			{
				off = -1;
			}
			else
			{
				// TODO special case when x=0, may not have glyph index for x=-1
				// tab
				off = -1;
			}
		}
		else
		{
			off = row.getCharIndex(glyphIndex);
		}
		
		int flags = 0;
		for(SelectionSegment ss: segments)
		{
			if(ss.isCaretLine(line))
			{
				flags |= CARET_LINE;
				
				if(ss.isCaret(line, off))
				{
					flags |= CARET;
				}
			}
			
			if(ss.contains(line, off))
			{
				flags |= SELECTED;
			}
		}
		
		return flags;
	}


//	public static boolean isCaret(int flags)
//	{
//		return (flags & CARET) != 0;
//	}
//	
//	
//	public static boolean isCaretLine(int flags)
//	{
//		return (flags & CARET_LINE) != 0;
//	}
	
	
	public static boolean isSelected(int flags)
	{
		return (flags & SELECTED) != 0;
	}
}
