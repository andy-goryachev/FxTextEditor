// Copyright © 2020-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.fx.FX;
import goryachev.fx.FxAction;
import goryachev.fxtexteditor.op.Backspace;
import goryachev.fxtexteditor.op.Copy;
import goryachev.fxtexteditor.op.CopyHTML;
import goryachev.fxtexteditor.op.CopyPlainText;
import goryachev.fxtexteditor.op.CopyRTF;
import goryachev.fxtexteditor.op.Delete;
import goryachev.fxtexteditor.op.MoveDocumentEnd;
import goryachev.fxtexteditor.op.MoveDocumentEndAtPos0;
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
	private final FxAction backspace;
	private final FxAction copy;
	private FxAction copyHtml;
	private FxAction copyPlainText;
	private FxAction copyRtf;
	private final FxAction delete;
	private final FxAction moveDocumentEnd;
	private MoveDocumentEndAtPos0 moveDocumentEndAtPos0;
	private final FxAction moveDocumentStart;
	private final FxAction moveDown;
	private final FxAction moveEnd;
	private final FxAction moveHome;
	private final FxAction moveLeft;
	private final FxAction moveRight;
	private final FxAction moveUp;
	private final FxAction pageDown;
	private final FxAction pageUp;
	private final FxAction selectAll;
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
		
		FX.onChange(this::handleSelectionChange, true, ed.selectionProperty());
	}
	
	
	protected void handleSelectionChange()
	{
		boolean on = (editor.getNonEmptySelection() != null);
		
		copy.setEnabled(on);
		
		if(copyHtml != null)
		{
			copyHtml.setEnabled(on);
		}
		
		if(copyPlainText != null)
		{
			copyPlainText.setEnabled(on);
		}
		
		if(copyRtf != null)
		{
			copyRtf.setEnabled(on);
		}
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
	
	
	public FxAction moveDocumentEnd()
	{
		return moveDocumentEnd;
	}
	
	
	public MoveDocumentEndAtPos0 moveDocumentEndAtPos0()
	{
		if(moveDocumentEndAtPos0 == null)
		{
			moveDocumentEndAtPos0 = new MoveDocumentEndAtPos0(editor);
		}
		return moveDocumentEndAtPos0;
	}
	
	
	public FxAction moveDocumentStart()
	{
		return moveDocumentStart;
	}
	
	
	public FxAction moveDown()
	{
		return moveDown;
	}
	
	
	public FxAction moveEnd()
	{
		return moveEnd;
	}
	
	
	public FxAction moveHome()
	{
		return moveHome;
	}
	
	
	public FxAction moveLeft()
	{
		return moveLeft;
	}
	
	
	public FxAction moveRight()
	{
		return moveRight;
	}
	
	
	public FxAction moveUp()
	{
		return moveUp;
	}
	
	
	public FxAction pageDown()
	{
		return pageDown;
	}
	
	
	public FxAction pageUp()
	{
		return pageUp;
	}
	
	
	public FxAction selectAll()
	{
		return selectAll;
	}
}
