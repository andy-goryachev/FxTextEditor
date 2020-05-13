// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fx.FxAction;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.EditorSelection;
import goryachev.fxtexteditor.FxTextEditor;
import goryachev.fxtexteditor.FxTextEditorModel;
import goryachev.fxtexteditor.SelectionController;
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
	
	
	public boolean isWrapLines()
	{
		return vflow().isWrapLines();
	}
	
	
	public SelectionController selector()
	{
		return actions.selector();
	}
	
	
	public EditorSelection selection()
	{
		return actions.selector().getSelection();
	}
	
	
	public FxTextEditorModel model()
	{
		return actions.model();
	}
	
	
	public Markers markers()
	{
		return actions.markers();
	}
	
	
	public WrapInfo wrapInfo(int line)
	{
		return vflow().getWrapInfo(line);
	}
}
