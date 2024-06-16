// Copyright © 2020-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.EditorAction;
import goryachev.fxtexteditor.FxTextEditor;


/**
 * Copies selection as RTF.
 */
public class CopyRTF
	extends EditorAction
{
	public CopyRTF(FxTextEditor ed)
	{
		super(ed);
	}
	

	@Override
	protected void action()
	{
		editor().copyRTF();
	}
}
