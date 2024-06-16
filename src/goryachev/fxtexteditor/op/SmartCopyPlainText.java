// Copyright © 2020-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.EditorAction;
import goryachev.fxtexteditor.FxTextEditor;


/**
 * Copies selection as plain text, or all if no selection.
 */
public class SmartCopyPlainText
	extends EditorAction
{
	public SmartCopyPlainText(FxTextEditor ed)
	{
		super(ed);
	}
	

	@Override
	protected void action()
	{
		editor().smartCopyPlainText();
	}
}
