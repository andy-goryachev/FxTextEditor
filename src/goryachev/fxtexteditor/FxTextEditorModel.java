// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.util.CList;
import goryachev.fx.FxBoolean;
import goryachev.fx.FxObject;
import java.util.function.Consumer;
import javafx.beans.property.BooleanProperty;


/**
 * FxTextEditor Model.
 */
public abstract class FxTextEditorModel
{
	/** 
	 * returns information about the loading process status and an estimate of line count/file size. 
	 * returns null if the data has been already loaded.
	 */ 
	public abstract LoadInfo getLoadInfo();
	
	/** 
	 * returns a known line count.  
	 * if the model is still loading, returns the best estimate of the number of lines. 
	 */
	public abstract int getLineCount();
	
	/** returns the number of cells on a particular line */
	public final void getCellCount(int line) { } // FIX remove
	
	/** returns plain text at the specified line, or null if unknown */
	public abstract String getPlainText(int line);
	
	/**
	 * returns styling information for a particular line:
	 * style segments in terms of string characters and not grapheme blocks.
	 * The editor will use this information to decorate grapheme blocks extracted
	 * from the plain text.
	 * @param line - text line index
	 * @param text - plain text obtained earlier from getPlainText()
	 * @param d - pre-allocated and reset object that receives styling info
	 * @return the styling info object or null if no styling is desired or available
	 */
	public abstract TextDecor getTextDecor(int line, String text, TextDecor d);
	
	/**
	 * Applies modification to the model.  The model makes necessary changes to its internal state, 
	 * calls FxTextEditor's event* callbacks, and returns a corresponding undo Edit object.
	 * Throws an exception if this model is read-only.
	 */
	public abstract Edit edit(Edit ed) throws Exception;
	
	//
	
	protected final FxBoolean editableProperty = new FxBoolean(false);
	protected final FxObject<LoadStatus> loadStatus = new FxObject(LoadStatus.UNKNOWN);
	protected final CList<FxTextEditorModelListener> listeners = new CList<>();
	
	
	public FxTextEditorModel()
	{
	}
	
	
	public FxObject<LoadStatus> loadStatus()
	{
		return loadStatus;
	}
	

	public void addListener(FxTextEditorModelListener li)
	{
		listeners.add(li);
	}
	
	
	public void removeListener(FxTextEditorModelListener li)
	{
		listeners.remove(li);
	}
	
	
	public boolean isEditable()
	{
		return editableProperty.get();
	}
	
	
	public void setEditable(boolean on)
	{
		editableProperty.set(on);
	}
	
	
	public BooleanProperty editableProperty()
	{
		return editableProperty;
	}
	
	
	public void fireAllChanged()
	{
		fireEvent((li) -> li.eventAllLinesChanged());
	}
	
	
	public void fireTextUpdated(int startLine, int startPos, int startCharsInserted, int linesInserted, int endLine, int endPos, int endCharsInserted)
	{
		fireEvent((li) -> li.eventTextUpdated(startLine, startPos, startCharsInserted, linesInserted, endLine, endPos, endCharsInserted));
	}
	
	
	protected void fireEvent(Consumer<FxTextEditorModelListener> f)
	{
		for(FxTextEditorModelListener li: listeners)
		{
			f.accept(li);
		}
	}
	
	
	public void setLoadStatus(LoadStatus s)
	{
		if(s == null)
		{
			throw new NullPointerException("load status");
		}
		loadStatus.set(s);
	}
}
