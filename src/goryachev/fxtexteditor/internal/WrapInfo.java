// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.GlyphType;
import goryachev.fxtexteditor.ITabPolicy;


/**
 * Provides mapping between text characters, glyphs, and screen cells.
 * This information is cached by FlowLine.
 */
public abstract class WrapInfo
{
	/** returns true if the current wrap info can be reused with the new screen configuration */
	public abstract boolean isCompatible(ITabPolicy tabPolicy, int width, boolean wrapLines);
	
	/** returns the number of screen rows occupied by the flow line */
	public abstract int getWrapRowCount();
	
	/** finds wrapped row for the given glyph index */
	public abstract int findRowForGlyphIndex(int glyphIndex);
	
	/** returns the wrapped row index for the given glyph index */
	public abstract int getWrapRowForGlyphIndex(int glyphIndex);
	
	/** returns the wrapped row index for the given character */
	public abstract int getWrapRowForCharIndex(int charIndex);

	/** returns the screen column for the given character */
	public abstract int getColumnForCharIndex(int charIndex);

	/** returns the number of text glyphs at the specific wrap row */
	public abstract int getGlyphCountAtRow(int wrapRow);
	
	/** returns glyph index for wrapped row */
	@Deprecated // FIX may not return the right index (if in a tab)
	public abstract int getGlyphIndexForRow_DELETE(int row);
	
	/** 
	 * returns the character index for the given column and wrap row.
	 * returns the nearest insert position if inside a tab:
	 * a|--tab--|b
	 */
	@Deprecated // replace with getCell() TODO
	public abstract int getCharIndexForColumn(int wrapRow, int column);
	
	/** 
	 * returns a glyph index (>=0),
	 * or a negative value that corresponds to either
	 * -glyphIndex for a tab, or
	 * a value that can be checked with GlyphIndex.isEOL(x) or GlyphIndex.isEOF(x)
	 */
	@Deprecated // replace with getCell() TODO
	protected abstract int getGlyphIndex(int wrapRow, int column);
	
	/** 
	 * returns all information about screen cell at the given wrap row and column.
	 * this method should be sufficient for paintCell and getInsertPosition.
	 * 
	 * TODO for performance reasons, we may pass a pointer to a statically allocated TextCell instance.
	 */
	public abstract TextCell getCell(int wrapRow, int column);
	
	//
	
	public static final WrapInfo EMPTY = new SingleRowWrapInfo(0);
	
	
	protected WrapInfo()
	{
	}
	
	
	public static WrapInfo create(FlowLine fline, ITabPolicy tabPolicy, int width, boolean wrapLines)
	{
		int lineIndex = fline.getModelIndex();
		if(lineIndex < 0)
		{
			return EMPTY;
		}
		
		boolean complex = fline.hasComplexGlyphs();
		if(!complex)
		{
			if(!tabPolicy.isSimple())
			{
				complex |= fline.hasTabs();
			}
		}
		
		if(complex)
		{
			return ComplexWrapInfo.createComplexWrapInfo(fline, tabPolicy, width, wrapLines);
		}
		else
		{
			int len = fline.getGlyphCount();
			if(wrapLines)
			{
				return new SimpleWrapInfo(len, width);
			}
			else
			{
				return new SingleRowWrapInfo(len);
			}
		}
	}
}
