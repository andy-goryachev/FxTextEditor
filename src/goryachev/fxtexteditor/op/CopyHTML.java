// Copyright © 2020-2022 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.EditorAction;
import goryachev.fxtexteditor.FxTextEditor;


/**
 * Copies selection as HTML.
 */
public class CopyHTML
	extends EditorAction
{
	public CopyHTML(FxTextEditor ed)
	{
		super(ed);
	}
	

	protected void action()
	{
		editor().copyHTML();
	}
}
