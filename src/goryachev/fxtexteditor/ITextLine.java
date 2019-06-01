// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;


/**
 * ITextLine interface represents a single line of text, comprised of a number
 * of cells.
 * 
 * Each cell represents a grapheme cluster, and therefore might contain more than one
 * unicode code point.
 */
public interface ITextLine
{
	/** returns the number of cells on a row */
	public int getCellCount();

	/** returns a single cell */
	public TCell getCell(int ix);
}
