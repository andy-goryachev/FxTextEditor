// Copyright © 2020-2021 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.VFlow;


/**
 * Scroll Assist computes the number of additional rows
 * (total and before startRow) due to wrapping, for better
 * approximation of scroll bar position and thumb size
 * in the wrapped mode.
 * 
 * In theory, this object can be cached until one or more of the following changes:
 * screen row or column count, model line count, top line, wrap mode.
 */
public class ScrollAssist
{
	public static final int FRAME_SIZE = 64;
	private final long additionalTopRows;
	private final long additionalRows;
	
	
	protected ScrollAssist(long additionalTopRows, long additionalRows)
	{
		this.additionalTopRows = additionalTopRows;
		this.additionalRows = additionalRows;
	}
	
	
	public long getAdditionalTopRows()
	{
		return additionalTopRows;
	}
	
	
	public long getAdditionalRows()
	{
		return additionalRows;
	}
	
	
	public static ScrollAssist create(VFlow vflow, int startLine, int startWrapRow)
	{
		int lineCount = vflow.getModelLineCount();
		int screenRowCount = vflow.getScreenRowCount();
		int frameSize = Math.max(FRAME_SIZE, screenRowCount);
		
		int start = startLine - frameSize;
		if(start < 0)
		{
			start = 0;
		}
		
		int end = start + frameSize + frameSize + screenRowCount;
		if(end >= lineCount)
		{
			int d = end - lineCount;
			end = lineCount - 1;
			
			start -= d;
			if(start < 0)
			{
				start = 0;
			}
		}
		
		long additionalRows = 0;
		long additionalTopRows = 0;
		
		for(int line=start; line<=end; line++)
		{
			WrapInfo wr = vflow.getWrapInfo(line);
			int ct = wr.getWrapRowCount();
			
			if(ct > 1)
			{
				ct--;
				
				if(line < startLine)
				{
					additionalRows += ct;
					additionalTopRows += ct;
				}
				else if(line == startLine)
				{
					additionalRows += ct;
					additionalTopRows += (Math.max(0, startWrapRow - 1));
				}
				else
				{
					additionalRows += ct;
				}
			}
		}
		
		return new ScrollAssist(additionalTopRows, additionalRows);
	}
}
