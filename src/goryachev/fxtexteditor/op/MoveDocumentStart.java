// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.Marker;
import goryachev.fxtexteditor.internal.NavigationAction;


/**
 * Moves cursor to the start of the document.
 */
public class MoveDocumentStart
	extends NavigationAction
{
	public MoveDocumentStart(Actions a)
	{
		super(a);
	}
	

	protected Marker move(Marker m)
	{
		setPhantomColumn(0);
		return editor().newMarker(0, 0);
	}
}
