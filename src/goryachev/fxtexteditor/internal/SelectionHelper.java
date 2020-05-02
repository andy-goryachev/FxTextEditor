// Copyright © 2019-2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.SelectionSegment;
import goryachev.fxtexteditor.VFlow;


/**
 * Selection Helper.
 */
public class SelectionHelper
{
	private static final int CARET = 1;
	private static final int CARET_LINE = 2;
	private static final int SELECTED = 4;
	
	
	public static int getFlags(VFlow vflow, SelectionSegment seg, ScreenRow row, int x)
	{
		if(row == null)
		{
			// except when last line
			return 0;
		}
		
		int line = row.getLineIndex();
		if(line < 0)
		{
			line = row.getAppendModelIndex();
		}
		
		GlyphIndex gix = row.getGlyphIndex(x);
		int off;
		int selOff = -1;
		if(gix.isRegular())
		{
			off = row.getCharIndex(gix);
		}
		else if(gix.isEOL())
		{
			off = gix.isAtEOL() ? row.getTextLength() : -1;
			selOff = row.getTextLength();
		}
		else if(gix.isEOF())
		{
			if((x == 0) && (row.getLineIndex() == line))
			{
				off = 0;
			}
			else
			{
				off = -1;
			}
		}
		else if(gix.isBOL())
		{
			off = 0; // TODO check
		}
		else if(gix.isInsideTab())
		{
			off = gix.getLeadingCharIndex();
			selOff = gix.getTabCharIndex();
		}
		else
		{
			throw new Error(gix.toString());
		}
		
		if(seg == null)
		{
			return 0;
		}
		
		int flags = 0;
		
		if(seg.isCaretLine(line))
		{
			flags |= CARET_LINE;
			
			if(off >= 0)
			{
				if(seg.isCaret(line, off))
				{
					if(!vflow.isWrapColumn(x))
					{
						flags |= CARET;
					}
				}
			}
		}
		
		int selectionOffset = (selOff >= 0 ? selOff : off);
		if(selectionOffset >= 0)
		{
			if(seg.contains(line, selectionOffset))
			{
				flags |= SELECTED;
			}
		}
		
		return flags;
	}
	
	
	public static boolean isCaretLine(SelectionSegment seg, ScreenRow row)
	{
		if(row != null)
		{
			if(seg != null)
			{
				int line = row.getLineIndex();
				if(line < 0)
				{
					line = row.getAppendModelIndex();
				}
				
				if(seg.isCaretLine(line))
				{
					return true;
				}
			}
		}
		return false;
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
