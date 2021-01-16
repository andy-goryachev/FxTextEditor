// Copyright © 2020-2021 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.Marker;
import goryachev.fxtexteditor.internal.NavigationAction;


/**
 * Moves the cursor to the end of the current text line.
 */
public class MoveEnd
	extends NavigationAction
{
	public MoveEnd(Actions a)
	{
		super(a);
	}
	

	protected Marker move(Marker m)
	{
		int line = m.getLine();
		int pos = editor().getTextLength(line);
		setPhantomColumn(line, pos);
		return editor().newMarker(line, pos);
	}
}
