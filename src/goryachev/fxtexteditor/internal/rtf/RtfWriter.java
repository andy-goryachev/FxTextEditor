// Copyright © 2020-2022 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal.rtf;
import goryachev.common.log.Log;
import goryachev.common.util.CKit;
import goryachev.fx.FX;
import goryachev.fx.TextCellStyle;
import goryachev.fxtexteditor.ITextLine;
import goryachev.fxtexteditor.ITextSource;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.function.Supplier;
import javafx.scene.paint.Color;


/**
 * RTF Writer.
 * 
 * RTF 1.5 Spec:
 * www.biblioscape.com/rtf15_spec.htm
 */
public class RtfWriter
{
	protected static final Log log = Log.get("RtfWriter");
	private final Supplier<ITextSource> source;
	private final OutputStream out;
	private String fontName = "Courier New";
	private String fontSize = "18"; // double the actual size
	private ColorTable colorTable; 
	
	
	public RtfWriter(Supplier<ITextSource> source, OutputStream out)
	{
		this.source = source;
		this.out = out;
	}
	
	
	public static String writeString(Supplier<ITextSource> src) throws Exception
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		RtfWriter wr = new RtfWriter(src, out);
		wr.write();
		byte[] b = out.toByteArray();
		return new String(b, CKit.CHARSET_ASCII);
	}
	
	
	public void setFont(String fontName, int fontSize)
	{
		this.fontName = fontName;
		this.fontSize = String.valueOf(2 * fontSize);
	}
	

	public void write() throws Exception
	{
		// pass 1: enumerate colors
		ITextSource src = source.get();
		ITextLine t;
		
		colorTable = new ColorTable();
		
		while((t = src.nextLine()) != null)
		{
			CKit.checkCancelled();
			
			int start = src.getStart();
			int end = src.getEnd();
			collectColors(t, start, end);
		}
		
		// pass 2: generate rtf
		
		src = source.get();
		
		writeBeginning();
		
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
	
	
	protected void collectColors(ITextLine t, int start, int end)
	{
		CKit.checkCancelled();
		
		TextCellStyle prevStyle = null;
		
		for(int i=start; i<end; i++)
		{
			TextCellStyle st = t.getCellStyle(i);
			if(prevStyle != st)
			{
				if(st != null)
				{
					Color c = st.getTextColor();
					if(c != null)
					{
						colorTable.add(c);
					}
					
					c = mixBackground(st.getBackgroundColor());
					if(c != null)
					{
						colorTable.add(c);
					}
				}
				prevStyle = st;
			}
		}
	}
	
	
	// FIX https://github.com/andy-goryachev/AccessPanelPublic/issues/2
	protected Color mixBackground(Color c)
	{
		return FX.mix(c, Color.WHITE, 0.85);
	}
	

	protected void writeBeginning() throws Exception
	{
		// preamble
		write("{\\rtf1\\ansi\\ansicpg1252\\uc1\\sl0\\sb0\\sa0\\deff0{\\fonttbl{\\f0\\fnil ");
		write(fontName);
		write(";}}\r\n");
		
		// color table
		write("{\\colortbl ;");
		for(Color c: colorTable.getColors())
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
		
		write("{\\f0\\fs");
		write(fontSize);
		write(" \\fi0\\ql ");
	}

	
	protected void writeLine(ITextLine t, int startPos, int endPos) throws Exception
	{
		CKit.checkCancelled();
		
		if(t == null)
		{
			return;
		}
		
		write("\\fi0\\ql ");
		
		TextCellStyle prevStyle = null;
		Color color = null;
		Color background = null;
		boolean bold = false;
		boolean italic = false;
		boolean under = false;
		boolean strike = false;
		
		String text = t.getPlainText();
		for(int i=startPos; i<endPos; i++)
		{
			TextCellStyle st = t.getCellStyle(i);
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
					bg = mixBackground(st.getBackgroundColor());
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
						String s = colorTable.getIndexFor(col);
						if(s == null)
						{
							s = "0";
							log.warn("no entry for " + col);
						}

						write("\\cf");
						write(s);
						write(" ");
					}
					
					color = col;
				}
				
				if(CKit.notEquals(bg, background))
				{
					if(bg == null)
					{
						write("\\highlight0 ");
					}
					else
					{
						String s = colorTable.getIndexFor(bg);
						
						write("\\highlight");
						write(s);
						write(" ");
					}
					
					background = bg;
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
			else if(ch == '\\')
			{
				write("\\\\");
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
		
		if(background != null)
		{
			write("\\highlight0 ");
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
}
