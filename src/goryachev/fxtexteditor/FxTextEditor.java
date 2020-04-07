// Copyright © 2019-2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.log.Log;
import goryachev.common.util.CKit;
import goryachev.common.util.D;
import goryachev.fx.CPane;
import goryachev.fx.FX;
import goryachev.fx.Formatters;
import goryachev.fx.FxAction;
import goryachev.fx.FxBoolean;
import goryachev.fx.FxFormatter;
import goryachev.fx.FxObject;
import goryachev.fx.XScrollBar;
import goryachev.fxtexteditor.internal.GlyphIndex;
import goryachev.fxtexteditor.internal.InputHandler;
import goryachev.fxtexteditor.internal.Markers;
import goryachev.fxtexteditor.internal.TabPolicy;
import java.io.StringWriter;
import java.io.Writer;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.DataFormat;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;


/**
 * Monospaced Text Editor.
 */
public class FxTextEditor
	extends CPane
{
	public final FxAction copyAction = new FxAction(this::copy);
	public final FxAction selectAllAction = new FxAction(this::selectAll);
	
	protected final Log log = Log.get("FxTextEditor");
	protected final FxObject<Color> backgroundColorProperty = new FxObject(Color.WHITE);
	protected final FxObject<Font> fontProperty = new FxObject(Font.font("Monospace", 12));
	protected final FxBoolean editableProperty = new FxBoolean(false);
	protected final ReadOnlyObjectWrapper<FxTextEditorModel> modelProperty = new ReadOnlyObjectWrapper<>();
	protected final FxBoolean wrapLinesProperty = new FxBoolean(true);
	protected final ReadOnlyBooleanWrapper multipleSelectionProperty = new ReadOnlyBooleanWrapper(false);
	protected final FxBoolean displayCaretProperty = new FxBoolean(true);
	protected final FxBoolean showLineNumbersProperty = new FxBoolean(false);
	protected final FxBoolean highlightCaretLineProperty = new FxBoolean(true);
	protected final ReadOnlyObjectWrapper<Duration> caretBlinkRateProperty = new ReadOnlyObjectWrapper(Duration.millis(500));
	protected final FxObject<FxFormatter> lineNumberFormatterProperty = new FxObject<>();
	protected final FxObject<ITabPolicy> tabPolicy = new FxObject();
	protected final FxTextEditorModelListener modelListener;
	protected final SelectionController selector;
	protected final Markers markers = new Markers(32);
	protected final VFlow vflow;
	protected final ScrollBar vscroll;
	protected final ScrollBar hscroll;
	protected boolean handleScrollEvents = true;
	protected final ChangeListener<LoadStatus> loadStatusListener;
	protected BiConsumer<FxTextEditor,Marker> wordSelector = new SimpleWordSelector();

	
	// TODO perhaps pass final Capabilities object that defines basic parameters
	// such as cache size, various limits, behaviors, etc.
	public FxTextEditor()
	{
		modelListener = new FxTextEditorModelListener()
		{
			public void eventAllLinesChanged()
			{
				handleAllLinesChanged();
			}

			public void eventTextUpdated(int startLine, int startPos, int startCharsInserted, int linesInserted, int endLine, int endPos, int endCharsInserted)
			{
				handleTextUpdated(startLine, startPos, startCharsInserted, linesInserted, endLine, endPos, endCharsInserted);
			}
		};
		
		loadStatusListener = new ChangeListener<LoadStatus>()
		{
			public void changed(ObservableValue<? extends LoadStatus> observable, LoadStatus prev, LoadStatus cur)
			{
				updateLoadStatus(cur);
			}
		};
		
		selector = createSelectionController();
		
		vflow = new VFlow(this);
		
		vscroll = createVScrollBar();
		vscroll.setOrientation(Orientation.VERTICAL);
		vscroll.setManaged(true);
		vscroll.setMin(0.0);
		vscroll.setMax(1.0);
		vscroll.addEventFilter(ScrollEvent.ANY, (ev) -> ev.consume());
		vscroll.valueProperty().addListener((s,p,c) -> handleVerticalScroll(c.doubleValue()));
		
		hscroll = createHScrollBar();
		hscroll.setOrientation(Orientation.HORIZONTAL);
		hscroll.setManaged(true);
		hscroll.setMin(0.0);
		hscroll.setMax(1.0);
		hscroll.addEventFilter(ScrollEvent.ANY, (ev) -> ev.consume());
		hscroll.visibleProperty().bind(wrapLinesProperty.not());
		hscroll.valueProperty().addListener((s,p,c) -> handleHorizontalScroll(c.doubleValue()));
		
		getChildren().addAll(vflow, vscroll, hscroll);
		
		selector.segments.addListener((ListChangeListener.Change<? extends SelectionSegment> ss) -> handleSelectionSegmentUpdate(ss)); 
		
		// TODO
//		FX.onChange(vflow::updateBlinkRate, true, blinkRateProperty());
		FX.onChange(this::handleWrapChange, wrapLinesProperty);
		
		initInputHandler();
		setFocusTraversable(true);
		
		setTabPolicy(TabPolicy.create(4));
	}
	
	
	/** override to provide your own input handler */
	protected void initInputHandler()
	{
		new InputHandler(this, vflow, selector);
	}
	
	
	public Font getFont()
	{
		return fontProperty.get();
	}
	

	public void setFont(Font f)
	{
		fontProperty.set(f);
	}
	
	
	public void setFontSize(double size)
	{
		Font f = getFont();
		f = Font.font(f.getFamily(), size);
		setFont(f);
	}
	
	
	public ScrollBar getVerticalScrollBar()
	{
		return vscroll;
	}
	
	
	public ScrollBar getHorizontalScrollBar()
	{
		return hscroll;
	}
	
	
	public void setContentPadding(Insets m)
	{
//		vflow.setPadding(m);
	}
	
	
	public FxObject<FxFormatter> lineNumberFormatterProperty()
	{
		return lineNumberFormatterProperty;
	}
	
	
	public FxFormatter getLineNumberFormatter()
	{
		FxFormatter f = lineNumberFormatterProperty.get();
		if(f == null)
		{
			f = Formatters.getIntegerFormat();
		}
		return f;
	}
	
	
	public void setLineNumberFormatter(FxFormatter f)
	{
		lineNumberFormatterProperty.set(f);
	}
	
	
	/** override to provide your own selection model */
	protected SelectionController createSelectionController()
	{
		return new SelectionController();
	}
	
	
	public ObservableList<SelectionSegment> selectionSegmentsProperty()
	{
		return selector.selectionSegmentsProperty();
	}
	
	
	public ReadOnlyObjectProperty<EditorSelection> selectionProperty()
	{
		return selector.selectionProperty();
	}
	
	
	public EditorSelection getSelection()
	{
		return selector.getSelection();
	}
	
	
	public void clearSelection()
	{
		selector.clear();
	}

	
	public void setModel(FxTextEditorModel m)
	{
		markers.clear();
		
		FxTextEditorModel old = getModel();
		if(old != null)
		{
			old.removeListener(modelListener);
			old.loadStatus().removeListener(loadStatusListener);
		}
		
		modelProperty.set(m);
		vflow.reset();
		vflow.clearTextCellsCache();
		
		if(m != null)
		{
			m.addListener(modelListener);
			m.loadStatus().addListener(loadStatusListener);
		}
		
		handleAllLinesChanged();
	}
	
	
	public FxTextEditorModel getModel()
	{
		return modelProperty.get();
	}
	
	
	public int getLineCount()
	{
		FxTextEditorModel m = getModel();
		return m == null ? 0 : m.getLineCount();
	}
	
	
	protected ScrollBar createVScrollBar()
	{
		return new XScrollBar();
	}
	
	
	protected ScrollBar createHScrollBar()
	{
		return new XScrollBar();
	}
	
	
	protected void updateLoadStatus(LoadStatus s)
	{
		if(vscroll instanceof XScrollBar)
		{
			XScrollBar vs = (XScrollBar)vscroll;
			if(s.isValid())
			{
				vs.setPainer((canvas) ->
				{
					double w = canvas.getWidth();
					double h = canvas.getHeight();
					double y = s.getProgress() * h;
					GraphicsContext g = canvas.getGraphicsContext2D();
					g.setFill(Color.LIGHTGRAY);
					g.fillRect(0, y, w, h - y);
				});
			}
			else
			{
				vs.setPainer(null);
			}
		}
	}
	
	
	protected void setHandleScrollEvents(boolean on)
	{
		handleScrollEvents = on;
	}
	
	
	protected boolean isHandleScrollEvents()
	{
		return handleScrollEvents;
	}
	
	
	public boolean isWrapLines()
	{
		return wrapLinesProperty.get();
	}
	
	
	public void setWrapLines(boolean on)
	{
		wrapLinesProperty.set(on);
	}
	
	
	public BooleanProperty wrapLinesProperty()
	{
		return wrapLinesProperty;
	}
	
	
	public void setMultipleSelectionEnabled(boolean on)
	{
		multipleSelectionProperty.set(on);
	}
	
	
	public boolean isMultipleSelectionEnabled()
	{
		return multipleSelectionProperty.get();
	}
	
	
	public ReadOnlyBooleanProperty multipleSelectionProperty()
	{
		return multipleSelectionProperty.getReadOnlyProperty();
	}
	
	
	public ReadOnlyObjectProperty<FxTextEditorModel> modelProperty()
	{
		return modelProperty.getReadOnlyProperty();
	}
	
	
	protected void handleWrapChange()
	{
		requestLayout();
		vflow.invalidate();
	}
	
	
	protected void handleSelectionSegmentUpdate(ListChangeListener.Change<? extends SelectionSegment> ss)
	{
		vflow.repaintSegment(ss);
	}
	
	
	protected void layoutChildren()
	{
		Insets m = getPadding();
		double x0 = m.getLeft();
		double y0 = m.getTop();
		
		double vscrollWidth = 0.0;
		double hscrollHeight = 0.0;
		
		// position the scrollbar(s)
		if(vscroll.isVisible())
		{
			vscrollWidth = vscroll.prefWidth(-1);
		}
		
		if(hscroll.isVisible())
		{
			hscrollHeight = hscroll.prefHeight(-1);
		}
		
		// TODO line numbers column
		
		double w = getWidth() - m.getLeft() - m.getRight() - vscrollWidth - 1;
		double h = getHeight() - m.getTop() - m.getBottom() - hscrollHeight - 1;

		// layout children
		layoutInArea(vscroll, w, y0 + 1, vscrollWidth, h, 0, null, true, true, HPos.RIGHT, VPos.TOP);
		layoutInArea(hscroll, x0 + 1, h, w, hscrollHeight, 0, null, true, true, HPos.LEFT, VPos.BOTTOM);
		layoutInArea(vflow, x0, y0, w, h, 0, null, true, true, HPos.LEFT, VPos.TOP);
	}
	
	
	/** returns a new Marker at the specified screen coordinates */
	public Marker getInsertPosition(double screenx, double screeny)
	{
		TextPos p = vflow.getInsertPosition(screenx, screeny);
		int line = p.getLine();
		int off = p.getOffset();
		
		if(line < 0)
		{
			line = getLineCount();
			off = 0;
		}
		else if(off < 0)
		{
			String s = getPlainText(line);
			if(s == null)
			{
				off = 0;
			}
			else
			{
				off = s.length();
			}
		}
		
		return markers.newMarker(line, off);
	}
	
	
	public Marker newMarker(int lineNumber, int position)
	{
		return markers.newMarker(lineNumber, position);
	}
	
	
	public ReadOnlyObjectProperty<Duration> blinkRateProperty()
	{
		return caretBlinkRateProperty.getReadOnlyProperty();
	}
	
	
	public Duration getBlinkRate()
	{
		return caretBlinkRateProperty.get();
	}
	
	
	public void setBlinkRate(Duration d)
	{
		caretBlinkRateProperty.set(d);
	}
	
	
	public boolean isEditable()
	{
		return editableProperty.get();
	}
	
	
	/** enables editing in the component.  this setting will be ignored if a a model is read only */
	public void setEditable(boolean on)
	{
		editableProperty.set(on);
	}

	
	protected void handleAllLinesChanged()
	{
		clearSelection();
		
		if(vscroll != null)
		{
			vscroll.setValue(0);
		}
		
		if(hscroll != null)
		{
			hscroll.setValue(0);
		}
		
		vflow.invalidate();
	}


	protected void handleTextUpdated(int startLine, int startPos, int startCharsInserted, int linesInserted, int endLine, int endPos, int endCharsInserted)
	{
		log.debug("startLine={} startPos={} startCharsInserted={} linesInserted={} endLine={} endPos={} endCharsInserted={}", startLine, startPos, startCharsInserted, linesInserted, endLine, endPos, endCharsInserted);
		
		// update markers
		markers.update(startLine, startPos, startCharsInserted, linesInserted, endLine, endPos, endCharsInserted);
		
		// update vflow
		vflow.update(startLine, linesInserted, endLine);
	}

	
	public void setDisplayCaret(boolean on)
	{
		displayCaretProperty.set(on);
	}
	
	
	public boolean isDisplayCaret()
	{
		return displayCaretProperty.get();
	}
	
	
	public void setShowLineNumbers(boolean on)
	{
		showLineNumbersProperty.set(on);
	}
	
	
	public boolean isShowLineNumbers()
	{
		return showLineNumbersProperty.get();
	}
	
	
	public BooleanProperty showLineNumbersProperty()
	{
		return showLineNumbersProperty;
	}
	
	
	public void setHighlightCaretLine(boolean on)
	{
		highlightCaretLineProperty.set(on);
	}
	
	
	public boolean isHighlightCaretLine()
	{
		return highlightCaretLineProperty.get();
	}
	

	/** returns plain text on the specified line */
	public String getPlainText(int line)
	{
		return getModel().getPlainText(line);
	}


	/** returns selected plain text, concatenating multiple selection segments if necessary */
	public String getSelectedText() throws Exception
	{
		StringWriter wr = new StringWriter();
		// TODO
//		getModel().getPlainText(getSelection(), wr);
		return wr.toString();
	}
	
	
	/** 
	 * outputs selected plain text, concatenating multiple selection segments if necessary.
	 * this method should be used where allocating a single (potentially large) string is undesirable,
	 * for example when saving to a file.
	 * any exceptions thrown by the writer are silently ignored and the process is aborted.
	 */
	public void writeSelectedText(Writer wr)
	{
//		try
//		{
//			getModel().getPlainText(getSelection(), wr);
//		}
//		catch(Exception ignored)
//		{
//		}
	}
	
	
	public void pageUp()
	{
		// TODO
//		vflow.pageUp();
	}
	
	
	public void pageDown()
	{
		// TODO need the concept of last caret
		// single caret: create phantom x position, move caret + screen height
		// multiple carets: reset to a single caret using last caret, then follow the single caret logic
		D.print("pageDown"); // FIX
	}
	
	
	public void moveUp()
	{
		// TODO
//		vflow.moveUp();
	}
	
	
	public void moveDown()
	{
		// TODO
//		vflow.moveDown();
	}
	
	
	public void moveHome()
	{
		// TODO
	}
	
	
	public void moveEnd()
	{
		// TODO
	}
	
	
	public void keyBackspace()
	{
		// TODO
	}
	
	
	public void keyDelete()
	{
		// TODO
	}
	
	
	public void scroll(double fractionOfHeight)
	{
		vflow.scroll(fractionOfHeight);
	}
	
	
	/** scrolls up (deltaInPixels < 0) or down (deltaInPixels > 0) */
	public void blockScroll(double deltaInPixels)
	{
		// TODO
//		vflow.blockScroll(deltaInPixels);
	}
	
	
	/** copies all supported formats */
	public void copy()
	{
		// TODO
//		copy(null, getModel().getSupportedFormats());
	}
	
	
	/** copies specified formats to clipboard, using an error handler */
	public void copy(Consumer<Throwable> errorHandler, DataFormat ... formats)
	{
		// TODO
//		getModel().copy(getSelection(), errorHandler, formats);
	}
	
	
	public void selectAll()
	{
		int ix = getLineCount();
		if(ix > 0)
		{
			--ix;
			
			String s = getModel().getPlainText(ix);
			Marker beg = markers.newMarker(0, 0);
			Marker end = markers.newMarker(ix, Math.max(0, s.length()));
			
			selector.setSelection(beg, end);
			selector.commitSelection();
		}
	}
	

	public void select(Marker start, Marker end)
	{
		selector.setSelection(start, end);
		selector.commitSelection();
	}
	
	
	public void setOrigin(int row)
	{
		if(row >= getLineCount())
		{
			row = getLineCount() - 1;
		}
		
		if(row < 0)
		{
			row = 0;
		}
		
		vflow.setOrigin(row, GlyphIndex.ZERO);
	}
	

	// TODO
//	public void scrollToVisible(int row)
//	{
//		if((row >= 0) && (row < getLineCount()))
//		{
//			// FIX smarter positioning so the target line is somewhere at 25% of the height
//			vflow.scrollToVisible(row);
//		}
//	}
	
	
	// TODO
	public void scrollToVisible(Point2D screenPoint)
	{
//		Point2D p = vflow.screenToLocal(screenPoint);
//		double y = p.getY();
//		if(y < 0)
//		{
//			// above
//			// FIX for now, just show the upper portion of the top line
//			vflow.scrollToVisible(vflow.getTopLine());
//		}
//		else if(y > vflow.getHeight())
//		{
//			// below
//			// FIX for now, just show the lower portion of the bottom line
//			int ix = vflow.getTopLine() + Math.max(0, vflow.getVisibleLineCount() - 1);
//			vflow.scrollToVisible(ix);
//		}
	}
	
	
	protected void handleHorizontalScroll(double val)
	{
		if(handleScrollEvents)
		{
			if(!isWrapLines())
			{
				int max = vflow.getMaxCellCount() + 1; // allow for 1 blank space at the end
				int vis = vflow.getMaxColumnCount();
				int fr = Math.max(0, max - vis);
				
				int off = CKit.round(fr * val);
				vflow.setTopCellIndex(off);
			}
		}
	}
	
	
	protected void handleVerticalScroll(double val)
	{
		if(handleScrollEvents)
		{
			log.debug("val={}", val);
			
			vflow.verticalScroll(val, isWrapLines());
		}
	}
	
	
	public void setCaret(int row, int charIndex)
	{
		Marker m = newMarker(row, charIndex);
		select(m, m);
	}


	public void selectLine(Marker m)
	{
		if(m != null)
		{
			int line = m.getLine();
			Marker start = markers.newMarker(line, 0);
			
			int len = getTextLength(line);
			Marker end = markers.newMarker(line, len);
			
			selector.setSelection(start, end);
		}
	}
	
	
	public void selectWord(Marker m)
	{
		if(m != null)
		{
			if(wordSelector != null)
			{
				wordSelector.accept(this, m);
			}
		}
	}
	
	
	public void setWordSelector(BiConsumer<FxTextEditor,Marker> s)
	{
		wordSelector = s;
	}
	
	
	public int getTextLength(int line)
	{
		String s = getModel().getPlainText(line);
		return s == null ? 0 : s.length();
	}

	
	public Color getCaretLineColor()
	{
		// TODO property
		return FX.rgb(255, 200, 255);
	}
	
	
	public Color getSelectionBackgroundColor()
	{
		// TODO property
		return FX.rgb(255, 255, 128);
	}
	
	
	public void reloadVisibleArea()
	{
		vflow.repaint();
	}


	public ITabPolicy getTabPolicy()
	{
		return tabPolicy.get();
	}
	

	public void setTabPolicy(ITabPolicy p)
	{
		if(p == null)
		{
			p = TabPolicy.create(1);
		}
		tabPolicy.set(p);
	}
	
	
	public void setTabSize(int size)
	{
		setTabPolicy(TabPolicy.create(size));
	}

	
	public boolean isCaretLine(int line)
	{
		return selector.isCaretLine(line);
	}
	
	
	public boolean isCaret(int line, int pos)
	{
		return selector.isCaret(line, pos);
	}
	
	
	public boolean isSelected(int line, int pos)
	{
		return selector.isSelected(line, pos);
	}
	
	
	public void setBackgroundColor(Color c)
	{
		backgroundColorProperty.set(c);
	}
	
	
	public Color getBackgroundColor()
	{
		return backgroundColorProperty.get();
	}
	
	
	public FxObject<Color> backgroundColorProperty()
	{
		return backgroundColorProperty;
	}
}
