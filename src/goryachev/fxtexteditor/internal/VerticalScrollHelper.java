// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.util.D;
import goryachev.common.util.ElasticIntArray;
import goryachev.fxtexteditor.ITabPolicy;
import goryachev.fxtexteditor.VFlow;


/**
 * Vertical Scroll Helper.
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
	private ElasticIntArray entries;
	private int newLineNumber;
	private int newGlyphIndex;
	
	
	public VerticalScrollHelper(VFlow vflow, int frameSize, int max, int center, double fraction)
	{
		this.vflow = vflow;
		this.frameSize = frameSize;
		this.max = max;
		this.center = center;
		this.fraction = fraction;
		entries = new ElasticIntArray(frameSize * 4);
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
		entries.add(line);
		entries.add(gix.intValue());
	}
	

	public void process()
	{
		int width = vflow.getScreenColumnCount();
		ITabPolicy tabPolicy = vflow.getEditor().getTabPolicy();
		
		if(center > 0)
		{
			int start = Math.max(0, center - frameSize);
			int end = Math.min(center, start + frameSize);
			
			for(int ix=start; ix<end; ix++)
			{
				FlowLine fline = vflow.getTextLine(ix);
				WrappingReflowHelper.computeBreaks(this, tabPolicy, fline, width);
			}
		}
		
		int end = Math.min(max, center + frameSize);
		
		for(int ix=center; ix<end; ix++)
		{
			FlowLine fline = vflow.getTextLine(ix);
			WrappingReflowHelper.computeBreaks(this, tabPolicy, fline, width);
		}
		
		// TODO this is incorrect, need to adjust only for added rows
		int frameRowCount = entries.size() / 2;
		int ix = 2 * (int)(frameRowCount * fraction);
		
		newLineNumber = entries.get(ix++);
		newGlyphIndex = entries.get(ix);
		
		// FIX 
		D.print(fraction, newLineNumber, newGlyphIndex);
	}
}
