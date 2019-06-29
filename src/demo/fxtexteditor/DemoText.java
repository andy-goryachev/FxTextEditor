// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import goryachev.common.util.CKit;
import goryachev.fx.FxTask;
import goryachev.fxtexteditor.SimplePlainTextEditorModel;


/**
 * Demo Text.
 */
public class DemoText
{
	public static void load(MainPane p)
	{
		switch(2)
		{
		case 1:
			p.setModel(new SimplePlainTextEditorModel("333\n22\n1"));
			break;
		case 2:
			new FxTask<String>().
				producer(() -> loadFile("million.txt")).
				onSuccess((text) -> p.setModel(new SimplePlainTextEditorModel(text))).
				submit();
			break;
		case 3:
			new FxTask<String>().
				producer(() -> loadFile("demo.txt")).
				onSuccess((text) -> p.setModel(new DemoTextEditorModel(text, 1))).
				submit();
			break;
		case 4:
			new FxTask<String>().
				producer(() -> loadFile("demo.txt")).
				onSuccess((text) -> p.setModel(new DemoTextEditorModel(text, 100))).
				submit();
			break;
		}
	}
	

	protected static String loadFile(String name)
	{
		return CKit.readStringQuiet(DemoText.class, name);
	}
}
