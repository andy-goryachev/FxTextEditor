// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.util.CKit;
import goryachev.common.util.CList;
import goryachev.common.util.CMap;
import goryachev.fxtexteditor.CellStyle;
import goryachev.fxtexteditor.FxTextEditorModel;
import goryachev.fxtexteditor.ITextLine;
import java.io.OutputStream;
import javafx.scene.paint.Color;


/**
 * RTF Writer.
 * 
 * RTF 1.5 Spec:
 * www.biblioscape.com/rtf15_spec.htm
 */
public class RtfWriter
{
	private final FxTextEditorModel model;
	private final OutputStream out;
	private final int startLine;
	private final int startPos;
	private final int endLine;
	private final int endPos;
	private String fontName = "Courier New";
	private String fontSize = "18"; // double the actual size
	private CList<Color> colors;
	private CMap<Color,Integer> colorTable; 
	
	
	public RtfWriter(FxTextEditorModel m, OutputStream out, int startLine, int startPos, int endLine, int endPos)
	{
		this.model = m;
		this.out = out;
		this.startLine = startLine;
		this.startPos = startPos;
		this.endLine = endLine;
		this.endPos = endPos;
	}
	
	
	public void setFont(String fontName, int fontSize)
	{
		this.fontName = fontName;
		this.fontSize = String.valueOf(2 * fontSize);
	}
	
	
	public void write() throws Exception
	{
		prepareColorTable();
		
		writeBeginning();
		
		if(startLine == endLine)
		{
			ITextLine t = model.getTextLine(startLine);
			writeLine(t, startPos, endPos);
		}
		else
		{
			ITextLine t = model.getTextLine(startLine);
			writeLine(t, startPos, t.getTextLength());
			writeNL();
			
			for(int i=startLine+1; i<endLine; i++)
			{
				CKit.checkCancelled();
				
				t = model.getTextLine(i);
				writeLine(t, 0, t.getTextLength());
				writeNL();
			}
			
			t = model.getTextLine(endLine);
			writeLine(t, 0, endPos);
		}
		
		writeEnd();
		
		out.flush();
	}
	

	protected void writeBeginning() throws Exception
	{
		// preamble
		write("{\\rtf1\\ansi\\ansicpg1252\\uc1\\sl0\\sb0\\sa0\\deff0{\\fonttbl{\\f0\\fnil ");
		write(fontName);
		write(";}}\r\n");
		
		// color table from the model style sheet
		if(colors != null)
		{
			write("{\\colortbl");
			
			for(Color c: colors)
			{
				write("\\red");
				write(toInt255(c.getRed()));
				write("\\green");
				write(toInt255(c.getGreen()));
				write("\\blue");
				write(toInt255(c.getBlue()));
				write(";");
			}
			
			write("}\r\n");
		}
		
		write("{\\f0\\fs");
		write(fontSize);
		write(" \\fi0\\ql ");
	}

	
	protected void writeLine(ITextLine t, int startPos, int endPos) throws Exception
	{
		write("\\fi0\\ql ");
		
		CellStyle prevStyle = null;
		Color color = null;
		boolean bold = false;
		boolean italic = false;
		boolean under = false;
		boolean strike = false;
		
		String text = t.getPlainText();
		for(int i=startPos; i<endPos; i++)
		{
			CellStyle st = t.getCellStyle(i);
			if(prevStyle != st)
			{
				Color col;
				boolean bld;
				boolean ita;
				boolean und;
				boolean str;
				
				if(st == null)
				{
					col = null;
					bld = false;
					ita = false;
					und = false;
					str = false;
				}
				else
				{
					col = st.getTextColor();
					bld = st.isBold();
					ita = st.isItalic();
					und = st.isUnderscore();
					str = st.isStrikeThrough();
				}
				
				prevStyle = st;
				
				// emit changes
				if(CKit.notEquals(col, color))
				{
					if(col == null)
					{
						write("\\cf0 ");
					}
					else
					{
						Integer ix = colorTable.get(col);
						if(ix == null)
						{
							ix = 0; // should not happen
						}
						write("\\cf");
						write(String.valueOf(ix));
						write(" ");
					}
					
					color = col;
				}
				
				if(bld != bold)
				{
					write(bld ? "\\b " : "\\b0 ");
					bold = bld;
				}
				
				if(ita != italic)
				{
					write(ita ? "\\i " : "\\i0 ");
					italic = ita;
				}
				
				if(und != under)
				{
					write(und ? "\\ul " : "\\ul0 ");
					under = und;
				}
				
				if(str != strike)
				{
					write(str ? "\\strike " : "\\strike0 ");
					strike = str;
				}
			}
			
			char ch = text.charAt(i);
			if(ch < 0x20)
			{
				switch(ch)
				{
				case '\n':
				case '\r':
					break;
				case '\t':
					out.write(ch);
					break;
				}
			}
			else if(ch < 0x80)
			{
				out.write(ch);
			}
			else
			{
				write("\\u");
				write(String.valueOf((short)ch));
				write("?");
			}
		}
		
		if(color != null)
		{
			write("\\cf0 ");
		}
		
		if(bold)
		{
			write("\\b0 ");
		}
		
		if(italic)
		{
			write("\\i0 ");
		}
		
		if(under)
		{
			write("\\ul0 ");
		}
		
		if(strike)
		{
			write("\\strike0 ");
		}
	}
	
	
	protected void writeNL() throws Exception
	{
		write("\\par\r\n");
	}
	

	protected void writeEnd() throws Exception
	{
		write("\r\n}}\r\n");
	}
	
	
	protected void write(String rtf) throws Exception
	{
		byte[] b = rtf.getBytes(CKit.CHARSET_ASCII);
		out.write(b);
	}
	
	
	protected static String toInt255(double x)
	{
		int v = CKit.round(255 * x);
		if(v < 0)
		{
			v = 0;
		}
		else if(v > 255)
		{
			v = 255;
		}
		return String.valueOf(v);
	}
	
	
	private void prepareColorTable()
	{
		CellStyle[] styles = model.getStyles();
		if(styles != null)
		{
			CList<Color> cs = new CList();
			CMap<Color,Integer> m = new  CMap(styles.length);
			
			// TODO entry at 0 index is background color
			Color c = Color.BLACK;
			cs.add(c);
			m.put(c, 0);

			for(int i=0; i<styles.length; i++)
			{
				c = styles[i].getTextColor();
				if(c != null)
				{
					if(!m.containsKey(c))
					{
						cs.add(c);
						m.put(c, Integer.valueOf(m.size()));
					}
				}
			}
			
			colors = cs;
			colorTable = m;
		}
	}
}
