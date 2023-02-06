// Copyright © 2017-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.fx.FxObject;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;


/**
 * FxTextEditor Selection Controller.
 */
public class SelectionController
{
	private final FxObject<SelectionSegment> segment =  new FxObject(null);
	private final FxObject<EditorSelection> selectionProperty = new FxObject(EditorSelection.EMPTY);
	private Marker anchor;
	private SelectionSegment originalSelection;


	public SelectionController()
	{
	}
	
	
	public ReadOnlyObjectProperty<EditorSelection> selectionProperty()
	{
		return selectionProperty.getReadOnlyProperty();
	}
	
	
	/** non-null */
	public EditorSelection getSelection()
	{
		return selectionProperty.get();
	}


	public void clear()
	{
		segment.set(null);
		originalSelection = null;
	}
	
	
	/** returns true if marker is inside of any selection segment */
	public boolean isSelected(Marker pos)
	{
		SelectionSegment s = segment.get();
		if(s != null)
		{
			if(s.contains(pos))
			{
				return true;
			}
		}
		return false;
	}
	

	public boolean isSelected(int line, int pos)
	{
		SelectionSegment s = segment.get();
		if(s != null)
		{
			if(s.contains(line, pos))
			{
				return true;
			}
		}
		return false;
	}
	
	
	public boolean isCaretLine(int line)
	{
		if(line >= 0)
		{
			SelectionSegment s = segment.get();
			if(s != null)
			{
				if(s.isCaretLine(line))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	
	public void setSelection(Marker anchor, Marker caret)
	{
		clear();
		addSelectionSegment(anchor, caret);
	}
	

	public void addSelectionSegment(Marker anchor, Marker caret)
	{
		addSelectionSegment(new SelectionSegment(anchor, caret));
	}
	
	
	public void addSelectionSegment(SelectionSegment seg)
	{
		mergeSegments(seg);
		originalSelection = null;
	}
	
	
	public void setSelection(Marker m)
	{
		setSelection(m, m);
	}
	
	
	public void clearAndExtendLastSegment(Marker pos)
	{
		if(anchor == null)
		{
			anchor = pos;
		}
		
		setSelection(anchor, pos);
	}
	
	
	public void setAnchor(Marker p)
	{
		anchor = p;
		originalSelection = segment.get();
	}
	
	
	/** 
	 * extends the new selection segment from the anchor point to the specified position,
	 * updating the segments list such that it remains to be ordered and the segments do not overlap each other
	 */
	public void extendLastSegment(Marker pos)
	{
		if(anchor == null)
		{
			anchor = pos;
		}
		
		SelectionSegment seg = new SelectionSegment(anchor, pos);
		mergeSegments(seg);
	}
	
	
	public ReadOnlyProperty<SelectionSegment> selectionSegmentProperty()
	{
		return segment.getReadOnlyProperty();
	}
	
	
	public SelectionSegment getSelectedSegment()
	{
		return segment.get();
	}
	
	
	protected void mergeSegments(SelectionSegment seg)
	{
		if(seg == null)
		{
			return;
		}
		
		if(originalSelection == null)
		{
			originalSelection = segment.get();
		}
		
		// merge last segment and original selection to produce ordered, non-overlapping segments
		SelectionSegment rv;
		if(originalSelection == null)
		{
			rv = seg;
		}
		else if(seg.overlaps(originalSelection))
		{
			rv = seg.merge(originalSelection);
		}
		else
		{
			rv = seg;
		}
		
		segment.set(rv);
	}
	

	/** called at the end of drag gesture to clear transient values and update the selection property */
	public void commitSelection()
	{
		originalSelection = null;
		
		EditorSelection es = new EditorSelection(segment.get());
		selectionProperty.set(es);
	}


	/** when markers change, so should the selection */
	public void refresh()
	{
		SelectionSegment seg = getSelectedSegment();
		if(seg != null)
		{
			seg = seg.copy();
			segment.set(seg);
			selectionProperty.set(new EditorSelection(seg));
		}
	}
}
