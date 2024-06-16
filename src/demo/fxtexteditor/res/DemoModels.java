// Copyright © 2019-2023 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor.res;
import goryachev.common.util.CKit;
import goryachev.common.util.CList;
import goryachev.common.util.SB;
import goryachev.common.util.text.IBreakIterator;
import goryachev.fx.Formatters;
import goryachev.fxtexteditor.Edit;
import goryachev.fxtexteditor.FxTextEditorModel;
import goryachev.fxtexteditor.ITextLine;
import goryachev.fxtexteditor.InMemoryPlainTextEditorModel;
import goryachev.fxtexteditor.LoadStatus;
import goryachev.fxtexteditor.PlainTextLine;
import goryachev.fxtexteditor.SimpleStyledTextEditorModel;
import java.text.DecimalFormat;
import demo.fxtexteditor.AnItem;
import demo.fxtexteditor.DemoTextEditorModel;
import javafx.scene.paint.Color;


/**
 * Demo Text Models.
 */
public class DemoModels
{
	public static final AnItem NO_TABS_NO_UNICODE = new AnItem("NO_TABS_NO_UNICODE", "No Tabs, No Unicode");
	public static final AnItem TABS_NO_UNICODE = new AnItem("TABS_NO_UNICODE", "Tabs, No Unicode");
	public static final AnItem EDITABLE = new AnItem("EDITABLE", "Editable Model");
	public static final AnItem JAVA_LARGE = new AnItem("JAVA", "CKit.java");
	public static final AnItem JAVA_SMALL = new AnItem("JAVA_SMALL", "A small java file");
	public static final AnItem LONG_LINES = new AnItem("LONG_LINES", "Long lines (1M characters)");
	public static final AnItem BILLION_LINES = new AnItem("BILLION_LINES", "One billion lines");
	public static final AnItem SIMPLE_STYLED = new AnItem("SimpleStyledTextEditorModel", "SimpleStyledTextEditorModel");
	public static final AnItem LOADING = new AnItem("LOADING", "Loading...");
	public static final AnItem NULL = new AnItem("null", "null");
	
	
	public static AnItem[] getAll()
	{
		return new AnItem[]
		{
			JAVA_SMALL,
			NO_TABS_NO_UNICODE,
			EDITABLE,
			TABS_NO_UNICODE,
			JAVA_LARGE,
			LOADING,
			LONG_LINES,
			BILLION_LINES,
			SIMPLE_STYLED,
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
				"\t\t\n" +
				" h 0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
				" //end"
			);
		}
		else if(x == JAVA_LARGE)
		{
			return new DemoTextEditorModel(loadResource("CKit.java.txt"));
		}
		else if(x == JAVA_SMALL)
		{
			return new DemoTextEditorModel(loadResource("Edit.java.txt"));
		}
		else if(x == LONG_LINES)
		{
			return makeLongLinesModel();
		}
		else if(x == BILLION_LINES)
		{
			String text = loadResource("demo.txt");
			return new DemoTextEditorModel(text, 1_000_000_000);
		}
		else if(x == SIMPLE_STYLED)
		{
			return makeSimpleStyled();
		}
		else if(x == NULL)
		{
			return null;
		}
		else if(x == LOADING)
		{
			return makeLoadingModel();
		}
		else if(x == EDITABLE)
		{
			return new EditableModel();
		}
		return null;
	}
	

	protected static String loadResource(String name)
	{
		return CKit.readStringQuiet(DemoModels.class, name);
	}
	
	
	protected static FxTextEditorModel toSimpleModel(String text)
	{
		String[] lines = CKit.split(text, '\n');
		return new InMemoryPlainTextEditorModel(lines);
	}
	
	
	protected static FxTextEditorModel makeLongLinesModel()
	{
		DecimalFormat f = new DecimalFormat("#,##0");
		
		int len = 100;
		int sz = 1_000_000 / len;
		SB sb = new SB(1_000_010);
		
		for(int i=0; i<sz; i++)
		{
			String s = f.format(i * len);
			sb.a(s);
			sb.repeat('.', len - s.length());
		}
		String longLines = sb.toString();
		
		CList<String> a = new CList();
		for(int i=0; i<1; i++)
		{
			a.add(f.format(i));
			a.add(longLines);
		}
		return new InMemoryPlainTextEditorModel(CKit.toArray(a));
	}
	
	
	protected static FxTextEditorModel makeLoadingModel()
	{
		return new FxTextEditorModel()
		{
			{
				setLoadStatus(new LoadStatus(0.5, true, true));
			}
			
			
			@Override
			public IBreakIterator getBreakIterator()
			{
				return null;
			}


			@Override
			public int getLineCount()
			{
				return 5_000;
			}


			@Override
			public ITextLine getTextLine(int line)
			{
				String text = Formatters.integerFormatter().format(line + 1);
				return new PlainTextLine(line, text);
			}


			@Override
			public Edit edit(Edit ed) throws Exception
			{
				throw new Error();
			}
		};
	}
	
	
	protected static FxTextEditorModel makeSimpleStyled()
	{
		SimpleStyledTextEditorModel m = new SimpleStyledTextEditorModel();
		m.setBold(true);
		m.append("SimpleStyledTextEditorModel");
		m.setBold(false);
		m.nl();
		m.nl();
		m.append("This is a ");
		m.setItalic(true);
		m.append("simple");
		m.setItalic(false);
		m.append(", ");
		m.setUnderscore(true);
		m.append("styled");
		m.setUnderscore(false);
		m.append(", ");
		m.setBold(true);
		m.setItalic(true);
		m.append("read-only");
		m.setItalic(false);
		m.setBold(false);
		m.append(" model.");
		m.nl();
		m.setTextColor(Color.GRAY);
		m.append("This model is designed mostly for presenting an ");
		m.setBackgroundColor(Color.YELLOW);
		m.append("immutable");
		m.setBackgroundColor(null);
		m.append(" styled text ");
		m.setStrikeThrough(true);
		m.append("and no images");
		m.setStrikeThrough(false);
		m.append(".");
		m.setTextColor(null);
		m.nl();
		return m;
	}
}
