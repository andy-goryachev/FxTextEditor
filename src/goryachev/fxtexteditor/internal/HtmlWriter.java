// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.util.CKit;
import goryachev.fx.FX;
import goryachev.fxtexteditor.CellStyle;
import goryachev.fxtexteditor.FxTextEditorModel;
import goryachev.fxtexteditor.ITextLine;
import java.io.StringWriter;
import javafx.scene.paint.Color;


/**
 * HTML Writer.
 */
public class HtmlWriter
{
	private final FxTextEditorModel model;
	private final StringWriter out;
	private final int startLine;
	private final int startPos;
	private final int endLine;
	private final int endPos;
	private String fontName = "Courier New";
	
	
	public HtmlWriter(FxTextEditorModel m, StringWriter out, int startLine, int startPos, int endLine, int endPos)
	{
		this.model = m;
		this.out = out;
		this.startLine = startLine;
		this.startPos = startPos;
		this.endLine = endLine;
		this.endPos = endPos;
	}
	
	
	public void setFont(String fontName)
	{
		this.fontName = fontName;
	}
	
	
	public void write() throws Exception
	{
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
		out.write("<pre><font face='");
		out.write(fontName);
		out.write("'>");
	}

	
	protected void writeLine(ITextLine t, int startPos, int endPos) throws Exception
	{
		if(t == null)
		{
			return;
		}
		
		CellStyle prevStyle = null;
		Color color = null;
		Color background = null;
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
				Color bg;
				boolean bld;
				boolean ita;
				boolean und;
				boolean str;
				
				if(st == null)
				{
					col = null;
					bg = null;
					bld = false;
					ita = false;
					und = false;
					str = false;
				}
				else
				{
					col = st.getTextColor();
					bg = st.getBackgroundColor();
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
						out.write("</font>");
					}
					else
					{
						out.write("<font color='");
						out.write(FX.toFormattedColorRGB(col));
						out.write("'>");
					}
					
					color = col;
				}
				
				if(CKit.notEquals(bg, background))
				{
					if(bg == null)
					{
						out.write("</span>");
					}
					else
					{
						out.write("<span style='background-color:");
						out.write(FX.toFormattedColorRGB(bg));
						out.write(";'>");
					}
					
					background = bg;
				}
				
				if(bld != bold)
				{
					out.write(bld ? "<b>" : "</b>");
					bold = bld;
				}
				
				if(ita != italic)
				{
					out.write(ita ? "<i>" : "</i>");
					italic = ita;
				}
				
				if(und != under)
				{
					out.write(und ? "<u>" : "</u>");
					under = und;
				}
				
				if(str != strike)
				{
					out.write(str ? "<strike>" : "</strike>");
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
				default:
					break;
				}
			}
			else
			{
				switch(ch)
				{
				case '&':
					out.write("&amp;");
					break;
				case '<':
					out.write("&lt;");
					break;
				default:
					out.write(ch);
					break;
				}
			}
		}
		
		if(strike)
		{
			out.write("</strike>");
		}
		
		if(under)
		{
			out.write("</u>");
		}
		
		if(italic)
		{
			out.write("</i>");
		}
		
		if(bold)
		{
			out.write("</b>");
		}
		
		if(background != null)
		{
			out.write("</span>");
		}
		
		if(color != null)
		{
			out.write("</font>");
		}
	}
	
	
	protected void writeNL() throws Exception
	{
		out.write("\n");
	}
	

	protected void writeEnd() throws Exception
	{
		out.write("</font></pre>\n");
	}
}
