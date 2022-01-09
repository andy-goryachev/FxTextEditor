// Copyright © 2020-2022 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.EditorAction;
import goryachev.fxtexteditor.EditorSelection;
import goryachev.fxtexteditor.FxTextEditor;
import goryachev.fxtexteditor.Marker;
import goryachev.fxtexteditor.SelectionSegment;


/**
 * Navigation Editor Action.
 */
public abstract class NavigationAction
	extends EditorAction
{
	/** returns the new cursor position, or null to stay in place */
	protected abstract Marker move(Marker m);
	
	//
	
	public NavigationAction(FxTextEditor ed)
	{
		super(ed);
	}
	
	
	/** 
	 * returns the cursor column at the moment the movement was first initiated.
	 * sets the phantom column if it's the first move
	 */
	protected int updatePhantomColumn(int line, int charIndex)
	{
		return vflow().updatePhantomColumn(line, charIndex);
	}
	
	
	/** retains the cursor x position when navigating up/down */
	protected int getPhantomColumn()
	{
		return vflow().getPhantomColumn();
	}
	
	
	protected void setPhantomColumn(int x)
	{
		vflow().setPhantomColumn(x);
	}
	
	
	protected void setPhantomColumn(int line, int charIndex)
	{
		vflow().setPhantomColumn(line, charIndex);
	}
	
	
	/** returns the leftmost display cell index (glyph index) */
	protected int getTopCellIndex()
	{
		return vflow().getTopCellIndex();
	}
	
	
	protected void action()
	{
		move();
	}
	
	
	protected void move()
	{
		vflow().setSuppressBlink(true);
		
		try
		{
			EditorSelection sel = selection();
			SelectionSegment seg = sel.getSegment();
			if(seg == null)
			{
				return;
			}
			
			Marker from = seg.getCaret();
			Marker to = move(from);
			if(to != null)
			{
				selector().clear();
				selector().addSelectionSegment(to, to);
				selector().commitSelection();
			}
		}
		finally
		{
			vflow().setSuppressBlink(false);
			vflow().scrollCaretToView();
		}
	}
}
