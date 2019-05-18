// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;


/**
 * ITextCells interface represents a single line of text.
 * Each cell [will eventually be] a grapheme block, 
 * but for now there is a 1:1 correspondence of cell to a character
 * (i.e. it does not yet support unicode)
 */
public interface ITextCells
{
	/** returns the number of cells on a row */
	public int getCellCount();

	/** returns a single cell */
	public TCell getCell(int ix);
}
