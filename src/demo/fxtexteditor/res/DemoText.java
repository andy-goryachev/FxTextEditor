// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor.res;
import goryachev.common.util.CKit;
import goryachev.fx.FxTask;
import goryachev.fxtexteditor.FxTextEditorModel;
import goryachev.fxtexteditor.InMemoryPlainTextEditorModel;
import demo.fxtexteditor.AnItem;
import demo.fxtexteditor.DemoTextEditorModel;
import demo.fxtexteditor.MainPane;


/**
 * Demo Text Models.
 */
public class DemoText
{
	public static final AnItem NO_TABS_NO_UNICODE = new AnItem("NO_TABS_NO_UNICODE", "No Tabs, No Unicode");
	public static final AnItem TABS_NO_UNICODE = new AnItem("TABS_NO_UNICODE", "Tabs, No Unicode");
	public static final AnItem JAVA = new AnItem("JAVA", "A large java file");
	public static final AnItem LONG_LINES = new AnItem("LONG_LINES", "Long lines (1M characters)");
	
	
	public static AnItem[] getAll()
	{
		return new AnItem[]
		{
			NO_TABS_NO_UNICODE,
			TABS_NO_UNICODE,
			JAVA,
			LONG_LINES
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
		else if(x == LONG_LINES)
		{
			String text = loadResource("million.txt");
			return toSimpleModel(text);
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
		}
	}
	

	protected static String loadResource(String name)
	{
		return CKit.readStringQuiet(DemoText.class, name);
	}
	
	
	protected static InMemoryPlainTextEditorModel toSimpleModel(String text)
	{
		String[] lines = CKit.split(text, '\n');
		return new InMemoryPlainTextEditorModel(lines);
	}
}
