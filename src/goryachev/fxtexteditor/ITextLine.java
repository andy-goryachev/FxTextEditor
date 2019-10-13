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


	/** 
	 * returns the number of glyphs in the text line.  
	 * one glyph is rendered in one fixed width cell.
	 * this code does not support rendering of double width symbols such as full-width CJK.
	 */
	public int getCellCount();


	/** 
	 * returns the text to be rendered in one cell
	 */
	public String getCellText(int offset);


	/**
	 * updates cell styles.
	 */
	public void updateStyle(int offset, CellStyles styles);
}
