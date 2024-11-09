// Copyright © 2019-2024 Andy Goryachev <andy@goryachev.com>
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
import demo.fxtexteditor.DemoTextEditorModel;
import javafx.scene.paint.Color;


/**
 * Demo Text Models.
 */
public enum DemoModels
{
	NO_TABS_NO_UNICODE,
	TABS_NO_UNICODE,
	EDITABLE,
	JAVA_LARGE,
	JAVA_SMALL,
	LONG_LINES,
	BILLION_LINES,
	SIMPLE_STYLED,
	LOADING,
	NULL;
	
	
	@Override
	public String toString()
	{
		switch(this)
		{
		case BILLION_LINES:
			return "2 billion lines";
		case EDITABLE:
			return "Editable Model";
		case JAVA_LARGE:
			return "CKit.java";
		case JAVA_SMALL:
			return "Small java file";
		case LOADING:
			return "Loading...";
		case LONG_LINES:
			return "Long lines (1M characters)";
		case NO_TABS_NO_UNICODE:
			return "No Tabs, No Unicode";
		case SIMPLE_STYLED:
			return "SimpleStyledTextEditorModel";
		case TABS_NO_UNICODE:
			return "Tabs, No Unicode";
		case NULL:
		default:
			return "<null>";
		}
	}
	
	
	public static FxTextEditorModel getModel(Object x)
	{
		DemoModels choice;
		if(x instanceof DemoModels ch)
		{
			choice = ch;
		}
		else
		{
			choice = DemoModels.NULL;
		}
		
		switch(choice)
		{
		case NO_TABS_NO_UNICODE:
			return toSimpleModel
			(
				" h 0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
				" //end"
			);
		case TABS_NO_UNICODE:
			return toSimpleModel
			(
				"\t1\t2\t3\t4\t5\t6\t7\n" +
				"\t\t\t3\t\t\t6\n" +
				"\t\t\n" +
				" h 0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
				" //end"
			);
		case JAVA_LARGE:
			return new DemoTextEditorModel(loadResource("CKit.java.txt"));
		case JAVA_SMALL:
			return new DemoTextEditorModel(loadResource("Edit.java.txt"));
		case LONG_LINES:
			return makeLongLinesModel();
		case BILLION_LINES:
			String text = loadResource("demo.txt");
			return new DemoTextEditorModel(text, 2_000_000_000);
		case SIMPLE_STYLED:
			return makeSimpleStyled();
		case LOADING:
			return makeLoadingModel();
		case EDITABLE:
			return new EditableModel();
		case NULL:
		default:
			return null;
		}
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
