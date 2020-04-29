// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.fxtexteditor.internal.GlyphIndex;


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
	
	
	public GlyphIndex getGlyphIndex()
	{
		return GlyphIndex.of(glyphIndex);
	}
}
