// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.Marker;
import goryachev.fxtexteditor.internal.EditorAction;


/**
 * Select All Action.
 */
public class SelectAll
	extends EditorAction
{
	public SelectAll(Actions a)
	{
		super(a);
	}


	protected void action()
	{
		int ix = editor().getLineCount();
		if(ix > 0)
		{
			--ix;
			
			String s = model().getPlainText(ix);
			Marker beg = markers().newMarker(0, 0);
			Marker end = markers().newMarker(ix, Math.max(0, s.length()));
			
			selector().setSelection(beg, end);
			selector().commitSelection();
			vflow().scrollSelectionToVisible();
		}
	}
}
