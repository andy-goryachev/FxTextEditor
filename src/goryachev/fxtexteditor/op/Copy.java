// Copyright © 2020-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.EditorAction;
import goryachev.fxtexteditor.FxTextEditor;


/**
 * Copies selection.
 */
public class Copy
	extends EditorAction
{
	public Copy(FxTextEditor ed)
	{
		super(ed);
	}
	

	protected void action()
	{
		editor().copy();
	}
}
