// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.util.text.IBreakIterator;


/**
 * Text Glyph Info provides bidirectional offset translation
 * between chars and glyphs.
 */
public abstract class TextGlyphInfo
{
	public abstract boolean hasTabs();

	public abstract String getGlyphText(int cellIndex);

	public abstract int getCharIndex(int glyphIndex);

	public abstract int getGlyphCount();

	public abstract boolean hasComplexGlyphs();
	
	//
	
	public static final TextGlyphInfo BLANK = new SIMPLE("");
	protected final String text;

	
	protected TextGlyphInfo(String text)
	{
		this.text = text;
	}
	
	
	public static TextGlyphInfo create(String text, IBreakIterator breakIterator)
	{
		if(text == null)
		{
			return BLANK;
		}
		
		if(breakIterator != null)
		{
			for(int i=0; i<text.length(); i++)
			{
				char c = text.charAt(i);
				if(isTabOrComplex(c))
				{
					return createComplex(text, breakIterator);
				}
			}
		}
		
		return new SIMPLE(text);
	}
	

	private static boolean isTabOrComplex(char c)
	{
		switch(c)
		{
		case '\t':
			return true;
		}
		
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
		case Character.MODIFIER_SYMBOL:
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


	private static COMPLEX createComplex(String text, IBreakIterator bi)
	{
		boolean hasTabs = false;
		boolean hasComplex = false;
		
		int len = text.length();
		int[] glyphOffsets = new int[len];
		int gi = 0;
		int goff = 0;
		
		bi.setText(text);
		int start = bi.first();
		for(int end = bi.next(); end != IBreakIterator.DONE; start = end, end = bi.next())
		{
			String s = text.substring(start, end);
			
			glyphOffsets[gi++] = goff;
			goff += s.length();
		}
		
		return new COMPLEX(text, hasTabs, hasComplex, glyphOffsets);
	}


	//
	
	
	public static class SIMPLE extends TextGlyphInfo
	{
		public SIMPLE(String text)
		{
			super(text);
		}


		public boolean hasTabs()
		{
			return false;
		}


		public String getGlyphText(int cellIndex)
		{
			if((cellIndex >= 0) && (cellIndex < text.length()))
			{
				return text.substring(cellIndex, cellIndex + 1);
			}
			return "";
		}


		public int getCharIndex(int glyphIndex)
		{
			return glyphIndex;
		}


		public int getGlyphCount()
		{
			return text.length();
		}


		public boolean hasComplexGlyphs()
		{
			return false;
		}
	}
	
	
	//
	
	
	public static class COMPLEX extends TextGlyphInfo
	{
		private final boolean hasTabs;
		private final boolean hasComplex;
		private final int[] glyphOffsets;
		
		
		public COMPLEX(String text, boolean hasTabs, boolean hasComplex, int[] glyphOffsets)
		{
			super(text);
			this.hasTabs = hasTabs;
			this.hasComplex = hasComplex;
			this.glyphOffsets = glyphOffsets;
		}


		public boolean hasComplexGlyphs()
		{
			return hasComplex;
		}
		
		
		public boolean hasTabs()
		{
			return hasTabs;
		}


		public int getCharIndex(int glyphIndex)
		{
			return glyphOffsets[glyphIndex];
		}


		public String getGlyphText(int cellIndex)
		{
			int start = glyphOffsets[cellIndex];
			if(cellIndex == glyphOffsets.length)
			{
				return text.substring(start);
			}
			else
			{
				int end = glyphOffsets[cellIndex + 1];
				return text.substring(start);
			}
		}


		public int getGlyphCount()
		{
			return glyphOffsets.length;
		}
	}
}
