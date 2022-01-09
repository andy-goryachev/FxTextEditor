// Copyright © 2020-2022 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal.html;
import goryachev.common.util.CKit;
import goryachev.fx.FX;
import goryachev.fxtexteditor.CellStyle;
import goryachev.fxtexteditor.ITextLine;
import goryachev.fxtexteditor.ITextSource;
import java.io.StringWriter;
import java.io.Writer;
import javafx.scene.paint.Color;


/**
 * HTML Writer.
 */
public class HtmlWriter
{
	private final ITextSource src;
	private final Writer out;
	private String fontName = "Courier New";
	
	
	public HtmlWriter(ITextSource src, Writer out)
	{
		this.src = src;
		this.out = out;
	}
	
	
	public static String writeString(ITextSource src) throws Exception
	{
		StringWriter out = new StringWriter();
		HtmlWriter wr = new HtmlWriter(src, out);
		wr.write();
		return out.toString();
	}
	
	
	public void setFont(String fontName)
	{
		this.fontName = fontName;
	}
	
	
	public void write() throws Exception
	{
		writeBeginning();
		
		ITextLine t;
		boolean nl = false;
		while((t = src.nextLine()) != null)
		{
			CKit.checkCancelled();
			
			if(nl)
			{
				writeNL();
			}
			else
			{
				nl = true;
			}
			
			int start = src.getStart();
			int end = src.getEnd();
			writeLine(t, start, end);
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
		CKit.checkCancelled();
		
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
						out.write("</span><span style='background-color:");
						// TODO alpha
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
