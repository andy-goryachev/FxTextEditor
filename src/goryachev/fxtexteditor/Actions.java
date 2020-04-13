// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.fxtexteditor.op.MoveDown;
import goryachev.fxtexteditor.op.MoveLeft;
import goryachev.fxtexteditor.op.MoveRight;
import goryachev.fxtexteditor.op.MoveUp;


/**
 * Actions.
 */
public class Actions
{
	public final MoveDown moveDown = new MoveDown(this);
	public final MoveLeft moveLeft = new MoveLeft(this);
	public final MoveRight moveRight = new MoveRight(this);
	public final MoveUp moveUp = new MoveUp(this);
	//
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
	
	
	public SelectionController selector()
	{
		return editor.selector;
	}
}
