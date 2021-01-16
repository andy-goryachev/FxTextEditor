// Copyright © 2020-2021 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;


/**
 * A transient position in text: 
 * model line number + glyph index.
 */
public class GlyphPos
{
	public final int line;
	public final int glyphIndex;
	
	
	public GlyphPos(int line, int glyphIndex)
	{
		this.line = line;
		this.glyphIndex = glyphIndex;
	}
	
	
	public int getLine()
	{
		return line;
	}
	
	
	public int getGlyphIndex()
	{
		return glyphIndex;
	}
}
