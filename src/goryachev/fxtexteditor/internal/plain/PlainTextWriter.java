// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal.plain;
import goryachev.fxtexteditor.ITextSource;
import java.io.StringWriter;
import java.io.Writer;


/**
 * Plain Text Writer.
 */
public class PlainTextWriter
{
	private final ITextSource src;
	private final Writer wr;
	
	
	public PlainTextWriter(ITextSource src, Writer wr)
	{
		this.src = src;
		this.wr = wr;
	}
	
	
	public static String writeString(ITextSource src) throws Exception
	{
		StringWriter out = new StringWriter();
		PlainTextWriter wr = new PlainTextWriter(src, out);
		wr.write();
		return out.toString();
	}
	
	
	public void write() throws Exception
	{
		String t;
		boolean nl = false;
		while((t = src.nextPlainTextLine()) != null)
		{
			if(nl)
			{
				// TODO system line separator?
				wr.write('\n');
			}
			else
			{
				nl = true;
			}
			
			int start = src.getStart();
			int len = src.getEnd() - start;
			wr.write(t, start, len);
		}
		
		wr.flush();
	}
}
