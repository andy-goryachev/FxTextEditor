// Copyright © 2020-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.FxTextEditor;
import goryachev.fxtexteditor.Marker;
import goryachev.fxtexteditor.internal.FlowLine;
import goryachev.fxtexteditor.internal.NavigationAction;


/**
 * Moves cursor to the end of the document at position 0.
 */
public class MoveDocumentEndAtPos0
	extends NavigationAction
{
	public MoveDocumentEndAtPos0(FxTextEditor ed)
	{
		super(ed);
	}
	

	protected Marker move(Marker m)
	{
		int line = vflow().getModelLineCount() - 1;
		if(line < 0)
		{
			line = 0;
		}
		
		setPhantomColumn(line, 0);	
		return editor().newMarker(line, 0);
	}
}
