// Copyright © 2020-2021 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.internal.EditorAction;


/**
 * Copies selection, or all if selection is empty.
 */
public class SmartCopy
	extends EditorAction
{
	public SmartCopy(Actions a)
	{
		super(a);
	}
	

	protected void action()
	{
		editor().smartCopy();
	}
}
