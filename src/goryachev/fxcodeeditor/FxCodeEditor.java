// Copyright © 2024-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxcodeeditor;
import goryachev.fxcodeeditor.internal.Defaults;
import goryachev.fxcodeeditor.internal.SelectionModel;
import goryachev.fxcodeeditor.model.CodeModel;
import goryachev.fxcodeeditor.skin.FxCodeEditorSkin;
import goryachev.fxtexteditor.TextPos;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.css.StyleablePropertyFactory;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.text.Font;


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
	private final Config config;
    private final ReadOnlyObjectWrapper<TextPos> anchorPosition = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<TextPos> caretPosition = new ReadOnlyObjectWrapper<>();
	private SimpleObjectProperty<CodeModel> model;
	private final SelectionModel selectionModel = new SelectionModel();
	// styleable properties are not created lazily
	private static final StyleablePropertyFactory<FxCodeEditor> SPF = new StyleablePropertyFactory<>(Control.getClassCssMetaData());
	private final StyleableProperty<Insets> contentPadding = SPF.createStyleableInsetsProperty(this, "contentPadding", "-ag-content-padding", (c) -> c.contentPadding, Defaults.CONTENT_PADDING);
	private final StyleableProperty<Font> font = SPF.createStyleableFontProperty(this, "font", "-ag-font", (c) -> c.font, Defaults.FONT);
	private final StyleableProperty<Boolean> wrapText = SPF.createStyleableBooleanProperty(this, "wrapText", "-ag-wrap-text", (c) -> c.wrapText, Defaults.WRAP_TEXT);


	public FxCodeEditor(Config config, CodeModel model)
	{
		this.config = config.copy();
		setModel(model);
	}
	

	public FxCodeEditor(CodeModel model)
	{
		this(Config.getDefault(), model);
	}


	@Override
	protected FxCodeEditorSkin createDefaultSkin()
	{
		return new FxCodeEditorSkin(this);
	}


	@Override
	public List<CssMetaData<? extends Styleable,?>> getControlCssMetaData()
	{
		return SPF.getCssMetaData();
	}


	public final ReadOnlyProperty<TextPos> anchorPositionProperty()
	{
		return anchorPosition.getReadOnlyProperty();
	}


	public final TextPos getAnchorPosition()
	{
		return anchorPositionProperty().getValue();
	}
	
	
	public final ReadOnlyProperty<TextPos> caretPositionProperty()
	{
		return caretPosition.getReadOnlyProperty();
	}
	
	
	public final ObservableValue<Insets> contentPaddingProperty()
	{
		return (ObservableValue<Insets>)contentPadding;
	}


	public final Insets getContentPadding()
	{
		return contentPadding.getValue();
	}


	public final void setContentPadding(Insets v)
	{
		contentPadding.setValue(v);
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


	public final ObservableValue<Boolean> wrapTextProperty()
	{
		return (ObservableValue<Boolean>)wrapText;
	}


	public final boolean isWrapText()
	{
		return wrapText.getValue();
	}


	public final void setWrapText(boolean on)
	{
		wrapText.setValue(on);
	}
}
