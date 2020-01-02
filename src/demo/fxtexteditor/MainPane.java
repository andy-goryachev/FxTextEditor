// Copyright © 2017-2020 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import goryachev.fx.CInsets;
import goryachev.fx.CPane;
import goryachev.fx.CssStyle;
import goryachev.fx.FX;
import goryachev.fxtexteditor.FxTextEditor;
import goryachev.fxtexteditor.FxTextEditorModel;
import goryachev.fxtexteditor.internal.TabPolicy;
import javafx.scene.text.Font;
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
		editor.setContentPadding(new CInsets(2, 4));
		editor.setBlinkRate(Duration.millis(600));
		editor.setMultipleSelectionEnabled(true);
		editor.setWrapLines(false);
		editor.setTabPolicy(TabPolicy.create(4));
		editor.setFont(Font.font("Monospace", 18));
		
		setCenter(editor);
		
		showFindPane();
	}
	
	
	public void setModel(FxTextEditorModel m)
	{
		editor.setModel(m);
	}
	
	
	public void showFindPane()
	{
//		FindPane p = new FindPane();
//		setBottom(p);
//		
//		FX.later(() -> p.focusSearch());
	}
}
