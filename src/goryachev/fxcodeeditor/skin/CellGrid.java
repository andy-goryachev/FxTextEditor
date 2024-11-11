// Copyright © 2024-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxcodeeditor.skin;
import goryachev.fx.FX;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.Pane;


/**
 * Cell Grid.
 * 
 * Contains:
 * - canvas
 * - vertical and horizontal scroll bars
 */
public class CellGrid
	extends Pane
{
	private final FxCodeEditorSkin skin;
	private final ScrollBar vscroll;
	private final ScrollBar hscroll;


	public CellGrid(FxCodeEditorSkin skin, ScrollBar vscroll, ScrollBar hscroll)
	{
		this.skin = skin;
		this.vscroll = configureScrollBar(vscroll);
		this.hscroll = configureScrollBar(hscroll);

		getChildren().addAll(vscroll, hscroll);
		
		// TODO paragraph cache
		
		FX.addInvalidationListener(widthProperty(), this::handleWidthChange);
		FX.addInvalidationListener(heightProperty(), this::handleHeightChange);
	}


	private static ScrollBar configureScrollBar(ScrollBar b)
	{
		b.setManaged(false);
		b.setMin(0.0);
		b.setMax(1.0);
		b.setUnitIncrement(0.01);
		b.setBlockIncrement(0.05);
		return b;
	}
	
	
	void handleWidthChange()
	{
		// TODO scroll horizontally
		requestLayout();
	}
	
	
	void handleHeightChange()
	{
		requestLayout();
	}


	@Override
	protected void layoutChildren()
	{
		// TODO two separate steps:
		// 1. compute layout (check if canvas needs to be re-created, origin, scroll bars, ...)
		// 2. paint the canvas
	}
}
