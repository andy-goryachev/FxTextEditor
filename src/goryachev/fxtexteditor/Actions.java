// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.fx.FxAction;
import goryachev.fxtexteditor.internal.Markers;
import goryachev.fxtexteditor.op.Backspace;
import goryachev.fxtexteditor.op.Copy;
import goryachev.fxtexteditor.op.Delete;
import goryachev.fxtexteditor.op.MoveDocumentEnd;
import goryachev.fxtexteditor.op.MoveDocumentStart;
import goryachev.fxtexteditor.op.MoveDown;
import goryachev.fxtexteditor.op.MoveEnd;
import goryachev.fxtexteditor.op.MoveHome;
import goryachev.fxtexteditor.op.MoveLeft;
import goryachev.fxtexteditor.op.MoveRight;
import goryachev.fxtexteditor.op.MoveUp;
import goryachev.fxtexteditor.op.PageDown;
import goryachev.fxtexteditor.op.PageUp;
import goryachev.fxtexteditor.op.SelectAll;


/**
 * Built-in Actions.
 */
public class Actions
{
	public FxAction backspace = new Backspace(this);
	public FxAction copy = new Copy(this);
	public FxAction delete = new Delete(this);
	public FxAction moveDocumentEnd = new MoveDocumentEnd(this);
	public FxAction moveDocumentStart = new MoveDocumentStart(this);
	public FxAction moveDown = new MoveDown(this);
	public FxAction moveEnd = new MoveEnd(this);
	public FxAction moveHome = new MoveHome(this);
	public FxAction moveLeft = new MoveLeft(this);
	public FxAction moveRight = new MoveRight(this);
	public FxAction moveUp = new MoveUp(this);
	public FxAction pageDown = new PageDown(this);
	public FxAction pageUp = new PageUp(this);
	public FxAction selectAll = new SelectAll(this);
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
	
	
	public FxTextEditorModel model()
	{
		return editor.getModel();
	}
	
	
	public Markers markers()
	{
		return editor.markers;
	}
}
