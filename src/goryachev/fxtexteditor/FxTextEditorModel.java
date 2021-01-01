// Copyright © 2019-2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.log.Log;
import goryachev.common.util.CKit;
import goryachev.common.util.CList;
import goryachev.common.util.CMap;
import goryachev.common.util.text.IBreakIterator;
import goryachev.fx.FxBoolean;
import goryachev.fx.FxObject;
import goryachev.fxtexteditor.internal.SelectedTextSource;
import goryachev.fxtexteditor.internal.html.HtmlWriter;
import goryachev.fxtexteditor.internal.plain.PlainTextWriter;
import goryachev.fxtexteditor.internal.rtf.RtfWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Set;
import java.util.function.Consumer;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;


/**
 * FxTextEditor Model base class.
 */
public abstract class FxTextEditorModel
{
	// TODO thread-safe getPlainText() and getLineCount() ?
	// perhaps we should not allow access to the model outside of the FX thread
	// TODO temporary setMutable(false) to block edits/updates during long I/O access 
	// such as saving or exporting.

	
	/** 
	 * Returns the number of lines available.  
	 * This number is expected to be changed only as a result of loading progress
	 * (see getLoadInfo()) or an edit().
	 */
	public abstract int getLineCount();
	
	
	/**
	 * Returns the representaion of text on the specified line, 
	 * or null if lineIndex is outside of 0...getLineCount()-1
	 * 
	 * TODO should not return null
	 * TODO ensure that will not get called with lineIndex 
	 * outside of 0...getLineCount()-1 range
	 */
	public abstract ITextLine getTextLine(int line);
	
	
	/**
	 * Applies modification to the model.  The model makes necessary changes to its internal state, 
	 * calls FxTextEditor's event* callbacks, and returns a corresponding undo Edit object.
	 * Throws an exception if this model is read-only.
	 * 
	 * TODO perhaps returning null means no undo, and all prior undoable edits must be cleared. 
	 */
	public abstract Edit edit(Edit ed) throws Exception;
	
	
	/** 
	 * returns the break iterator for mapping characters to glyphs, 
	 * or null when 1:1 correspondence is desired (i.e. for performance reasons)
	 */
	public abstract IBreakIterator getBreakIterator();
	
	//
	
	protected static final Log log = Log.get("FxTextEditorModel");
	protected final FxBoolean editableProperty = new FxBoolean(false);
	protected final FxObject<LoadStatus> loadStatus = new FxObject(LoadStatus.UNKNOWN);
	protected final CList<FxTextEditorModelListener> listeners = new CList<>();
	protected final CMap<DataFormat,IClipboardCopyHandler> copyHandlers = new CMap(1);
	protected final CMap<DataFormat,IClipboardPasteHandler> pasteHandlers = new CMap(0);
	
	
	public FxTextEditorModel()
	{
		setCopyHandler(DataFormat.PLAIN_TEXT, (m,sL,sC,eL,eC) -> copyPlainText(sL, sC, eL, eC));
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
		fireEvent((li) -> 
		{
			li.eventAllLinesChanged();
		});
	}
	
	
	/**
	 * Informs the listeners about the model change.
	 * 
	 * A model mutation must be performed in the FX application thread, 
	 * involve a single contiguous block, and be followed by calling this method.
	 * 
	 * @param startLine - line index where the change begins
	 * @param startPos - character index where the change begins
	 * @param startCharsInserted - the number of characters inserted at (startLine,startPos)
	 * @param linesInserted - the number of lines inserted between startLine and endLine
	 * @param endLine - line index where the change ends
	 * @param endPos - character index where the change ends
	 * @param endCharsInserted - the number of characters inserted at (endLine,endPos)
	 * 
	 * These arguments deal with indexes in order to avoid passing any text strings. 
	 */
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
	
	
	/** 
	 * returns an array of supported formats for copy or paste operation, 
	 * or null if an operation is not supported
	 */
	protected DataFormat[] getSupportedFormats(boolean forCopy) 
	{
		Set<DataFormat> fs =
			forCopy ?
			copyHandlers.keySet() :
			pasteHandlers.keySet();
		return fs.toArray(new DataFormat[fs.size()]);
	}
	
	
	/** to be used by subclasses for additional format support */
	protected void setCopyHandler(DataFormat f, IClipboardCopyHandler h)
	{
		copyHandlers.put(f, h);
	}
	

	/** to be used by subclasses for default RTF copy support */
	protected void setDefaultRtfCopyHandler()
	{
		setCopyHandler(DataFormat.RTF, (m,sL,sC,eL,eC) -> copyRTF(sL, sC, eL, eC));
	}
	
	
	/** to be used by subclasses for default HTML copy support */
	protected void setDefaultHtmlCopyHandler()
	{
		setCopyHandler(DataFormat.HTML, (m,sL,sC,eL,eC) -> copyHTML(sL, sC, eL, eC));
	}
	
	
	/** returns plain text at the specified line, or null if not loaded */
	public final String getPlainText(int line)
	{
		if(line < 0)
		{
			throw new Error("line=" + line);
		}
		else if(line >= getLineCount())
		{
			return null;
		}
		
		ITextLine t = getTextLine(line);
		if(t == null)
		{
			return null;
		}
		return t.getPlainText();
	}
	
	
	/** copies text in the specified format(s) to the clipboard */
	public void copyToClipboard(int startLine, int startPos, int endLine, int endPos, Consumer<Throwable> errorHandler, DataFormat[] formats)
	{
		try
		{
			CMap<DataFormat,Object> m = null;
			
			for(DataFormat f: formats)
			{
				CKit.checkCancelled();
				
				try
				{
					IClipboardCopyHandler h = copyHandlers.get(f);
					if(h != null)
					{
						Object v = h.copy(this, startLine, startPos, endLine, endPos);
						if(v != null)
						{
							if(m == null)
							{
								m = new CMap();
							}
							m.put(f, v);
						}						
					}
				}
				catch(Throwable e)
				{
					if(errorHandler == null)
					{
						log.error("copy " + f, e);
					}
					else
					{
						errorHandler.accept(e);
					}
				}
			}
			
			if(m != null)
			{
				Clipboard c = Clipboard.getSystemClipboard();
				c.setContent(m);
			}
		}
		catch(Throwable e)
		{
			if(errorHandler == null)
			{
				log.error("copy", e);
			}
			else
			{
				errorHandler.accept(e);
			}
		}
	}
	
	
	public String copyPlainText(int startLine, int startPos, int endLine, int endPos) throws Exception
	{		
		StringWriter wr = new StringWriter();
		writePlainText(startLine, startPos, endLine, endPos, wr);
		return wr.toString();
	}
	
	
	public String copyRTF(int startLine, int startPos, int endLine, int endPos) throws Exception
	{
		return RtfWriter.writeString(() -> new SelectedTextSource(this, startLine, startPos, endLine, endPos));
	}
	
	
	public String copyHTML(int startLine, int startPos, int endLine, int endPos) throws Exception
	{
		SelectedTextSource src = new SelectedTextSource(this, startLine, startPos, endLine, endPos);
		return HtmlWriter.writeString(src);
	}
	
	
	public void writePlainText(int startLine, int startPos, int endLine, int endPos, Writer wr) throws Exception
	{
		SelectedTextSource src = new SelectedTextSource(this, startLine, startPos, endLine, endPos);
		new PlainTextWriter(src, wr).write();
	}
}
