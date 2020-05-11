// Copyright © 2019-2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.util.text.IBreakIterator;
import java.util.Arrays;


/**
 * Text Glyph Info provides bidirectional offset translation
 * between chars and glyphs.
 */
public abstract class TextGlyphInfo
{
	public abstract String getGlyphText(int glyphIndex);

	public abstract int getCharIndex(int glyphIndex);
	
	public abstract GlyphIndex getGlyphIndex(int charIndex);

	public abstract int getGlyphCount();

	public abstract boolean hasComplexGlyphs();
	
	//
	
	public static final TextGlyphInfo BLANK = new SIMPLE("", false);
	protected final String text;
	protected final boolean hasTabs;

	
	protected TextGlyphInfo(String text, boolean hasTabs)
	{
		this.text = text;
		this.hasTabs = hasTabs;
	}
	
	
	public final boolean hasTabs()
	{
		return hasTabs;
	}
	
	
	public static TextGlyphInfo create(String text, IBreakIterator breakIterator)
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
		
		return new SIMPLE(text, hasTabs);
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


	private static COMPLEX createComplex(String text, boolean hasTabs, IBreakIterator bi)
	{
		boolean hasComplex = false;
		
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
		
		return new COMPLEX(text, hasTabs, hasComplex, offsets);
	}
	
	
	public String getGlyphText(GlyphIndex glyphIndex)
	{
		return getGlyphText(glyphIndex.intValue());
	}
	
	
	public int getCharIndex(GlyphIndex gix)
	{
		if(gix.isBOL())
		{
			return 0;
		}
		else if(gix.isEOF())
		{
			return 0;
		}
		else if(gix.isEOL())
		{
			return text.length();
		}
		else if(gix.isInsideTab())
		{
			return gix.getLeadingCharIndex();
		}
		else
		{
			return getCharIndex(gix.intValue());
		}
	}


	//
	
	
	public static class SIMPLE extends TextGlyphInfo
	{
		public SIMPLE(String text, boolean hasTabs)
		{
			super(text, hasTabs);
		}


		public String getGlyphText(int ix)
		{
			if((ix >= 0) && (ix < text.length()))
			{
				return text.substring(ix, ix + 1);
			}
			return null;
		}


		public int getCharIndex(int glyphIndex)
		{
			if(glyphIndex < 0)
			{
				return 0;
			}
			else if(glyphIndex > text.length())
			{
				return text.length();
			}
			return glyphIndex;
		}
		
		
		public GlyphIndex getGlyphIndex(int charIndex)
		{
			return new GlyphIndex(charIndex);
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
		private final boolean hasComplex;
		private final int[] charOffsets;
		
		
		public COMPLEX(String text, boolean hasTabs, boolean hasComplex, int[] charOffsets)
		{
			super(text, hasTabs);
			this.hasComplex = hasComplex;
			this.charOffsets = charOffsets;
		}


		public boolean hasComplexGlyphs()
		{
			return hasComplex;
		}
		

		public int getCharIndex(int glyphIndex)
		{
			return charOffsets[glyphIndex];
		}
		
		
		public GlyphIndex getGlyphIndex(int charIndex)
		{
			// this can be replaced either by a separate array
			// possibly created on demand.
			// but for now, let's assume the binary search should be faster and easier on memory
			int ix = Arrays.binarySearch(charOffsets, charIndex);
			if(ix < 0)
			{
				ix = -ix;
			}
			return GlyphIndex.of(ix);
		}


		public String getGlyphText(int ix)
		{
			if(ix > charOffsets.length)
			{
				return null;
			}

			int start = charOffsets[ix];
			if(ix == charOffsets.length)
			{
				return text.substring(start);
			}
			else
			{
				int end = charOffsets[ix + 1];
				return text.substring(start);
			}
		}


		public int getGlyphCount()
		{
			return charOffsets.length;
		}
	}
}
