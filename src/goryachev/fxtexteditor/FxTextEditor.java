// Copyright © 2019-2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.log.Log;
import goryachev.fx.CPane;
import goryachev.fx.FX;
import goryachev.fx.Formatters;
import goryachev.fx.FxBoolean;
import goryachev.fx.FxFormatter;
import goryachev.fx.FxInt;
import goryachev.fx.FxObject;
import goryachev.fx.XScrollBar;
import goryachev.fxtexteditor.internal.InputHandler;
import goryachev.fxtexteditor.internal.Markers;
import goryachev.fxtexteditor.internal.TabPolicy;
import java.io.StringWriter;
import java.io.Writer;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
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
	protected static final Log log = Log.get("FxTextEditor");
	public final Actions actions = new Actions(this);
	protected final FxObject<Color> backgroundColor = new FxObject(Color.WHITE);
	protected final FxObject<Color> caretLineColor = new FxObject(FX.rgb(255, 200, 255));
	protected final FxObject<Color> selectionBackgroundColor = new FxObject(FX.rgb(255, 255, 128));
	protected final FxObject<Color> lineNumberColor = new FxObject(Color.GRAY);
	protected final FxObject<Font> fontProperty = new FxObject(Font.font("Monospace", 12));
	protected final FxBoolean editableProperty = new FxBoolean(false);
	protected final ReadOnlyObjectWrapper<FxTextEditorModel> modelProperty = new ReadOnlyObjectWrapper<>();
	protected final FxBoolean wrapLinesProperty = new FxBoolean(true);
	protected final FxBoolean displayCaretProperty = new FxBoolean(true);
	protected final FxBoolean showLineNumbersProperty = new FxBoolean(false);
	protected final FxBoolean highlightCaretLineProperty = new FxBoolean(true);
	protected final FxInt scrollWheelStepSize = new FxInt(Integer.MAX_VALUE);
	protected final ReadOnlyObjectWrapper<Duration> caretBlinkRateProperty = new ReadOnlyObjectWrapper(Duration.millis(500));
	protected final FxObject<FxFormatter> lineNumberFormatterProperty = new FxObject<>();
	protected final FxObject<ITabPolicy> tabPolicy = new FxObject();
	protected final FxTextEditorModelListener modelListener;
	protected final SelectionController selector;
	protected final Markers markers = new Markers(32);
	protected final VFlow vflow;
	protected final ScrollBar vscroll;
	protected final ScrollBar hscroll;
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
		
		vscroll = createVScrollBar();
		vscroll.setOrientation(Orientation.VERTICAL);
		vscroll.setManaged(true);
		vscroll.setMin(0.0);
		vscroll.setMax(1.0);
		vscroll.addEventFilter(ScrollEvent.ANY, (ev) -> ev.consume());
		
		hscroll = createHScrollBar();
		hscroll.setOrientation(Orientation.HORIZONTAL);
		hscroll.setManaged(true);
		hscroll.setMin(0.0);
		hscroll.setMax(1.0);
		hscroll.addEventFilter(ScrollEvent.ANY, (ev) -> ev.consume());
		hscroll.visibleProperty().bind(wrapLinesProperty.not());
		
		vflow = new VFlow(this);
		
		getChildren().addAll(vflow, vscroll, hscroll);
				
		// TODO
//		FX.onChange(vflow::updateBlinkRate, true, blinkRateProperty());
		
		createInputHandler();
		setFocusTraversable(true);
		
		setTabPolicy(TabPolicy.create(4));
	}
	
	
	/** override to provide your own implementation.  warning: this method is called from the constructor */
	protected ScrollBar createVScrollBar()
	{
		return new XScrollBar();
	}
	
	
	/** override to provide your own implementation.  warning: this method is called from the constructor */
	protected ScrollBar createHScrollBar()
	{
		return new XScrollBar();
	}
	
	
	/** override to provide your own implementation.  warning: this method is called from the constructor */
	protected void createInputHandler()
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
			f = Formatters.getIntegerFormatter();
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
	
	
	public ReadOnlyProperty<SelectionSegment> selectionSegmentProperty()
	{
		return selector.selectionSegmentProperty();
	}
	
	
	public SelectionSegment getSelectedSegment()
	{
		return selector.getSelectedSegment();
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
			updateLoadStatus(m.getLoadStatus());
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
	
	
	public ReadOnlyObjectProperty<FxTextEditorModel> modelProperty()
	{
		return modelProperty.getReadOnlyProperty();
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
		int off = p.getCharIndex();
		
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
	
	
	/** sets the scroll wheel step size (in lines).  the actual value will be clipped to the range [1..screenRowCount] */
	public void setScrollWheelStepSize(int n)
	{
		scrollWheelStepSize.set(n);
	}
	
	
	public int getScrollWheelStepSize()
	{
		return scrollWheelStepSize.get();
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
		
		vflow.setOrigin(row, 0);
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
			
			int endLine = line + 1;
			
			Marker end;
			if(endLine >= getLineCount())
			{
				int len = getTextLength(line);
				end = markers.newMarker(line, len);
			}
			else
			{
				end = markers.newMarker(endLine, 0);
			}
			
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
		FxTextEditorModel m = getModel();
		if(m == null)
		{
			return 0;
		}
		String s = m.getPlainText(line);
		if(s == null)
		{
			return 0;
		}
		return s.length();
	}

	
	public Color getCaretLineColor()
	{
		return caretLineColor.get();
	}
	
	
	public void setCaretLineColor(Color c)
	{
		caretLineColor.set(c);
	}
	
	
	public Color getSelectionBackgroundColor()
	{
		return selectionBackgroundColor.get();
	}
	
	
	public void setSelectionBackgroundColor(Color c)
	{
		selectionBackgroundColor.set(c);
	}
	
	
	public Color getLineNumberColor()
	{
		return lineNumberColor.get();
	}
	
	
	public void setLineNumberColor(Color c)
	{
		lineNumberColor.set(c);
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
	
	
	public boolean isSelected(int line, int pos)
	{
		return selector.isSelected(line, pos);
	}
	
	
	public void setBackgroundColor(Color c)
	{
		backgroundColor.set(c);
	}
	
	
	public Color getBackgroundColor()
	{
		return backgroundColor.get();
	}
	
	
	public FxObject<Color> backgroundColorProperty()
	{
		return backgroundColor;
	}
	
	
	public int getColumnAt(Marker m)
	{
		int line = m.getLine();
		int pos = m.getCharIndex();
		return vflow.getColumnAt(line, pos);
	}
	

	/** returns plain text on the specified line */
	public String getPlainText(int line)
	{
		FxTextEditorModel m = getModel();
		if(m == null)
		{
			return null;
		}
		return m.getPlainText(line);
	}


	/** returns selected plain text */
	public String getSelectedPlainText(int maxLength) throws Exception
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
	
	
	/** copies all supported formats */
	public void doCopy()
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
	

	public void select(Marker start, Marker end)
	{
		selector.setSelection(start, end);
		selector.commitSelection();
	}
	

	protected void handleTextUpdated(int startLine, int startPos, int startCharsInserted, int linesInserted, int endLine, int endPos, int endCharsInserted)
	{
		log.debug("startLine=%d startPos=%d startCharsInserted=%d linesInserted=%d endLine=%d endPos=%d endCharsInserted=%d", startLine, startPos, startCharsInserted, linesInserted, endLine, endPos, endCharsInserted);
		
		markers.update(startLine, startPos, startCharsInserted, linesInserted, endLine, endPos, endCharsInserted);
		vflow.update(startLine, linesInserted, endLine);
	}
}
