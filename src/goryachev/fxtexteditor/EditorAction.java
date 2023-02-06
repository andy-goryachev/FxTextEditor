// Copyright © 2020-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.fx.FxAction;
import goryachev.fxtexteditor.internal.Markers;
import goryachev.fxtexteditor.internal.WrapInfo;


/**
 * Editor Action.
 */
public abstract class EditorAction
	extends FxAction
{
	protected abstract void action();
	
	//
	
	protected final FxTextEditor editor;

	
	public EditorAction(FxTextEditor ed)
	{
		this.editor = ed;
		setOnAction(this::action);
	}
	
	
	public final FxTextEditor editor()
	{
		return editor;
	}
	
	
	public final VFlow vflow()
	{
		return editor.vflow;
	}
	
	
	public final SelectionController selector()
	{
		return editor.selector;
	}
	
	
	public final FxTextEditorModel model()
	{
		return editor.getModel();
	}
	
	
	public final boolean isWrapLines()
	{
		return vflow().isWrapLines();
	}
	
	
	public final EditorSelection selection()
	{
		return editor.selector.getSelection();
	}
	
	
	public final Markers markers()
	{
		return editor.markers;
	}
	
	
	public final WrapInfo wrapInfo(int line)
	{
		return vflow().getWrapInfo(line);
	}
}
