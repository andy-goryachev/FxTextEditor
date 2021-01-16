// Copyright © 2020-2021 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.Marker;
import goryachev.fxtexteditor.internal.FlowLine;
import goryachev.fxtexteditor.internal.NavigationAction;


/**
 * Moves cursor to the end of the document.
 */
public class MoveDocumentEnd
	extends NavigationAction
{
	public MoveDocumentEnd(Actions a)
	{
		super(a);
	}
	

	protected Marker move(Marker m)
	{
		int line = vflow().getModelLineCount() - 1;
		if(line < 0)
		{
			line = 0;
		}
		
		FlowLine fline = vflow().getTextLine(line);
		int pos = fline.getTextLength();
		
		setPhantomColumn(line, pos);
		
		return editor().newMarker(line, pos);
	}
}
