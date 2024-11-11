// Copyright © 2024-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxcodeeditor.internal;
import goryachev.common.log.Log;
import goryachev.fx.FX;
import goryachev.fxcodeeditor.skin.FxCodeEditorSkin;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;


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
	private static final Log log = Log.get("CellGrid");
	private final FxCodeEditorSkin skin;
	private final ScrollBar vscroll;
	private final ScrollBar hscroll;
	private final SimpleObjectProperty<Origin> origin = new SimpleObjectProperty<>(Origin.ZERO);
	private Canvas canvas;
	private GraphicsContext gx;
	private FlowInfo flow;


	public CellGrid(FxCodeEditorSkin skin, ScrollBar vscroll, ScrollBar hscroll)
	{
		this.skin = skin;
		this.vscroll = configureScrollBar(vscroll);
		this.hscroll = configureScrollBar(hscroll);

		getChildren().addAll(vscroll, hscroll);
		
		// TODO paragraph cache
		
		FX.addInvalidationListener(widthProperty(), this::handleWidthChange);
		FX.addInvalidationListener(heightProperty(), this::handleHeightChange);
		FX.addInvalidationListener(scaleXProperty(), this::handleScaleChange);
		FX.addInvalidationListener(scaleYProperty(), this::handleScaleChange);
		FX.addInvalidationListener(origin, this::requestLayout);
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
	
	
	void handleScaleChange()
	{
		requestLayout();
	}
	
	
	private Canvas createCanvas()
	{
		Insets m = getInsets();
		double w = snapSizeX(getWidth() - snappedLeftInset() - snappedRightInset());
		double h = snapSizeY(getHeight() - snappedTopInset() - snappedBottomInset());
		
		log.trace("w=%.1f, h=%.1f", w, h);
		
		return new Canvas(w, h);
	}
	
	
	private FlowInfo computeLayout()
	{
		// compute:
		// - canvas size
		// - flow cells
		// - scroll bar visibility
		// TODO need origin, need LI field
		return new FlowInfo();
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
		FlowInfo f = computeLayout();
		if(f.isCanvasDifferent(flow))
		{
			if(canvas != null)
			{
				getChildren().remove(canvas);
			}
			canvas = createCanvas(); // TODO get the new size from LayoutInfo?
			gx = canvas.getGraphicsContext2D();
			
			getChildren().add(canvas);
		}
		
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
		// FIX debug
		{
			gx.setFill(Color.LIGHTGRAY);
			gx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		}
		
		double x0 = snappedLeftInset();
		double y0 = snappedTopInset();
		double cw = canvas.getWidth();
		double ch = canvas.getHeight();

		if(vsb)
		{
			layoutInArea(vscroll, x0 + cw, y0, vsbWidth, ch, 0.0, null, true, true, HPos.CENTER, VPos.CENTER);
		}
		
		if(hsb)
		{
			layoutInArea(hscroll, x0, y0 + ch, cw, hsbHeight, 0.0, null, true, true, HPos.CENTER, VPos.CENTER);
		}
		
		layoutInArea(canvas, x0, y0, cw, ch, 0.0, null, true, true, HPos.CENTER, VPos.CENTER);
	}
}
