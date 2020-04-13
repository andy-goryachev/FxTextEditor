// Copyright © 2017-2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.util.CKit;
import java.util.Iterator;


/**
 * An immutable object that represents text selection within FxEditor.
 * Segments are guaranteed to be non-overlapping and ordered from top to bottom.
 */
public class EditorSelection
	implements Iterable<SelectionSegment>
{
	public static final EditorSelection EMPTY = createEmpty();
	private final SelectionSegment[] segments;
	
	
	public EditorSelection(SelectionSegment[] segments)
	{
		this.segments = segments;
		
		// TODO remove this check later
		check();
	}
	
	
	public String toString()
	{
		return CKit.toString(segments);
	}
	
	
	private void check()
	{
		SelectionSegment prev = null;
		for(SelectionSegment s: segments)
		{
			if(prev != null)
			{
				if(!prev.isBefore(s))
				{
					throw new Error("selection is not ordered " + this);
				}
			}
			prev = s;
		}
	}
	
	
	private static EditorSelection createEmpty()
	{
		return new EditorSelection(new SelectionSegment[] { new SelectionSegment(Marker.ZERO, Marker.ZERO, false) });
	}

	
	/** returns original segment array */
	public SelectionSegment[] getSegments()
	{
		return segments;
	}
	
	
	/** returns last or the only selection segment */
	public SelectionSegment getSegment()
	{
		if(segments.length == 0)
		{
			return null;
		}
		return segments[segments.length - 1];
	}
	
	
	public int getSegmentCount()
	{
		return segments.length;
	}
	
	
	public boolean isEmpty()
	{
		for(SelectionSegment s: segments)
		{
			if(!s.isEmpty())
			{
				return false;
			}
		}
		return false;
	}
	
	
	public boolean isNotEmpty()
	{
		return !isEmpty();
	}
	
	
	public boolean hasMultipleSegments()
	{
		return segments.length > 1;
	}


	public EditorSelection getSelection()
	{
		return new EditorSelection(segments);
	}
	
	
	public Marker getLastCaret()
	{
		if(isEmpty())
		{
			return null;
		}
		
		return segments[segments.length - 1].getCaret();
	}


	public Iterator<SelectionSegment> iterator()
	{
		return CKit.iterator(segments);
	}
}
