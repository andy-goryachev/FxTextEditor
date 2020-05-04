// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.Marker;
import goryachev.fxtexteditor.internal.NavDirection;
import goryachev.fxtexteditor.internal.NavigationAction;


/**
 * Moves Cursor(s) one page up.
 */
public class PageUp
	extends NavigationAction
{
	public PageUp(Actions a)
	{
		super(a, NavDirection.UP);
	}
	
	
	public void action()
	{
		super.action();
	}
	

	protected Marker move(Marker m)
	{
		int pos = m.getCharIndex();
		int line = m.getLine();
		
		// TODO need the concept of last caret
		// single caret: create phantom x position, move caret + screen height
		// multiple carets: reset to a single caret using last caret, then follow the single caret logic
		
		return editor().newMarker(line, pos);
	}
}
