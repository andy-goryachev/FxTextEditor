// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.util.CKit;
import goryachev.common.util.CList;
import goryachev.common.util.CMap;
import goryachev.common.util.ElasticIntArray;
import goryachev.common.util.SB;
import goryachev.common.util.text.IBreakIterator;
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
	private final CMap<CellStyle,CellStyle> styles = new CMap();
	private final CellStyle style = new CellStyle();
	private final IBreakIterator breakIterator; 
	private CellStyle prevStyle;
	
	
	public SimpleStyledTextEditorModel(IBreakIterator bi)
	{
		this.breakIterator = bi;
	}
	
	
	public SimpleStyledTextEditorModel()
	{
		// WARNING: this break iterator fails with emoji and flags
		this.breakIterator = IBreakIterator.wrap(BreakIterator.getCharacterInstance());
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
		CList<CellStyle> ss = new  CList();
		CellStyle st = new CellStyle();
		
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
			else if(x instanceof CellStyle)
			{
				st = (CellStyle)x;
			}
		}
		
		String text = sb.toString();
		CellStyle[] cs = ss.toArray(new CellStyle[ss.size()]);
		return new TextLine(line, text, null, cs);
	}


	public Edit edit(Edit ed) throws Exception
	{
		throw new Error("this model is read only");
	}
	
	
	protected CellStyle style()
	{
		CellStyle s = styles.get(style);
		if(s == null)
		{
			s = style.copy();
			styles.put(s, s);
		}
		return s;
	}
	
	
	public SimpleStyledTextEditorModel append(String text)
	{
		CellStyle s = style();
		
		if(!CKit.equals(prevStyle, s))
		{
			data.add(s);
			prevStyle = s;
		}
		data.add(text);
		return this;
	}
	
	
	public SimpleStyledTextEditorModel append(CellStyle st, String text)
	{
		style.init(st);
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
	
	
	//
	
	
	protected static class TextLine implements ITextLine
	{
		private final int lineNumber;
		private final String text;
		private final Color lineColor;
		private final CellStyle[] styles;
		
		
		public TextLine(int lineNumber, String text, Color lineColor, CellStyle[] styles)
		{
			this.lineNumber = lineNumber;
			this.text = text;
			this.lineColor = lineColor;
			this.styles = styles;
		}


		public int getTextLength()
		{
			return text.length();
		}
		
		
		public String getPlainText()
		{
			return text;
		}
		
		
		public int getModelIndex()
		{
			return lineNumber;
		}
		
		
		public int getLineNumber()
		{
			return lineNumber;
		}
		
		
		public CellStyle getCellStyle(int charOffset)
		{
			return styles[charOffset];
		}


		public Color getLineColor()
		{
			return lineColor;
		}
	}
}
