// Copyright © 2019-2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
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
	private final int initialLine;
	private final double fraction;
	private final int topGlyphIndex;
	private int newLineNumber;
	private int newGlyphIndex;
	
	
	public VerticalScrollHelper(VFlow vflow, int max, int top, double fraction, GlyphIndex topGlyphIndex)
	{
		this.vflow = vflow;
		this.max = max;
		this.initialLine = top;
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
	// hopefully, this would lessen sudden jumps associated with extra long lines.
	public void process()
	{
		int width = vflow.getScreenColumnCount();
		ITabPolicy tabPolicy = vflow.getEditor().getTabPolicy();
		int frameSize = 2 * Math.max(100, vflow.getScreenRowCount()); // ?

		int topSize = 0;
		int topRows = 0;
		
		if(initialLine > 0)
		{
			int start = Math.max(0, initialLine - frameSize);
			int end = Math.min(initialLine, start + frameSize);
			topSize = end - start;
			
			for(int ix=start; ix<end; ix++)
			{
				FlowLine fline = vflow.getTextLine(ix);
				WrapInfo wr = vflow.getWrapInfo(fline);
				topRows += wr.getRowCount();
			}
		}

		int additionalTopCount = topRows - topSize;
		
		int end = Math.min(max, initialLine + frameSize);
		int bottomSize = end - initialLine;
		int bottomRows = 0;
		boolean hasLastLine = (end == max);
		
		for(int ix=initialLine; ix<=end; ix++)
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
		
		// scan one more time to get the line number and glyph index
		
		int direction = Integer.signum(ix - topRows);
		if(direction == 0)
		{
			// unchanged
			newLineNumber = initialLine;
			newGlyphIndex = 0;
		}
		else
		{			
			int toSkip = Math.abs(delta);
			int gix = 0;
			int modelLineCount = vflow.getModelLineCount();
			int lineix = initialLine;
			
			while(toSkip > 0)
			{
				lineix = initialLine + direction;
				
				if((lineix < 0) || (lineix >= modelLineCount))
				{
					throw new Error("lineix=" + lineix); // sanity check
				}
				
				FlowLine fline = vflow.getTextLine(lineix);
				WrapInfo wr = vflow.getWrapInfo(fline);
				
				int ct = wr.getRowCount();
				if(toSkip > ct)
				{
					toSkip -= ct;
					// keep going
				}
				else
				{
					if(direction < 0)
					{
						gix = wr.getIndexForRow(wr.getRowCount() - toSkip);
					}
					else
					{
						gix = wr.getIndexForRow(toSkip);
					}
					
					break;
				}
			}
			
			newLineNumber = lineix;
			newGlyphIndex = gix;
		}
	}
}
