// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.util.D;
import goryachev.fxtexteditor.SelectionSegment;
import java.util.List;


/**
 * Selection Helper.
 * 
 * TODO quick hack, rewrite later
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
		
		int off = row.getGlyphIndex(x);
		
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
