// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.Marker;
import goryachev.fxtexteditor.internal.NavDirection;
import goryachev.fxtexteditor.internal.NavigationAction;


/**
 * Moves Cursor(s) to the beginning of row.
 */
public class MoveHome
	extends NavigationAction
{
	public MoveHome(Actions a)
	{
		super(a, NavDirection.LEFT);
	}
	

	protected Marker move(Marker m)
	{
		int pos = m.getCharIndex();
		int line = m.getLine();
		
		// TODO
		// if wrapped: row start, then prev row start, then start of text line.
		
		return editor().newMarker(line, pos);
	}
}
