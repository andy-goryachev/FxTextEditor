// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.CellStyles;
import goryachev.fxtexteditor.ITextLine;


/**
 * Flow Line (cache entry).
 */
public class FlowLine
{
	public static final FlowLine BLANK = new FlowLine(null, TextGlyphInfo.BLANK);
	
	private final ITextLine tline;
	private final TextGlyphInfo info;
	
	
	public FlowLine(ITextLine tline, TextGlyphInfo info)
	{
		this.tline = tline;
		this.info = info;
	}
	
	
	public ITextLine getTextLine()
	{
		return tline;
	}
	
	
	public String getText()
	{
		return tline == null ? null : tline.getPlainText();
	}
	
	
	public TextGlyphInfo info()
	{
		return info;
	}
	

	/** 
	 * returns true if translation from text to glyphs is not 1:1 (except tabs) 
	 * (i.e. has modifying characters, surrogate pairs, emojis, etc.)
	 */ 
	public boolean hasComplexGlyphs()
	{
		return info.hasComplexGlyphs();
	}
	
	
	/** returns true if the text contains one or more tab characters */
	public boolean hasTabs()
	{
		return info.hasTabs();
	}
	

	/** 
	 * returns the number of glyphs in the text line.  
	 * one glyph is rendered in one fixed width cell (even full width CJK)
	 * A tab is one glyph.
	 */
	public int getGlyphCount()
	{
		return info.getGlyphCount();
	}


	/** 
	 * returns the text to be rendered in one cell
	 */
	public String getCellText(int cellIndex)
	{
		return info.getGlyphText(cellIndex);
	}

	
	/** returns the offest into plain text string for the given glyph index */
	public int getCharIndex(int glyphIndex)
	{
		return info.getCharIndex(glyphIndex);
	}
	
	
	public int getTextLength()
	{
		return tline == null ? 0 : tline.getTextLength();
	}
	
	
	public int getModelIndex()
	{
		return tline == null ? -1 : tline.getModelIndex();
	}
	
	
	public void updateStyle(int x, CellStyles style)
	{
		if(tline != null)
		{
			tline.updateStyle(x, style);
		}
	}
}