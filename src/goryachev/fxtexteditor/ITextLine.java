// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;


/**
 * ITextLine interface represents a single line of text, comprised of a number
 * of cells.
 * 
 * Each cell represents a single grapheme cluster, and therefore might contain more than one
 * unicode code point.
 */
public interface ITextLine
{
	/** returns the number of cells in this text line */
	public int getCellCount();

	/** returns the cell at the given offset.  may return null */
	public TCell getCell(int offset);
}
