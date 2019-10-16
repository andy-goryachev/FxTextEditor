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
	
	
	public PlainTextLine(int line, String text)
	{
		this.line = line;
		this.text = text;
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
	
	
	public boolean hasComplexGlyphLogic()
	{
		return false;
	}


	public int getCellCount()
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
		if((cellIndex >= 0) && (cellIndex  < text.length()))
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
}
