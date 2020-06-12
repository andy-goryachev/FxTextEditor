// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.internal.EditorAction;


/**
 * Copies selection as plain text.
 */
public class CopyPlainText
	extends EditorAction
{
	public CopyPlainText(Actions a)
	{
		super(a);
	}
	

	protected void action()
	{
		editor().copyPlainText();
	}
}
