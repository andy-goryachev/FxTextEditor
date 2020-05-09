// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.EditorSelection;
import goryachev.fxtexteditor.Marker;
import goryachev.fxtexteditor.SelectionSegment;


/**
 * Navigation Editor Action.
 */
public abstract class NavigationAction
	extends EditorAction
{
	protected abstract Marker move(Marker m);
	
	//
	
	protected final NavDirection direction;
	private int phantomPosition = -1;


	public NavigationAction(Actions a, NavDirection dir)
	{
		super(a);
		this.direction = dir;
	}
	
	
	/** retains the cursor x position when navigating up/down */
	protected int getPhantomPosition()
	{
		return phantomPosition;
	}
	
	
	protected void setPhantomPosition(int x)
	{
		phantomPosition = x;
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
			
			selector().clear();

			Marker from = seg.getCaret();
			Marker to = move(from);
			
			selector().addSelectionSegment(to, to);
			selector().commitSelection();
		}
		finally
		{
			vflow().setSuppressBlink(false);
			vflow().scrollSelectionToVisible(direction);
		}
	}
}
