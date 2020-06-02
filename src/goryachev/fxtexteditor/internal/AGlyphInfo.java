// Copyright © 2019-2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.util.text.IBreakIterator;


/**
 * Text Glyph Info provides bidirectional offset translation
 * between chars and glyphs.
 */
public abstract class AGlyphInfo
{
	public abstract String getGlyphText(int glyphIndex);

	public abstract int getCharIndex(int glyphIndex);
	
	public abstract int getGlyphIndex(int charIndex);

	public abstract int getGlyphCount();

	public abstract boolean hasComplexGlyphs();
	
	//
	
	public static final AGlyphInfo BLANK = new GlyphInfoSimple("", false);
	protected final String text;
	protected final boolean hasTabs;

	
	protected AGlyphInfo(String text, boolean hasTabs)
	{
		this.text = text;
		this.hasTabs = hasTabs;
	}
	
	
	public final boolean hasTabs()
	{
		return hasTabs;
	}
	
	
	public static AGlyphInfo create(String text, IBreakIterator breakIterator)
	{
		if(text == null)
		{
			return BLANK;
		}
		
		boolean hasTabs = false;
		
		if(breakIterator == null)
		{
			hasTabs = (text.indexOf('\t') >= 0);
		}
		else
		{
			for(int i=0; i<text.length(); i++)
			{
				char c = text.charAt(i);
				if(c == '\t')
				{
					hasTabs = true;
				}
				else if(isComplex(c))
				{
					return createComplex(text, hasTabs, breakIterator);
				}
			}
		}
		
		return new GlyphInfoSimple(text, hasTabs);
	}
	

	private static boolean isComplex(char c)
	{
		if(Character.isSurrogate(c))
		{
			return true;
		}
		
		int t = Character.getType(c);
		switch(t)
		{
		case Character.COMBINING_SPACING_MARK: // Mark, spacing combining
//		case Character.CONNECTOR_PUNCTUATION: // Includes "_" underscore
//		case Character.CONTROL:
//		case Character.CURRENCY_SYMBOL:
//		case Character.DASH_PUNCTUATION:
//		case Character.DECIMAL_DIGIT_NUMBER:
//		case Character.ENCLOSING_MARK:
//		case Character.END_PUNCTUATION:
//		case Character.FINAL_QUOTE_PUNCTUATION:
		case Character.FORMAT: // Includes the soft hyphen, joining control characters (zwnj and zwj), control characters to support bi-directional text, and language tag characters 
//		case Character.INITIAL_QUOTE_PUNCTUATION:
//		case Character.LETTER_NUMBER:
		case Character.LINE_SEPARATOR: // Only U+2028 LINE SEPARATOR (LSEP) 
//		case Character.LOWERCASE_LETTER:
//		case Character.MATH_SYMBOL:
		case Character.MODIFIER_LETTER:
//		case Character.MODIFIER_SYMBOL:
		case Character.NON_SPACING_MARK:
//		case Character.OTHER_LETTER:
//		case Character.OTHER_NUMBER:
//		case Character.OTHER_PUNCTUATION:
//		case Character.OTHER_SYMBOL:
		case Character.PARAGRAPH_SEPARATOR:
//		case Character.PRIVATE_USE:
//		case Character.SPACE_SEPARATOR:
//		case Character.START_PUNCTUATION:
		case Character.SURROGATE:
//		case Character.TITLECASE_LETTER:
		case Character.UNASSIGNED:
//		case Character.UPPERCASE_LETTER:
			return true;
		}
		
		return false;
	}


	private static GlyphInfoComplex createComplex(String text, boolean hasTabs, IBreakIterator bi)
	{
		int len = text.length();
		int[] offsets = new int[len];
		int gi = 0;
		int off = 0;
		
		bi.setText(text);
		int start = bi.first();
		for(int end = bi.next(); end != IBreakIterator.DONE; start = end, end = bi.next())
		{
			String s = text.substring(start, end);
			
			if(!hasTabs)
			{
				if("\t".equals(s))
				{
					hasTabs = true;
				}
			}
			
			offsets[gi++] = off;
			off += s.length();
		}
		
		return new GlyphInfoComplex(text, hasTabs, offsets);
	}
}
