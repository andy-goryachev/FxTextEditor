// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor.res;
import goryachev.common.util.CKit;
import goryachev.fx.FxTask;
import goryachev.fxtexteditor.FxTextEditorModel;
import goryachev.fxtexteditor.SimplePlainTextEditorModel;
import demo.fxtexteditor.AnItem;
import demo.fxtexteditor.DemoTextEditorModel;
import demo.fxtexteditor.MainPane;


/**
 * Demo Text.
 */
public class DemoText
{
	public static final AnItem NO_TABS_NO_UNICODE = new AnItem("NO_TABS_NO_UNICODE", "No Tabs, No Unicode");
	public static final AnItem TABS_NO_UNICODE = new AnItem("TABS_NO_UNICODE", "Tabs, No Unicode");
	public static final AnItem JAVA = new AnItem("Java", "A large java file");
	
	
	public static AnItem[] getAll()
	{
		return new AnItem[]
		{
			NO_TABS_NO_UNICODE,
			TABS_NO_UNICODE,
			JAVA
//			full
//			also large
//			also bidirectional
		};
	}
	
	
	public static FxTextEditorModel getModel(Object x)
	{
		if(x == NO_TABS_NO_UNICODE)
		{
			return toSimpleModel
			(
				" h 0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
				" //end"
			);
		}
		else if(x == TABS_NO_UNICODE)
		{
			return toSimpleModel
			(
				"\t1\t2\t3\t4\t5\t6\t7\n" +
				"\t\t\t3\t\t\t6\n" +
				" h 0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
				" //end"
			);
		}
		else if(x == JAVA)
		{
			return toSimpleModel(loadResource("CKit.java.txt"));
		}
		return null;
	}
	
	
	public static void load(MainPane p)
	{
		switch(5)
		{
		case 1:
			// hsort text
			p.setModel(toSimpleModel("333\n22\n1"));
			break;
		case 2:
			// very long lines
			new FxTask<String>().
				producer(() -> loadResource("million.txt")).
				onSuccess((text) -> p.setModel(toSimpleModel(text))).
				submit();
			break;
		case 3:
			new FxTask<String>().
				producer(() -> loadResource("demo.txt")).
				onSuccess((text) -> p.setModel(new DemoTextEditorModel(text, 1))).
				submit();
			break;
		case 4:
			// large model
			new FxTask<String>().
				producer(() -> loadResource("demo.txt")).
				onSuccess((text) -> p.setModel(new DemoTextEditorModel(text, 100))).
				submit();
			break;
		case 5:
			p.setModel(toSimpleModel
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
	

	protected static String loadResource(String name)
	{
		return CKit.readStringQuiet(DemoText.class, name);
	}
	
	
	protected static SimplePlainTextEditorModel toSimpleModel(String text)
	{
		String[] lines = CKit.split(text, '\n');
		return new SimplePlainTextEditorModel(lines);
	}
}
