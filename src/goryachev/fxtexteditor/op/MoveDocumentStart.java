// Copyright © 2020-2022 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.FxTextEditor;
import goryachev.fxtexteditor.Marker;
import goryachev.fxtexteditor.internal.NavigationAction;


/**
 * Moves cursor to the start of the document.
 */
public class MoveDocumentStart
	extends NavigationAction
{
	public MoveDocumentStart(FxTextEditor ed)
	{
		super(ed);
	}
	

	protected Marker move(Marker m)
	{
		editor().setOrigin(0);
		setPhantomColumn(0);
		return editor().newMarker(0, 0);
	}
}
