// Copyright © 2019-2020 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import goryachev.common.util.CList;
import goryachev.fxtexteditor.CellStyles;


/**
 * Text Line Attributes.
 */
public class TAttributes
{
	private CList<TSegment> segments;
	
	
	public TAttributes()
	{
	}
	
	
	public int size()
	{
		return segments.size();
	}


	public void addSegment(int start, TSegment seg)
	{
		if(segments == null)
		{
			segments = new CList();
		}
		segments.add(seg);
	}
	
	
	protected TSegment segmentAt(int pos)
	{
		int low = 0;
		int high = segments.size() - 1;
		
		while(low <= high)
		{
			int mid = (low + high) >>> 1;
			TSegment seg = segments.get(mid);
			
			if(seg.end <= pos)
			{		
				low = mid + 1;
			}
			else if(seg.start > pos)
			{
				high = mid - 1;
			}
			else
			{
				return seg;
			}
		}
		
		return null;
	}


	public void update(CellStyles styles, int charIndex)
	{
		TSegment s = segmentAt(charIndex);
		if(s != null)
		{
			styles.update
			(
				s.backgroundColor, 
				s.textColor,
				s.bold,
				false, // italic
				false, // strikeThrough
				false  // s.underscore
			);
		}
	}
}
