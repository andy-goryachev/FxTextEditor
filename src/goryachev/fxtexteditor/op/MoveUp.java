// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.common.util.CList;
import goryachev.common.util.CSet;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.EditorSelection;
import goryachev.fxtexteditor.Marker;
import goryachev.fxtexteditor.SelectionSegment;


/**
 * Moves Cursor(s) Up.
 */
public class MoveUp
	extends EditorAction
{
	public MoveUp(Actions a)
	{
		super(a);
	}
	
	
	protected void action()
	{
		moveUp();
	}
	
	
	public void moveUp()
	{
		vflow().setSuppressBlink(true);
		
		EditorSelection sel = editor().getSelection();
		int sz = sel.getSegmentCount();
		// TODO is this necessary?
		CSet<Marker> dup = null;
		CList<SelectionSegment> ss = new CList(sz);
		
		for(SelectionSegment seg: sel)
		{
			Marker from = seg.getCaret();
			Marker to = moveUp(from);
			
			if(sz > 1)
			{
				if(dup == null)
				{
					dup = new CSet();
				}
				
				if(dup.add(to))
				{
					// duplicate, skip
					continue;
				}
			}
			
			ss.add(new SelectionSegment(to, to));
		}
		
		// set new selection
		selector().clear();
		for(SelectionSegment seg: ss)
		{
			selector().addSelectionSegment(seg);
		}
		selector().commitSelection();
		
		// TODO
		// - is phantom carets present? create if not
		// - for all carets: move up
		// - check if any carets collide and need to be removed
		// - check if scroll is needed
		
		vflow().setSuppressBlink(false);
	}


	protected Marker moveUp(Marker m)
	{
		int pos = m.getCharIndex();
		if(pos > 0)
		{
			pos--;
		}
		
		int line = m.getLine();
		
		return editor().newMarker(line, pos);
	}
}
