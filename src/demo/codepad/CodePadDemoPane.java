// Copyright © 2017-2024 Andy Goryachev <andy@goryachev.com>
package demo.codepad;
import goryachev.codepad.CodePad;
import goryachev.codepad.model.CodeModel;
import goryachev.fx.CPane;
import goryachev.fx.CssStyle;
import goryachev.fx.FX;
import goryachev.fx.FxPopupMenu;


/**
 * CodePad Demo Pane.
 */
public class CodePadDemoPane
	extends CPane
{
	public static final CssStyle PANE = new CssStyle("CodePadDemoPane_PANE");
	public final CodePad editor;

	
	public CodePadDemoPane()
	{
		FX.style(this, PANE);
		
		editor = new CodePad(null);
		editor.setContentPadding(FX.insets(2, 4));
//		editor.setBlinkRate(Duration.millis(600));
//		editor.setWrapLines(false);
//		editor.setTabPolicy(TabPolicy.create(4));
		
		setCenter(editor);
		
		showFindPane();
		
		FX.setPopupMenu(editor, this::createPopupMenu);
	}
	
	
	protected FxPopupMenu createPopupMenu()
	{
		FxPopupMenu p = new FxPopupMenu();
//		FxMenu m = p.menu("Copy", editor.actions.copy());
//		{
//			m.item("Copy Plain Text", editor.actions.copyPlainText());
//			m.item("RTF", editor.actions.copyRtf());
//			m.item("HTML", editor.actions.copyHtml());
//		}
//		m = p.menu("Smart Copy", editor.actions.smartCopy());
//		{
//			m.item("Plain Text", editor.actions.smartCopyPlainText());
//			m.item("RTF", editor.actions.smartCopyRtf());
//			m.item("HTML", editor.actions.smartCopyHtml());
//		}
//		p.separator();
//		p.item("Select All", editor.actions.selectAll());
		p.item("Select All");
		return p;
	}
	
	
	public void setModel(CodeModel m)
	{
		editor.setModel(m);
	}
	
	
	public CodeModel getModel()
	{
		return editor.getModel();
	}
	

	public void showFindPane()
	{
//		FindPane p = new FindPane();
//		setBottom(p);
//		
//		FX.later(() -> p.focusSearch());
	}
}
