// Copyright © 2024-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxcodeeditor.internal;
import goryachev.fx.FX;
import goryachev.fxcodeeditor.skin.FxCodeEditorSkin;
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
	
	
	private LayoutInfo computeLayout()
	{
		// TODO
		return new LayoutInfo();
	}


	@Override
	protected void layoutChildren()
	{
		// TODO two separate steps:
		// 1. compute layout (check if canvas needs to be re-created, origin, scroll bars, ...)
		//    may need to bail out and repeat if the scroll bar visibility changed and the layout needs to be recomputed
		// 2. paint the canvas

		double width = getWidth();
		if(width == 0.0)
		{
			return;
		}
		
		double vsbWidth = vscroll.isVisible() ? 0.0 : vscroll.prefWidth(-1);
		double hsbHeight = hscroll.isVisible() ? 0.0 : hscroll.prefHeight(-1);

		// TODO compute geometry in order to determine whether any of the properties (scroll bars, origin) need to be changed
		// if so, change them and bail out early.  changing any of the properties results in another layout request.
		// 
		LayoutInfo la = computeLayout();
		
		boolean vsb = true;
		boolean hsb = true;

		if(vsb != vscroll.isVisible())
		{
			// causes another layout pass
			vscroll.setVisible(vsb);
			return;
		}

		if(hsb != hscroll.isVisible())
		{
			// causes another layout pass
			hscroll.setVisible(hsb);
			return;
		}
		
		// geometry is fine at this point
		// TODO recreate the canvas if necessary
		// TODO repaint damaged areas on the canvas

		if(vsb)
		{
			//layoutInArea();
		}
		
		if(hsb)
		{
			// layout hscroll
		}
	}
}
