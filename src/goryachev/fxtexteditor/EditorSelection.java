// Copyright © 2017-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.util.CKit;


/**
 * An immutable object that represents text selection within FxEditor.
 * FxTextEditor supports neither multiple carets nor multiple selection segments.
 */
public class EditorSelection
{
	public static final EditorSelection EMPTY = createEmpty();
	private final SelectionSegment segment;
	
	
	public EditorSelection(SelectionSegment segment)
	{
		this.segment = segment;
	}
	
	
	public String toString()
	{
		return CKit.toStringOrNull(segment);
	}
	
	
	private static EditorSelection createEmpty()
	{
		return new EditorSelection(new SelectionSegment(Marker.ZERO, Marker.ZERO, false));
	}

	
	/** returns original segment array */
	public SelectionSegment getSegment()
	{
		return segment;
	}
	
	
	public boolean isEmpty()
	{
		return (segment == null);
	}
	
	
	public boolean isNotEmpty()
	{
		return !isEmpty();
	}
	

	public EditorSelection getSelection()
	{
		return new EditorSelection(segment);
	}
	
	
	public Marker getCaret()
	{
		if(isEmpty())
		{
			return null;
		}
		
		return segment.getCaret();
	}
	
	
	public Marker getAnchor()
	{
		if(isEmpty())
		{
			return null;
		}
		
		return segment.getAnchor();
	}
}
