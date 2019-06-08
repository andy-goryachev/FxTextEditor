// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.util.CKit;
import goryachev.fx.Binder;
import goryachev.fx.CPane;
import goryachev.fx.CssStyle;
import goryachev.fx.FX;
import goryachev.fx.FxBoolean;
import goryachev.fxtexteditor.internal.ScreenCell;
import goryachev.fxtexteditor.internal.TextCells;
import java.util.Locale;
import com.ibm.icu.text.BreakIterator;
import goryachev.fxtexteditor.internal.ScreenBuffer;
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
		
		FX.listen(this::handleSizeChange, widthProperty());
		FX.listen(this::handleSizeChange, heightProperty());
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
	
	
//	protected Color backgroundColor(TCell cell, TextPos pos)
//	{
//		Color c = backgroundColor;
//		
//		if(editor.isHighlightCaretLine())
//		{
//			if(pos.isValidCaretLine() && editor.selector.isCaretLine(pos.getLine()))
//			{
//				c = mixColor(c, editor.getCaretLineColor(), CARET_LINE_OPACITY);
//			}
//		}
//		
//		if(pos.isValidCaretOffset() && editor.selector.isSelected(pos.getLine(), pos.getOffset()))
//		{
//			c = mixColor(c, editor.getSelectionBackgroundColor(), SELECTION_BACKGROUND_OPACITY);
//		}
//		
//		if(cell != null)
//		{
//			c = mixColor(c, cell.getBackgroundColor(), CELL_BACKGROUND_OPACITY);
//		}
//		
//		return c;
//	}
	
	
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
	
	
	protected void reflow()
	{
		int w = getColumnCount() + 1;
		int h = getLineCount() + 1;
		
		int sz = buffer.setSize(w, h);
		
		FxTextEditor ed = getEditor();
		boolean wrap = ed.isWrapLines();
		FxTextEditorModel m = ed.getModel();
		int lineIndex = getTopLine();
		int topOffset = getTopOffset();
		int y = 0;
		int x = 0;
		int off = topOffset;
		boolean eof = false;
		boolean eol = false;
		boolean caretLine = false;
		Color bg = Color.WHITE; // TODO null;
		Color fg = Color.BLACK; // TODO
		Color textColor = Color.BLACK; // FIX null
		TextCells textLine = null;
		TextCells.LCell cell = null;
		
		for(int ix=0; ix<sz; ix++)
		{
			ScreenCell screenCell = buffer.getCell(ix);
			
			String text;
			if(eof)
			{
				text = null;
			}
			else if(eol)
			{
				text = null;
			}
			else
			{
				if(textLine == null)
				{
					if(lineIndex >= m.getLineCount())
					{
						eof = true;
					}
					else
					{
						String s = m.getPlainText(lineIndex);
						TextDecor d = m.getTextLine(lineIndex, s, decor);
						textLine = createTextLine(lineIndex, s, d);
					}
				}
				
				if(eof || eol || (textLine == null))
				{
					cell = null;
				}
				else 
				{
					cell = textLine.getCell(off);
					off++;
					
					// TODO tabs
				}
			}
			
			screenCell.setCell(cell);
			screenCell.setBackgroundColor(bg);
			screenCell.setTextColor(textColor);
			// TODO colors
			x++;
				
			if(x > w)
			{
				x = 0;
				y++;
				lineIndex++;
				textLine = null;
				
				if(y > h)
				{
					break;
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
			String s = text.substring(start,end);
			cs.addCell(start, end, s);
		}
		
		if(d != null)
		{
			// TODO populate styles
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
			ScreenCell c = paintCell(x, y);
			x++;
			if(x >= max)
			{
				x = 0;
				y++;
				if(y > rowCount)
				{
					break;
				}
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
		if(paintCaret.get())
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
