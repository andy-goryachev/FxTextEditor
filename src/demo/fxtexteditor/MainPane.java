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
import goryachev.fxtexteditor.SimplePlainTextEditorModel;
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
//		editor.setFont(Font.font("Monospace", 18));
		
		setCenter(editor);
		
		showFindPane();
		
		boolean plain = false;
		if(plain)
		{
			new FxTask<String>().
				producer(() -> loadFile("million.txt")).
				onSuccess((text) -> setModel(new SimplePlainTextEditorModel(text))).
				submit();
		}
		else
		{
			new FxTask<String>().
				producer(() -> loadFile("demo.txt")).
				onSuccess((text) -> setModel(new DemoTextEditorModel(text))).
				submit();
		}
	}
	
	
	protected String loadFile(String name)
	{
		return CKit.readStringQuiet(getClass(), name);
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
