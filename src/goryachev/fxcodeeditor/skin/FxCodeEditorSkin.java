// Copyright © 2024-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxcodeeditor.skin;
import goryachev.fxcodeeditor.FxCodeEditor;
import goryachev.fxcodeeditor.internal.FxCodeEditorBehavior;
import goryachev.fxtexteditor.VFlow;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SkinBase;
import javafx.scene.input.ScrollEvent;


/**
 * FxCodeEditor Skin.
 */
public class FxCodeEditorSkin
	extends SkinBase<FxCodeEditor>
{
	private final ScrollBar vscroll;
	private final ScrollBar hscroll;
	private final CellGrid grid;
	private final FxCodeEditorBehavior behavior;


	public FxCodeEditorSkin(FxCodeEditor ed)
	{
		super(ed);

		vscroll = createVScrollBar();
		vscroll.setOrientation(Orientation.VERTICAL);
		vscroll.addEventFilter(ScrollEvent.ANY, (ev) -> ev.consume());

		hscroll = createHScrollBar();
		hscroll.setOrientation(Orientation.HORIZONTAL);
		hscroll.addEventFilter(ScrollEvent.ANY, (ev) -> ev.consume());

		grid = new CellGrid(this, vscroll, hscroll);
		getChildren().add(grid);

		behavior = new FxCodeEditorBehavior(ed);
	}


	protected ScrollBar createVScrollBar()
	{
		return new ScrollBar();
	}


	protected ScrollBar createHScrollBar()
	{
		return new ScrollBar();
	}
}
