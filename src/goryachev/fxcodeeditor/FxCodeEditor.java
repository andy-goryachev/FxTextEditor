// Copyright © 2024-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxcodeeditor;
import goryachev.fxcodeeditor.internal.SelectionModel;
import goryachev.fxcodeeditor.model.CodeModel;
import goryachev.fxtexteditor.TextPos;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;


/**
 * Fx Code Editor.
 * 
 * Supports:
 * - large virtualized models
 * - long paragraphs
 * - fixed-cell grid rendering
 * - limited text attributes
 * - limited decorations
 */
public class FxCodeEditor
	extends Control
{
	private final FxCodeEditorConfig config;
    private final ReadOnlyObjectWrapper<TextPos> anchorPosition = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<TextPos> caretPosition = new ReadOnlyObjectWrapper<>();
	private SimpleObjectProperty<CodeModel> model;
	private final SelectionModel selectionModel = new SelectionModel();


	public FxCodeEditor(FxCodeEditorConfig config, CodeModel model)
	{
		this.config = config.copy();
		setModel(model);
	}


	public final ReadOnlyProperty<TextPos> anchorPositionProperty()
	{
		return anchorPosition.getReadOnlyProperty();
	}


	public final TextPos getAnchorPosition()
	{
		return anchorPositionProperty().getValue();
	}
	
	
	public ReadOnlyProperty<TextPos> caretPositionProperty()
	{
		return caretPosition.getReadOnlyProperty();
	}
	

	public final ObjectProperty<CodeModel> modelProperty()
	{
		if(model == null)
		{
			model = new SimpleObjectProperty<>(this, "model")
			{
				@Override
				protected void invalidated()
				{
					selectionModel.clear();
				}
			};
		}
		return model;
	}


	public final void setModel(CodeModel m)
	{
		modelProperty().set(m);
	}


	public final CodeModel getModel()
	{
		return model == null ? null : model.get();
	}


	public final ReadOnlyProperty<SelectionRange> selectionProperty()
	{
		return selectionModel.selectionProperty();
	}


	public final SelectionRange getSelection()
	{
		return selectionModel.getSelection();
	}
}
