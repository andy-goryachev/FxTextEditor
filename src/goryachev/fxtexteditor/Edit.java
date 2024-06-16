// Copyright © 2017-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.util.CKit;


/**
 * Represents a change that needs to be applied to the model.
 */
public class Edit
{
	private final int minLine;
	private final int minPos;
	private final int maxLine;
	private final int maxPos;
	private final boolean caretAtMin;
	private final Object text;
	
	
	public Edit(int minLine, int minPos, int maxLine, int maxPos, boolean caretAtMin, Object text)
	{
		this.minLine = minLine;
		this.minPos = minPos;
		this.maxLine = maxLine;
		this.maxPos = maxPos;
		this.caretAtMin = caretAtMin;
		this.text = text;
	}
	
	
	public static Edit create(SelectionSegment seg, Object text)
	{
		if(text instanceof String)
		{
			// ok
		}
		else if(text instanceof String[])
		{
			// ok
		}
		else
		{
			throw new Error("?" + CKit.className(text));
		}
		
		int minLine = seg.getMinLine();
		int minPos = seg.getMinCharIndex();
		int maxLine = seg.getMaxLine();
		int maxPos = seg.getMaxCharIndex();
		boolean caretAtMin = seg.isCaretAtMin();
		
		return new Edit(minLine, minPos, maxLine, maxPos, caretAtMin, text);
	}
	
	
	public boolean isText()
	{
		return (text instanceof String);
	}
	
	
	public boolean isTextLines()
	{
		return (text instanceof String[]);
	}
	
	
	public String getText()
	{
		return (String)text;
	}
	
	
	public String[] getTextLines()
	{
		return (String[])text;
	}
	
	
	public int getMinLine()
	{
		return minLine;
	}
	
	
	public int getMaxLine()
	{
		return maxLine;
	}
	
	
	public int getMinCharIndex()
	{
		return minPos;
	}
	
	
	public int getMaxCharIndex()
	{
		return maxPos;
	}
	
	
	public boolean isOnSameLine()
	{
		return (minLine == maxLine);
	}
}
