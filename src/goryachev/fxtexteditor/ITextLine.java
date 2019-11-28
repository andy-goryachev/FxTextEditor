// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;


/**
 * Text Row in the model.
 * 
 * valid until the row is modified in any wa (such as modification of the text, 
 * changing of any attributes, or a line number.
 */
public interface ITextLine
{
	/** underlying line number, or -1 if the model line does not correspond to a text line */
	public int getLineNumber();


	/** 
	 * returns index of this text line in the model.
	 * this number may be different from line number reported by getLineNumber()
	 * when the model inserts additional text lines.
	 */
	public int getModelIndex();


	/** returns the plain text, or null */
	public String getPlainText();
	
	
	/** length of the plain text, or 0 if unknown */
	public int getTextLength();


	/** 
	 * returns the number of glyphs in the text line.  
	 * one glyph is rendered in one fixed width cell (even full width CJK)
	 * A tab is one glyph.
	 */
	public int getGlyphCount();


	/** 
	 * returns the text to be rendered in one cell
	 */
	public String getCellText(int glyphIndex);


	/**
	 * updates cell styles.
	 * FIX offset needs to be glyph index, currently not.
	 */
	public void updateStyle(int offset, CellStyles styles);


	/** 
	 * returns true if translation from text to glyphs is not 1:1 (except tabs) 
	 * (i.e. has modifying characters, surrogate pairs, emojis, etc.)
	 */ 
	public boolean hasComplexGlyphs();
	
	
	/** returns true if the text contains one or more tab characters */
	public boolean hasTabs();


	/** returns the type of a glyph at the specified cell index.  this method should be fast */
	public GlyptType getGlyphType(int cellIndex);
	
	
	/** returns the offest into plain text string for the given glyph index */
	public int getCharIndex(int glyphIndex);
}
