// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.util.D;
import goryachev.fx.Binder;
import goryachev.fx.CPane;
import goryachev.fx.FX;
import goryachev.fx.Formatters;
import goryachev.fx.FxAction;
import goryachev.fx.FxBoolean;
import goryachev.fx.FxFormatter;
import goryachev.fx.FxObject;
import goryachev.fx.KeyMap;
import goryachev.fx.XScrollBar;
import goryachev.fxtexteditor.internal.Markers;
import java.io.StringWriter;
import java.io.Writer;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ListChangeListener;
import javafx.event.EventType;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;


/**
 * Fx Text Editor.
 */
public class FxTextEditor
	extends CPane
{
	public final FxAction copyAction = new FxAction(this::copy);
	public final FxAction selectAllAction = new FxAction(this::selectAll);
	
	protected final FxBoolean editableProperty = new FxBoolean(false);
	protected final ReadOnlyObjectWrapper<FxTextEditorModel> modelProperty = new ReadOnlyObjectWrapper<>();
	protected final FxBoolean wrapLinesProperty = new FxBoolean(true);
	protected final ReadOnlyBooleanWrapper multipleSelectionProperty = new ReadOnlyBooleanWrapper(false);
	protected final FxBoolean displayCaretProperty = new FxBoolean(true);
	protected final FxBoolean showLineNumbersProperty = new FxBoolean(false);
	protected final FxBoolean highlightCaretLineProperty = new FxBoolean(true);
	protected final ReadOnlyObjectWrapper<Duration> caretBlinkRateProperty = new ReadOnlyObjectWrapper(Duration.millis(500));
	protected final FxObject<FxFormatter> lineNumberFormatterProperty = new FxObject<>();
	protected final FxTextEditorModelListener modelListener;
	protected final SelectionController selector;
	protected final Markers markers = new Markers(32);
	protected final VTextFlow vflow;
	protected final ScrollBar vscroll;
	protected final ScrollBar hscroll;
	protected boolean handleScrollEvents = true;
	protected BiConsumer<FxTextEditor,Marker> wordSelector = new SimpleWordSelector();

	
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
		
		selector = createSelectionController();
		
		vflow = new VTextFlow(this);
		
		vscroll = createVScrollBar();
		vscroll.setOrientation(Orientation.VERTICAL);
		vscroll.setManaged(true);
		vscroll.setMin(0.0);
		vscroll.setMax(1.0);
		vscroll.valueProperty().addListener((src,old,val) -> setAbsolutePositionVertical(val.doubleValue()));
		vscroll.addEventFilter(ScrollEvent.ANY, (ev) -> ev.consume());
		
		hscroll = createHScrollBar();
		hscroll.setOrientation(Orientation.HORIZONTAL);
		hscroll.setManaged(true);
		hscroll.setMin(0.0);
		hscroll.setMax(1.0);
		hscroll.valueProperty().addListener((src,old,val) -> setAbsolutePositionHorizontal(val.doubleValue()));
		hscroll.addEventFilter(ScrollEvent.ANY, (ev) -> ev.consume());
		hscroll.visibleProperty().bind(wrapLinesProperty.not());
		hscroll.valueProperty().addListener((s,p,c) -> handleHorizontalScroll(c.doubleValue()));
		
		getChildren().addAll(vflow, vscroll, hscroll);
		
		selector.segments.addListener(new ListChangeListener<SelectionSegment>()
		{
			public void onChanged(Change<? extends SelectionSegment> ch)
			{
				vflow.repaintSegment(ch);
			}
		});
		
//		Binder.onChange(vflow::updateBlinkRate, true, blinkRateProperty());
		Binder.onChange(this::updateLayout, widthProperty(), heightProperty(), showLineNumbersProperty);
		wrapLinesProperty.addListener((s,p,c) -> handleWrapChange());
		
		// key map
		KeyMap.onKeyPressed(this, KeyCode.A, KeyMap.SHORTCUT, this::selectAll);
		KeyMap.onKeyPressed(this, KeyCode.C, KeyMap.SHORTCUT, this::copy);
		KeyMap.onKeyPressed(this, KeyCode.DOWN, this::moveDown);
		KeyMap.onKeyPressed(this, KeyCode.PAGE_DOWN, this::pageDown);
		KeyMap.onKeyPressed(this, KeyCode.PAGE_UP, this::pageUp);
		KeyMap.onKeyPressed(this, KeyCode.UP, this::moveUp);
		
		initMouseController();
		
		// init key handler
		addEventFilter(KeyEvent.ANY, (ev) -> handleKeyEvent(ev));
		
		setFont(Font.font("Monospace", 18));
	}
	
	
	public void setFont(Font f)
	{
		vflow.setFont(f);
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
		requestLayout();
	}
	
	
	/** override to provide your own selection model */
	protected SelectionController createSelectionController()
	{
		return new SelectionController();
	}
	
	
	/** override to provide your own mouse handler */
	protected void initMouseController()
	{
		FxTextEditorMouseHandler h = new FxTextEditorMouseHandler(this, selector);
		
		vflow.addEventFilter(MouseEvent.MOUSE_CLICKED, (ev) -> h.handleMouseClicked(ev));
		vflow.addEventFilter(MouseEvent.MOUSE_PRESSED, (ev) -> h.handleMousePressed(ev));
		vflow.addEventFilter(MouseEvent.MOUSE_RELEASED, (ev) -> h.handleMouseReleased(ev));
		vflow.addEventFilter(MouseEvent.MOUSE_DRAGGED, (ev) -> h.handleMouseDragged(ev));
		vflow.addEventFilter(ScrollEvent.ANY, (ev) -> h.handleScroll(ev));
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

	
	protected Runnable getActionForKeyEvent(KeyEvent ev)
	{
		return null;
	}
	
	
	public void setModel(FxTextEditorModel m)
	{
		markers.clear();
		
		FxTextEditorModel old = getModel();
		if(old != null)
		{
			old.removeListener(modelListener);
//			old.loadStatus.removeListener(loadStatusListener);
		}
		
		modelProperty.set(m);
		
		if(m != null)
		{
			m.addListener(modelListener);
//			m.loadStatus.addListener(loadStatusListener);
		}
		
		selector.clear();
		vflow.repaint();
		
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
	
	
//	protected void updateLoadStatus(LoadStatus s)
//	{
//		if(vscroll instanceof XScrollBar)
//		{
//			XScrollBar vs = (XScrollBar)vscroll;
//			if(s.isValid())
//			{
//				vs.setPainer((canvas) ->
//				{
//					double w = canvas.getWidth();
//					double h = canvas.getHeight();
//					double y = s.getProgress() * h;
//					GraphicsContext g = canvas.getGraphicsContext2D();
//					g.setFill(Color.LIGHTGRAY);
//					g.fillRect(0, y, w, h - y);
//				});
//			}
//			else
//			{
//				vs.setPainer(null);
//			}
//		}
//	}
	
	
	protected void setAbsolutePositionVertical(double pos)
	{
		if(handleScrollEvents)
		{
			// TODO account for visible line count
			int start = FX.round(getModel().getLineCount() * pos);
			setTopLine(start);
		}
	}
	
	
	protected void setAbsolutePositionHorizontal(double pos)
	{
		int off = FX.round(vflow.getMaxColumnCount() * pos);
		setTopOffset(off);
	}
	
	
	protected void setHandleScrollEvents(boolean on)
	{
		handleScrollEvents = on;
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
	
	
	protected void setTopLine(int ix)
	{
		vflow.setTopLine(ix);
		updateLayout();
	}
	
	
	protected void setTopOffset(int off)
	{
		vflow.setTopOffset(off);
		updateLayout();
	}
	
	
	// FIX rename invalidate?
	protected void updateLayout()
	{
		// TODO
//			if(wordWrapProperty.get())
//			{
//				vflow.offsetx = 0;
//			}
		vflow.invalidate();
		repaint();
	}
	
	
	protected void handleWrapChange()
	{
		D.print("handleWrapChange", isWrapLines()); // FIX
		vflow.invalidate();
		requestLayout();
		repaint();
	}
	
	
	protected void layoutChildren()
	{
		D.print("layoutChildren"); // FIX
		
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
		
		double w = getWidth() - m.getLeft() - m.getRight() - vscrollWidth - 1;
		double h = getHeight() - m.getTop() - m.getBottom() - hscrollHeight - 1;

		// layout children
		layoutInArea(vscroll, w, y0 + 1, vscrollWidth, h, 0, null, true, true, HPos.RIGHT, VPos.TOP);
		layoutInArea(hscroll, x0 + 1, h, w, hscrollHeight, 0, null, true, true, HPos.LEFT, VPos.BOTTOM);
		layoutInArea(vflow, x0, y0, w, h, 0, null, true, true, HPos.LEFT, VPos.TOP);
	}
	
	
	/** returns text position at the specified screen coordinates */
	public Marker getTextPos(double screenx, double screeny)
	{
		return vflow.getTextPos(screenx, screeny, markers);
	}
	
	
	public Marker newMarker(int lineNumber, int position)
	{
		return markers.newMarker(lineNumber, position);
	}
	
	
//	protected CaretLocation getCaretLocation(Marker pos)
//	{
//		return vflow.layout.getCaretLocation(this, pos);
//	}
	
	
//	protected int getViewStartLine()
//	{
//		return vflow.layout.getTopLine();
//	}
	
	
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
		
//			vflow.invalidateLayout();
//			vflow.reset();
		
		vflow.repaint();
		
		if(vscroll != null)
		{
			vscroll.setValue(0);
		}
		
		if(hscroll != null)
		{
			hscroll.setValue(0);
		}
		
		updateLayout();
	}


	protected void handleTextUpdated(int startLine, int startPos, int startCharsInserted, int linesInserted, int endLine, int endPos, int endCharsInserted)
	{
		// TODO
		D.print(startLine, startPos, startCharsInserted, linesInserted, endLine, endPos, endCharsInserted);
		
		// update markers
//		markers.update(startLine, startPos, startCharsInserted, linesInserted, endLine, endPos, endCharsInserted);
//		// update vflow
//		vflow.update(startLine, linesInserted, endLine);
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
//		vflow.pageUp();
	}
	
	
	public void pageDown()
	{
//		vflow.pageDown();
	}
	
	
	public void moveUp()
	{
		// TODO
		D.print("moveUp");
	}
	
	
	public void moveDown()
	{
		// TODO
		D.print("moveDown");
	}
	
	
	public void scroll(double fractionOfHeight)
	{
		// TODO
//		vflow.scroll(fractionOfHeight);
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
		// TODO
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
	
	
	protected void setSuppressBlink(boolean on)
	{
		// TODO
//		vflow.setSuppressBlink(on);
	}


//	public void scrollToVisible(int row)
//	{
//		if((row >= 0) && (row < getLineCount()))
//		{
//			// FIX smarter positioning so the target line is somewhere at 25% of the height
//			vflow.scrollToVisible(row);
//		}
//	}
//	
//	
//	public void setOrigin(int row)
//	{
//		if((row >= 0) && (row < getLineCount()))
//		{
//			vflow.setOrigin(row, 0);
//		}
//	}
	
	
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
			// TODO
//			vflow.setHorizontalScroll(val);
		}
	}
	
	
	protected void handleKeyEvent(KeyEvent ev)
	{
		if(!ev.isConsumed())
		{
			EventType<KeyEvent> t = ev.getEventType();
			if(t == KeyEvent.KEY_PRESSED)
			{
				handleKeyPressed(ev);
			}
			else if(t == KeyEvent.KEY_RELEASED)
			{
				handleKeyReleased(ev);
			}
			else if(t == KeyEvent.KEY_TYPED)
			{
				handleKeyTyped(ev);
			}
		}
	}


	protected void handleKeyPressed(KeyEvent ev)
	{
	}
	
	
	protected void handleKeyReleased(KeyEvent ev)
	{
	}
	
	
	protected void handleKeyTyped(KeyEvent ev)
	{
//		FxEditorModel m = getModel();
//		if(m.isEditable())
//		{
//			String ch = ev.getCharacter();
//			if(isTypedCharacter(ch))
//			{
//				Edit ed = new Edit(getSelection(), ch);
//				try
//				{
//					Edit undo = m.edit(ed);
//					// TODO add to undo manager
//				}
//				catch(Exception e)
//				{
//					// TODO provide user feedback
//					Log.ex(e);
//				}
//			}
//		}
	}


	protected boolean isTypedCharacter(String ch)
	{
		if(KeyEvent.CHAR_UNDEFINED.equals(ch))
		{
			return false;
		}
		
		int len = ch.length();
		switch(len)
		{
		case 0:
			return false;
		case 1:
			break;
		default:
			return true;
		}
		
		char c = ch.charAt(0);
		if(c < ' ')
		{
			return false;
		}
		
		switch(c)
		{
		case 0x7f:
			return false;
		default:
			return true;
		}
	}
	
	
	public void setCaret(int row, int position)
	{
		Marker m = newMarker(row, position);
		select(m, m);
	}


	public void selectLine(Marker m)
	{
		if(m != null)
		{
			int line = m.getLine();
			Marker start = markers.newMarker(line, 0);
			
			int len = getModel().getCellCount(line);
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
		return getModel().getCellCount(line);
	}


	public void repaint()
	{
		vflow.repaint();
	}
	
	
	public Color getCaretLineColor()
	{
		// TODO property
		return FX.rgb(200, 255, 200, 0.05);
	}
	
	
	public Color getSelectionBackgroundColor()
	{
		// TODO property
		return FX.rgb(255, 255, 128, 0.7);
	}
	
	
	public boolean isCaretLine(int line)
	{
		return selector.isCaretLine(line);
	}


	public boolean isSelectedCell(int line, int pos)
	{
		return selector.isSelected(line, pos);
	}
}
