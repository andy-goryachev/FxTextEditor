// Copyright © 2020-2021 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.internal.EditorAction;


/**
 * Copies selection as plain text, or all if no selection.
 */
public class SmartCopyPlainText
	extends EditorAction
{
	public SmartCopyPlainText(Actions a)
	{
		super(a);
	}
	

	protected void action()
	{
		editor().smartCopyPlainText();
	}
}
