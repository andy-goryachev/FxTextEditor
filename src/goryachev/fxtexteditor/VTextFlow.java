// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.util.CKit;
import goryachev.common.util.D;
import goryachev.fx.CPane;
import goryachev.fx.CssStyle;
import goryachev.fx.FX;
import goryachev.fx.FxBoolean;
import goryachev.fxtexteditor.internal.Markers;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.collections.ListChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;


/**
 * Paints the text on canvas. 
 */
public class VTextFlow
	extends CPane
{
	public static final CssStyle PANE = new CssStyle("FxTermView_PANE");
	protected final FxTextEditor editor;
	protected final FxBoolean caretVisible = new FxBoolean(true);
	protected final FxBoolean suppressBlink = new FxBoolean(false);
	protected final BooleanExpression paintCaret;
	private Timeline cursorAnimation;
	private boolean cursorEnabled = true;
	private boolean cursorOn = true;
	private Font font;
	private Font boldFont;
	private Font boldItalicFont;
	private Font italicFont;
	private Canvas canvas;
	private GraphicsContext gx;
	private int colCount;
	private int rowCount;
	private TextMetrics metrics;
	protected final Text proto = new Text();
	private Color backgroundColor = Color.WHITE;
	private Color textColor = Color.BLACK;
	private Color caretColor = Color.BLACK;
	private int topLine;
	private int topOffset;
	private FxTextEditorLayout layout;
	private boolean repaintRequested;
	
	
	public VTextFlow(FxTextEditor ed)
	{
		this.editor = ed;
		
		FX.style(this, PANE);
		
		setMinWidth(0);
		setMinHeight(0);
		
		cursorAnimation = createCursorAnimation();
		
		setFocusTraversable(true);
		
		FX.listen(this::handleSizeChange, widthProperty());
		FX.listen(this::handleSizeChange, heightProperty());
		
		// TODO clip rect
		
		paintCaret = new BooleanBinding()
		{
			{
				bind(caretVisible, editor.displayCaretProperty, editor.focusedProperty(), editor.disabledProperty(), suppressBlink);
			}

			protected boolean computeValue()
			{
				return (isCaretVisible() || suppressBlink.get()) && editor.isDisplayCaret() && editor.isFocused() && (!editor.isDisabled());
			}
		};
		paintCaret.addListener((s,p,c) -> refreshCursor());
	}
	
	
	public FxTextEditor getEditor()
	{
		return editor;
	}
	
	
	public int getTopLine()
	{
		return topLine;
	}
	
	
	public void setTopLine(int y)
	{
		topLine = y;
	}
	
	
	public int getTopOffset()
	{
		return topOffset;
	}
	
	
	public void setTopOffset(int off)
	{
		topOffset = off;
	}
	
	
	public int getVisibleColumnCount()
	{
		return colCount;
	}
	
	
	public int getVisibleRowCount()
	{
		return rowCount;
	}
	
	
	public int getMaxColumnCount()
	{
		return getLayout().getMaxColumnCount();
	}
	
	
	public void setSuppressBlink(boolean on)
	{
		suppressBlink.set(on);
		
		if(!on)
		{
			// restart animation cycle
			updateBlinkRate();
		}
	}
	
	
	public void updateBlinkRate()
	{
		Duration d = editor.getBlinkRate();
		Duration period = d.multiply(2);
		
		cursorAnimation.stop();
		cursorAnimation.getKeyFrames().setAll
		(
			new KeyFrame(Duration.ZERO, (ev) -> setCaretVisible(true)),
			new KeyFrame(d, (ev) -> setCaretVisible(false)),
			new KeyFrame(period)
		);
		cursorAnimation.play();
	}
	
	
	/** used for blinking animation */
	protected void setCaretVisible(boolean on)
	{
		caretVisible.set(on);
	}
	
	
	public boolean isCaretVisible()
	{
		return caretVisible.get();
	}
	
	
	public void setFont(Font f)
	{
		this.font = f;
		updateFonts();
		metrics = null;
	}
	
	
	public Font getFont()
	{
		if(font == null)
		{
			font = Font.font("Monospace", 12);
			updateFonts();
		}
		return font;
	}
	
	
	protected void updateFonts()
	{
		boldFont = Font.font(font.getFamily(), FontWeight.BOLD, FontPosture.REGULAR, font.getSize());
		boldItalicFont = Font.font(font.getFamily(), FontWeight.BOLD, FontPosture.ITALIC, font.getSize());
		italicFont = Font.font(font.getFamily(), FontWeight.NORMAL, FontPosture.ITALIC, font.getSize());
	}
	
	
	public void setBackgroundColor(Color c)
	{
		backgroundColor = c;
		repaint();
	}
	
	
	public Color getBackgroundColor()
	{
		return backgroundColor;
	}
	
	
	protected Color backgroundColor(TCell cell, int x, int y)
	{
		Color c = backgroundColor;
		
		if(editor.isHighlightCaretLine())
		{
			if(editor.selector.isCaretLine(topLine + y))
			{
				c = mixColor(c, editor.getCaretLineColor(), 0.3);
			}
		}
		
		if(editor.selector.isSelected(topLine + y, x + layout.getLineOffset(y)))
		{
			c = mixColor(c, editor.getSelectionBackgroundColor(), 0.4);
		}
		
		if(cell != null)
		{
			c = mixColor(c, cell.getBackgroundColor(), 0.8);
		}
		
		return c;
	}
	
	
	protected Color mixColor(Color base, Color added, double fraction)
	{
		if(base == null)
		{
			return added;
		}
		else if(added == null)
		{
			return base;
		}
		
		return FX.mix(base, added, fraction);
	}
	
	
	public void setTextColor(Color c)
	{
		textColor = c;
		repaint();
	}
	
	
	public Color getTextColor()
	{
		return textColor;
	}
	
	
	protected TextMetrics textMetrics()
	{
		if(metrics == null)
		{
			Font f = getFont();
			
			proto.setText("8");
			proto.setFont(f);
			
			Bounds b = proto.getBoundsInLocal();
			int w = FX.round(b.getWidth());
			int h = FX.round(b.getHeight());
			
			metrics = new TextMetrics(f, b.getMinY(), w, h);
		}
		return metrics;
	}
	
	
	protected Timeline createCursorAnimation()
	{
		Timeline t = new Timeline(new KeyFrame(Duration.millis(500), (ev) -> blinkCursor()));
		t.setCycleCount(Timeline.INDEFINITE);
		t.play();
		return t;
	}
	
	
	protected void blinkCursor()
	{
		cursorOn = !cursorOn;
		refreshCursor();
	}
	
	
	protected void refreshCursor()
	{
		// TODO
		repaint();
	}
	
	
	protected void handleSizeChange()
	{
		canvas = createCanvas();
		setCenter(canvas);
		gx = canvas.getGraphicsContext2D();
		gx.setFontSmoothingType(FontSmoothingType.GRAY);
		
		gx.setFill(getBackgroundColor());
		gx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		draw();
	}
	
	
	protected GraphicsContext getGraphicsContext()
	{
		return gx;
	}
	
	
	// TODO create canvas +1 size
	protected Canvas createCanvas()
	{
		TextMetrics tm = textMetrics();
		Insets m = getInsets();
		
		double w = getWidth() - m.getLeft() - m.getRight();
		double h = getHeight() - m.getTop() - m.getBottom();
		
		colCount = CKit.floor(w / tm.cellWidth);
		rowCount = CKit.floor(h / tm.cellHeight);
		
		D.print(rowCount); // FIX
		return new Canvas(w + 1, h + 1);
	}
	
	
	public void invalidate()
	{
		layout = null;
	}
	
	
	protected FxTextEditorLayout createLayout()
	{
		int sz = getVisibleRowCount() + 1;
		int[] offsets = new int[sz];
		ITextCells[] cells = new ITextCells[sz];
		FxTextEditorModel m = editor.getModel();
		
		int ix = getTopLine();
		int y = 0;
		int max;
		
		if(editor.isWrapLines())
		{
			int colCount = getVisibleColumnCount();
			max = colCount;
			
			for(;;)
			{
				ITextCells tc = m.getTextCells(ix);
				if(tc == null)
				{
					break;
				}
				
				int len = tc.getCellCount();
				int off = getTopOffset();
				while(off < len)
				{
					if(y >= sz)
					{
						break;
					}
					
					cells[y] = tc;
					offsets[y] = off;
					
					off += colCount;
					y++;
				}
				
				ix++;
			}
		}
		else
		{
			int off = getTopOffset();
			max = 0;

			for(;;)
			{
				ITextCells tc = m.getTextCells(ix);
				if(tc == null)
				{
					break;
				}
				
				cells[y] = tc;
				offsets[y] = off;
				int w = tc.getCellCount();
				if(max < w)
				{
					max = w;
				}
					
				y++;
					
				if(y >= sz)
				{
					break;
				}
				
				ix++;
			}
		}
		
		return new FxTextEditorLayout(cells, offsets, max);
	}
	
	
	protected Font getFont(TCell c)
	{
		if(c.isBold())
		{
			if(c.isItalic())
			{
				return boldItalicFont;
			}
			else
			{
				return boldFont;
			}
		}
		else
		{
			if(c.isItalic())
			{
				return italicFont;
			}
			else
			{
				return font;
			}
		}
	}


	public Marker getTextPos(double screenx, double screeny, Markers markers)
	{
		Point2D p = canvas.screenToLocal(screenx, screeny);
		TextMetrics m = textMetrics();
		// TODO hor scrolling
		int x = FX.round(p.getX() / m.cellWidth);
		int y = FX.floor(p.getY() / m.cellHeight);
		int pos = layout.getTextPos(x, y);
		int line = topLine + y;
		return markers.newMarker(line, pos);
	}
	
	
	protected FxTextEditorLayout getLayout()
	{
		if(layout == null)
		{
			layout = createLayout();
		}
		return layout;
	}
	
	
	public void repaintSegment(ListChangeListener.Change<? extends SelectionSegment> ch)
	{
		// TODO repaint only the damaged area
		repaint();
	}
	

	/** requests a repaint.  the actual drawing happens in runLater() */
	public void repaint()
	{
		if(!repaintRequested)
		{
			repaintRequested = true;
			FX.later(this::draw);
		}
	}
	
	
	protected void draw()
	{
		repaintRequested = false;
		
		if((colCount == 0) || (rowCount == 0))
		{
			return;
		}
		
		if(editor.getModel() == null)
		{
			return;
		}
		
		boolean wrap = editor.isWrapLines();
		int x = 0;
		int y = 0;
		int max = wrap ? colCount : colCount + 1;
		
		for(;;)
		{
			TCell c = getLayout().getCell(x, y);
			if(c == null)
			{
				clearToEndOfLine(x, y);
			}
			else
			{
				paintCell(x, y, c);
			}
			
			x++;
			if(x >= max)
			{
				if(wrap)
				{
					clearToEndOfLine(x, y);
				}
				
				x = 0;
				y++;
				if(y > rowCount)
				{
					break;
				}
			}
		}
	}
	
	
	protected void clearToEndOfLine(int x, int y)
	{
		TextMetrics m = textMetrics();
		double px = x * m.cellWidth;
		double py = y * m.cellHeight;
		
		// TODO selection color, line color
		Color bg = backgroundColor(null, x, y);
		gx.setFill(bg);
		gx.fillRect(px, py,canvas.getWidth() - px, m.cellHeight);
		
		// caret
		if(paintCaret.get())
		{
			if(editor.selector.isCaret(topLine + y, x + layout.getLineOffset(y)))
			{
				// TODO insert mode
				gx.setFill(caretColor);
				gx.fillRect(px, py, 2, m.cellHeight);
			}
		}
	}
	
	
	protected void paintCell(int x, int y, TCell cell)
	{		
		TextMetrics m = textMetrics();
		double px = x * m.cellWidth;
		double py = y * m.cellHeight;
		
		// background
		Color bg = backgroundColor(cell, x, y); 
		gx.setFill(bg);
		gx.fillRect(px, py, m.cellWidth, m.cellHeight);
		
		Color fg = cell.getTextColor();
		if(fg == null)
		{
			fg = textColor;
		}
		
		// caret
		if(paintCaret.get())
		{
			if(editor.selector.isCaret(topLine + y, x + layout.getLineOffset(y)))
			{
				// TODO insert mode
				gx.setFill(caretColor);
				gx.fillRect(px, py, 2, m.cellHeight);
			}
		}
		
		// text
		String text = cell.getText();
		gx.setFont(getFont(cell));
		gx.setFill(fg);
		gx.fillText(text, px, py - m.baseline, m.cellWidth);
		
		// TODO underline, strikethrough
	}
}
