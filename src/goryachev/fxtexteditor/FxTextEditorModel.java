// Copyright © 2019-2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.util.CList;
import goryachev.fx.FxBoolean;
import goryachev.fx.FxObject;
import java.util.function.Consumer;


/**
 * FxTextEditor Model.
 */
public abstract class FxTextEditorModel
{
	/** 
	 * Returns the number of lines available.  
	 * This number is expected to be changed only as a result of loading progress
	 * (see getLoadInfo()) or an edit().
	 */
	public abstract int getLineCount();
	
	/**
	 * Returns the representaion of text on the specified line, 
	 * or null if lineIndex is outside of 0...getLineCount()-1
	 */
	public abstract ITextLine getTextLine(int line);
	
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
	
	
	public FxBoolean editableProperty()
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
	
	
	public void setLoadComplete()
	{
		setLoadStatus(LoadStatus.COMPLETE);
	}
	
	
	public LoadStatus getLoadStatus()
	{
		return loadStatus.get();
	}
	
	
	/** returns plain text at the specified line, or null if not loaded */
	public final String getPlainText(int line)
	{
		ITextLine t = getTextLine(line);
		if(t == null)
		{
			return null;
		}
		return t.getPlainText();
	}
}
