// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.util.CKit;
import goryachev.common.util.D;
import goryachev.fx.Binder;
import goryachev.fx.CPane;
import goryachev.fx.FX;
import goryachev.fx.FxBoolean;
import goryachev.fx.FxBooleanBinding;
import goryachev.fxtexteditor.internal.ScreenBuffer;
import goryachev.fxtexteditor.internal.ScreenRow;
import goryachev.fxtexteditor.internal.TextCellsCache;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
public class VFlow
	extends CPane
{
	private static final double CARET_LINE_OPACITY = 0.3;
	private static final double SELECTION_BACKGROUND_OPACITY = 0.4;
	private static final double CELL_BACKGROUND_OPACITY = 0.8;
	protected final FxTextEditor editor;
	protected final FxBoolean showCaret = new FxBoolean(true);
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
	private Color backgroundColor = Color.WHITE; // TODO properties
	private Color textColor = Color.BLACK;
	private Color caretColor = Color.BLACK;
	private int topLine;
	private int topCellIndex;
	private boolean screenBufferValid;
	private boolean repaintRequested;
	protected final TextCellsCache cache = new TextCellsCache(256);
	protected final CellStyles cell = new CellStyles();
	
	
	public VFlow(FxTextEditor ed)
	{
		this.editor = ed;
		
		setMinWidth(0);
		setMinHeight(0);
		
		cursorAnimation = createCursorAnimation();
		
		setFocusTraversable(true);
		
		Binder.onChange(this::handleSizeChange,  widthProperty(), heightProperty());
		Binder.onChange(this::updateLineNumbers, ed.showLineNumbersProperty(), ed.lineNumberFormatterProperty());
		Binder.onChange(this::updateModel, ed.modelProperty());
		
		// TODO clip rect
		
		paintCaret = new FxBooleanBinding(showCaret, editor.displayCaretProperty, editor.focusedProperty(), editor.disabledProperty(), suppressBlink)
		{
			protected boolean computeValue()
			{
				return (isShowCaret() || suppressBlink.get()) && editor.isDisplayCaret() && editor.isFocused() && (!editor.isDisabled());
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
	
	
	/** returns the leftmost display cell index */
	public int getTopCellIndex()
	{
		return topCellIndex;
	}
	
	
	public void setTopCellIndex(int ix)
	{
		topCellIndex = ix;
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
			new KeyFrame(Duration.ZERO, (ev) -> setShowCaret(true)),
			new KeyFrame(d, (ev) -> setShowCaret(false)),
			new KeyFrame(period)
		);
		cursorAnimation.play();
	}
	
	
	/** used for blinking animation */
	protected void setShowCaret(boolean on)
	{
		showCaret.set(on);
	}
	
	
	public boolean isShowCaret()
	{
		return showCaret.get();
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
		invalidate();
	}
	
	
	protected void handleSizeChange()
	{
		invalidate();
		
		canvas = createCanvas();
		setCenter(canvas);
		
		gx = canvas.getGraphicsContext2D();
		gx.setFontSmoothingType(FontSmoothingType.GRAY);
		
		paintAll();
	}
	
	
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
	
	
	/** makes screen buffer invalid.  triggers full screen update */
	public void invalidate()
	{
		screenBufferValid = false;
		repaint();
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
	
	
	public void repaintSegment(ListChangeListener.Change<? extends SelectionSegment> ss)
	{
		// TODO repaint only the damaged area
		repaint();
	}
	
	
	protected Font getFont(CellStyles st)
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
		int x = FX.round(p.getX() / m.cellWidth);
		int y = FX.floor(p.getY() / m.cellHeight);
		return buffer().getInsertPosition(x, y);
	}
	
	
	protected Color backgroundColor(boolean caretLine, boolean selected, Color cellBG)
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
		
		if(cellBG != null)
		{
			c = mixColor(c, cellBG, CELL_BACKGROUND_OPACITY);
		}
		
		return c;
	}
	
	
	protected ScreenBuffer buffer()
	{
		if(!screenBufferValid)
		{
			reflow();
			screenBufferValid = true;
		}
		return buffer;
	}
	
	
	protected ITextLine getTextLine(int lineIndex)
	{
		FxTextEditorModel m = editor.getModel();
		if(lineIndex < m.getLineCount())
		{
			ITextLine t = cache.get(lineIndex);
			if(t == null)
			{
				t = m.getTextLine(lineIndex);
				cache.put(lineIndex, t);
			}
			return t;
		}
		return null;
	}
	
	
	public void clearTextCellsCache()
	{
		cache.clear();
	}
	
	
	protected void reflow()
	{
		boolean wrap = editor.isWrapLines();
		int bufferWidth = getColumnCount() + 1;
		int bufferHeight = getLineCount() + 1;
		
		buffer.setSize(bufferWidth, bufferHeight);
		
		ITabPolicy tabPolicy = editor.getTabPolicy();
		
		if(wrap)
		{
			reflowWrapped(getColumnCount(), bufferHeight, tabPolicy);
			D.print(buffer.dump());
//			System.exit(0); // FIX
		}
		else
		{
			reflowNonWrapped(bufferWidth, bufferHeight, tabPolicy);
		}
	}
	
	
	// TODO
	@SuppressWarnings("null") // due to offsets
	protected void reflowWrapped(int xmax, int ymax, ITabPolicy tabPolicy)
	{
		int lineIndex = getTopLine();
		int topCellIndex = getTopCellIndex();
		boolean run = true;
		int x = 0;
		int y = 0;
		ScreenRow r = null;
		ITextLine tline = null;
		int cellIndex = 0;
		int glyphIndex = 0;
		int glyphCount = 0;
		int tabDistance = 0;
		boolean complex = false;
		int[] offsets = null;
		int startOffset = 0;
		int rowSize = 0;
		
		while(y < ymax)
		{
			if(r == null)
			{
				r = buffer.getRow(y);
				x = 0;
			}
			
			if(tline == null)
			{
				tline = getTextLine(lineIndex);
				if(tline == null)
				{
					complex = false;
				}
				else
				{
					complex = tline.hasComplexGlyphs();
					if(!complex)
					{
						if(!tabPolicy.isSimple())
						{
							complex |= tline.hasTabs();
						}
					}
					
					if(complex)
					{
						offsets = r.getOffsets(xmax);
						glyphCount = tline.getGlyphCount();
						rowSize = 0;
					}
				}
				
				startOffset = 0;
				r.setComplex(complex);
			}
			
			if(x == 0)
			{
				r.setTextLine(tline, startOffset);
			}
			
			// main FSM loop
				
			if(tline == null)
			{
				// next line
				r.setSize(0);
				r = null;
				x = 0;
				y++;
				lineIndex++;
			}
			else if(tabDistance > 0)
			{
				int off = cellIndex - startOffset;
				if(off > xmax)
				{
					// next line
					cellIndex += tabDistance;
					startOffset = cellIndex;
					tabDistance = 0;
					y++;
				}
				else
				{
					offsets[cellIndex - startOffset] = -tabDistance;
					--tabDistance;
					cellIndex++;
				}
			}
			else if(complex)
			{
				int off = cellIndex - startOffset;
				if(off > xmax)
				{
					// next line
					cellIndex += tabDistance;
					startOffset = cellIndex;
					tabDistance = 0;
					y++;
				}
				else
				{
					GlyptType gt = tline.getGlyphType(glyphIndex);
					switch(gt)
					{
					case EOL:
						r.setSize(rowSize);
						r = null;
						tline = null;
						lineIndex++;
						y++;
						break;
					case TAB:
						tabDistance = tabPolicy.nextTabStop(cellIndex) - cellIndex;
						glyphIndex++;
						offsets[off] = -tabDistance;
						--tabDistance;
						rowSize++;
						cellIndex++;
						break;
					case NORMAL:
						if(offsets == null)
						{
							throw new Error();
						}
						offsets[off] = glyphIndex;
						rowSize++;
						cellIndex++;
						break;
					default:
						throw new Error("?" + gt);
					}
				}
			}
			else
			{
				if(cellIndex + xmax > tline.getGlyphCount())
				{
					// end of line
					int sz = tline.getGlyphCount() - cellIndex;
					r.setSize(sz);
					
					tline = null;
					lineIndex++;
				}
				else
				{
					// middle of line
					r.setSize(xmax);
					cellIndex += xmax;
					startOffset += xmax;
				}
				
				y++;
				x = 0;
				r = null;
			}
		}
	}
	
	
	protected void reflowNonWrapped(int xmax, int ymax, ITabPolicy tabPolicy)
	{
		int lineIndex = getTopLine();
		int topCellIndex = getTopCellIndex();
		
		for(int y=0; y<ymax; y++)
		{
			ScreenRow r = buffer.getRow(y);
			
			ITextLine tline = getTextLine(lineIndex);
			if(tline == null)
			{
				r.setSize(0);
			}
			else
			{
				r.setTextLine(tline, topCellIndex);
				
				boolean complex = tline.hasComplexGlyphs();
				if(!complex)
				{
					if(!tabPolicy.isSimple())
					{
						complex |= tline.hasTabs();
					}
				}
				
				if(complex)
				{
					r.setComplex(true);
					int[] offsets = r.getOffsets(xmax);
					
					int glyphCount = tline.getGlyphCount();
					int maxCellIndex = topCellIndex + xmax;
					int size = 0;
					int glyphIndex = 0;
					int cellIndex = 0;
					boolean run = true;
					
					while(run)
					{
						GlyptType gt = tline.getGlyphType(glyphIndex);
						switch(gt)
						{
						case EOL:
							run = false;
							break;
						case TAB:
							int d = tabPolicy.nextTabStop(cellIndex);
							int ct = d - cellIndex;
							for( ; ct>0; ct--)
							{
								if(cellIndex >= topCellIndex)
								{
									offsets[cellIndex - topCellIndex] = -ct;
									size++;
								}
								cellIndex++;
							}
							glyphIndex++;
							break;
						case NORMAL:
							if(cellIndex >= topCellIndex)
							{
								offsets[cellIndex - topCellIndex] = glyphIndex;
								size++;
							}
							glyphIndex++;
							cellIndex++;
							break;
						default:
							throw new Error("?" + gt);
						}
					}
					
					r.setSize(size);
					
					if(glyphIndex >= glyphCount)
					{
						run = false;
					}
					else if(cellIndex >= maxCellIndex)
					{
						run = false;
					}
				}
				else
				{
					r.setComplex(false);
				}
			}
			
			lineIndex++;
		}
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
	
	
	protected void paintAll()
	{
		if((colCount == 0) || (rowCount == 0))
		{
			return;
		}
		
		if(editor.getModel() == null)
		{
			return;
		}
		
		ScreenBuffer b = buffer();
		int xmax = colCount + 1;
		int ymax = rowCount + 1;
		for(int y=0; y<ymax; y++)
		{
			ScreenRow row = b.getScreenRow(y);
			if(row == null)
			{
				paintBlank(0, y, xmax);
			}
			else
			{
				for(int x=0; x<xmax; x++)
				{
					int off = row.getCellOffset(x);
					if(off == ScreenBuffer.EOF)
					{
						paintBlank(x, y, xmax - x);
						break;
					}
					
					if(off == ScreenBuffer.EOL)
					{
						paintBlank(x, y, xmax - x);
					}
					else if(off < 0)
					{
						paintBlank(x, y, -off);
					}
					else
					{
						paintCell(row, x, y);	
					}
				}
			}
		}
	}
	
	
	protected void paintBlank(int x, int y, int count)
	{
		TextMetrics m = textMetrics();
		double ch = m.cellHeight;
		double cw = m.cellWidth;
		double cx = x * cw;
		double cy = y * ch;
		
		cw *= count;
		
		// TODO bg
		boolean selected = false;
		Color bg = backgroundColor(false, selected, null);
		
		// FIX
		bg = Color.RED;
		
		gx.setFill(bg);
		gx.fillRect(cx, cy, cw, ch);
	}
	

	protected void paintCell(ScreenRow row, int x, int y)
	{
		// TODO optimize, we could get this into a structure at the start of paint* methods
		TextMetrics m = textMetrics();
		double ch = m.cellHeight;
		double cw = m.cellWidth;
		double cx = x * cw;
		double cy = y * ch;
		
		boolean caretLine = false;
		boolean caret = false;
		boolean selected = false;
//		if(row.hasSelection())
//		{
//			selected = row.
//		}
//		else
//		{
//			selected = false;
//		}

//		int line = cell.getLine();
//		int off = cell.getOffset();
		// TODO this can be optimized by returning an int bitmap? maybe... isValid*
//		for(SelectionSegment ss: editor.selector.segments)
//		{
//			if(cell.isValidLine() && ss.isCaretLine(line))
//			{
//				caretLine = true;
//				
//				if(cell.isValidCaret() && ss.isCaret(line, off))
//				{
//					caret = true;
//				}
//			}
//			
//			if(ss.contains(line, off))
//			{
//				selected = true;
//			}
//		}
		
		// style
		row.updateStyle(x, cell);
		
		// background
		Color bg = backgroundColor(caretLine, selected, cell.getBackgroundColor());
		gx.setFill(bg);
		gx.fillRect(cx, cy, cw, ch);
		
		// caret
		if(paintCaret.get()) // TODO move to screen buffer
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
			Color fg = cell.getTextColor();
			if(fg == null)
			{
				fg = getTextColor();
			}
			
			Font f = getFont(cell);
			gx.setFont(f);
			gx.setFill(fg);
			gx.fillText(text, cx, cy - m.baseline, cw);
		
			// TODO underline, strikethrough
		}
	}
}
