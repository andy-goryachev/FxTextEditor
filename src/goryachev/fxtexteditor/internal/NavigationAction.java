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
	
	public NavigationAction(Actions a)
	{
		super(a);
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
		}
	}
}
