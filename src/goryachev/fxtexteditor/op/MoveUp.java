// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.Actions;


/**
 * Move Up.
 */
public class MoveUp
	extends EditorAction
{
	public MoveUp(Actions a)
	{
		super(a);
	}
	
	
	protected void action()
	{
		moveUp();
	}
	
	
	public void moveUp()
	{
		editor();
		vflow();
		// TODO
		// - is phantom carets present?
		// - for all carets: move up
		// - check if any carets collide and need to be removed
		// - check if scroll is needed
	}
}
