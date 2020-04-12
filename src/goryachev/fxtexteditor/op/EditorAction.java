// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fx.FxAction;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.FxTextEditor;
import goryachev.fxtexteditor.VFlow;


/**
 * Editor Action.
 */
public abstract class EditorAction
	extends FxAction
{
	protected abstract void action();
	
	//
	
	protected final Actions actions;

	
	public EditorAction(Actions a)
	{
		actions = a;
		setOnAction(this::action);
	}
	
	
	public FxTextEditor editor()
	{
		return actions.editor();
	}
	
	
	public VFlow vflow()
	{
		return actions.vflow();
	}
}
