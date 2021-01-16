// Copyright © 2020-2021 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.internal.EditorAction;


/**
 * Copies selection as HTML.
 */
public class CopyHTML
	extends EditorAction
{
	public CopyHTML(Actions a)
	{
		super(a);
	}
	

	protected void action()
	{
		editor().copyHTML();
	}
}
