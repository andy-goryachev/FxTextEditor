// Copyright © 2020-2021 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.fx.FxAction;
import goryachev.fxtexteditor.internal.Markers;
import goryachev.fxtexteditor.op.Backspace;
import goryachev.fxtexteditor.op.Copy;
import goryachev.fxtexteditor.op.CopyHTML;
import goryachev.fxtexteditor.op.CopyPlainText;
import goryachev.fxtexteditor.op.CopyRTF;
import goryachev.fxtexteditor.op.Delete;
import goryachev.fxtexteditor.op.MoveDocumentEnd;
import goryachev.fxtexteditor.op.MoveDocumentStart;
import goryachev.fxtexteditor.op.MoveDown;
import goryachev.fxtexteditor.op.MoveEnd;
import goryachev.fxtexteditor.op.MoveHome;
import goryachev.fxtexteditor.op.MoveLeft;
import goryachev.fxtexteditor.op.MoveRight;
import goryachev.fxtexteditor.op.MoveUp;
import goryachev.fxtexteditor.op.PageDown;
import goryachev.fxtexteditor.op.PageUp;
import goryachev.fxtexteditor.op.SelectAll;
import goryachev.fxtexteditor.op.SmartCopy;
import goryachev.fxtexteditor.op.SmartCopyHTML;
import goryachev.fxtexteditor.op.SmartCopyPlainText;
import goryachev.fxtexteditor.op.SmartCopyRTF;


/**
 * Built-in Actions.
 */
public class Actions
{
	public FxAction backspace = new Backspace(this);
	private final FxAction copy = new Copy(this);
	private FxAction copyHtml;
	private FxAction copyPlainText;
	private FxAction copyRtf;
	public FxAction delete = new Delete(this);
	public FxAction moveDocumentEnd = new MoveDocumentEnd(this);
	public FxAction moveDocumentStart = new MoveDocumentStart(this);
	public FxAction moveDown = new MoveDown(this);
	public FxAction moveEnd = new MoveEnd(this);
	public FxAction moveHome = new MoveHome(this);
	public FxAction moveLeft = new MoveLeft(this);
	public FxAction moveRight = new MoveRight(this);
	public FxAction moveUp = new MoveUp(this);
	public FxAction pageDown = new PageDown(this);
	public FxAction pageUp = new PageUp(this);
	public FxAction selectAll = new SelectAll(this);
	private FxAction smartCopy;
	private FxAction smartCopyHtml;
	private FxAction smartCopyPlainText;
	private FxAction smartCopyRtf;
	//
	private final FxTextEditor editor;
	
	
	public Actions(FxTextEditor ed)
	{
		this.editor = ed;
	}
	
	
	public FxTextEditor editor()
	{
		return editor;
	}
	
	
	public VFlow vflow()
	{
		return editor.vflow;
	}
	
	
	public SelectionController selector()
	{
		return editor.selector;
	}
	
	
	public FxTextEditorModel model()
	{
		return editor.getModel();
	}
	
	
	// TODO private or move elsewhere
	public Markers markers()
	{
		return editor.markers;
	}
	
	
	/** @return action which copies selection to clipboard in all supported formats */
	public FxAction copy()
	{
		return copy;
	}
	
	
	/** 
	 * @return action which copies selection to clipboard in all supported formats.
	 * when selection is empty, copies all.
	 */
	public FxAction smartCopy()
	{
		if(smartCopy == null)
		{
			smartCopy = new SmartCopy(this);
		}
		return smartCopy;
	}
	
	
	/** @return action which copies selection to clipboard in HTML format, if supported */
	public FxAction copyHtml()
	{
		if(copyHtml == null)
		{
			copyHtml = new CopyHTML(this);
		}
		return copyHtml;
	}
	
	
	/** 
	 * @return action which copies selection to clipboard in HTML format, if supported.
	 * when selection is empty, copies all.
	 */
	public FxAction smartCopyHtml()
	{
		if(smartCopyHtml == null)
		{
			smartCopyHtml = new SmartCopyHTML(this);
		}
		return smartCopyHtml;
	}
	
	
	/** @return action which copies selection to clipboard in plain text format, if supported */
	public FxAction copyPlainText()
	{
		if(copyPlainText == null)
		{
			copyPlainText = new CopyPlainText(this);
		}
		return copyPlainText;
	}
	
	
	/** 
	 * @return action which copies selection to clipboard in plain text format, if supported.
	 * when selection is empty, copies all.
	 */
	public FxAction smartCopyPlainText()
	{
		if(smartCopyPlainText == null)
		{
			smartCopyPlainText = new SmartCopyPlainText(this);
		}
		return smartCopyPlainText;
	}
	
	
	/** @return action which copies selection to clipboard in RTF format, if supported */
	public FxAction copyRtf()
	{
		if(copyRtf == null)
		{
			copyRtf = new CopyRTF(this);
		}
		return copyRtf;
	}
	
	
	/** 
	 * @return action which copies selection to clipboard in RTF format, if supported.
	 * when selection is empty, copies all.
	 */
	public FxAction smartCopyRtf()
	{
		if(smartCopyRtf == null)
		{
			smartCopyRtf = new SmartCopyRTF(this);
		}
		return smartCopyRtf;
	}
}
