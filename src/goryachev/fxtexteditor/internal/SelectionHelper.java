// Copyright © 2019-2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.util.D;
import goryachev.fxtexteditor.GlyphType;
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
		if(seg == null)
		{
			return 0;
		}
		
		if(row == null)
		{
			// except when last line
			return 0;
		}
		
		int line = row.getLineNumber();
		if(line < 0)
		{
			return 0;
		}
		
		int off;
		int selOff = -1; // TODO

		GlyphType t = row.getGlyphTypeAtColumn(x);
		switch(t)
		{
		case REG:
			off = row.getCharIndexForColumn(x);
			break;
		case EOL:
			// TODO check
//			off = gix.isAtEOL() ? row.getTextLength() : -1;
//			selOff = row.getTextLength();
			
			off = row.getCharIndexForColumn(x);
			break;
		case EOF:
//			if((x == 0) && (row.getLineNumber() == line))
//			{
//				off = 0;
//			}
//			else
//			{
//				off = -1;
//			}
			off = 0;
			break;
		case TAB:
			// TODO add a special method to return tab char index only for the leading column 
			if(row.isLeadingTabColumn(x))
			{
				off = row.getCharIndexForColumn(x);
			}
			else
			{
				off = -1;
			}
			break;
		default:
			throw new Error("?" + t);
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
//						D.print(x, off); // FIX
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
				int line = row.getLineNumber();
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
