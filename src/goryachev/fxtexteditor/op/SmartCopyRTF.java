// Copyright © 2020-2021 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.internal.EditorAction;


/**
 * Copies selection as RTF, or all if no selection.
 */
public class SmartCopyRTF
	extends EditorAction
{
	public SmartCopyRTF(Actions a)
	{
		super(a);
	}
	

	protected void action()
	{
		editor().smartCopyRTF();
	}
}
