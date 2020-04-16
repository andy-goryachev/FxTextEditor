// Copyright © 2019-2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.log.Log;
import goryachev.common.util.CKit;
import goryachev.common.util.D;
import goryachev.fx.CPane;
import goryachev.fx.FX;
import goryachev.fx.FxBoolean;
import goryachev.fx.FxBooleanBinding;
import goryachev.fxtexteditor.internal.FlowLine;
import goryachev.fxtexteditor.internal.FlowLineCache;
import goryachev.fxtexteditor.internal.GlyphIndex;
import goryachev.fxtexteditor.internal.NonWrappingReflowHelper;
import goryachev.fxtexteditor.internal.ScreenBuffer;
import goryachev.fxtexteditor.internal.ScreenRow;
import goryachev.fxtexteditor.internal.SelectionHelper;
import goryachev.fxtexteditor.internal.VerticalScrollHelper;
import goryachev.fxtexteditor.internal.WrapInfo;
import goryachev.fxtexteditor.internal.WrappingReflowHelper;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.BooleanExpression;
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
 * Visual flow container lays out cells inside the screen buffer and paints the canvas. 
 */
public class VFlow
	extends CPane
{
	private static final int LINE_CACHE_SIZE = 1024;
	private static final double LINE_NUMBERS_BG_OPACITY = 0.1;
	private static final double CARET_LINE_OPACITY = 0.3;
	private static final double SELECTION_BACKGROUND_OPACITY = 0.4;
	private static final double CELL_BACKGROUND_OPACITY = 0.8;
	
	protected final Log log = Log.get("VFlow");
	protected final FxTextEditor editor;
	protected final FxBoolean caretEnabledProperty = new FxBoolean(true);
	protected final FxBoolean suppressBlink = new FxBoolean(false);
	protected final BooleanExpression paintCaret;
	protected final ScreenBuffer buffer = new ScreenBuffer(this);
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
	private int columnCount;
	private int rowCount;
	private int lineNumbersCellCount;
	private int lineNumbersBarWidth;
	private int minLineNumberCellCount = 3; // arbitrary number
	private int lineNumbersGap = 5; // arbitrary number
	private Color textColor = Color.BLACK;
	private Color caretColor = Color.BLACK;
	private int topLine;
	private GlyphIndex topGlyphIndex = GlyphIndex.ZERO;
	private int topCellIndex;
	private boolean screenBufferValid;
	private boolean repaintRequested;
	protected final FlowLineCache cache;
	protected final SelectionHelper selectionHelper = new SelectionHelper();
	private static final CellStyle NO_STYLE = new CellStyle();
	
	
	public VFlow(FxTextEditor ed)
	{
		this.editor = ed;
		cache = new FlowLineCache(ed, LINE_CACHE_SIZE);
		
		setMinWidth(0);
		setMinHeight(0);
		
		cursorAnimation = createCursorAnimation();
		
		setFocusTraversable(true);
		
		FX.onChange(this::repaint, ed.backgroundColorProperty());
		FX.onChange(this::handleSizeChange,  widthProperty(), heightProperty());
		FX.onChange(this::updateModel, ed.modelProperty());
		FX.onChange(this::updateLineNumbers, ed.showLineNumbersProperty, ed.lineNumberFormatterProperty, ed.modelProperty);
		FX.onChange(this::updateFont, true, ed.fontProperty);
		
		// TODO clip rect
		
		paintCaret = new FxBooleanBinding(caretEnabledProperty, editor.displayCaretProperty, editor.focusedProperty(), editor.disabledProperty(), suppressBlink)
		{
			protected boolean computeValue()
			{
				return (caretEnabledProperty.get() || suppressBlink.get()) && editor.isDisplayCaret() && editor.isFocused() && (!editor.isDisabled());
			}
		};
		paintCaret.addListener((s,p,c) -> refreshCursor());
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
	
	
	public GlyphIndex getTopGlyphIndex()
	{
		return topGlyphIndex;
	}
	
	
	public void setOrigin(int top, GlyphIndex ix)
	{
		topLine = top;
		topGlyphIndex = ix;
		
		updateLineNumbers();
		invalidate();
	}
	
	
	/** returns the leftmost display cell index */
	public int getTopCellIndex()
	{
		return topCellIndex;
	}
	
	
	/** meaningful only in non-wrapped mode */
	public void setTopCellIndex(int ix)
	{
		topCellIndex = ix;
		invalidate();
	}
	
	
	public int getScreenColumnCount()
	{
		return columnCount;
	}
	
	
	public int getScreenRowCount()
	{
		return rowCount;
	}
	
	
	public int getModelLineCount()
	{
		return editor.getLineCount();
	}
	
	
	public int getMaxColumnCount()
	{
		return buffer().getWidth();
	}
	
	
	/** 
	 * returns the maximum number of horizontal screen cells required to display the 
	 * visible text in the screen buffer.
	 * valid only in non-wrapping mode.
	 */
	public int getMaxCellCount()
	{
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
	
	
	protected void updateModel()
	{
		log.trace();
		invalidate();
	}
	
	
	protected void handleSizeChange()
	{
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
		double w = getWidth() - m.getLeft() - m.getRight();
		double h = getHeight() - m.getTop() - m.getBottom();
		return new Canvas(w + 1, h + 1);
	}
	
	
	/** makes screen buffer invalid.  triggers full screen update */
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
			int lastLine = getTopLine() + rowCount;
			String s = editor.getLineNumberFormatter().format(lastLine);
			count = Math.max(minLineNumberCellCount, s.length());
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

		columnCount = CKit.floor(w / tm.cellWidth);
		rowCount = CKit.floor(h / tm.cellHeight);
		
//		FX.later(this::updateScrollBars);
//		updateScrollBars();
	}
	
	
	/** updates thumb size; depends on a wrapping pass in reflow() */
	protected void updateHorizontalScrollBarSize()
	{
		if(!editor.isWrapLines())
		{
			int max = getMaxCellCount() + 1; // allow for 1 blank space at the end
			double vis = getMaxColumnCount();
			double thumbSize = vis / max;
			editor.hscroll.setVisibleAmount(thumbSize);
		}
	}
	
	
	protected void updateVerticalScrollBarSize()
	{
		if(editor.isWrapLines())
		{
			double val = computeVerticalScrollBarThumbSize();
			editor.getVerticalScrollBar().setVisibleAmount(val);
		}
	}
	
	
	protected double computeVerticalScrollBarThumbSize()
	{
		FxTextEditorModel model = editor.getModel();
		if(model == null)
		{
			return 1.0;
		}
		
		// add the number of additional rows created due to wrapping
		int extraRows = 0;
		
		int frameSize = 10;
		
		int min = Math.max(0, topLine - frameSize);
		for(int ix=topLine; ix>=min; ix--)
		{
			FlowLine fline = getTextLine(ix);
			WrapInfo wr = getWrapInfo(fline);
			int ct = wr.getRowCount();
			if(ct > 1)
			{
				extraRows += (ct - 1);
			}
		}
		
		// TODO when startGlyphIndex != 0
		
		int max = Math.min(topLine + getScreenRowCount() + frameSize, model.getLineCount());
		for(int ix=topLine; ix<max; ix++)
		{
			FlowLine fline = getTextLine(ix);
			WrapInfo wr = getWrapInfo(fline);
			int ct = wr.getRowCount();
			if(ct > 1)
			{
				extraRows += (ct - 1);
			}
		}
		
		// TODO add the number of extra rows due to wrapping (for visible lines)
		int total = model.getLineCount() + 2 + extraRows;
		int visible = getScreenRowCount();
		if(visible > total)
		{
			// TODO perhaps suppress scrollbar thumb, but keep the scroll bar itself to avoid another reflow
			visible = total;
		}
		return visible / (double)total;
	}
	

	/** requests a repaint.  the actual drawing happens in runLater() */
	protected void repaint()
	{
		if(!repaintRequested)
		{
			repaintRequested = true;
			FX.later(() ->
			{
				paintAll();
				repaintRequested = false;
			});
		}
	}
	
	
	public void repaintSegment(SelectionSegment s)
	{
		if(s != null)
		{
			// TODO repaint only the damaged area
			repaint();
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
		TextMetrics m = textMetrics();
		// TODO hor scrolling
		
		double sx = p.getX() - lineNumbersBarWidth;
		if(sx < 0)
		{
			sx = 0;
		}
		double sy = p.getY();
		
		int x = CKit.round(sx / m.cellWidth);
		int y = CKit.floor(sy / m.cellHeight);
		TextPos pos = buffer().getInsertPosition(x, y);
		if(pos == null)
		{
			pos = new TextPos(getModelLineCount(), 0, true);
		}
		
		log.debug(pos);
		return pos;
	}
	
	
	protected Color backgroundColor(boolean caretLine, boolean selected, Color cellBG)
	{
		Color c = editor.getBackgroundColor();
		
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
		return buffer;
	}
	
	
	public FlowLine getTextLine(int lineIndex)
	{
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
		buffer.reset();
	}
	
	
	public void clearTextCellsCache()
	{
		cache.clear();
	}
	
	
	protected void reflow()
	{
		log.trace();
		
		boolean wrap = editor.isWrapLines();
		int bufferWidth = getScreenColumnCount() + 1;
		int bufferHeight = getScreenRowCount() + 1;
		
		buffer.setSize(bufferWidth, bufferHeight);
		
		ITabPolicy tabPolicy = editor.getTabPolicy();
		
		if(wrap)
		{
			WrappingReflowHelper.reflow(this, buffer, getScreenColumnCount(), bufferHeight, tabPolicy);
		}
		else
		{
			NonWrappingReflowHelper.reflow(this, buffer, bufferWidth, bufferHeight, tabPolicy);
		}
		
		log.trace(() -> buffer.dump());
	}
	
	
	/** returns true if update resulted in a visual change */
	public boolean update(int startLine, int linesInserted, int endLine)
	{
		int max = Math.max(endLine, startLine + linesInserted);
		if(max < topLine)
		{
			return false;
		}
		else if(startLine > (topLine + getScreenRowCount()))
		{
			return false;
		}
		
		// TODO optimize, but for now simply
		invalidate();
		requestLayout();
		
		return true;
	}
	
	
	protected void paintAll()
	{
		log.trace();
		
		if((columnCount == 0) || (rowCount == 0))
		{
			return;
		}
		
		boolean wrap = editor.isWrapLines();
		boolean showLineNumbers = editor.isShowLineNumbers(); // TODO
		ScreenBuffer b = buffer();
		
		int xmax = columnCount;
		if(!wrap)
		{
			xmax++;
		}
		int ymax = rowCount + 1;
		
		for(int y=0; y<ymax; y++)
		{
			ScreenRow row = b.getScreenRow(y);

			if(showLineNumbers)
			{
				paintLineNumber(row, y);
			}
			
			for(int x=0; x<xmax; x++)
			{
				GlyphIndex gix = row.getGlyphIndex(x);
				if(gix.isEOF())
				{
					paintBlank(row, x, y, xmax - x);
					break;
				}
				
				if(gix.isEOL())
				{
					paintBlank(row, x, y, xmax - x);
					x = xmax;
				}
				else if(gix.isInsideTab())
				{
					int w = -gix.intValue();
					paintBlank(row, x, y, w);
					x += (w - 1);
				}
				else
				{
					paintCell(row, x, y);	
				}
			}
			
			if(wrap)
			{
				paintBlank(row, xmax, y, 1);
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
	
	
	protected void paintLineNumber(ScreenRow row, int y)
	{
		TextMetrics tm = textMetrics();
		double ch = tm.cellHeight;
		double cw = tm.cellWidth;
		double cy = y * ch;
		
		boolean caretLine = SelectionHelper.isCaretLine(editor.selector.getSelectedSegment(), row);
		
		Color bg = lineNumberBackgroundColor(caretLine);
		gx.setFill(bg);
		
		Color fg = Color.GRAY; // FIX
		
		gx.fillRect(0, cy, cw * lineNumbersCellCount + lineNumbersGap + lineNumbersGap, ch);
		
		if((y == 0) || row.isBOL())
		{
			int ix = row.getLineIndex();
			if((ix >= 0) && (ix <= (editor.getLineCount() + 1)))
			{
				String text = editor.getLineNumberFormatter().format(ix + 1);
	
				for(int i=0; i<lineNumbersCellCount; i++)
				{
					String s = charAt(text, i, lineNumbersCellCount);
					if(s != null)
					{
						double cx = i * cw + lineNumbersGap;
						
						gx.setFont(font);
						gx.setFill(fg);
						gx.fillText(s, cx, cy - tm.baseline, cw);
					}
				}
			}
		}
	}
	
	
	protected void paintBlank(ScreenRow row, int x, int y, int count)
	{
		TextMetrics tm = textMetrics();
		double ch = tm.cellHeight;
		double cw = tm.cellWidth;
		double cx = x * cw + lineNumbersBarWidth;
		double cy = y * ch;
		
		cw *= count;
		
		int flags = SelectionHelper.getFlags(editor.selector.getSelectedSegment(), row, x);
		boolean caretLine = SelectionHelper.isCaretLine(flags);
		boolean caret = paintCaret.get() ? SelectionHelper.isCaret(flags) : false;
		boolean selected = SelectionHelper.isSelected(flags);
		
		Color bg = backgroundColor(caretLine, selected, null);
		gx.setFill(bg);
		gx.fillRect(cx, cy, cw, ch);
		
		// caret
		if(caret)
		{
			// TODO insert mode
			gx.setFill(caretColor);
			gx.fillRect(cx, cy, 2, ch);
		}
	}
	

	protected void paintCell(ScreenRow row, int x, int y)
	{
		TextMetrics tm = textMetrics();
		double ch = tm.cellHeight;
		double cw = tm.cellWidth;
		double cx = x * cw + lineNumbersBarWidth;
		double cy = y * ch;
		
		int flags = SelectionHelper.getFlags(editor.selector.getSelectedSegment(), row, x);
		boolean caretLine = SelectionHelper.isCaretLine(flags);
		boolean caret = SelectionHelper.isCaret(flags);
		boolean selected = SelectionHelper.isSelected(flags);
		
		// style
		CellStyle style = row.getCellStyles(x);
		if(style == null)
		{
			style = NO_STYLE;
		}
		
		// background
		Color bg = backgroundColor(caretLine, selected, style.getBackgroundColor());
		gx.setFill(bg);
		gx.fillRect(cx, cy, cw, ch);
		
		// caret
		if(paintCaret.get())
		{
			if(caret)
			{
				// TODO insert mode
				gx.setFill(caretColor);
				gx.fillRect(cx, cy, 2, ch);
			}
		}
		
		// text
		String text = row.getCellText(x);
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
			gx.fillText(text, cx, cy - tm.baseline, cw);
		
			// TODO underline, strikethrough
		}
	}
	
	
	public void scroll(double fractionOfHeight)
	{
		// TODO
		log.debug("scroll={}", fractionOfHeight);
	}


	public void verticalScroll(double fraction, boolean wrapLines)
	{
		int lineCount = getModelLineCount();
		int vis = getScreenRowCount();
		int max = Math.max(0, lineCount + 1 - vis);
		int top = CKit.round(max * fraction);
		GlyphIndex gix;

		if(wrapLines)
		{
			// TODO this does not account for all additional rows wrapped below the view port
			// we need to account for those for correct scrolling of very long lines (or maybe approximate)
			
			// TODO startGlyphIndex is also needed
			
			VerticalScrollHelper h = new VerticalScrollHelper(this, lineCount, top, fraction, topGlyphIndex);
			h.process();

			top = h.getNewTopLine();
			gix = h.getNewGlyphIndex();
		}
		else
		{
			gix = GlyphIndex.ZERO;
		}

		setOrigin(top, gix);
	}


	public WrapInfo getWrapInfo(FlowLine fline)
	{
		// wrapping info is cached
		return fline.getWrapInfo(editor.getTabPolicy(), getScreenColumnCount());
	}
	
	
	public void pageUp()
	{
		// TODO
	}
	
	
	public void pageDown()
	{
		// TODO need the concept of last caret
		// single caret: create phantom x position, move caret + screen height
		// multiple carets: reset to a single caret using last caret, then follow the single caret logic
		
		// TODO
	}
	

	/** 
	 * scrolls the last selection caret to visible area.  
	 * makes an attempt to make visible all the carets, if that is possible 
	 */
	public void scrollSelectionToVisible()
	{
		int min;
		int max;
		int last;
		
		// do we need to move at all (check only last caret)?
		// if yes, find out where
		
//		SelectionSegment[] sel = getSelectionSegments();
		EditorSelection sel = editor.getSelection();
		Marker m = sel.getCaret();
		if(isVisible(m))
		{
			return;
		}
		
		// TODO
		D.print("scrollSelectionToVisible TODO");
	}
	
	
	protected boolean isVisible(Marker m)
	{
		FlowLine fline = getTextLine(topLine);
		int pos = fline.getCharIndex(topGlyphIndex);
		if(m.isBefore(topLine, pos))
		{
			return false;
		}
		
		int h = buffer.getHeight();
		int w = buffer.getWidth();
		
		ScreenRow r = buffer.getScreenRow(h - 1);
		int line = r.getLineIndex();
		if(line < 0)
		{
			return true;
		}
		
		if(topLine != line)
		{
			fline = getTextLine(line);
			if(fline == null)
			{
				return true;
			}
		}
		
		GlyphIndex gix = r.getGlyphIndex(w - 1);
		pos = fline.getCharIndex(gix);
		if(m.isAfter(line, pos))
		{
			return false;
		}
		
		return true;
	}
}
