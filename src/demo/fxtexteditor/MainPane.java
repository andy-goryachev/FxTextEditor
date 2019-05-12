// Copyright © 2017-2019 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import goryachev.fx.CInsets;
import goryachev.fx.CPane;
import goryachev.fx.CssStyle;
import goryachev.fx.FX;
import goryachev.fxtexteditor.FxTextEditor;
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
		
		setCenter(editor);
		
		showFindPane();
	}
	
	
//	public void setModel(FxEditorModel m)
//	{
//		editor.setModel(m);
//	}
	
	
	public void showFindPane()
	{
//		FindPane p = new FindPane();
//		setBottom(p);
//		
//		FX.later(() -> p.focusSearch());
	}
}
