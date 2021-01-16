// Copyright © 2020-2021 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.fx.FxAction;
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
	// TODO all private
	private final FxAction backspace;
	private final FxAction copy;
	private FxAction copyHtml;
	private FxAction copyPlainText;
	private FxAction copyRtf;
	private FxAction delete;
	public FxAction moveDocumentEnd;
	public FxAction moveDocumentStart;
	public FxAction moveDown;
	public FxAction moveEnd;
	public FxAction moveHome;
	public FxAction moveLeft;
	public FxAction moveRight;
	public FxAction moveUp;
	public FxAction pageDown;
	public FxAction pageUp;
	public FxAction selectAll;
	private FxAction smartCopy;
	private FxAction smartCopyHtml;
	private FxAction smartCopyPlainText;
	private FxAction smartCopyRtf;
	//
	private final FxTextEditor editor;
	
	
	public Actions(FxTextEditor ed)
	{
		this.editor = ed;
		
		backspace = new Backspace(ed);
		copy = new Copy(ed);
		delete = new Delete(ed);
		moveDocumentEnd = new MoveDocumentEnd(ed);
		moveDocumentStart = new MoveDocumentStart(ed);
		moveDown = new MoveDown(ed);
		moveEnd = new MoveEnd(ed);
		moveHome = new MoveHome(ed);
		moveLeft = new MoveLeft(ed);
		moveRight = new MoveRight(ed);
		moveUp = new MoveUp(ed);
		pageDown = new PageDown(ed);
		pageUp = new PageUp(ed);
		selectAll = new SelectAll(ed);
	}
	

	public FxAction backspace()
	{
		return backspace;
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
			smartCopy = new SmartCopy(editor);
		}
		return smartCopy;
	}
	
	
	/** @return action which copies selection to clipboard in HTML format, if supported */
	public FxAction copyHtml()
	{
		if(copyHtml == null)
		{
			copyHtml = new CopyHTML(editor);
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
			smartCopyHtml = new SmartCopyHTML(editor);
		}
		return smartCopyHtml;
	}
	
	
	/** @return action which copies selection to clipboard in plain text format, if supported */
	public FxAction copyPlainText()
	{
		if(copyPlainText == null)
		{
			copyPlainText = new CopyPlainText(editor);
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
			smartCopyPlainText = new SmartCopyPlainText(editor);
		}
		return smartCopyPlainText;
	}
	
	
	/** @return action which copies selection to clipboard in RTF format, if supported */
	public FxAction copyRtf()
	{
		if(copyRtf == null)
		{
			copyRtf = new CopyRTF(editor);
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
			smartCopyRtf = new SmartCopyRTF(editor);
		}
		return smartCopyRtf;
	}
	
	
	public FxAction delete()
	{
		return delete;
	}
}
