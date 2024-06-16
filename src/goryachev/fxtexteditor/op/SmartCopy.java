// Copyright © 2020-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.EditorAction;
import goryachev.fxtexteditor.FxTextEditor;


/**
 * Copies selection, or all if selection is empty.
 */
public class SmartCopy
	extends EditorAction
{
	public SmartCopy(FxTextEditor ed)
	{
		super(ed);
	}
	

	@Override
	protected void action()
	{
		editor().smartCopy();
	}
}
