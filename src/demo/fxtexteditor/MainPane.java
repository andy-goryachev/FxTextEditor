// Copyright © 2017-2019 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import goryachev.common.util.CKit;
import goryachev.fx.CInsets;
import goryachev.fx.CPane;
import goryachev.fx.CssStyle;
import goryachev.fx.FX;
import goryachev.fx.FxTask;
import goryachev.fxtexteditor.FxTextEditor;
import goryachev.fxtexteditor.FxTextEditorModel;
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
		
		setCenter(editor);
		
		showFindPane();
		
		new FxTask<String>().
			producer(this::loadDemo).
			onSuccess(this::openText).
			submit();
	}
	
	
	protected String loadDemo()
	{
		return CKit.readStringQuiet(getClass(), "demo.txt");
	}
	
	
	protected void openText(String text)
	{
		setModel(new PlainTextEditorModel(text));
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
