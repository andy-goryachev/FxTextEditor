// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import goryachev.common.util.CKit;
import goryachev.fx.FxTask;
import goryachev.fxtexteditor.FxTextEditorModel;
import goryachev.fxtexteditor.SimplePlainTextEditorModel;


/**
 * Demo Text.
 */
public class DemoText
{
	public static final AnItem NO_TABS_NO_UNICODE = new AnItem("NO_TABS_NO_UNICODE", "No Tabs, No Unicode");
	public static final AnItem TABS_NO_UNICODE = new AnItem("TABS_NO_UNICODE", "Tabs, No Unicode");
	
	
	public static FxTextEditorModel getModel(Object x)
	{
		if(x == NO_TABS_NO_UNICODE)
		{
			return new SimplePlainTextEditorModel
			(
				" h 0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
				" //end"
			);
		}
		else if(x == TABS_NO_UNICODE)
		{
			return new SimplePlainTextEditorModel
			(
				"\t1\t2\t3\t4\t5\t6\t7\n" +
				" h 0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
				" //end"
			);
		}
		return null;
	}
	
	
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
//				"\t\t\t\t\t5\t\t\t\t\t5\t\t\t\t\t5\n" +
//				"\t1\n" +
//				"\t\t2\n" +
//				"\t\t\t3\n" +
//				"\t\t\t\t4\n" +
//				" a 0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
//				" b 0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
//				" c 0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
//				" d 0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
//				" e 0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
//				" f 0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
//				" g 0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
				" h 0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
				" //end"
			));
			break;
		}
	}
	

	protected static String loadFile(String name)
	{
		return CKit.readStringQuiet(DemoText.class, name);
	}
}
