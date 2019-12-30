// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor.res;
import goryachev.common.util.CKit;
import goryachev.fxtexteditor.FxTextEditorModel;
import goryachev.fxtexteditor.InMemoryPlainTextEditorModel;
import demo.fxtexteditor.AnItem;
import demo.fxtexteditor.DemoTextEditorModel;


/**
 * Demo Text Models.
 */
public class DemoModels
{
	public static final AnItem NO_TABS_NO_UNICODE = new AnItem("NO_TABS_NO_UNICODE", "No Tabs, No Unicode");
	public static final AnItem TABS_NO_UNICODE = new AnItem("TABS_NO_UNICODE", "Tabs, No Unicode");
	public static final AnItem JAVA = new AnItem("JAVA", "A large java file");
	public static final AnItem LONG_LINES = new AnItem("LONG_LINES", "Long lines (1M characters)");
	public static final AnItem BILLION_LINES = new AnItem("BILLION_LINES", "One billion lines");
	public static final AnItem NULL = new AnItem("null", "null");
	
	
	public static AnItem[] getAll()
	{
		return new AnItem[]
		{
			NO_TABS_NO_UNICODE,
			TABS_NO_UNICODE,
			JAVA,
			LONG_LINES,
			BILLION_LINES,
			NULL
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
			return new DemoTextEditorModel(loadResource("CKit.java.txt"));
		}
		else if(x == LONG_LINES)
		{
			String text = loadResource("million.txt");
			return toSimpleModel(text);
		}
		else if(x == BILLION_LINES)
		{
			String text = loadResource("demo.txt");
			return new DemoTextEditorModel(text, 1_000_000_000);
		}
		else if(x == BILLION_LINES)
		{
			return null;
		}
		return null;
	}
	

	protected static String loadResource(String name)
	{
		return CKit.readStringQuiet(DemoModels.class, name);
	}
	
	
	protected static InMemoryPlainTextEditorModel toSimpleModel(String text)
	{
		String[] lines = CKit.split(text, '\n');
		return new InMemoryPlainTextEditorModel(lines);
	}
}
