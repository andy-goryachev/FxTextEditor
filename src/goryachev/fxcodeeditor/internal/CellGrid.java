// Copyright © 2024-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxcodeeditor.internal;
import goryachev.common.log.Log;
import goryachev.fx.FX;
import goryachev.fx.TextCellMetrics;
import goryachev.fxcodeeditor.FxCodeEditor;
import goryachev.fxcodeeditor.model.CodeModel;
import goryachev.fxcodeeditor.skin.FxCodeEditorSkin;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


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
	private final FxCodeEditor editor;
	private final ScrollBar vscroll;
	private final ScrollBar hscroll;
	private final SimpleObjectProperty<Origin> origin = new SimpleObjectProperty<>(Origin.ZERO);
	private Canvas canvas;
	private GraphicsContext gx;
	private TextCellMetrics metrics;
	private Font baseFont;
	private Font boldFont;
	private Font boldItalicFont;
	private Font italicFont;


	public CellGrid(FxCodeEditorSkin skin, ScrollBar vscroll, ScrollBar hscroll)
	{
		this.skin = skin;
		this.editor = skin.getSkinnable();
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
	
	
	public void setFont(Font f)
	{
		baseFont = f;
		boldFont = null;
		boldItalicFont = null;
		italicFont = null;
	}
	
	
	private Font font()
	{
		return baseFont;
	}


	private static ScrollBar configureScrollBar(ScrollBar b)
	{
		b.setManaged(false);
		b.setMin(0.0);
		b.setMax(1.0);
		b.setUnitIncrement(0.01);
		b.setBlockIncrement(0.05);
		b.setVisible(false);
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
	
	
	private int paragraphCount()
	{
		CodeModel m = editor.getModel();
		return (m == null) ? 0 : m.size();
	}
	
	
	private TextCellMetrics textCellMetrics()
	{
		if(metrics == null)
		{
			Font font = font();
			Text t = new Text("8");
			t.setFont(font);
			
			getChildren().add(t);
			try
			{
				double fontAspect = 0.8; // TODO property
				Bounds b = t.getBoundsInLocal();
				double w = snapSizeX(b.getWidth() * fontAspect);
				double h = snapSizeY(b.getHeight());
				double baseLine = b.getMinY();
				metrics = new TextCellMetrics(font, baseLine, w, h);
			}
			finally
			{
				getChildren().remove(t);
			}
		}
		return metrics;
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
		
		boolean wrap = editor.isWrapText();
		
		// FIX move down
		double vsbWidth = 0.0; // vscroll.isVisible() ? 0.0 : vscroll.prefWidth(-1);
		double hsbHeight = 0.0; //hscroll.isVisible() ? 0.0 : hscroll.prefHeight(-1);
		
		Origin or = origin.get();
		double canvasWidth = snapSizeX(getWidth() - snappedLeftInset() - snappedRightInset());
		double canvasHeight = snapSizeY(getHeight() - snappedTopInset() - snappedBottomInset());
		TextCellMetrics tm = textCellMetrics();
		
		int size = paragraphCount();
		int viewCols = (int)(canvasWidth / tm.cellWidth);
		int viewRows = (int)(canvasHeight / tm.cellHeight);
		Arrangement arr = null;
		CodeModel model = editor.getModel();
		
		// determine if the vertical scroll bar is needed
		// easy answers first
		boolean vsb = (size > viewRows) || (or.index() > 0);
		if(!vsb)
		{
			// attempt to lay out w/o the vertical scroll bar
			arr = new Arrangement(model, viewCols);
			arr.layout(viewRows, or.index(), or.glyphIndex());
			// layout and see if vsb is needed
			if(arr.isVsbNeeded())
			{
				vsb = true;
				arr = null;
			}
		}
		
		if(vsb)
		{
			// view got narrower due to vsb
			vsbWidth = snapSizeX(vscroll.prefWidth(-1));
			canvasWidth -= vsbWidth;
			viewCols = (int)(canvasWidth / tm.cellWidth);
		}
		
		if(arr == null)
		{
			arr = new Arrangement(model, viewCols);
			arr.layout(viewRows, or.index(), or.glyphIndex());
		}
		
		// lay out bottom half of the sliding window
		int last = arr.getLastIndex();
		int max = Math.min(size, last + Defaults.SLIDING_WINDOW_HALF);
		int ct = arr.layout(Defaults.SLIDING_WINDOW_HALF, last, 0); 
		if(ct < Defaults.SLIDING_WINDOW_HALF)
		{
			ct = (Defaults.SLIDING_WINDOW_HALF - ct) + Defaults.SLIDING_WINDOW_HALF;
		}
		else
		{
			ct = Defaults.SLIDING_WINDOW_HALF;
		}
		
		// layout upper half of the sliding window
		int top = Math.max(0, or.index() - ct);
		ct = or.index() - top;
		if(ct > 0)
		{
			arr.layout(top, ct, 0);
		}

		// we now have the layout
		boolean hsb = arr.isHsbNeeded();
		if(hsb)
		{
			hsbHeight = snapSizeY(hscroll.prefHeight(-1));
			canvasHeight -= hsbHeight;
		}
		
//		double y = 0.0;
//		int gix = or.glyphIndex();
//		int row = 0;
//		boolean first = true;
//		//FlowCache cache = getFlowCache(wrap, ??);
//		int ix=or.index();
//		for( ; ix<size; ix++)
//		{
//			FlowPar p = getFlowCell(ix);
//			if(first)
//			{
//				int r = p.rowAtGlyphIndex(gix);
//				row += (p.rowCount() - r);
//				first = false;
//			}
//			else
//			{
//				row += p.rowCount();
//			}
//			
//			if(row >= viewRows)
//			{
//				break;
//			}
//		}


		// we have all the information, so we can re-flow in one pass!  steps:
		// - get the canvas size w/o scroll bars, rowCount
		// - is vsb needed? (easy answers: origin > ZERO, rowCount > model.size)
		// - if vsb not needed, lay out w/o vsb.  if does not fit, must use vsb.
		// - determine if hsb is needed.  easy answers(wrap on, unwrapped width > grid.width)
		// - if vsb not needed, but hsb is needed, lay out one more time, vsb may be needed after all
		// - do the layout: view port, N lines after, M lines before (adjusting N,M when close to the model edges)
		// the new flow contains all the parameters

		boolean recreateCanvas =
			(canvas == null) || 
			GridUtils.notClose(canvasWidth, canvas.getWidth()) ||
			GridUtils.notClose(canvasHeight, canvas.getHeight());
		if(recreateCanvas)
		{
			if(canvas != null)
			{
				getChildren().remove(canvas);
			}
			
			// create new canvas
			canvas = new Canvas(canvasWidth, canvasHeight);
			gx = canvas.getGraphicsContext2D();
			
			getChildren().add(canvas);
		}
		
		vscroll.setVisible(vsb);
		hscroll.setVisible(hsb);
		
		// geometry is fine at this point
		// TODO recreate the canvas if necessary
		// TODO repaint damaged areas on the canvas
		// FIX debug
		{
			gx.setFill(Color.LIGHTSALMON);
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
