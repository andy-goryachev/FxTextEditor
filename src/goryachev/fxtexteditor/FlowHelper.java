// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.util.ElasticIntArray;


/**
 * Flow Helper.
 */
public class FlowHelper
{
	private final int width;
	private final IBreakIterator br;
	private int line;
	private int off;
	private final ElasticIntArray lineOffsetArray = new ElasticIntArray();
	
	
	public FlowHelper(int width, int start, IBreakIterator b)
	{
		this.width = width;
		this.line = start;
		this.br = b;
	}
	
	
	public void addLine(String text)
	{
		off = 0;
		
		lineOffsetArray.add(line++);
		lineOffsetArray.add(off);
		
		br.setText(text);

		int col = 0;
		int start = br.first();
		for(int end=br.next(); end!=IBreakIterator.DONE; start=end, end=br.next())
		{
			col++;
			off++;
			if(col >= width)
			{
				lineOffsetArray.add(line);
				lineOffsetArray.add(off);
				line++;
				col = 0;
			}
		}
	}
	
	
	public int getRowCount()
	{
		return lineOffsetArray.size() / 2;
	}
	
	
	public int getLineAt(int ix)
	{
		return lineOffsetArray.get(ix * 2);
	}
	
	
	public int getOffsetAt(int ix)
	{
		return lineOffsetArray.get(ix * 2 + 1);
	}
}
