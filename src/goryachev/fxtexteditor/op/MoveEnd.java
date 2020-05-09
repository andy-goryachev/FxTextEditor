// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.Marker;
import goryachev.fxtexteditor.internal.NavDirection;
import goryachev.fxtexteditor.internal.NavigationAction;


/**
 * Moves cursor to the end of row.
 */
public class MoveEnd
	extends NavigationAction
{
	public MoveEnd(Actions a)
	{
		super(a, NavDirection.RIGHT);
	}
	

	protected Marker move(Marker m)
	{
		int pos = m.getCharIndex();
		int line = m.getLine();
		
		// TODO
		// if wrapped: end of row, then end of next row, ... finally end of text line.
		
		return editor().newMarker(line, pos);
	}
}
