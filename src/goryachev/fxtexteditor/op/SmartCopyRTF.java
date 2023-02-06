// Copyright © 2020-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.EditorAction;
import goryachev.fxtexteditor.FxTextEditor;


/**
 * Copies selection as RTF, or all if no selection.
 */
public class SmartCopyRTF
	extends EditorAction
{
	public SmartCopyRTF(FxTextEditor ed)
	{
		super(ed);
	}
	

	protected void action()
	{
		editor().smartCopyRTF();
	}
}
