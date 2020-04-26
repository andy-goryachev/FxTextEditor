// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.internal.EditorAction;


/**
 * Copies selection.
 */
public class Copy
	extends EditorAction
{
	public Copy(Actions a)
	{
		super(a);
	}
	

	protected void action()
	{
		editor().doCopy();
	}
}
