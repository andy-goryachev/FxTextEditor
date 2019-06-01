// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.util.CKit;
import goryachev.fx.Binder;
import goryachev.fx.CPane;
import goryachev.fx.CssStyle;
import goryachev.fx.FX;
import goryachev.fx.FxBoolean;
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
import javafx.scene.control.ScrollBar;
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
	private static final double CARET_LINE_OPACITY = 0.3;
	private static final double SELECTION_BACKGROUND_OPACITY = 0.4;
	private static final double CELL_BACKGROUND_OPACITY = 0.8;
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
	private Canvas lineNumberCanvas;
	private GraphicsContext lineNumberGx;
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
	private FxTextEditorLayout layout; // TODO reuse layout instance: invalidate(), reset()
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
		Binder.onChange(this::updateLineNumbers, ed.showLineNumbersProperty(), ed.lineNumberFormatterProperty());
		
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
	
	
	public void setOrigin(int top, double offy)
	{
		topLine = top;
		// FIX
//		offsety = offy;
		
//		layoutChildren();
		
		// TODO
//		updateVerticalScrollBar();
		
		invalidate();
		repaint();
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
	
	
	protected Color backgroundColor(TCell cell, TextPos pos)
	{
		Color c = backgroundColor;
		
		if(editor.isHighlightCaretLine())
		{
			if(editor.selector.isCaretLine(pos.getLine())) // FIX
			{
				c = mixColor(c, editor.getCaretLineColor(), CARET_LINE_OPACITY);
			}
		}
		
		if(editor.selector.isSelected(pos.getLine(), pos.getOffset())) // FIX
		{
			c = mixColor(c, editor.getSelectionBackgroundColor(), SELECTION_BACKGROUND_OPACITY);
		}
		
		if(cell != null)
		{
			c = mixColor(c, cell.getBackgroundColor(), CELL_BACKGROUND_OPACITY);
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
	
	
	protected void updateLineNumbers()
	{
		if(editor.isShowLineNumbers())
		{
			if(lineNumberCanvas != null)
			{
				// TODO check w,h,format
			}
			
			// TODO create canvas, context
		}
		else
		{
			if(lineNumberCanvas != null)
			{
				setLeft(null);
				lineNumberCanvas = null;
			}
		}
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
		
		return new Canvas(w + 1, h + 1);
	}
	
	
	public void invalidate()
	{
		layout = null;
	}
	
	
	protected FxTextEditorLayout createLayout()
	{
		int sz = getVisibleRowCount() + 1;
		int[] lines = new int[sz];
		int[] offsets = new int[sz];
		ITextCells[] cells = new ITextCells[sz];
		FxTextEditorModel m = editor.getModel();
		
		int lineIndex = getTopLine();
		int y = 0;
		int maxColumns;
		
		if(editor.isWrapLines())
		{
			int colCount = getVisibleColumnCount();
			maxColumns = colCount;
			
			for(;;)
			{
				if(y >= sz)
				{
					break;
				}

				ITextCells tc = m.getTextCells(lineIndex);
				int len;
				if(tc == null)
				{
					len = 0;
				}
				else
				{
					len = tc.getCellCount();
				}
				
				int off = getTopOffset();
				
				for(;;)
				{
					cells[y] = tc;
					lines[y] = lineIndex;
					offsets[y] = off;
					
					off += colCount;
					y++;
					
					if(y >= sz)
					{
						break;
					}

					if(off < len)
					{
						continue;
					}
					else
					{
						break;
					}
				}
				
				lineIndex++;
			}
		}
		else
		{
			int off = getTopOffset();
			maxColumns = 0;

			for(;;)
			{
				ITextCells tc = m.getTextCells(lineIndex);
				if(tc == null)
				{
					break;
				}
				
				cells[y] = tc;
				lines[y] = lineIndex;
				offsets[y] = off;
				int w = tc.getCellCount();
				if(maxColumns < w)
				{
					maxColumns = w;
				}
					
				y++;
					
				if(y >= sz)
				{
					break;
				}
				
				lineIndex++;
			}
		}
		
		return new FxTextEditorLayout(cells, lines, offsets, rowCount, maxColumns);
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


	public TextPos getInsertPosition(double screenx, double screeny)
	{
		Point2D p = canvas.screenToLocal(screenx, screeny);
		TextMetrics m = textMetrics();
		// TODO hor scrolling
		int x = FX.round(p.getX() / m.cellWidth);
		int y = FX.floor(p.getY() / m.cellHeight);
		return layout.getInsertPosition(x, y);
	}
	
	
	protected FxTextEditorLayout getLayout()
	{
		if(layout == null)
		{
			layout = createLayout();
		}
		return layout;
	}
	

	/** returns true if update resulted in a visual change */
	public boolean update(int startLine, int linesInserted, int endLine)
	{
		try
		{
			int max = Math.max(endLine, startLine + linesInserted);
			if(max < topLine)
			{
				return false;
			}
			else if(startLine > (topLine + getVisibleLineCount()))
			{
				return false;
			}
			
			// TODO optimize, but for now simply
			invalidate();
			requestLayout();
			
			return true;
		}
		finally
		{
			updateVerticalScrollBar();
		}
	}
	
	
	protected void updateVerticalScrollBar()
	{
		editor.setHandleScrollEvents(false);
		
		int max;
		int visible;
		double val;
		
//		double v = (max == 0 ? 0.0 : topLine / (double)max); 
//		editor.vscroll.setValue(v);
		
		FxTextEditorModel model = editor.getModel();
		if(model == null)
		{
			visible = 1;
			max = 1;
			val = 0;
		}
		else
		{
			max = model.getLineCount();
			visible = getVisibleLineCount();
			val = topLine; //(max - visible);
		}
		
		ScrollBar vscroll = editor.getVerticalScrollBar();
		vscroll.setMin(0);
        vscroll.setMax(max);
        vscroll.setVisibleAmount(visible);
        vscroll.setValue(val);
        
		editor.setHandleScrollEvents(true);
	}
	
	
	public int getVisibleLineCount()
	{
		if(layout == null)
		{
			return 0;
		}
		return layout.getVisibleLineCount();
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
		TextPos p = getLayout().getInsertPosition(x, y);

		// TODO selection color, line color
		Color bg = backgroundColor(null, p);
		gx.setFill(bg);
		gx.fillRect(px, py, canvas.getWidth() - px, m.cellHeight);

		// caret
		if(paintCaret.get())
		{
			if(editor.selector.isCaret(p.getLine(), p.getOffset()))
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
		TextPos pos = getLayout().getInsertPosition(x, y);

		// background
		Color bg = backgroundColor(cell, pos);
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
			if(editor.selector.isCaret(pos.getLine(), pos.getOffset()))
			{
				// FIX
//				if(pos.isValidCaret())
				{
					// TODO insert mode
					gx.setFill(caretColor);
					gx.fillRect(px, py, 2, m.cellHeight);
				}
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
