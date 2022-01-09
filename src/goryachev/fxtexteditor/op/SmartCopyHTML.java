// Copyright © 2020-2022 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.EditorAction;
import goryachev.fxtexteditor.FxTextEditor;


/**
 * Copies selection as HTML (all if no selection).
 */
public class SmartCopyHTML
	extends EditorAction
{
	public SmartCopyHTML(FxTextEditor ed)
	{
		super(ed);
	}
	

	protected void action()
	{
		editor().smartCopyHTML();
	}
}
