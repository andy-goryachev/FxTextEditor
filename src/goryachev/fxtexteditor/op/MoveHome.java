// Copyright © 2020-2021 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.Marker;
import goryachev.fxtexteditor.internal.NavigationAction;


/**
 * Moves the cursor to the beginning of the current line.
 */
public class MoveHome
	extends NavigationAction
{
	public MoveHome(Actions a)
	{
		super(a);
	}
	

	protected Marker move(Marker m)
	{
		int line = m.getLine();
		int pos = 0;
		setPhantomColumn(line, pos);
		return editor().newMarker(line, pos);
	}
}
