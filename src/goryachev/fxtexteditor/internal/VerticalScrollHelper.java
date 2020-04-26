// Copyright © 2019-2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.log.Log;
import goryachev.common.util.CKit;
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
	protected static final int WINDOW_SIZE = 100;
	protected static final Log log = Log.get("VerticalScrollHelper");
	private final VFlow vflow;
	private final int modelLineCount;
	private final int originalTarget;
	private final double fraction;
	private int newLineNumber;
	private int newGlyphIndex;
	
	
	public VerticalScrollHelper(VFlow vflow, int modelLineCount, int top, double fraction)
	{
		this.vflow = vflow;
		this.modelLineCount = modelLineCount;
		this.originalTarget = top;
		this.fraction = fraction;
	}
	
	
	public int getNewTopLine()
	{
		return newLineNumber;
	}


	public GlyphIndex getNewGlyphIndex()
	{
		return GlyphIndex.of(newGlyphIndex);
	}
	

	// in order to minimize sudden jumps due to very long lines, this method
	// looks at windowSize text lines back and forward, computes the number of wrapped rows,
	// in order to position the origin using the fraction argument.
	public void process()
	{
		int screenRowCount = vflow.getScreenRowCount();
		int windowSize = 2 * Math.max(WINDOW_SIZE, screenRowCount);
		
		// count additional rows

		int start = Math.max(originalTarget - windowSize, 0);
		int end = Math.min(originalTarget + screenRowCount + windowSize, modelLineCount);
		
		int additionalTopRows = 0;
		int additionalBottomRows = 0;
		
		for(int ix=start; ix<end; ix++)
		{
			FlowLine fline = vflow.getTextLine(ix);
			WrapInfo wr = vflow.getWrapInfo(fline);
			int additional = wr.getRowCount() - 1;
			if(additional > 0)
			{
				if(ix < originalTarget)
				{
					additionalTopRows += additional;
				}
				else
				{
					additionalBottomRows += additional;
				}
			}
		}
		
		// compute new origin
		
		int delta = CKit.round((additionalBottomRows - additionalTopRows) * fraction);
		
		log.debug("ori={%d} add.top={%d} add.bottom={%d} frac={%f} delta={%d}", originalTarget, additionalTopRows, additionalBottomRows, fraction, delta);
		
		if(delta == 0)
		{
			newLineNumber = originalTarget;
			newGlyphIndex = 0;
		}
		else
		{
			int adjustCount = Math.abs(delta);
			int step = Integer.signum(delta);
			int modelLineCount = vflow.getModelLineCount();
			int lineix = originalTarget;
			int gix = 0;
			
			while(adjustCount > 0)
			{
				FlowLine fline = vflow.getTextLine(lineix);
				WrapInfo wr = vflow.getWrapInfo(fline);
				
				int ct = wr.getRowCount();
				if(adjustCount > ct)
				{
					adjustCount -= ct;
					lineix += step;
					// next text line
					continue;
				}
				
				if(step < 0)
				{
					gix = wr.getIndexForRow(ct - adjustCount);
				}
				else
				{
					gix = wr.getIndexForRow(adjustCount);
				}
				
				break;
			}
			
			newLineNumber = lineix;
			newGlyphIndex = gix;
		}
	}
}
