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
		switch(5)
		{
		case 1:
			// hsort text
			p.setModel(new SimplePlainTextEditorModel("333\n22\n1"));
			break;
		case 2:
			// very long lines
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
			// large model
			new FxTask<String>().
				producer(() -> loadFile("demo.txt")).
				onSuccess((text) -> p.setModel(new DemoTextEditorModel(text, 100))).
				submit();
			break;
		case 5:
			p.setModel(new SimplePlainTextEditorModel
			(
				"\t1\t2\t3\t4\t5\t6\t7\n" +
				"\t\t\t\t\t5\t\t\t\t\t5\t\t\t\t\t5\n" +
				"\t1\n" +
				"\t\t2\n" +
				"\t\t\t3\n" +
				"\t\t\t\t4\n" +
				"012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789\n" +
				""
			));
			break;
		}
	}
	

	protected static String loadFile(String name)
	{
		return CKit.readStringQuiet(DemoText.class, name);
	}
}
