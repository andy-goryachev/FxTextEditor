// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.util.CKit;
import goryachev.fx.Binder;
import goryachev.fx.CPane;
import goryachev.fx.CssStyle;
import goryachev.fx.FX;
import goryachev.fx.FxBoolean;
import goryachev.fxtexteditor.internal.ScreenBuffer;
import goryachev.fxtexteditor.internal.ScreenCell;
import goryachev.fxtexteditor.internal.TextCell;
import goryachev.fxtexteditor.internal.TextCells;
import java.util.Locale;
import com.ibm.icu.text.BreakIterator;
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
	protected final FxBoolean caretVisible = new FxBoolean(true); // FIX move to the editor
	protected final FxBoolean suppressBlink = new FxBoolean(false);
	protected final BooleanExpression paintCaret;
	protected final ScreenBuffer buffer = new ScreenBuffer();
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
	private boolean repaintRequested;
	private BreakIterator breakIterator;
	protected final TextDecor decor = new TextDecor();
	private boolean screenBufferValid;
	
	
	public VTextFlow(FxTextEditor ed)
	{
		this.editor = ed;
		
		FX.style(this, PANE);
		
		setMinWidth(0);
		setMinHeight(0);
		
		cursorAnimation = createCursorAnimation();
		
		setFocusTraversable(true);
		
		Binder.onChange(this::handleSizeChange,  widthProperty(), heightProperty());
		Binder.onChange(this::updateLineNumbers, ed.showLineNumbersProperty(), ed.lineNumberFormatterProperty());
		Binder.onChange(this::updateModel, ed.modelProperty());
		
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
	
	
	public int getColumnCount()
	{
		return colCount;
	}
	
	
	public int getLineCount()
	{
		return rowCount;
	}
	
	
	public int getMaxColumnCount()
	{
		return buffer().getWidth();
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
	
	
	protected void updateModel()
	{
		// TODO from model
		breakIterator = BreakIterator.getCharacterInstance(Locale.US);
	}
	
	
	protected void handleSizeChange()
	{
		invalidate();
		
		canvas = createCanvas();
		setCenter(canvas);
		gx = canvas.getGraphicsContext2D();
		gx.setFontSmoothingType(FontSmoothingType.GRAY);
		
		// TODO perhaps not needed
//		gx.setFill(getBackgroundColor());
//		gx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		draw();
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
		screenBufferValid = false;
	}
	
	
	protected Font getFont(ScreenCell c)
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
		return buffer().getInsertPosition(x, y);
	}
	
	
	protected ScreenBuffer buffer()
	{
		if(!screenBufferValid)
		{
			reflow();
		}
		return buffer;
	}
	
	
	protected Color backgroundColor(boolean caretLine, boolean selected, TextCell cell)
	{
		Color c = backgroundColor;
		
		if(caretLine)
		{
			c = mixColor(c, editor.getCaretLineColor(), CARET_LINE_OPACITY);
		}
		
		if(selected) // pos.isValidCaretOffset() && editor.selector.isSelected(pos.getLine(), pos.getOffset()))
		{
			c = mixColor(c, editor.getSelectionBackgroundColor(), SELECTION_BACKGROUND_OPACITY);
		}
		
		if(cell != null)
		{
			c = mixColor(c, cell.getBackgroundColor(), CELL_BACKGROUND_OPACITY);
		}
		
		return c;
	}
	

	protected void reflow()
	{
		FxTextEditorModel model = editor.getModel();
		boolean wrap = editor.isWrapLines();

		int bufferWidth = getColumnCount() + 1;
		int bufferHeight = getLineCount() + 1;
		
		buffer.setSize(bufferWidth, bufferHeight);
		
		int xmax = wrap ? getColumnCount() : bufferWidth;
		int ymax = bufferHeight;
		
		int lineIndex = getTopLine();
		int topOffset = getTopOffset();
		int screenBufferIndex = 0;
		int off = topOffset;
		boolean eof = false;
		boolean eol = false;
		boolean caretLine = false;
		boolean selected = false;
		boolean isCaret = false;
		boolean highlightCaretLine = editor.isHighlightCaretLine();
		Color bg = null;
		Color fg = Color.BLACK; // TODO
		Color textColor = Color.BLACK; // FIX null
		TextCells textLine = null;
		TextCell cell = null;
		
		for(int y=0; y<ymax; y++)
		{
			caretLine = highlightCaretLine && editor.isCaretLine(lineIndex); 
				
			for(int x=0; x<xmax; x++)
			{
				if(eof)
				{
					cell = null;
				}
				else if(eol)
				{
					cell = null;
				}
				else
				{
					if(textLine == null)
					{
						if(lineIndex >= model.getLineCount())
						{
							eof = true;
						}
						else
						{
							decor.reset();
							String s = model.getPlainText(lineIndex);
							TextDecor d = model.getTextDecor(lineIndex, s, decor);
							textLine = createTextLine(lineIndex, s, d);
						}
					}
					
					if(eof || eol || (textLine == null))
					{
						cell = null;
						textLine = null;
					}
					else 
					{
						cell = textLine.getCell(off);
						if(cell == null)
						{
							eol = true;
						}
						else
						{
							off++;
						}
						
						// TODO tabs
					}
				}
				
				selected = editor.isSelected(lineIndex, off);
				bg = backgroundColor(caretLine, selected, cell);
				
				// FIX eof: allow caret on last line
				isCaret = caretLine && editor.isCaret(lineIndex, off) && !eol && !eof; // FIX boolean to indicate that we are pass the end of line
				
				ScreenCell screenCell = buffer.getCell(screenBufferIndex++);
				screenCell.setLine(lineIndex);
				screenCell.setOffset(off);
				screenCell.setCaret(isCaret);
				screenCell.setCell(cell);
				screenCell.setBackgroundColor(bg);
				screenCell.setTextColor(textColor);
				// TODO colors
			}
			
			if(!eof)
			{
				if(wrap)
				{
					// extra cell when wrap is on
					ScreenCell screenCell = buffer.getCell(screenBufferIndex++);
					screenCell.setLine(lineIndex);
					screenCell.setOffset(off);
					screenCell.setCell(null);
					screenCell.setBackgroundColor(bg);
					screenCell.setTextColor(null);
					// TODO colors
					
					if(eol)
					{
						lineIndex++;
						textLine = null;
						eol = false;
						off = 0;
					}
				}
				else
				{
					lineIndex++;
					textLine = null;
					eol = false;
					off = topOffset;
				}
			}
		}
		
		screenBufferValid = true;
	}
	
	
	protected TextCells createTextLine(int lineIndex, String text, TextDecor d)
	{
		TextCells cs = new TextCells();
		breakIterator.setText(text);

		int start = breakIterator.first();
		for(int end=breakIterator.next(); end!=BreakIterator.DONE; start=end, end=breakIterator.next())
		{
			String s = text.substring(start, end);
			cs.addCell(start, end, s);
		}
		
		if(d != null)
		{
			d.applyStyles(cs);
		}
		
		return cs;
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
			else if(startLine > (topLine + getLineCount()))
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
			visible = getLineCount();
			val = topLine; //(max - visible);
		}
		
		ScrollBar vscroll = editor.getVerticalScrollBar();
		vscroll.setMin(0);
        vscroll.setMax(max);
        vscroll.setVisibleAmount(visible);
        vscroll.setValue(val);
        
		editor.setHandleScrollEvents(true);
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
			screenBufferValid = false;
			repaintRequested = true; // TODO this variable is not needed
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
		
		int xmax = colCount + 1;
		for(int y=0; y<rowCount; y++)
		{
			for(int x=0; x<xmax; x++)
			{
				paintCell(x, y);
			}
		}
	}
	

	protected ScreenCell paintCell(int x, int y)
	{
		ScreenCell cell = buffer().getCell(x, y);
		
		TextMetrics m = textMetrics();
		double ch = m.cellHeight;
		double cw = m.cellWidth;
		double cx = x * cw;
		double cy = y * ch;

		// background
		Color bg = cell.getBackgroundColor();
		gx.setFill(bg);
		gx.fillRect(cx, cy, cw, ch);
		
		// caret
		if(paintCaret.get()) // TODO move to screen buffer
		{
			if(cell.isCaret())
			{
				// TODO insert mode
				gx.setFill(caretColor);
				gx.fillRect(cx, cy, 2, ch);
			}
		}
		
		// text
		String text = cell.getText();
		if(text != null)
		{
			Color fg = cell.getTextColor();
			Font f = getFont(cell);
			gx.setFont(f);
			gx.setFill(fg);
			gx.fillText(text, cx, cy - m.baseline, m.cellWidth);
		
			// TODO underline, strikethrough
		}
		
		return cell;
	}
}
