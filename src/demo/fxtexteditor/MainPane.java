// Copyright © 2017-2021 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import goryachev.fx.CPane;
import goryachev.fx.CssStyle;
import goryachev.fx.FX;
import goryachev.fx.FxMenu;
import goryachev.fx.FxPopupMenu;
import goryachev.fxtexteditor.FxTextEditor;
import goryachev.fxtexteditor.FxTextEditorModel;
import goryachev.fxtexteditor.internal.TabPolicy;
import javafx.util.Duration;


/**
 * Main Pane.
 */
public class MainPane
	extends CPane
{
	public static final CssStyle PANE = new CssStyle("MainPane_PANE");
	public final FxTextEditor editor;

	
	public MainPane()
	{
		FX.style(this, PANE);
		
		editor = new FxTextEditor();
		editor.setContentPadding(FX.insets(2, 4));
		editor.setBlinkRate(Duration.millis(600));
		editor.setWrapLines(false);
		editor.setTabPolicy(TabPolicy.create(4));
		
		setCenter(editor);
		
		showFindPane();
		
		FX.setPopupMenu(editor, this::createPopupMenu);
	}
	
	
	protected FxPopupMenu createPopupMenu()
	{
		FxPopupMenu p = new FxPopupMenu();
		FxMenu m = p.menu("Copy", editor.actions.copy());
		{
			m.item("Copy Plain Text", editor.actions.copyPlainText());
			m.item("RTF", editor.actions.copyRtf());
			m.item("HTML", editor.actions.copyHtml());
		}
		m = p.menu("Smart Copy", editor.actions.smartCopy());
		{
			m.item("Plain Text", editor.actions.smartCopyPlainText());
			m.item("RTF", editor.actions.smartCopyRtf());
			m.item("HTML", editor.actions.smartCopyHtml());
		}
		p.separator();
		p.item("Select All", editor.actions.selectAll);
		return p;
	}
	
	
	public void setModel(FxTextEditorModel m)
	{
		editor.setModel(m);
	}
	
	
	public FxTextEditorModel getModel()
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
