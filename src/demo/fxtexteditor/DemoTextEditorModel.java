// Copyright © 2019-2024 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import goryachev.common.util.CKit;
import goryachev.common.util.text.IBreakIterator;
import goryachev.fx.TextCellStyle;
import goryachev.fxtexteditor.Edit;
import goryachev.fxtexteditor.FxTextEditorModel;
import goryachev.fxtexteditor.ITextLine;
import goryachev.fxtexteditor.PlainTextLine;


/**
 * Demo FxTextEditorModel.
 */
public class DemoTextEditorModel
	extends FxTextEditorModel
{
	protected final String[] lines;
	protected final int lineCount;
	private static TAttributes NONE = new TAttributes();
	private static int cachedStylesLine = -1;
	private static TSegment cachedSegment;
	

	public DemoTextEditorModel(String text, int lineCount)
	{
		lines = CKit.split(text, '\n');
		this.lineCount = (lineCount <= 0 ? lines.length : lineCount);
		
		setDefaultRtfCopyHandler();
		setDefaultHtmlCopyHandler();
	}
	
	
	public DemoTextEditorModel(String text)
	{
		this(text, -1);
	}
	
	
	@Override
	public IBreakIterator getBreakIterator()
	{
		return null;
	}
	
	
	@Override
	public Edit edit(Edit ed) throws Exception
	{
		throw new Exception("not supported");
	}


	@Override
	public int getLineCount()
	{
		return lineCount;
	}

	
	protected String plainText(int line)
	{
		if(line < 0)
		{
			throw new IllegalArgumentException("line=" + line);
		}
		
		if(line < getLineCount())
		{
			int ix = line % lines.length;
			String s = lines[ix];
			if(s.length() > 0)
			{
				switch(s.charAt(s.length() - 1))
				{
				case '\r':
				case '\n':
					return s.substring(0, s.length() - 1);
				}
			}
			return s;
		}
		return null;
	}
	
	
	protected TAttributes applySyntax(String text)
	{
		if(CKit.isBlank(text))
		{
			return NONE;
		}
		
		TAttributes a = new TAttributes();
		for(TSegment seg: new DemoSyntax(text).generateSegments())
		{
			a.addSegment(seg);
		}
		return a;
	}
	
	
	// caches last segment for faster access
	protected TSegment fastGetSegment(int line, int off, TAttributes attributes)
	{
		if(line == cachedStylesLine)
		{
			if(cachedSegment != null)
			{
				if(cachedSegment != null)
				{
					if(cachedSegment.contains(off))
					{
						return cachedSegment;
					}
				}
			}
		}
		
		TSegment seg = attributes.getSegmentAt(off);
		cachedStylesLine = line;
		cachedSegment = seg;
		return seg;
	}
	
	
	@Override
	public ITextLine getTextLine(int line)
	{
		String text = plainText(line);
		if(text != null)
		{
			return new PlainTextLine(line, text)
			{
				private TAttributes attributes;
				
				
				@Override
				public TextCellStyle getCellStyle(int off)
				{
					if(attributes == null)
					{
						String text = getPlainText();
						attributes = applySyntax(text); 
					}
					
					TSegment seg = fastGetSegment(line, off, attributes);
					if(seg != null)
					{
						return seg.style;
					}
					return null;
				}
			};
		}
		return null;
	}
}
