// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;


/**
 * Represents a chain of graphemes taken from the model text line.
 * 
 * A single grapheme may contain multiple surrogate pairs or even chars (emoji),
 * and is expected to be rendered in one text cell, with the exception of TAB characters
 * which may span multiple cells.
 */
public interface ITextCells
{
	public Grapheme getCell(int pos);
}
