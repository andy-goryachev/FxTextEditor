// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;


/**
 * Plain Text Line.
 */
public class PlainTextLine
	implements ITextLine
{
	private final int line;
	private final String text;
	private final boolean hasTabs;
	
	
	public PlainTextLine(int line, String text)
	{
		this.line = line;
		this.text = text;
		this.hasTabs = (text == null ? false : (text.indexOf('\t') >= 0));
	}


	public int getLineNumber()
	{
		return line;
	}
	
	
	public int getModelIndex()
	{
		return line;
	}


	public String getPlainText()
	{
		return text;
	}
	
	
	public int getTextLength()
	{
		return text == null ? 0 : text.length();
	}
	
	
	public boolean hasComplexGlyphs()
	{
		return false;
	}


	public boolean hasTabs()
	{
		return hasTabs;
	}
	

	public int getGlyphCount()
	{
		return text.length();
	}


	public String getCellText(int cellIndex)
	{
		if((cellIndex >= 0) && (cellIndex  < text.length()))
		{
			return text.substring(cellIndex, cellIndex + 1);
		}
		return null;
	}


	public void updateStyle(int offset, CellStyles styles)
	{
		styles.update
		(
			null,
			null,
			false,
			false,
			false,
			false
		);
	}


	public GlyptType getGlyphType(int cellIndex)
	{
		if((cellIndex >= 0) && (cellIndex < text.length()))
		{
			char c = text.charAt(cellIndex);
			if(c == '\t')
			{
				return GlyptType.TAB;
			}
			else
			{
				return GlyptType.NORMAL;
			}
		}
		return GlyptType.EOL;
	}

	
	public int getCharIndex(int glyphIndex)
	{
		// FIX hasTabs! hasComplexUnicode
		return glyphIndex;
	}
}
