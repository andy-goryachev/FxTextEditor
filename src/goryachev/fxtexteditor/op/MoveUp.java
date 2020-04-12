// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.FxTextEditor;


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
