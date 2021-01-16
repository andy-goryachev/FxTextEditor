// Copyright © 2020-2021 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.internal.EditorAction;


/**
 * Copies selection as HTML (all if no selection).
 */
public class SmartCopyHTML
	extends EditorAction
{
	public SmartCopyHTML(Actions a)
	{
		super(a);
	}
	

	protected void action()
	{
		editor().smartCopyHTML();
	}
}
