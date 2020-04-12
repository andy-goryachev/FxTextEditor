// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fx.FxAction;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.FxTextEditor;
import goryachev.fxtexteditor.VFlow;


/**
 * Editor Action.
 */
public class EditorAction
	extends FxAction
{
	protected final FxTextEditor editor;
	protected final VFlow vflow;

	
	public EditorAction(Actions a)
	{
		editor = a.editor();
		vflow = a.vflow();
	}
	
	
	public FxTextEditor editor()
	{
		return editor;
	}
	
	
	public VFlow vflow()
	{
		return vflow;
	}
}
