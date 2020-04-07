// Copyright © 2019-2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.util.D;
import goryachev.common.util.ElasticIntArray;
import goryachev.fxtexteditor.ITabPolicy;
import goryachev.fxtexteditor.VFlow;


/**
 * Vertical Scroll Helper for wrapped mode.
 * 
 * Computes line indexes and starting points for [count] rows up and down relative
 * to the rough target scroll line [center], 
 * in order to account for text lines that take more than one screen row.
 */
public class VerticalScrollHelper
{
	private final VFlow vflow;
	private final int max;
	private final int top;
	private final double fraction;
	private final int topGlyphIndex;
	private int newLineNumber;
	private int newGlyphIndex;
	
	
	public VerticalScrollHelper(VFlow vflow, int max, int top, double fraction, GlyphIndex topGlyphIndex)
	{
		this.vflow = vflow;
		this.max = max;
		this.top = top;
		this.fraction = fraction;
		this.topGlyphIndex = topGlyphIndex.intValue(); // TODO use this
	}
	
	
	public int getNewTopLine()
	{
		return newLineNumber;
	}


	public GlyphIndex getNewGlyphIndex()
	{
		return GlyphIndex.of(newGlyphIndex);
	}
	

	// looks at frameSize flow lines back and forward, computer the number of rows,
	// then uses fraction to select the new origin.
	// hopefully, this would lessen sudden jumps associated with uneven line lengths.
	public void process()
	{
		int width = vflow.getScreenColumnCount();
		ITabPolicy tabPolicy = vflow.getEditor().getTabPolicy();
		int frameSize = 2 * Math.max(100, vflow.getScreenRowCount());

		int topSize = 0;
		int topRows = 0;
		
		if(top > 0)
		{
			int start = Math.max(0, top - frameSize);
			int end = Math.min(top, start + frameSize);
			topSize = end - start;
			
			for(int ix=start; ix<end; ix++)
			{
				FlowLine fline = vflow.getTextLine(ix);
				WrapInfo wr = vflow.getWrapInfo(fline);
				topRows += wr.getRowCount();
			}
		}

		int additionalTopCount  = topRows - topSize;
		int end = Math.min(max, top + frameSize);
		int bottomSize = end - top;
		int bottomRows = 0;
		boolean hasLastLine = (end == max);
		
		for(int ix=top; ix<=end; ix++)
		{
			FlowLine fline = vflow.getTextLine(ix);
			WrapInfo wr = vflow.getWrapInfo(fline);
			bottomRows += wr.getRowCount();
		}
		
		int additionalBottomCount = bottomRows - bottomSize;
		
		// the new scroll position is center + delta
		int delta = (int)((additionalBottomCount + additionalTopCount) * fraction) - additionalTopCount;
		int ix = topSize + delta;
		
		if(hasLastLine)
		{
			ix = Math.min(ix, topRows + bottomRows - vflow.getScreenRowCount() + 1);
			if(ix < 0)
			{
				ix = 0;
			}
		}
		
		// TODO scan one more time to get the line number and glyph index
		
		int step = Integer.signum(ix - topRows);
		if(step == 0)
		{
			// unchanged
			newLineNumber = top;
			newGlyphIndex = 0;
		}
		else
		{
			int i = top;
			int gix = 0;
			while(delta != 0)
			{
				i += step;
				
				FlowLine fline = vflow.getTextLine(i);
				WrapInfo wr = vflow.getWrapInfo(fline);
				if(wr.getRowCount() < delta)
				{
					delta -= wr.getRowCount();
					continue;
				}
				else
				{
					if(step < 0)
					{
						gix = wr.getIndexForRow(wr.getRowCount() +  delta);
					}
					else
					{
						gix = wr.getIndexForRow(delta);
					}
					delta = 0;
				}
			}

			newLineNumber = i;
			newGlyphIndex = gix;
		}
		
		// FIX
//		D.print
//		(
//			"fraction=", fraction,
//			"center=", center,
//			"max=", max,
//			"topSize=", topSize,
//			"additionalTopCount=", additionalTopCount,
//			"end=", end,
//			"bottomSize=", bottomSize,
//			"additionalBottomCount=", additionalBottomCount,
//			"delta=", delta,
//			"ix=", ix,
//			"newLineNumber=", newLineNumber,
//			"newGlyphIndex=", newGlyphIndex
//		);
	}
}
