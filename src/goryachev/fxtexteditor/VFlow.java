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
import goryachev.fxtexteditor.internal.ScreenBuffer;
import goryachev.fxtexteditor.internal.ScreenRow;
import goryachev.fxtexteditor.internal.SelectionHelper;
import goryachev.fxtexteditor.internal.TextCell;
import goryachev.fxtexteditor.internal.VerticalScrollHelper;
import goryachev.fxtexteditor.internal.WrapAssist;
import goryachev.fxtexteditor.internal.WrapInfo;
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
	protected static final Log log = Log.get("VFlow");
	protected static final int LINE_CACHE_SIZE = 1024;
	protected static final double LINE_NUMBERS_BG_OPACITY = 0.1;
	protected static final double CARET_LINE_OPACITY = 0.3;
	protected static final double SELECTION_BACKGROUND_OPACITY = 0.4;
	protected static final double CELL_BACKGROUND_OPACITY = 0.8;
	protected static final int HORIZONTAL_SAFETY = 8;
	protected static final int VERTICAL_SAFETY = 2;
	
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
	private int phantomColumn = -1;
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
		FX.onChange(this::handleWrapChange, ed.wrapLinesProperty);
		
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
		log.debug("%d %s", top, ix);
		
		topLine = top;
		topGlyphIndex = ix;
		
		updateLineNumbers();
		invalidate();
	}
	
	
	/** returns the leftmost display cell index (glyph index) */
	public int getTopCellIndex()
	{
		return topCellIndex;
	}
	
	
	/** meaningful only in non-wrapped mode */
	public void setTopCellIndex(int ix)
	{
		if(topCellIndex != ix)
		{
			log.debug("%d", ix);
			
			topCellIndex = ix;
			invalidate();
		}
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
	
	
	public boolean isWrapColumn(int x)
	{
		if(isWrapLines())
		{
			if(x == getScreenColumnCount())
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
			if(max <= getScreenColumnCount())
			{
				v = 0.0;
			}
			else
			{
				max -= getScreenColumnCount();
				v = topCellIndex / (double)max;
			}
			
			editor.setHandleScrollEvents(false);
			try
			{
				editor.getHorizontalScrollBar().setValue(v);
			}
			finally
			{
				editor.setHandleScrollEvents(true);
			}
		}
	}
	
	
	protected void updateVerticalScrollBarSize()
	{
		if(isWrapLines())
		{
			double val = computeVerticalScrollBarThumbSize();
			editor.getVerticalScrollBar().setVisibleAmount(val);
		}
	}
	
	
	protected void updateVerticalScrollBarPosition()
	{
		D.print(); // FIX
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
			int ct = wr.getWrapRowCount();
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
			int ct = wr.getWrapRowCount();
			if(ct > 1)
			{
				extraRows += (ct - 1);
			}
		}
		
		// add the number of extra rows due to wrapping (for visible lines)
		int total = model.getLineCount() + 2 + extraRows;
		int visible = getScreenRowCount();
		if(visible > total)
		{
			// TODO perhaps suppress scrollbar thumb, but keep the scroll bar itself to avoid another reflow
			visible = total;
		}
		return visible / (double)total;
	}
	

	/** requests a repaint, the actual drawing will happen in runLater() */
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
	
	
	protected void handleWrapChange()
	{
		if(editor.isWrapLines())
		{
			topCellIndex = 0;
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
		
		double sx = p.getX() - lineNumbersBarWidth;
		if(sx < 0)
		{
			sx = 0;
		}

		double sy = p.getY();
		
		int x = CKit.round(sx / m.cellWidth);
		if(isWrapLines())
		{
			if(x >= getScreenColumnCount())
			{
				x = getScreenColumnCount();
			}
		}
		
		int y = CKit.floor(sy / m.cellHeight);
		
		int topWrapRow = buffer().getRow(0).getWrapRow();
		WrapPos wp = navigate(topLine, topWrapRow, y, false);
		
		TextPos pos;
		if(wp == null)
		{
			if(y < 0)
			{
				pos = new TextPos(0, 0);
			}
			else
			{
				pos = new TextPos(getModelLineCount(), 0);
			}
		}
		else
		{
			TextCell cell = wp.getWrapInfo().getCell(wp.getRow(), x + topCellIndex);
			int charIndex = cell.getInsertCharIndex();
			
			int line = wp.getLine();
			if(line > getModelLineCount())
			{
				line = getModelLineCount();
			}
		
			pos = new TextPos(line, charIndex);
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
		
		int bufferWidth = getScreenColumnCount() + 1;
		int bufferHeight = getScreenRowCount() + 1;
		buffer.setSize(bufferWidth, bufferHeight);
		
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
					startGlyphIndex = topGlyphIndex.intValue(); // TODO replace with int
					row = wr.getWrapRowForGlyphIndex(startGlyphIndex);
				}
				else
				{
					startGlyphIndex = wr.getGlyphIndexForRow(row);
				}
				
				int lineNumber = (line <= lineCount) ? line : -1;
				
				ScreenRow r = buffer.getRow(y);
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
			int startGlyphIndex = topGlyphIndex.intValue(); // TODO replace with int
			
			for(int y=0; y<bufferHeight; y++)
			{
				FlowLine fline = getTextLine(line);
				WrapInfo wr = getWrapInfo(fline);
				
				int lineNumber = (line <= lineCount) ? line : -1;
				
				ScreenRow r = buffer.getRow(y);
				r.init(fline, wr, lineNumber, 0, startGlyphIndex);
				
				line++;
			}
		}
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
		
		boolean wrap = isWrapLines();
		boolean showLineNumbers = editor.isShowLineNumbers();
		ScreenBuffer b = buffer();
		
		int xmax = columnCount;
		if(!wrap)
		{
			xmax++;
		}

		TextCell cell = null;
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
				cell = row.getCell(x + topCellIndex);
				GlyphType t = cell.getGlyphType();
				switch(t)
				{
				case EOF:
					paintBlank(row, cell, x, y, xmax - x);
					break;
				case EOL:
					paintBlank(row, cell, x, y, xmax - x);
					x = xmax;
					break;
				case TAB:
					int w = cell.getTabSpan();
					paintBlank(row, cell, x, y, w);
					x += (w - 1);
					break;
				case REG:
					paintCell(row, cell, x, y);
					break;
				default:
					throw new Error("?" + t);
				}
			}
			
			if(wrap)
			{
				paintBlank(row, cell, xmax, y, 1);
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
			int ix = row.getLineNumber();
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
	
	
	protected void paintBlank(ScreenRow row, TextCell cell, int x, int y, int count)
	{
		TextMetrics tm = textMetrics();
		double ch = tm.cellHeight;
		double cw = tm.cellWidth;
		double cx = x * cw + lineNumbersBarWidth;
		double cy = y * ch;
		
		cw *= count;
		
		int line = row.getLineNumber();
		int flags = SelectionHelper.getFlags(this, editor.selector.getSelectedSegment(), line, cell, x);
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
	

	protected void paintCell(ScreenRow row, TextCell cell, int x, int y)
	{
		TextMetrics tm = textMetrics();
		double ch = tm.cellHeight;
		double cw = tm.cellWidth;
		double cx = x * cw + lineNumbersBarWidth;
		double cy = y * ch;
		
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
			gx.fillText(text, cx, cy - tm.baseline, cw);
		
			// TODO underline, strikethrough
		}
	}
	
	
	public void scroll(double fractionOfHeight)
	{
		// TODO
		log.debug("scroll=%f", fractionOfHeight);
	}


	public void verticalScroll(double fraction)
	{
		int lineCount = getModelLineCount();
		int vis = getScreenRowCount();
		int max = Math.max(0, lineCount + 1 - vis);
		int top = CKit.round(max * fraction);
		GlyphIndex gix;

		if(isWrapLines())
		{
			VerticalScrollHelper h = new VerticalScrollHelper(this, lineCount, top, fraction);
			GlyphPos p = h.process();

			top = p.getLine();
			gix = p.getGlyphIndex();
		}
		else
		{
			gix = GlyphIndex.ZERO;
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
		return fline.getWrapInfo(editor.getTabPolicy(), getScreenColumnCount(), isWrapLines());
	}
	

	/** adjusts the scroll bars to make the caret visible. */
	public void scrollCaretToView()
	{
		int min;
		int max;
		int last;
		
		// TODO this needs to work in non-wrapped mode
		// TODO horizontal scroll
		
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
			// TODO use navigate()
			WrapAssist wr = new WrapAssist(this, caretLine, caret.getCharIndex());
	
			int delta;
			if(caretLine < topLine)
			{
				// above the view port: position caret on the 2nd line if possible
				delta = -2;
			}
			else
			{
				// below the view port: position caret on the 2nd line from the bottom
				delta = getScreenRowCount() - 2;
			}
	
			GlyphPos p = wr.move(delta);
			int line = p.getLine();
			GlyphIndex gix = p.getGlyphIndex();
			
			setOrigin(line, gix);
		}
		else
		{
			int topCell = topCellIndex;
			
			FlowLine fline = getTextLine(caretLine);
			int x = fline.getGlyphIndex(caret.getCharIndex());
			if(x < topCellIndex)
			{
				x = x - HORIZONTAL_SAFETY;
				if(x < HORIZONTAL_SAFETY)
				{
					x = 0;
				}
				topCell = x;
			}
			else if(x >= (topCellIndex + getScreenColumnCount()))
			{
				x = x + /*HORIZONTAL_SAFETY*/ 1 - getScreenColumnCount();
				if(x < 0)
				{
					x = 0;
				}
				topCell = x;
			}
			
			int top = topLine;
			if(caretLine < topLine)
			{
				int y = caretLine - VERTICAL_SAFETY;
				if(y < VERTICAL_SAFETY)
				{
					y = 0;
				}
				top = y;
			}
			else if(caretLine >= (topLine + getScreenRowCount()))
			{
				int y = caretLine + VERTICAL_SAFETY - getScreenRowCount();
				if(y < 0)
				{
					y = 0;
				}
				top = y;
			}
			
			int prevTopCell = topCellIndex;
			int prevTopLine = topLine;
			
			setTopCellIndex(topCell);
			setOrigin(top, GlyphIndex.ZERO);
			
			if(topCell != prevTopCell)
			{
				updateHorizontalScrollBarPosition();
			}
			
			if(top != prevTopLine)
			{
				updateVerticalScrollBarPosition();
			}
		}
	}
	
	
	protected boolean isVisible(Marker m)
	{
		boolean rv = isVisiblePrivate(m);
		log.debug("%s %s", m, rv);
		return rv;
	}
	
	
	protected boolean isVisiblePrivate(Marker m)
	{
		FlowLine fline = getTextLine(topLine);
		int pos = fline.getCharIndex(topGlyphIndex);
		if(m.isBefore(topLine, pos))
		{
			return false;
		}
		
		if(isWrapLines())
		{
			int h = buffer.getHeight() - 1;
			int w = buffer.getWidth();
			
			ScreenRow r = buffer.getScreenRow(h - 1);
			int line = r.getLineNumber();
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
			
			WrapPos wp = navigate(line, r.getWrapRow(), 1, false);
			if(wp == null)
			{
				// beyond EOF
				return true;
			}
			
			WrapInfo wr = getWrapInfo(wp.getLine());
			pos = wr.getCharIndexForColumn(wp.getRow(), 0);
			
			if(m.isAfter(line, pos))
			{
				return false;
			}
		}
		else
		{
			int gix = fline.getGlyphIndex(m.getCharIndex());
			if(gix < topCellIndex)
			{
				return false;
			}
			else if(gix >= (topCellIndex + getScreenColumnCount()))
			{
				return false;
			}
			
			int line = m.getLine();
			if(line >= (topLine + getScreenRowCount()))
			{
				return false;
			}
		}
		
		return true;
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
		int line = m.getLine();
		int charIndex = m.getCharIndex();
		int col = getColumnAt(line, charIndex);
		setPhantomColumn(col);
	}
	
	
	/** 
	 * Navigates the wrapped rows, starting with (startLine, startWrapRow) + delta.
	 * When the resulting position goes beyond the text limits, returns:
	 * clip=true: either the position at the beginning or the end of the document, or
	 * clip=false: null  
	 */ 
	public WrapPos navigate(int startLine, int startWrapRow, int delta, boolean clip)
	{
		log.debug("line=%d row=%d delta=%d clip=%s", startLine, startWrapRow, delta, clip);
		
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
					// TODO
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
					line++;
					wr = getWrapInfo(line);
					size = wr.getWrapRowCount();
				}
			}
		}
		
		WrapPos p = new WrapPos(line, row, wr);
		log.debug(p);
		return p;
	}
}
