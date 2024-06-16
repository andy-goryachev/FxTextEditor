// Copyright © 2020-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.EditorAction;
import goryachev.fxtexteditor.FxTextEditor;
import goryachev.fxtexteditor.Marker;


/**
 * Select All Action.
 */
public class SelectAll
	extends EditorAction
{
	public SelectAll(FxTextEditor ed)
	{
		super(ed);
	}


	@Override
	protected void action()
	{
		int ix = editor().getLineCount();
		if(ix > 0)
		{
			--ix;
			
			String s = model().getPlainText(ix);
			Marker beg = markers().newMarker(0, 0);
			Marker end = markers().newMarker(ix, Math.max(0, (s == null ? 0 : s.length())));
			
			selector().setSelection(beg, end);
			selector().commitSelection();
			vflow().scrollCaretToView();
		}
	}
}
