// Copyright © 2024-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxcodeeditor.skin;
import goryachev.fx.FX;
import goryachev.fx.FxDisconnector;
import goryachev.fxcodeeditor.CodePad;
import goryachev.fxcodeeditor.internal.CellGrid;
import goryachev.fxcodeeditor.internal.Defaults;
import goryachev.fxcodeeditor.internal.CodePadBehavior;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SkinBase;
import javafx.scene.input.ScrollEvent;


/**
 * FxCodeEditor Skin.
 */
public class FxCodeEditorSkin
	extends SkinBase<CodePad>
{
	private final ScrollBar vscroll;
	private final ScrollBar hscroll;
	private final CellGrid grid;
	private final CodePadBehavior behavior;
	private FxDisconnector disconnector;


	public FxCodeEditorSkin(CodePad ed)
	{
		super(ed);

		vscroll = createVScrollBar();
		vscroll.setOrientation(Orientation.VERTICAL);
		FX.consumeAllEvents(ScrollEvent.ANY, vscroll);

		hscroll = createHScrollBar();
		hscroll.setOrientation(Orientation.HORIZONTAL);
		FX.consumeAllEvents(ScrollEvent.ANY, hscroll);

		grid = new CellGrid(this, vscroll, hscroll);
		getChildren().add(grid);

		behavior = new CodePadBehavior(ed);
		
		disconnector = new FxDisconnector();
		disconnector.addChangeListener(ed.fontProperty(), true, grid::setFont);
	}
	
	
	/**
	 * Subclasses can override this method to provide a custom vertical scroll bar.
	 */
	protected ScrollBar createVScrollBar()
	{
		return new ScrollBar();
	}


	/**
	 * Subclasses can override this method to provide a custom horizontal scroll bar.
	 */
	protected ScrollBar createHScrollBar()
	{
		return new ScrollBar();
	}


	@Override
	public void dispose()
	{
		if(disconnector != null)
		{
			disconnector.disconnect();
			disconnector = null;
		}

		super.dispose();
	}


	@Override
	protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset)
	{
		return Defaults.PREF_HEIGHT;
	}


	@Override
	protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset)
	{
		return Defaults.PREF_WIDTH;
	}


	@Override
	protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset)
	{
		return Defaults.MIN_HEIGHT;
	}


	@Override
	protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset)
	{
		return Defaults.MIN_WIDTH;
	}
}
