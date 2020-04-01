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
	private final int frameSize;
	private final int max;
	private final int center;
	private final double fraction;
	private final Positions positions;
	private int newLineNumber;
	private int newGlyphIndex;
	private int additionalTopCount;
	private int additionalBottomCount;
	
	
	public VerticalScrollHelper(VFlow vflow, int frameSize, int max, int center, double fraction)
	{
		this.vflow = vflow;
		this.frameSize = frameSize;
		this.max = max;
		this.center = center;
		this.fraction = fraction;
		this.positions = new Positions(frameSize * 2);
	}
	
	
	public int getNewTopLine()
	{
		return newLineNumber;
	}


	public GlyphIndex getNewGlyphIndex()
	{
		return GlyphIndex.of(newGlyphIndex);
	}
	
	
	public void addEntry(int line, GlyphIndex gix)
	{
		positions.add(line, gix.intValue());
	}
	
	
	// TODO think of a better name
	public int getExtraRowCount()
	{
		return additionalBottomCount + additionalTopCount;
	}
	

	public void process()
	{
		int width = vflow.getScreenColumnCount();
		ITabPolicy tabPolicy = vflow.getEditor().getTabPolicy();

		int topSize = 0;
		
		if(center > 0)
		{
			int start = Math.max(0, center - frameSize);
			int end = Math.min(center, start + frameSize);
			topSize = end - start;
			
			for(int ix=start; ix<end; ix++)
			{
				FlowLine fline = vflow.getTextLine(ix);
				WrappingReflowHelper.computeBreaks(this, tabPolicy, fline, width);
			}
		}

		additionalTopCount  = positions.size() - topSize;
		int end = Math.min(max, center + frameSize);
		int bottomSize = end - center;
		boolean hasLastLine = (end == max);
		
		for(int ix=center; ix<=end; ix++)
		{
			FlowLine fline = vflow.getTextLine(ix);
			WrappingReflowHelper.computeBreaks(this, tabPolicy, fline, width);
		}
		
		additionalBottomCount = positions.size() - topSize - bottomSize;
		
		// the new scroll position is center + delta
		int delta = (int)((additionalBottomCount + additionalTopCount) * fraction) - additionalTopCount;
		int ix = topSize + delta;
		
		if(hasLastLine)
		{
			ix = Math.min(ix, positions.size() - vflow.getScreenRowCount() + 1);
			if(ix < 0)
			{
				ix = 0;
			}
		}
		
		newLineNumber = positions.lineNumberAt(ix);
		newGlyphIndex = positions.gyphIndexAt(ix);
		
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
	
	
	//
	
	
	protected static class Positions
	{
		private ElasticIntArray entries;
		
		
		public Positions(int size)
		{
			entries = new ElasticIntArray(size * 2);
		}
		

		public void add(int line, int gix)
		{
			entries.add(line);
			entries.add(gix);
		}
		
		
		public int size()
		{
			return entries.size() / 2;
		}
		
		
		public int lineNumberAt(int ix)
		{
			return entries.get(ix * 2);
		}


		public int gyphIndexAt(int ix)
		{
			return entries.get(ix * 2 + 1);
		}
	}
}
