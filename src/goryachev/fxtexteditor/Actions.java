// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
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
	public final Backspace backspace = new Backspace(this);
	public final Copy copy = new Copy(this);
	public final Delete delete = new Delete(this);
	public final MoveDocumentEnd moveDocumentEnd = new MoveDocumentEnd(this);
	public final MoveDocumentStart moveDocumentStart = new MoveDocumentStart(this);
	public final MoveDown moveDown = new MoveDown(this);
	public final MoveEnd moveEnd = new MoveEnd(this);
	public final MoveHome moveHome = new MoveHome(this);
	public final MoveLeft moveLeft = new MoveLeft(this);
	public final MoveRight moveRight = new MoveRight(this);
	public final MoveUp moveUp = new MoveUp(this);
	public final PageDown pageDown = new PageDown(this);
	public final PageUp pageUp = new PageUp(this);
	public final SelectAll selectAll = new SelectAll(this);
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
