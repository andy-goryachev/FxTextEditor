// Copyright © 2019-2021 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.log.Log;
import goryachev.common.util.CKit;
import goryachev.common.util.text.IBreakIterator;
import goryachev.fx.CPane;
import goryachev.fx.FX;
import goryachev.fx.FxBoolean;
import goryachev.fx.FxBooleanBinding;
import goryachev.fxtexteditor.internal.FlowLine;
import goryachev.fxtexteditor.internal.FlowLineCache;
import goryachev.fxtexteditor.internal.ScreenBuffer;
import goryachev.fxtexteditor.internal.ScreenRow;
import goryachev.fxtexteditor.internal.ScrollAssist;
import goryachev.fxtexteditor.internal.SelectionHelper;
import goryachev.fxtexteditor.internal.TextCell;
import goryachev.fxtexteditor.internal.VerticalScrollHelper;
import goryachev.fxtexteditor.internal.WrapInfo;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Window;
import javafx.util.Duration;


/**
 * Visual flow container lays out cells inside the screen buffer and paints the canvas. 
 */
public class VFlow
	extends CPane
{
	protected static final Log log = Log.get("VFlow");
	
	protected static final int LINE_CACHE_SIZE = 1024;
	protected static final double LINE_NUMBERS_BG_OPACITY = 0.1;
	protected static final double CARET_LINE_OPACITY = 0.3;
	protected static final double LINE_COLOR_OPACITY = 0.9;
	protected static final double SELECTION_BACKGROUND_OPACITY = 0.9;
	protected static final double CELL_BACKGROUND_OPACITY = 0.8;
	protected static final int HORIZONTAL_SAFETY = 8;
	protected static final int VERTICAL_SAFETY = 1;
	
	protected final FxTextEditor editor;
	protected final FxBoolean caretEnabledProperty = new FxBoolean(true);
	protected final FxBoolean suppressBlink = new FxBoolean(false);
	protected final BooleanExpression paintCaret;
	protected final ScreenBuffer screenBuffer = new ScreenBuffer(this);
	private Timeline cursorAnimation;
	private boolean cursorEnabled = true;
	private boolean cursorOn = true;
	private Font font;
	private Font boldFont;
	private Font boldItalicFont;
	private Font italicFont;
	private TextMetrics metrics;
	protected final Text proto = new Text();
	private GraphicsContext lineNumberGx;
	private Canvas canvas;
	private GraphicsContext gx;
	private int screenColumnCount;
	private int screenRowCount;
	private int lineNumbersCellCount;
	private int lineNumbersBarWidth;
	private int minLineNumberCellCount = 3; // arbitrary number
	private int lineNumbersGap = 5; // arbitrary number
	private Color textColor = Color.BLACK;
	private Color caretColor = Color.BLACK;
	private int topLine;
	private int topGlyphIndex;
	/** leftmost column in non-wrapped mode */
	private int topColumn;
	private boolean screenBufferValid;
	private boolean repaintRequested;
	protected final FlowLineCache cache;
	private int phantomColumn = -1;
	protected boolean handleScrollEvents = true;
	private static final CellStyle NO_STYLE = new CellStyle();
	
	
	public VFlow(FxTextEditor ed)
	{
		this.editor = ed;
		
		// TODO bind vflow background color to editor background color
		
		backgroundProperty().bind(Bindings.createObjectBinding(() ->
		{
			Color c = ed.getBackgroundColor();
			return new Background(new BackgroundFill(c, null, null));
		}, ed.backgroundColorProperty()));
		
		cache = new FlowLineCache(ed, LINE_CACHE_SIZE);
		
		setMinWidth(0);
		setMinHeight(0);
		
		setFocusTraversable(false);
		
		FX.onChange(this::repaint, ed.backgroundColorProperty());
		FX.onChange(this::handleSizeChange,  widthProperty(), heightProperty());
		FX.onChange(this::updateModel, ed.modelProperty());
		FX.onChange(this::updateLineNumbers, ed.showLineNumbersProperty, ed.lineNumberFormatterProperty, ed.modelProperty);
		FX.onChange(this::updateFont, true, ed.fontProperty);
		FX.onChange(this::handleWrapChange, ed.wrapLinesProperty);
		
		ed.getVerticalScrollBar().valueProperty().addListener((s,p,c) -> handleVerticalScroll(c.doubleValue()));
		ed.getHorizontalScrollBar().valueProperty().addListener((s,p,c) -> handleHorizontalScroll(c.doubleValue()));
		ed.selector.selectionSegmentProperty().addListener((s,p,c) -> handleSelectionSegmentUpdate(p, c)); 
		
		paintCaret = new FxBooleanBinding(caretEnabledProperty, editor.displayCaretProperty, editor.focusedProperty(), editor.disabledProperty(), suppressBlink)
		{
			protected boolean computeValue()
			{
				return (caretEnabledProperty.get() || suppressBlink.get()) && editor.isDisplayCaret() && editor.isFocused() && (!editor.isDisabled());
			}
		};
		paintCaret.addListener((s,p,c) -> refreshCursor());
		
		FX.parentWindowProperty(this).addListener((s,p,c) -> updateCursorAnimation(c));
	}
	
	
	public FxTextEditor getEditor()
	{
		return editor;
	}
	
	
	public SelectionSegment getSelectionSegment()
	{
		return editor.getSelection().getSegment();
	}
	
	
	public int getTopLine()
	{
		return topLine;
	}
	
	
	public int getTopGlyphIndex()
	{
		return topGlyphIndex;
	}
	
	
	/** shift viewport delta rows up (delta<0) or down (delta>0) */
	public void shiftViewPort(int delta)
	{
		int line = getTopLine();
		int gix = getTopGlyphIndex();
		
		WrapInfo wr = getWrapInfo(line);
		int wrapRow = wr.getWrapRowForGlyphIndex(gix);
		
		WrapPos wp = advance(line, wrapRow, delta);
		wp = ensureLastPageFullView(wp);
		
		int newLine = wp.getLine();
		int newGlyphIndex = wp.getStartGlyphIndex();
		setOrigin(newLine, newGlyphIndex);
	}
	
	
	protected WrapPos ensureLastPageFullView(WrapPos wp)
	{
		int newLine = wp.getLine();
		int newGlyphIndex = wp.getStartGlyphIndex();
		
		// avoid going beyond (lastRow - screenRowCount)
		int lineCount = getModelLineCount();
		if(newLine > (lineCount - screenRowCount))
		{
			WrapPos wp2 = advance(lineCount, 0, -screenRowCount);
			if(newLine > wp2.getLine())
			{
				return wp2;
			}
			else if(newLine == wp2.getLine())
			{
				if(newGlyphIndex > wp2.getStartGlyphIndex())
				{
					return wp2;
				}
			}
		}
		return wp;
	}
	
	
	public void setOrigin(int topLine, int glyphIndex)
	{
		if(topLine == this.topLine)
		{
			if(glyphIndex == this.topGlyphIndex)
			{
				return;
			}
		}
		
		log.debug("%d %s", topLine, glyphIndex);
		
		this.topLine = topLine;
		this.topGlyphIndex = glyphIndex;
		
		updateLineNumbers();
		invalidate();
		
		if(!isWrapLines())
		{
			updateHorizontalScrollBarPosition();
		}
		
		updateVerticalScrollBarPosition();
	}
	
	
	/** returns the leftmost display cell index (glyph index) */
	public int getTopCellIndex()
	{
		return topColumn;
	}
	
	
	/** meaningful only in non-wrapped mode */
	public void setTopCellIndex(int ix)
	{
		if(topColumn != ix)
		{
			log.debug("%d", ix);
			
			topColumn = ix;
			invalidate();
			
			if(!isWrapLines())
			{
				updateHorizontalScrollBarPosition();
			}
		}
	}
	

	public int getScreenColumnCount()
	{
		return screenColumnCount;
	}
	
	
	public int getScreenRowCount()
	{
		return screenRowCount;
	}
	
	
	public int getModelLineCount()
	{
		return editor.getLineCount();
	}
	
	
	public int getMaxColumnCount()
	{
		return buffer().getWidth();
	}
	
	
	public boolean isWrapColumn(int x)
	{
		if(isWrapLines())
		{
			if(x == screenColumnCount)
			{
				return true;
			}
		}
		return false;
	}
	
	
	public boolean isWrapLines()
	{
		return editor.isWrapLines();
	}
	
	
	/** 
	 * returns the maximum number of horizontal screen cells required to display the 
	 * visible text in the screen buffer.
	 * valid only in non-wrapping mode.
	 */
	public int getMaxCellCount()
	{
		if(isWrapLines())
		{
			throw new Error();
		}
		
		ITabPolicy p = editor.getTabPolicy();
		return buffer().getMaxCellCount(p);
	}
	
	
	/** use this to suppress blinking when the cursor moves, so the movement is apparent */ 
	public void setSuppressBlink(boolean on)
	{
		suppressBlink.set(on);
		
		if(!on)
		{
			// restart animation cycle
			if(cursorAnimation != null)
			{
				updateBlinkRate();
			}
		}
	}
	
	
	public void updateBlinkRate()
	{
		Duration d = editor.getBlinkRate();
		Duration period = d.multiply(2);
		
		cursorAnimation.stop();
		cursorAnimation.getKeyFrames().setAll
		(
			new KeyFrame(Duration.ZERO, (ev) -> caretEnabledProperty.set(true)),
			new KeyFrame(d, (ev) -> caretEnabledProperty.set(false)),
			new KeyFrame(period)
		);
		cursorAnimation.play();
	}
	
	
	protected void updateFont()
	{
		Font f = editor.getFont();
		if(f == null)
		{
			f = Font.font("Monospace", 12);
		}
		font = f; 
		boldFont = Font.font(font.getFamily(), FontWeight.BOLD, FontPosture.REGULAR, font.getSize());
		boldItalicFont = Font.font(font.getFamily(), FontWeight.BOLD, FontPosture.ITALIC, font.getSize());
		italicFont = Font.font(font.getFamily(), FontWeight.NORMAL, FontPosture.ITALIC, font.getSize());

		metrics = null;
		lineNumbersCellCount = -1;

		updateLineNumbers();
		invalidate();
	}
	
	
	protected void setHandleScrollEvents(boolean on)
	{
		handleScrollEvents = on;
	}
	
	
	protected boolean isHandleScrollEvents()
	{
		return handleScrollEvents;
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
			proto.setText("8");
			proto.setFont(font);
			
			Bounds b = proto.getBoundsInLocal();
			int w = CKit.round(b.getWidth());
			int h = CKit.round(b.getHeight());
			
			metrics = new TextMetrics(font, b.getMinY(), w, h);
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
	
	
	protected void updateCursorAnimation(Window w)
	{
		if(w == null)
		{
			if(cursorAnimation != null)
			{
				log.trace("stopping cursor animation");
				cursorAnimation.stop();
				cursorAnimation = null;
			}
		}
		else
		{
			if(cursorAnimation == null)
			{
				log.trace("starting cursor animation");
				cursorAnimation = createCursorAnimation();
			}
		}
	}
	
	
	protected void blinkCursor()
	{
		cursorOn = !cursorOn;
		refreshCursor();
	}
	
	
	protected void refreshCursor()
	{
		EditorSelection sel = editor.getSelection();
		Marker caret = sel.getCaret();
		if(isVisible(caret))
		{
			// TODO repaint only the damaged area
			repaint();
		}
	}
	
	
	protected void updateModel()
	{
		log.trace();
		
		invalidate();
	}
	
	
	protected void handleSizeChange()
	{
		log.trace(() -> String.format("width=%.1f, height=%.1f", getWidth(), getHeight()));
		
		invalidate();
		
		canvas = createCanvas();
		setCenter(canvas);
		
		gx = canvas.getGraphicsContext2D();
		gx.setFontSmoothingType(FontSmoothingType.GRAY);
		
		updateDimensions();
		
		paintAll();
	}
	
	
	protected Canvas createCanvas()
	{
		Insets m = getInsets();
		double w = getWidth() - m.getLeft() - m.getRight() + 1;
		double h = getHeight() - m.getTop() - m.getBottom() + 1;
		
		log.trace("w=%.1f, h=%.1f", w, h);
		
		return new Canvas(w, h);
	}
	
	
	/** makes screen buffer invalid, triggers full screen update */
	public void invalidate()
	{
		screenBufferValid = false;
		repaint();
	}
	
	
	protected void updateLineNumbers()
	{
		FxTextEditorModel m = editor.getModel();
		if(m == null)
		{
			return;
		}
		
		int count;
		if(editor.isShowLineNumbers())
		{
			int lastLine = getTopLine() + screenRowCount;
			ILineNumberFormatter fmt = editor.getLineNumberFormatter();
			if(fmt == null)
			{
				count = 0;
			}
			else
			{
				Object x = fmt.formatLineNumber(lastLine);
				count = Math.max(minLineNumberCellCount, x.toString().length());

				// last line may not have a valid line or formatted value
				if(lastLine >= 2)
				{
					x = fmt.formatLineNumber(lastLine - 2);
					int ct = Math.max(minLineNumberCellCount, x.toString().length());
					count = Math.max(count, ct);
				}
			}
		}
		else
		{
			count = 0;
		}
		
		if(count != lineNumbersCellCount)
		{
			lineNumbersCellCount = count;
			
			if(count == 0)
			{
				lineNumbersBarWidth = 0;
			}
			else
			{
				TextMetrics tm = textMetrics();
				lineNumbersBarWidth = (count * tm.cellWidth + lineNumbersGap + lineNumbersGap);
			}
			
			invalidate();
		}
		
		updateDimensions();
	}
	
	
	protected void updateDimensions()
	{
		log.trace();
		
		if((getWidth() == 0) || (getHeight() == 0))
		{
			return;
		}
		
		Insets m = getInsets();
		double w = getWidth() - m.getLeft() - m.getRight();
		double h = getHeight() - m.getTop() - m.getBottom();
	
		TextMetrics tm = textMetrics();
		if(lineNumbersCellCount > 0)
		{
			w -= (lineNumbersCellCount * tm.cellWidth + lineNumbersGap + lineNumbersGap);
		}
		
		if(w < 0.0)
		{
			w = 0.0;
		}
		
		if(h < 0.0)
		{
			h = 0.0;
		}

		screenColumnCount = CKit.floor(w / tm.cellWidth);
		screenRowCount = CKit.floor(h / tm.cellHeight);
	}
	
	
	/** updates thumb size; depends on a wrapping pass in reflow() */
	protected void updateHorizontalScrollBarSize()
	{
		if(!isWrapLines())
		{
			int max = getMaxCellCount() + 1; // allow for 1 blank space at the end
			double vis = getMaxColumnCount();
			double thumbSize = vis / max;
			editor.getHorizontalScrollBar().setVisibleAmount(thumbSize);
		}
	}
	
	
	protected void updateHorizontalScrollBarPosition()
	{
		if(!isWrapLines())
		{
			double v;
			
			int max = getMaxCellCount();
			if(max <= screenColumnCount)
			{
				v = 0.0;
			}
			else
			{
				max -= screenColumnCount;
				v = topColumn / (double)max;
			}
			
			setHandleScrollEvents(false);
			try
			{
				editor.getHorizontalScrollBar().setValue(v);
			}
			finally
			{
				setHandleScrollEvents(true);
			}
		}
	}
	
	
	protected void updateVerticalScrollBarSize()
	{
		int lineCount = getModelLineCount();

		double v;
		if(isWrapLines())
		{
			if(lineCount == 0)
			{
				v = 1.0;
			}
			else
			{
				ScrollAssist a = ScrollAssist.create(this, topLine, getTopWrapRow());
				
				// add the number of extra rows due to wrapping (for visible lines)
				double total = lineCount + a.getAdditionalRows();
				if(total < screenRowCount)
				{
					v = 1.0;
				}
				else
				{
					v = screenRowCount / total;
				}
			}
		}
		else
		{
			if(lineCount < screenRowCount)
			{
				v = 1.0;
			}
			else
			{
				v = screenRowCount / (double)lineCount;
			}
		}
		
		setHandleScrollEvents(false);
		try
		{
			editor.getVerticalScrollBar().setVisibleAmount(v);
		}
		finally
		{
			setHandleScrollEvents(true);
		}
	}
	
	
	protected void updateVerticalScrollBarPosition()
	{
		int lineCount = getModelLineCount();
		
		double v;
		if(lineCount == 0)
		{
			v = 0.0;
		}
		else
		{
			if(isWrapLines())
			{
				// adjust for additional wrapped rows  
				// vis / (lineCount + additionaRows)
				
				ScrollAssist a = ScrollAssist.create(this, topLine, getTopWrapRow());
				double max = getModelLineCount() + a.getAdditionalRows();
				v = (topLine + a.getAdditionalTopRows()) / max;
			}
			else
			{
				if(lineCount < screenRowCount)
				{
					v = 0.0;
				}
				else
				{
					v = topLine / (double)(lineCount - screenRowCount);
				}
			}
			
			double loaded = getLoadedRatio();
			if(loaded < 1.0)
			{
				v *= loaded;
			}
		}
		
		setHandleScrollEvents(false);
		try
		{
			editor.getVerticalScrollBar().setValue(v);
		}
		finally
		{
			setHandleScrollEvents(true);
		}
	}
	
	
	protected double getLoadedRatio()
	{
		FxTextEditorModel m = editor.getModel();
		if(m == null)
		{
			return 1.0;
		}
		
		double v = m.getLoadStatus().getProgress();
		
		double min = 0.01; // avoid div by 0
		if(v < min)
		{
			return min;
		}
		return v;
	}
	

	/** requests a repaint, the actual drawing will happen in runLater() */
	protected void repaint()
	{
		if(!repaintRequested)
		{
			repaintRequested = true;
			
			FX.later(() ->
			{
				long start = System.nanoTime();
				try
				{
					paintAll();
				}
				finally
				{
					long elapsed = (System.nanoTime() - start) / 1_000_000L;
					if(elapsed > 100)
					{
						log.warn("paintAll: %d", elapsed);
					}
					repaintRequested = false;
				}
			});
		}
	}
	
	
	protected void handleWrapChange()
	{
		if(editor.isWrapLines())
		{
			topColumn = 0;
		}
		else
		{
			editor.getHorizontalScrollBar().setValue(0);
		}
		
		requestLayout();
		invalidate();
	}
	
	
	public void handleSelectionSegmentUpdate(SelectionSegment prev, SelectionSegment sel)
	{
		// TODO repaint only the damaged area
		repaint();
	}
	
	
	protected void handleHorizontalScroll(double val)
	{
		if(handleScrollEvents)
		{
			if(!isWrapLines())
			{
				int max = getMaxCellCount() + 1; // allow for 1 blank space at the end
				int vis = getMaxColumnCount();
				int fr = Math.max(0, max - vis);
				
				int off = CKit.round(fr * val);
				setTopCellIndex(off);
			}
		}
	}
	
	
	protected void handleVerticalScroll(double val)
	{
		if(handleScrollEvents)
		{
			log.debug("val=%f", val);
			
			double loaded = getLoadedRatio();
			if(loaded < 1.0)
			{
				if(val > loaded)
				{
					// this causes flicker
					// TODO perhaps we could simply disable the thumb,
					// or we have to implement our own scroll bar, as the stock javafx
					// scroll bar does not allow for limiting the thumb travel.
					FX.later(() -> 
					{
						editor.getVerticalScrollBar().setValue(loaded);
					});
				}
				
				val /= loaded;
			}
			
			verticalScroll(val);
		}
	}
	
	
	protected Font getFont(CellStyle st)
	{
		if(st.isBold())
		{
			if(st.isItalic())
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
			if(st.isItalic())
			{
				return italicFont;
			}
			else
			{
				return font;
			}
		}
	}


	/** returns insert position or null if cannot find */
	public TextPos getInsertPosition(double screenx, double screeny)
	{
		Point2D p = canvas.screenToLocal(screenx, screeny);
		TextMetrics tm = textMetrics();
		
		double sx = p.getX() - lineNumbersBarWidth;
		if(sx < 0)
		{
			sx = 0;
		}

		double sy = p.getY();
		
		int x = CKit.round(sx / tm.cellWidth);
		if(isWrapLines())
		{
			if(x >= screenColumnCount)
			{
				x = screenColumnCount;
			}
		}
		
		int y = CKit.floor(sy / tm.cellHeight);
		
		int topWrapRow = getTopWrapRow();
		WrapPos wp = advance(topLine, topWrapRow, y);
		
		int charIndex;
		int line = wp.getLine();
		if(isBeyondEOF(wp, y))
		{
			charIndex = -1;
		}
		else
		{
			TextCell cell = wp.getWrapInfo().getCell(TextCell.globalInstance(), wp.getRow(), x + topColumn);
			charIndex = cell.getInsertCharIndex();
		}

		TextPos pos = new TextPos(line, charIndex);
		log.debug("screenx=%f, screeny=%f, pos=%s", screenx, screeny, pos);
		return pos;
	}
	
	
	protected boolean isBeyondEOF(WrapPos wp, int y)
	{
		int max = getModelLineCount();
		if(wp.getLine() >= (max - 1))
		{
			try
			{
				ScreenRow r = screenBuffer.getRow(y);
				if(r.getLineNumber() < 0)
				{
					return true;
				}
				else if(r.getLineNumber() >= max)
				{
					return true;
				}
			}
			catch(Exception e)
			{
				return true;
			}
		}
		return false;
	}
	
	
	protected int getTopWrapRow()
	{
		// I wonder if it's better to derive from topGlyphIndex
		return buffer().getRow(0).getWrapRow();
	}
	
	
	protected Color backgroundColor(boolean caretLine, boolean selected, Color lineColor, Color cellBG)
	{
		Color c = editor.getBackgroundColor();
		
		if(lineColor !=  null)
		{
			c = mixColor(c, lineColor, LINE_COLOR_OPACITY);
		}
		
		if(caretLine)
		{
			c = mixColor(c, editor.getCaretLineColor(), CARET_LINE_OPACITY);
		}
		
		if(selected)
		{
			c = mixColor(c, editor.getSelectionBackgroundColor(), SELECTION_BACKGROUND_OPACITY);
		}
		
		if(cellBG != null)
		{
			c = mixColor(c, cellBG, CELL_BACKGROUND_OPACITY);
		}
		
		return c;
	}
	
	
	protected Color lineNumberBackgroundColor(boolean caretLine)
	{
		Color c = editor.getBackgroundColor();
		
		if(caretLine)
		{
			return c;
		}
		else
		{
			return mixColor(c, Color.GRAY, LINE_NUMBERS_BG_OPACITY);
		}
	}
	
	
	protected ScreenBuffer buffer()
	{
		if(!screenBufferValid)
		{
			reflow();
			screenBufferValid = true;
			
			updateHorizontalScrollBarSize();
			updateVerticalScrollBarSize();
		}
		return screenBuffer;
	}
	
	
	public FlowLine getTextLine(int lineIndex)
	{
		if(lineIndex < 0)
		{
			throw new Error("lineIndex=" + lineIndex);
		}
		
		FxTextEditorModel m = editor.getModel();
		if(m != null)
		{
			if(lineIndex < m.getLineCount())
			{
				FlowLine f = cache.get(lineIndex);
				if(f == null)
				{
					ITextLine t = m.getTextLine(lineIndex);
					f = cache.insert(lineIndex, t);
				}
				return f;
			}
		}
		return FlowLine.BLANK;
	}
	
	
	public void reset()
	{
		screenBuffer.reset();		
		clearFlowLineCache();
		
		invalidate();
		
		if((screenColumnCount == 0) || (screenRowCount == 0))
		{
			return;
		}
		
		updateVerticalScrollBarPosition();
		updateVerticalScrollBarSize();
		
		updateHorizontalScrollBarPosition();
		updateHorizontalScrollBarSize();
	}
	
	
	public void clearFlowLineCache()
	{
		cache.clear();
	}
	
	
	public void setBreakIterator(IBreakIterator b)
	{
		cache.setBreakIterator(b);
	}
	
	
	protected void reflow()
	{
		log.trace();
		
		repaintRequested = false;
		
		int bufferWidth = screenColumnCount + 1;
		int bufferHeight = screenRowCount + 1;
		screenBuffer.setSize(bufferWidth, bufferHeight);
		
		ITabPolicy tabPolicy = editor.getTabPolicy();
		int lineCount = getModelLineCount();
		
		if(isWrapLines())
		{
			FlowLine fline = null;
			WrapInfo wr = null;
			int line = topLine;
			int rowCount = -1;
			int row = -1;
			
			for(int y=0; y<bufferHeight; y++)
			{
				if(fline == null)
				{
					fline = getTextLine(line);
				}
				
				if(wr == null)
				{
					wr = getWrapInfo(fline);
				}
				
				if(rowCount < 0)
				{
					rowCount = wr.getWrapRowCount();
				}

				int startGlyphIndex;
				if(y == 0)
				{
					startGlyphIndex = topGlyphIndex;
					row = wr.getWrapRowForGlyphIndex(startGlyphIndex);
				}
				else
				{
					startGlyphIndex = wr.getGlyphIndexForRow(row);
				}
				
				int lineNumber = (line <= lineCount) ? line : -1;
				
				ScreenRow r = screenBuffer.getRow(y);
				r.init(fline, wr, lineNumber, row, startGlyphIndex);
				
				++row;
				if(row >= rowCount)
				{
					line++;
					fline = null;
					wr = null;
					row = 0;
					rowCount = -1;
				}
			}
		}
		else
		{
			int line = topLine;
			int startGlyphIndex = topGlyphIndex;
			
			for(int y=0; y<bufferHeight; y++)
			{
				FlowLine fline = getTextLine(line);
				WrapInfo wr = getWrapInfo(fline);
				
				int lineNumber = (line <= lineCount) ? line : -1;
				
				ScreenRow r = screenBuffer.getRow(y);
				r.init(fline, wr, lineNumber, 0, startGlyphIndex);
				
				line++;
			}
		}
	}
	
	
	/** returns true if update resulted in a visual change */
	public void update(int startLine, int linesAdded, int endLine)
	{
		log.debug("start=%d end=%d inserted=%d", startLine, endLine, linesAdded);
		
		cache.invalidate(startLine, endLine, linesAdded);
		
		int max = Math.max(endLine, startLine + linesAdded);
		if(max < topLine)
		{
		}
		else if(startLine > (topLine + screenRowCount + 1))
		{
		}
		else
		{
			// TODO repaint only damaged area, unless lines are inserted/removed
			invalidate();
			requestLayout();
			return;
		}
		
		// update scroll bars
		if(linesAdded != 0)
		{
			updateVerticalScrollBarPosition();
			updateVerticalScrollBarSize();
		}
	}
	
	
	protected void paintAll()
	{
		if((screenColumnCount == 0) || (screenRowCount == 0))
		{
			log.trace("screenColumnCount=%d, screenRowCount=%d", screenColumnCount, screenRowCount);
			return;
		}
		
		boolean wrap = isWrapLines();
		boolean showLineNumbers = editor.isShowLineNumbers();
		ScreenBuffer buffer = buffer();
		
		int xmax = screenColumnCount;
		if(!wrap)
		{
			xmax++;
		}

		TextMetrics tm = textMetrics();
		TextCell cell = null;
		int ymax = screenRowCount + 1;
		
		for(int y=0; y<ymax; y++)
		{
			// attempt to limit the canvas queue
			// https://bugs.java.com/bugdatabase/view_bug.do?bug_id=8092801
			// https://github.com/kasemir/org.csstudio.display.builder/issues/174
			// https://stackoverflow.com/questions/18097404/how-can-i-free-canvas-memory
			// https://bugs.openjdk.java.net/browse/JDK-8103438
			gx.clearRect(0, y * tm.cellHeight + 0.5, getWidth(), tm.cellHeight);
			
			ScreenRow row = buffer.getScreenRow(y);

			if(showLineNumbers)
			{
				paintLineNumber(tm, row, y);
			}
			
			for(int x=0; x<xmax; x++)
			{
				cell = row.getCell(x + topColumn);
				GlyphType t = cell.getGlyphType();
				switch(t)
				{
				case EOF:
					paintBlank(tm, row, cell, x, y, xmax - x);
					break;
				case EOL:
					paintBlank(tm, row, cell, x, y, xmax - x);
					x = xmax;
					break;
				case TAB:
					int w = cell.getTabSpan();
					paintBlank(tm, row, cell, x, y, w);
					x += (w - 1);
					break;
				case REG:
					paintCell(tm, row, cell, x, y);
					break;
				default:
					throw new Error("?" + t);
				}
			}
			
			if(wrap)
			{
				paintBlank(tm, row, cell, xmax, y, 1);
			}
		}
	}
	
	
	protected String charAt(String text, int pos, int width)
	{
		int ix = pos - lineNumbersCellCount + text.length();
		if((ix >= 0) && (ix < (text.length() - 0)))
		{
			return text.substring(ix, ix + 1);
		}
		return null;
	}
	
	
	protected void paintLineNumber(TextMetrics tm, ScreenRow row, int y)
	{
		double cy = y * tm.cellHeight;
		
		boolean caretLine = SelectionHelper.isCaretLine(editor.selector.getSelectedSegment(), row);
		
		Color bg = lineNumberBackgroundColor(caretLine);
		gx.setFill(bg);
		gx.fillRect(0, cy, tm.cellWidth * lineNumbersCellCount + lineNumbersGap + lineNumbersGap, tm.cellHeight);
		
		Color fg = editor.getLineNumberColor();
		
		if((y == 0) || row.isBOL())
		{
			int ix = row.getLineNumber();
			if((ix >= 0) && (ix < editor.getLineCount()))
			{
				String text = editor.getLineNumberFormatter().formatLineNumber(ix + 1);
	
				for(int i=0; i<lineNumbersCellCount; i++)
				{
					String s = charAt(text, i, lineNumbersCellCount);
					if(s != null)
					{
						double cx = i * tm.cellWidth + lineNumbersGap;
						
						gx.setFont(font);
						gx.setFill(fg);
						gx.fillText(s, cx, cy - tm.baseline, tm.cellWidth);
					}
				}
			}
		}
	}
	
	
	protected void paintBlank(TextMetrics tm, ScreenRow row, TextCell cell, int x, int y, int count)
	{
		double cx = x * tm.cellWidth + lineNumbersBarWidth;
		double cy = y * tm.cellHeight;
		
		int line = row.getLineNumber();
		int flags = SelectionHelper.getFlags(this, editor.selector.getSelectedSegment(), line, cell, x);
		boolean caretLine = SelectionHelper.isCaretLine(flags);
		boolean caret = paintCaret.get() ? SelectionHelper.isCaret(flags) : false;
		boolean selected = SelectionHelper.isSelected(flags);
		
		Color bg = backgroundColor(caretLine, selected, row.getLineColor(), null);
		gx.setFill(bg);
		gx.fillRect(cx, cy, tm.cellWidth * count, tm.cellHeight);
		
		// caret
		if(caret)
		{
			// TODO insert mode
			gx.setFill(caretColor);
			gx.fillRect(cx, cy, 2, tm.cellHeight);
		}
	}
	

	protected void paintCell(TextMetrics tm, ScreenRow row, TextCell cell, int x, int y)
	{
		double cx = x * tm.cellWidth + lineNumbersBarWidth;
		double cy = y * tm.cellHeight;
		
		int line = row.getLineNumber();
		int flags = SelectionHelper.getFlags(this, editor.selector.getSelectedSegment(), line, cell, x);
		boolean caretLine = SelectionHelper.isCaretLine(flags);
		boolean caret = SelectionHelper.isCaret(flags);
		boolean selected = SelectionHelper.isSelected(flags);
		
		// style
		CellStyle style = row.getCellStyles(cell);
		if(style == null)
		{
			style = NO_STYLE;
		}
		
		// background
		Color bg = backgroundColor(caretLine, selected, row.getLineColor(), style.getBackgroundColor());
		gx.setFill(bg);
		gx.fillRect(cx, cy, tm.cellWidth, tm.cellHeight);
		
		// caret
		if(paintCaret.get())
		{
			if(caret)
			{
				// TODO insert mode
				gx.setFill(caretColor);
				gx.fillRect(cx, cy, 2, tm.cellHeight);
			}
		}
		
		if(style.isUnderscore())
		{
			// TODO special property, mix with background
			gx.setFill(textColor);
			gx.fillRect(cx, cy + tm.cellHeight - 1, tm.cellWidth, 1);
		}
		
		// text
		String text = row.getCellText(cell);
		if(text != null)
		{
			Color fg = style.getTextColor();
			if(fg == null)
			{
				fg = getTextColor();
			}
			
			Font f = getFont(style);
			gx.setFont(f);
			gx.setFill(fg);
			gx.fillText(text, cx, cy - tm.baseline, tm.cellWidth);
		
			if(style.isStrikeThrough())
			{
				// TODO special property, mix with background
				gx.setFill(textColor);
				gx.fillRect(cx, cy + tm.cellHeight/2, tm.cellWidth, 1);
			}
		}
	}
	
	
	public void scroll(int scrollSizeInLines, boolean up)
	{
		log.trace("scroll=%d %s", scrollSizeInLines, up);

		if(scrollSizeInLines < 1)
		{
			scrollSizeInLines = 1;
		}
		else if(scrollSizeInLines > getScreenRowCount())
		{
			scrollSizeInLines = getScreenRowCount();
		}
		
		shiftViewPort(up ? -scrollSizeInLines : scrollSizeInLines);
	}

	
	/** scrolls up (deltaInPixels < 0) or down (deltaInPixels > 0) */
	public void blockScroll(double deltaInPixels)
	{
		log.debug("blockScroll=%f", deltaInPixels);

		TextMetrics tm = textMetrics();
		double ch = tm.cellHeight;
		
		int delta;
		if(deltaInPixels < 0)
		{
			delta = (int)Math.floor(deltaInPixels / ch);
		}
		else
		{
			delta = (int)Math.ceil(deltaInPixels / ch);
		}
		
		shiftViewPort(delta);
	}
	

	public void verticalScroll(double fraction)
	{
		int lineCount = getModelLineCount();
		int vis = screenRowCount;
		int max = Math.max(0, lineCount + 1 - vis);
		int top = CKit.round(max * fraction);
		int gix;

		if(isWrapLines())
		{
			// TODO use ScrollAssist?
			VerticalScrollHelper h = new VerticalScrollHelper(this, lineCount, top, fraction);
			GlyphPos p = h.process();

			top = p.getLine();
			gix = p.getGlyphIndex();
		}
		else
		{
			gix = 0;
		}

		setOrigin(top, gix);
	}
	
	
	public WrapInfo getWrapInfo(int line)
	{
		FlowLine fline = getTextLine(line);
		return getWrapInfo(fline);
	}


	public WrapInfo getWrapInfo(FlowLine fline)
	{
		// wrapping info is cached byFlowLine
		return fline.getWrapInfo(editor.getTabPolicy(), screenColumnCount, isWrapLines());
	}
	

	/** adjusts the scroll bars to make the caret visible. */
	public void scrollCaretToView()
	{
		// do we need to move at all?
		// if yes, find out where
		
		EditorSelection sel = editor.getSelection();
		Marker caret = sel.getCaret();
		if(caret == null)
		{
			return;
		}
		else if(isVisible(caret))
		{
			return;
		}
		
		int caretLine = caret.getLine();
		
		if(isWrapLines())
		{
			int delta;
			if(caretLine <= topLine)
			{
				// above the view port: position caret on the top line
				delta = 0;
			}
			else
			{
				// below the view port: position caret on the bottom line
				delta = 1 - screenRowCount;
			}

			WrapInfo wr = getWrapInfo(caretLine);
			int caretWrapRow = wr.getWrapRowForCharIndex(caret.getCharIndex());
			
			WrapPos wp = advance(caretLine, caretWrapRow, delta);
			wp = ensureLastPageFullView(wp);
			int line = wp.getLine();
			int gix = wp.getStartGlyphIndex();
			
			setOrigin(line, gix);
		}
		else
		{
			int topCell = topColumn;
			
			FlowLine fline = getTextLine(caretLine);
			int x = getColumnAt(caretLine, caret.getCharIndex());
			if(x < topColumn)
			{
				x = x - HORIZONTAL_SAFETY;
				if(x < HORIZONTAL_SAFETY)
				{
					x = 0;
				}
				topCell = x;
			}
			else if(x >= (topColumn + screenColumnCount))
			{
				x = x + 1 - screenColumnCount;
				if(x < 0)
				{
					x = 0;
				}
				topCell = x;
			}
			
			int top = topLine;
			if(caretLine < topLine)
			{
				int y = caretLine;
				top = y;
			}
			else if(caretLine >= (topLine + screenRowCount))
			{
				int y = caretLine + VERTICAL_SAFETY - screenRowCount;
				if(y < 0)
				{
					y = 0;
				}
				top = y;
			}
			
			int prevTopCell = topColumn;
			int prevTopLine = topLine;
			
			setTopCellIndex(topCell);
			setOrigin(top, 0);
		}
	}
	
	
	protected boolean isVisible(Marker m)
	{
		if(m == null)
		{
			return false; // TODO double click after search??
		}
		
		// some quick checks
		int line = m.getLine();
		if(line < topLine)
		{
			return false;
		}
		else if(line >= (topLine + screenRowCount))
		{
			return false;
		}
		
		if(isWrapLines())
		{
			FlowLine fline = getTextLine(line);
			int gix = fline.getGlyphIndex(m.getCharIndex());
			
			ScreenRow r = buffer().getScreenRow(0);
			if(compare(line, gix, r.getLineNumber(), r.getStartGlyphIndex()) < 0)
			{
				return false;
			}
			
			r = buffer().getScreenRow(screenRowCount - 1);
			if(r.getLineNumber() < 0)
			{
				return true; // null model
			}
			
			if(compare(line, gix, r.getLineNumber(), r.getStartGlyphIndex() + r.getGlyphCount()) > 0)
			{
				return false;
			}
		}
		else
		{
			WrapInfo wr = getWrapInfo(m.getLine());
			int col = wr.getColumnForCharIndex(m.getCharIndex());
			if(col < topColumn)
			{
				return false;
			}
			else if(col >= (topColumn + screenColumnCount))
			{
				return false;
			}
		}
		
		return true;
	}
	
	
	protected static int compare(int lineA, int glyphIndexA, int lineB, int glyphIndexB)
	{
		// sanity checks
		if(lineA < 0)
		{
			throw new Error();
		}
		else if(lineB < 0)
		{
			throw new Error();
		}
		
		int d = lineA - lineB;
		if(d == 0)
		{
			// sanity checks
			if(glyphIndexA < 0)
			{
				throw new Error();
			}
			else if(glyphIndexB < 0)
			{
				throw new Error();
			}
			
			d = glyphIndexA - glyphIndexB;
		}
		return d;
	}
	
	
	public int getPhantomColumn()
	{
		return phantomColumn;
	}
	
	
	/** 
	 * returns the cursor column at the moment the movement was first initiated.
	 * sets the phantom column if it's the first move
	 */
	public int updatePhantomColumn(int line, int charIndex)
	{
		int col = getPhantomColumn();
		if(col < 0)
		{
			col = getColumnAt(line, charIndex);
			setPhantomColumn(col);
		}
		return col;
	}
	
	
	public int getColumnAt(int line, int charIndex)
	{
		WrapInfo wr = getWrapInfo(line);
		return wr.getColumnForCharIndex(charIndex);
	}
	
	
	public void setPhantomColumn(int x)
	{
		log.debug(x);
		phantomColumn = x;
	}
	
	
	public void setPhantomColumn(int line, int charIndex)
	{
		int col = getColumnAt(line, charIndex);
		setPhantomColumn(col);
	}
	
	
	public void setPhantomColumnFromCursor()
	{
		Marker m = editor.getSelection().getCaret();
		if(m == null)
		{
			return;
		}
		
		int line = m.getLine();
		int charIndex = m.getCharIndex();
		int col = getColumnAt(line, charIndex);
		setPhantomColumn(col);
	}
	
	
	/** 
	 * Walks the wrapped rows, starting with (startLine, startWrapRow),
	 * either forwards (delta > 0) or backwards (delta < 0).
	 * 
	 * Returns the new row position.
	 */ 
	public WrapPos advance(int startLine, int startWrapRow, int delta)
	{
		log.trace("line=%d row=%d delta=%d", startLine, startWrapRow, delta);
		
		WrapInfo wr = getWrapInfo(startLine);
		int line = startLine;
		int row = startWrapRow;
		int steps = Math.abs(delta);
		
		if(delta < 0)
		{
			while(steps > 0)
			{
				if(steps <= row)
				{
					row -= steps;
					break;
				}
				else if(line == 0)
				{
					break;
				}
				else
				{
					steps -= (row + 1);
					line--;
					wr = getWrapInfo(line);
					row = wr.getWrapRowCount() - 1;
				}
			}
		}
		else if(delta > 0)
		{
			int size = wr.getWrapRowCount() - row;
			int last = getModelLineCount() - 1;

			while(steps > 0)
			{
				if(steps < size)
				{
					row += steps;
					break;
				}
				else
				{
					steps -= size;
					
					if(line >= last)
					{
						break;
					}
					
					line++;
					row = 0;
					wr = getWrapInfo(line);
					size = wr.getWrapRowCount();
				}
			}
		}
		
		WrapPos p = new WrapPos(line, row, wr);
		log.trace(p);
		return p;
	}
}
