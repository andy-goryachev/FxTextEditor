// Copyright © 2016-2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.log.Log;
import goryachev.common.util.CKit;
import goryachev.fx.FX;
import goryachev.fx.KeyMap;
import goryachev.fxtexteditor.Edit;
import goryachev.fxtexteditor.EditorSelection;
import goryachev.fxtexteditor.FxTextEditor;
import goryachev.fxtexteditor.FxTextEditorModel;
import goryachev.fxtexteditor.Marker;
import goryachev.fxtexteditor.SelectionController;
import goryachev.fxtexteditor.VFlow;
import java.util.function.BiConsumer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;


/**
 * Keyboard and mouse input handler.
 */
public class InputHandler
{
	protected static final Log log = Log.get("InputHandler");
	protected final FxTextEditor editor;
	protected final VFlow vflow;
	protected final SelectionController selector;
	protected final Timeline autoScrollTimer;
	private boolean fastAutoScroll;
	private Duration autoScrollPeriod = Duration.millis(100); // arbitrary number
	private double fastScrollThreshold = 100; // arbitrary number
	private double autoScrollStepFast = 200; // arbitrary
	private double autoScrollStepSlow = 20; // arbitrary
	private boolean autoScrollUp;
	private double scrollWheelStepSize = 0.1;
	private int lastx = -1;
	private int lasty = -1;
	private boolean inTrackpadScroll;
	protected BiConsumer<FxTextEditor,Marker> doubleClickHandler = new SimpleWordSelector();
	protected BiConsumer<FxTextEditor,Marker> tripleClickHandler = new LineSelector();


	public InputHandler(FxTextEditor ed, VFlow f, SelectionController sel)
	{
		this.editor = ed;
		this.vflow = f;
		this.selector = sel;
		
		autoScrollTimer = new Timeline(new KeyFrame(autoScrollPeriod, (ev) -> autoScroll()));
		autoScrollTimer.setCycleCount(Timeline.INDEFINITE);
		
		vflow.addEventFilter(MouseEvent.MOUSE_CLICKED, (ev) -> handleMouseClicked(ev));
		vflow.addEventFilter(MouseEvent.MOUSE_PRESSED, (ev) -> handleMousePressed(ev));
		vflow.addEventFilter(MouseEvent.MOUSE_RELEASED, (ev) -> handleMouseReleased(ev));
		vflow.addEventFilter(MouseEvent.MOUSE_DRAGGED, (ev) -> handleMouseDragged(ev));
		vflow.addEventFilter(ScrollEvent.ANY, (ev) -> handleScrollWheel(ev));
		
		// key map
		KeyMap.onKeyPressed(ed, KeyCode.A, KeyMap.SHORTCUT, ed.actions.selectAll);
		KeyMap.onKeyPressed(ed, KeyCode.C, KeyMap.SHORTCUT, ed.actions.copy);
		KeyMap.onKeyPressed(ed, KeyCode.BACK_SPACE, ed.actions.backspace);
		KeyMap.onKeyPressed(ed, KeyCode.DELETE, ed.actions.delete);
		KeyMap.onKeyPressed(ed, KeyCode.DOWN, ed.actions.moveDown);
		KeyMap.onKeyPressed(ed, KeyCode.END, ed.actions.moveEnd);
		KeyMap.onKeyPressed(ed, KeyCode.END, KeyMap.SHORTCUT, ed.actions.moveDocumentEnd);
		KeyMap.onKeyPressed(ed, KeyCode.HOME, ed.actions.moveHome);
		KeyMap.onKeyPressed(ed, KeyCode.HOME, KeyMap.SHORTCUT, ed.actions.moveDocumentStart);
		KeyMap.onKeyPressed(ed, KeyCode.LEFT, ed.actions.moveLeft);
		KeyMap.onKeyPressed(ed, KeyCode.PAGE_DOWN, ed.actions.pageDown);
		KeyMap.onKeyPressed(ed, KeyCode.PAGE_UP, ed.actions.pageUp);
		KeyMap.onKeyPressed(ed, KeyCode.RIGHT, ed.actions.moveRight);
		KeyMap.onKeyPressed(ed, KeyCode.UP, ed.actions.moveUp);
		
		// TODO remove?
		ed.addEventFilter(KeyEvent.KEY_PRESSED, (ev) -> handleKeyPressed(ev));
		ed.addEventFilter(KeyEvent.KEY_RELEASED, (ev) -> handleKeyReleased(ev));
		ed.addEventFilter(KeyEvent.KEY_TYPED, (ev) -> handleKeyTyped(ev));
	}
	

	protected void handleScrollWheel(ScrollEvent ev)
	{
		EventType<ScrollEvent> t = ev.getEventType();
		if(t == ScrollEvent.SCROLL_STARTED)
		{
			inTrackpadScroll = true;
			return;
		}
		else if(t == ScrollEvent.SCROLL_FINISHED)
		{
			inTrackpadScroll = false;
			return;
		}
		
		int step;
		if(inTrackpadScroll)
		{
			// TODO another property?
			step = 3;
		}
		else
		{
			if(ev.isShortcutDown())
			{
				// full page
				step = Integer.MAX_VALUE;
			}
			else
			{
				step = getScrollStepSize();
			}
		}
		
		boolean up = (ev.getDeltaY() >= 0);
		vflow.scroll(step, up); 
	}
	
	
	protected int getScrollStepSize()
	{
		double v = editor.getScrollWheelStepSize();
		
		int step;
		if(v < 0)
		{
			step = CKit.round((-v) * vflow.getScreenRowCount());
		}
		else
		{
			step = (int)v;
		}
		
		if(step < 1)
		{
			return 1;
		}
		else
		{
			return step;
		}
	}
	
	
	protected Marker getMarker(MouseEvent ev)
	{
		double x = ev.getScreenX();
		double y = ev.getScreenY();
		return editor.getInsertPosition(x, y);
	}
	
	
	public void handleMouseClicked(MouseEvent ev)
	{
		if(ev.getButton() != MouseButton.PRIMARY)
		{
			return;
		}
		
		int clicks = ev.getClickCount();
		switch(clicks)
		{
		case 2:
			handleDoubleClick(ev);
			break;
		case 3:
			handleTripleClick(ev);
			ev.consume();
			break;
		}
	}
	
	
	public void handleDoubleClick(MouseEvent ev)
	{
		if(doubleClickHandler != null)
		{
			Marker m = getMarker(ev);
			if(m != null)
			{
				doubleClickHandler.accept(editor, m);
			}
		}
	}
	
	
	public void handleTripleClick(MouseEvent ev)
	{
		if(tripleClickHandler != null)
		{
			Marker m = getMarker(ev);
			if(m != null)
			{
				tripleClickHandler.accept(editor, m);
			}
		}
	}
	
	
	public void handleMousePressed(MouseEvent ev)
	{
		ev.consume();
		editor.requestFocus();
		
		Marker m = getMarker(ev);

		if(FX.isPopupTrigger(ev))
		{
			if(!selector.isSelected(m))
			{
				selector.setAnchor(m);
				selector.setSelection(m);
			}
			return;
		}

		vflow.setSuppressBlink(true);
		
		if(ev.isShiftDown())
		{
			// expand selection from the anchor point to the current position
			// clearing existing (possibly multiple) selection
			selector.clearAndExtendLastSegment(m);
		}
		else if(ev.isShortcutDown())
		{
			selector.setAnchor(m);
			selector.setSelection(m);
		}
		else
		{
			editor.clearSelection();
			selector.addSelectionSegment(m, m);
			selector.setAnchor(m);
		}
	}
	

	public void handleMouseDragged(MouseEvent ev)
	{
		if(!FX.isLeftButton(ev))
		{
			return;
		}
		
		double y = ev.getY();
		if(y < 0)
		{
			// above vflow
			autoScroll(y);
			return;
		}
		else if(y > vflow.getHeight())
		{
			// below vflow
			autoScroll(y - vflow.getHeight());
			return;
		}
		else
		{
			stopAutoScroll();
		}
		
		Marker m = getMarker(ev);
		selector.extendLastSegment(m);
	}
	
	
	public void handleMouseReleased(MouseEvent ev)
	{
		stopAutoScroll();
		selector.commitSelection();
		vflow.setSuppressBlink(false);
		vflow.scrollCaretToView();
		vflow.setPhantomColumnFromCursor();
		vflow.scrollCaretToView();
	}
	
	
	protected void autoScroll(double delta)
	{
		autoScrollUp = delta < 0;
		fastAutoScroll = Math.abs(delta) > fastScrollThreshold;
		autoScrollTimer.play();
	}
	
	
	protected void stopAutoScroll()
	{
		autoScrollTimer.stop();
	}
	
	
	protected void autoScroll()
	{
		double delta = fastAutoScroll ? autoScrollStepFast : autoScrollStepSlow;
		if(autoScrollUp)
		{
			delta = -delta;
		}
		vflow.blockScroll(delta);
		
		Point2D p;
		if(autoScrollUp)
		{
			p = vflow.localToScreen(0, 0);
		}
		else
		{
			p = vflow.localToScreen(0, vflow.getHeight());
		}
		
		Marker pos = editor.getInsertPosition(p.getX(), p.getY());
		selector.extendLastSegment(pos);
	}
	

	public void handleKeyPressed(KeyEvent ev)
	{
		if(ev.isConsumed())
		{
			return; // is this needed?
		}
		
		switch(ev.getCode())
		{
		case ENTER:
		case TAB:
			handleKeyTyped(ev.getCode(), ev.isControlDown(), ev.isShiftDown(), ev.isShortcutDown());
			break;
		default:
			return;
		}
		
		ev.consume();
	}
	

	public void handleKeyReleased(KeyEvent ev)
	{
		if(ev.isConsumed())
		{
			return; // is this needed?
		}
	}

		
	protected void handleKeyTyped(KeyCode code, boolean ctrl, boolean shift, boolean shortcut)
	{
		FxTextEditorModel m = editor.getModel();
		if(m.isEditable())
		{
			EditorSelection sel = editor.getSelection();
			if(sel != null)
			{
				vflow.setSuppressBlink(true);
				try
				{
					Object typed;

					switch(code)
					{
					case ENTER:
						typed = new String[] { "", "" };
						break;
					case TAB:
						// TODO shift (+selection), ctrl?
						typed = "\n";
						break;
					default:
						throw new Error("?" + code);
					}
					
					try
					{
						Edit ed = Edit.create(sel.getSegment(), typed);
						Edit undo = m.edit(ed);
						// TODO add to undo manager
						
						updateSelection(ed);
					}
					catch(Exception e)
					{
						// TODO provide user feedback
						log.error(e);
					}
				}
				finally
				{
					vflow.setSuppressBlink(false);
					
					// TODO update editor selection (selection and segment property)
				}
			}
		}
	}

	
	public void handleKeyTyped(KeyEvent ev)
	{
		if(ev.isConsumed())
		{
			return; // is this needed?
		}

		FxTextEditorModel m = editor.getModel();
		if(m == null)
		{
			return;
		}
		
		if(m.isEditable())
		{
			EditorSelection sel = editor.getSelection();
			if(sel != null)
			{
				vflow.setSuppressBlink(true);
				try
				{
					String ch = ev.getCharacter();
					if(isTypedCharacter(ch))
					{
						try
						{
							Edit ed = Edit.create(sel.getSegment(), ch);
							Edit undo = m.edit(ed);
							// TODO add to undo manager
							updateSelection(ed);
							ev.consume();
						}
						catch(Exception e)
						{
							// TODO provide user feedback
							log.error(e);
						}
					}
				}
				finally
				{
					vflow.setSuppressBlink(false);
				}
			}
		}
	}


	protected void updateSelection(Edit ed)
	{
		// TODO
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
			switch(c)
			{
			case '\t':
				return true;
			}
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
	
	
	public void setDoubleClickHandler(BiConsumer<FxTextEditor,Marker> h)
	{
		doubleClickHandler = h;
	}
	
	
	public BiConsumer<FxTextEditor,Marker> getDoubleClickHandler()
	{
		return doubleClickHandler;
	}
	
	
	public void setTripleClickHandler(BiConsumer<FxTextEditor,Marker> h)
	{
		tripleClickHandler = h;
	}
	
	
	public BiConsumer<FxTextEditor,Marker> getTripleClickHandler()
	{
		return tripleClickHandler;
	}
}