// Copyright © 2016-2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.util.D;
import goryachev.fx.FX;
import goryachev.fx.KeyMap;
import goryachev.fxtexteditor.FxTextEditor;
import goryachev.fxtexteditor.Marker;
import goryachev.fxtexteditor.SelectionController;
import goryachev.fxtexteditor.VFlow;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;


/**
 * FxTextEditor keyboard and mouse input handler.
 */
public class InputHandler
{
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
		vflow.addEventFilter(ScrollEvent.ANY, (ev) -> handleScroll(ev));
		
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
	
	
	protected void handleScroll(ScrollEvent ev)
	{
		if(ev.isShiftDown())
		{
			// TODO horizontal scroll perhaps?
			D.print("horizontal scroll", ev.getDeltaX());
		}
		else if(ev.isShortcutDown())
		{
			// page up / page down
			if(ev.getDeltaY() >= 0)
			{
				editor.actions.pageUp.action();
			}
			else
			{
				editor.actions.pageDown.action();
			}
		}
		else
		{
			// vertical block scroll
			double frac = scrollWheelStepSize * (ev.getDeltaY() >= 0 ? -1 : 1); 
			editor.scroll(frac); 
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
			editor.selectWord(getMarker(ev));
			break;
		case 3:
			editor.selectLine(getMarker(ev));
			ev.consume();
			break;
		}
	}
	
	
	public void handleMousePressed(MouseEvent ev)
	{
		// not sure - perhaps only ignore if the mouse press is within a selection
		// and reset selection if outside?
		if(FX.isPopupTrigger(ev))
		{
			return;
		}

		Marker pos = getMarker(ev);
		vflow.setSuppressBlink(true);
		
		if(ev.isShiftDown())
		{
			// expand selection from the anchor point to the current position
			// clearing existing (possibly multiple) selection
			selector.clearAndExtendLastSegment(pos);
		}
		else if(ev.isShortcutDown())
		{
			selector.setAnchor(pos);
			selector.setSelection(pos);
		}
		else
		{
			editor.clearSelection();
			selector.addSelectionSegment(pos, pos);
			selector.setAnchor(pos);
		}
		
		editor.requestFocus();
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
		
		Marker pos = getMarker(ev);
		selector.extendLastSegment(pos);
	}
	
	
	public void handleMouseReleased(MouseEvent ev)
	{
		stopAutoScroll();
		selector.commitSelection();
		vflow.setSuppressBlink(false);
		vflow.scrollSelectionToVisible();
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
		editor.blockScroll(delta);
		
		Point2D p;
		if(autoScrollUp)
		{
			p = vflow.localToScreen(0, 0);
		}
		else
		{
			p = vflow.localToScreen(0, vflow.getHeight());
		}
		
		// TODO this could be done on mouse released!
		editor.scrollToVisible(p);
		
		Marker pos = editor.getInsertPosition(p.getX(), p.getY());
		selector.extendLastSegment(pos);
	}
	

	public void handleKeyPressed(KeyEvent ev)
	{
		if(ev.isConsumed())
		{
			return; // is this needed?
		}
	}
	
	
	public void handleKeyReleased(KeyEvent ev)
	{
		if(ev.isConsumed())
		{
			return; // is this needed?
		}
	}
	
	
	public void handleKeyTyped(KeyEvent ev)
	{
		if(ev.isConsumed())
		{
			return; // is this needed?
		}
		
		// TODO
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
}