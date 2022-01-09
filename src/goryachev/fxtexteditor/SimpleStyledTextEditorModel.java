// Copyright © 2020-2022 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.util.CKit;
import goryachev.common.util.CList;
import goryachev.common.util.CMap;
import goryachev.common.util.ElasticIntArray;
import goryachev.common.util.SB;
import goryachev.common.util.text.IBreakIterator;
import goryachev.fx.TextCellStyle;
import java.text.BreakIterator;
import javafx.scene.paint.Color;


/**
 * Simple Styled FxTextEditorModel.
 * 
 * This model does not support and does not track mutations.
 */
public class SimpleStyledTextEditorModel
	extends FxTextEditorModel
{
	private final ElasticIntArray lines = new ElasticIntArray();
	private final CList<Object> data = new CList();
	private final CMap<TextCellStyle,TextCellStyle> styles = new CMap();
	private final TextCellStyle style = new TextCellStyle();
	private final IBreakIterator breakIterator; 
	private TextCellStyle prevStyle;
	
	
	public SimpleStyledTextEditorModel(IBreakIterator bi)
	{
		this.breakIterator = bi;
		
		setDefaultRtfCopyHandler();
		setDefaultHtmlCopyHandler();
	}
	
	
	public SimpleStyledTextEditorModel()
	{
		// WARNING: this break iterator fails with emoji and flags
		this(IBreakIterator.wrap(BreakIterator.getCharacterInstance()));
	}
	
	
	public IBreakIterator getBreakIterator()
	{
		return breakIterator;
	}


	public int getLineCount()
	{
		return lines.size();
	}


	public ITextLine getTextLine(int line)
	{
		if(line < 0)
		{
			return null;
		}
		else if(line >= getLineCount())
		{
			return null;
		}
		
		int start = (line == 0) ? 0 : lines.get(line - 1);
		int end = (line < getLineCount()) ? lines.get(line) : data.size();
		
		SB sb = new SB(128);
		CList<TextCellStyle> ss = new  CList();
		TextCellStyle st = new TextCellStyle();
		
		for(int i=start; i<end; i++)
		{
			Object x = data.get(i);
			if(x instanceof String)
			{
				String s = x.toString();
				sb.append(s);
				
				for(int j=0; j<s.length(); j++)
				{
					ss.add(st);
				}
			}
			else if(x instanceof TextCellStyle)
			{
				st = (TextCellStyle)x;
			}
		}
		
		String text = sb.toString();
		TextCellStyle[] cs = ss.toArray(new TextCellStyle[ss.size()]);
		return new SimpleStyledTextLine(line, text, null, cs);
	}


	public Edit edit(Edit ed) throws Exception
	{
		throw new Error("this model is read only");
	}
	
	
	protected TextCellStyle style()
	{
		TextCellStyle s = styles.get(style);
		if(s == null)
		{
			s = style.copy();
			styles.put(s, s);
		}
		return s;
	}
	
	
	public SimpleStyledTextEditorModel setStyle(TextCellStyle st)
	{
		style.init(st);
		return this;
	}
	
	
	public SimpleStyledTextEditorModel append(String text)
	{
		TextCellStyle s = style();
		
		if(!CKit.equals(prevStyle, s))
		{
			data.add(s);
			prevStyle = s;
		}
		data.add(text);
		return this;
	}
	
	
	public SimpleStyledTextEditorModel append(TextCellStyle st, String text)
	{
		setStyle(st);
		return append(text);
	}
	
	
	public SimpleStyledTextEditorModel nl()
	{
		lines.add(data.size());
		return this;
	}
	
	
	public SimpleStyledTextEditorModel setBold(boolean on)
	{
		style.setBold(on);
		return this;
	}
	
	
	public SimpleStyledTextEditorModel setItalic(boolean on)
	{
		style.setItalic(on);
		return this;
	}
	
	
	public SimpleStyledTextEditorModel setStrikeThrough(boolean on)
	{
		style.setStrikeThrough(on);
		return this;
	}
	
	
	public SimpleStyledTextEditorModel setUnderscore(boolean on)
	{
		style.setUnderscore(on);
		return this;
	}
	
	
	public SimpleStyledTextEditorModel setBackgroundColor(Color c)
	{
		style.setBackgroundColor(c);
		return this;
	}
	
	
	public SimpleStyledTextEditorModel setTextColor(Color c)
	{
		style.setTextColor(c);
		return this;
	}
}
