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
import goryachev.ibm.icu.IcuBreakIterator;
import java.util.Locale;
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
		editor.setBreakIterator(new IcuBreakIterator(Locale.US));
		editor.setContentPadding(new CInsets(2, 4));
		editor.setBlinkRate(Duration.millis(600));
		editor.setMultipleSelectionEnabled(true);
		editor.setWrapLines(false);
//		editor.setFont(Font.font("Monospace", 18));
		
		setCenter(editor);
		
		showFindPane();
		
		switch(4)
		{
		case 1:
			setModel(new SimplePlainTextEditorModel("333\n22\n1"));
			break;
		case 2:
			new FxTask<String>().
				producer(() -> loadFile("million.txt")).
				onSuccess((text) -> setModel(new SimplePlainTextEditorModel(text))).
				submit();
			break;
		case 3:
			new FxTask<String>().
				producer(() -> loadFile("demo.txt")).
				onSuccess((text) -> setModel(new DemoTextEditorModel(text, 1))).
				submit();
			break;
		case 4:
			new FxTask<String>().
				producer(() -> loadFile("demo.txt")).
				onSuccess((text) -> setModel(new DemoTextEditorModel(text, 10000))).
				submit();
			break;
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
