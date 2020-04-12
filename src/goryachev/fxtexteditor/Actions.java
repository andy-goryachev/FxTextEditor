// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.fx.FxAction;
import goryachev.fxtexteditor.op.MoveUp;


/**
 * Actions.
 */
public class Actions
{
	public final MoveUp moveUpAction = new MoveUp(this);
	private final FxTextEditor editor;
	
	
	public Actions(FxTextEditor ed)
	{
		this.editor = ed;
	}
	
	
	public FxTextEditor editor()
	{
		return editor;
	}
	
	
	public VFlow vflow()
	{
		return editor.vflow;
	}
}
