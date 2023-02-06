// Copyright © 2019-2023 Andy Goryachev <andy@goryachev.com>
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
	
	
	public static int getFlags(VFlow vflow, SelectionSegment seg, int line, TextCell cell, int x)
	{
		if(seg == null)
		{
			return 0;
		}
		
		if(line < 0)
		{
			return 0;
		}
		
		// new API
		
		int off = cell.getCaretCharIndex();
		int selOff = cell.getLeadingEdgeCharIndex();
		
		// flags
		
		int flags = 0;
		
		if(seg.isCaretLine(line))
		{
			if(vflow.getEditor().isHighlightCaretLine())
			{
				flags |= CARET_LINE;
			}
			
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
		
		int ix = (selOff >= 0 ? selOff : off);
		if(ix < 0)
		{
			ix = vflow.getTopCellIndex();
		}
		
		if(seg.contains(line, ix))
		{
			flags |= SELECTED;
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
