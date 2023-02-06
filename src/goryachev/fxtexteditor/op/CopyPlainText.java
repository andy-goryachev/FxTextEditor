// Copyright © 2020-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.EditorAction;
import goryachev.fxtexteditor.FxTextEditor;


/**
 * Copies selection as plain text.
 */
public class CopyPlainText
	extends EditorAction
{
	public CopyPlainText(FxTextEditor ed)
	{
		super(ed);
	}
	

	protected void action()
	{
		editor().copyPlainText();
	}
}
