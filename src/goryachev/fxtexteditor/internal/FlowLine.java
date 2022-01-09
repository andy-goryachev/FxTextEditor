// Copyright © 2019-2022 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.CellStyle;
import goryachev.fxtexteditor.GlyphType;
import goryachev.fxtexteditor.ITabPolicy;
import goryachev.fxtexteditor.ITextLine;


/**
 * Flow Line Cache Entry.
 */
public class FlowLine
{
	public static final FlowLine BLANK = new FlowLine(null, AGlyphInfo.BLANK);
	
	private final ITextLine tline;
	private final AGlyphInfo info;
	private WrapInfo wrap;
	
	
	public FlowLine(ITextLine tline, AGlyphInfo info)
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
	
	
	public AGlyphInfo glyphInfo()
	{
		return info;
	}
	
	
	public WrapInfo getWrapInfo(ITabPolicy tabPolicy, int width, boolean wrapLines)
	{
		if(wrap != null)
		{
			if(wrap.isCompatible(tabPolicy, width, wrapLines))
			{
				return wrap;
			}
		}
		
		wrap = WrapInfo.create(this, tabPolicy, width, wrapLines);
		return wrap;
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

	
	/** returns the offest into plain text string for the given glyph index */
	public int getCharIndex(int glyphIndex)
	{
		return info.getCharIndex(glyphIndex);
	}
	
	
	public int getGlyphIndex(int charIndex)
	{
		return info.getGlyphIndex(charIndex);
	}
	
	
	public int getTextLength()
	{
		return tline == null ? 0 : tline.getTextLength();
	}
	
	
	public int getModelIndex()
	{
		return tline == null ? -1 : tline.getModelIndex();
	}
	
	
	public CellStyle getCellStyle(int charOffset)
	{
		if(tline != null)
		{
			return tline.getCellStyle(charOffset);
		}
		return null;
	}

	
	/** returns the type of a glyph at the specified cell index. */
	public GlyphType getGlyphType(int glyphIndex)
	{
		String s = info.getGlyphText(glyphIndex);
		if(s == null)
		{
			return GlyphType.EOL;
		}
		else if("\t".equals(s))
		{
			return GlyphType.TAB;
		}
		else
		{
			return GlyphType.REG;
		}
	}
}
