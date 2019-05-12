// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;


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
	
	/** returns plain text at the specified line, or null if unknown */
	public abstract String getPlainText(int line);
	
	/** 
	 * returns a non-null TextCells for the given line.
	 */
	public abstract TextCells getTextCells(int line);
	
	/**
	 * Applies modification to the model.  The model makes necessary changes to its internal state, 
	 * calls FxTextEditor's event* callbacks, and returns a corresponding undo Edit object.
	 * Throws an exception if this model is read-only.
	 */
	public abstract Edit edit(Edit ed) throws Exception;
}
