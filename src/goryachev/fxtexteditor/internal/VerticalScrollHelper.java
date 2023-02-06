// Copyright © 2019-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.log.Log;
import goryachev.common.util.CKit;
import goryachev.fxtexteditor.GlyphPos;
import goryachev.fxtexteditor.VFlow;


/**
 * Vertical Scroll Helper for wrapped mode.
 * 
 * Computes line indexes and starting points for [count] rows up and down relative
 * to the rough target scroll line [center], 
 * in order to account for text lines that take more than one screen row.
 * 
 * FIX last line is slightly off
 */
@Deprecated // TODO use ScrollAssist?
public class VerticalScrollHelper
{
	protected static final Log log = Log.get("VerticalScrollHelper");
	private final VFlow vflow;
	private final int modelLineCount;
	private final int originalTarget;
	private final double fraction;
	
	
	public VerticalScrollHelper(VFlow vflow, int modelLineCount, int top, double fraction)
	{
		this.vflow = vflow;
		this.modelLineCount = modelLineCount;
		this.originalTarget = top;
		this.fraction = fraction;
	}
	

	// in order to minimize sudden jumps due to very long lines, this method
	// looks at frameSize text lines back and forward, computes the number of wrapped rows,
	// in order to position the origin using the fraction argument.
	public GlyphPos process()
	{
		int screenRows = vflow.getScreenRowCount();
		int frameSize = Math.max(ScrollAssist.FRAME_SIZE, screenRows);
		
		// determine frame boundaries

		int start = originalTarget - frameSize;
		int shift = 0;
		if(start < 0)
		{
			shift = -start;
			start = 0;
		}
		
		int end = originalTarget + screenRows + frameSize + shift;
		shift = 0;
		if(end > modelLineCount)
		{
			shift = end - modelLineCount;
			end = modelLineCount;
		}
		
		if(shift > 0)
		{
			start = Math.max(start - shift, 0);
		}
		
		// count the number of additional rows appears when wrapping lines
		// in the range of [start...end[
		
		int additionalRows = 0;
		
		for(int ix=start; ix<end; ix++)
		{
			WrapInfo wr = vflow.getWrapInfo(ix);
			int add = wr.getWrapRowCount();
			if(add > 1)
			{
				additionalRows += (add - 1);
			}
		}
		
		// new origin shall account for additional rows
		// here we magically switch from text line indexes (start) to rows
		int rowsToSkip = CKit.round((modelLineCount + additionalRows - screenRows) * fraction) - start;
		
		int newLineNumber;
		int newGlyphIndex;

		if(rowsToSkip == 0)
		{
			newLineNumber = originalTarget;
			newGlyphIndex = 0;
		}
		else
		{
			int modelLineCount = vflow.getModelLineCount();
			int lineix = start;
			int gix = 0;
			
			while(rowsToSkip > 0)
			{
				FlowLine fline = vflow.getTextLine(lineix);
				WrapInfo wr = vflow.getWrapInfo(fline);
				
				int wrapRowCount = wr.getWrapRowCount();
				if(rowsToSkip >= wrapRowCount)
				{
					rowsToSkip -= wrapRowCount;
					lineix++;
					// next text line
					continue;
				}
				else
				{
					gix = wr.getGlyphIndexForRow(rowsToSkip);
					break;
				}
			}
			
			newLineNumber = lineix;
			newGlyphIndex = gix;
			
			log.trace("ori=%d add=%d frac=%f start=%d skip=%d res=%d,%d", originalTarget, additionalRows, fraction, start, rowsToSkip, newLineNumber, gix);
		}
		
		return new GlyphPos(newLineNumber, newGlyphIndex);
	}
}
