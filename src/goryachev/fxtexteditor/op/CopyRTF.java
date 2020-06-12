// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.internal.EditorAction;


/**
 * Copies selection as RTF.
 */
public class CopyRTF
	extends EditorAction
{
	public CopyRTF(Actions a)
	{
		super(a);
	}
	

	protected void action()
	{
		editor().copyRTF();
	}
}
