// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.Marker;
import goryachev.fxtexteditor.internal.NavDirection;
import goryachev.fxtexteditor.internal.NavigationAction;


/**
 * Moves Cursor(s) Down.
 */
public class MoveDown
	extends NavigationAction
{
	public MoveDown(Actions a)
	{
		super(a, NavDirection.DOWN);
	}
	

	protected Marker move(Marker m)
	{
		int pos = m.getCharIndex();
		int line = m.getLine();
		
		// TODO
//		int pos = m.getCharIndex();
//		if(pos > 0)
//		{
//			pos--;
//		}
//		
//		int line = m.getLine();
		
		return editor().newMarker(line, pos);
	}
}
