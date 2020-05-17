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
	/** returns the number of screen rows occupied by the flow line */
	public abstract int getWrapRowCount();
	
	/** returns glyph index for wrapped row */
	@Deprecated // TODO may not return the right index (if in a tab)
	public abstract int getGlyphIndexForRow_DELETE(int row);
	
	/** finds wrapped row for the given glyph index */
	public abstract int findRowForGlyphIndex(int glyphIndex);
	
	/** returns true if the current wrap info can be reused with the new screen configuration */
	public abstract boolean isCompatible(ITabPolicy tabPolicy, int width, boolean wrapLines);
	
	/** returns the wrapped row index for the given glyph index */
	public abstract int getWrapRowForGlyphIndex(int glyphIndex);
	
	/** returns the wrapped row index for the given character */
	public abstract int getWrapRowForCharIndex(int charIndex);

	/** returns the screen column for the given character */
	public abstract int getColumnForCharIndex(int charIndex);

	/** 
	 * returns the character index for the given column and wrap row.
	 * returns the nearest insert position if inside a tab:
	 * a|--tab---|b
	 */
	public abstract int getCharIndexForColumn(int wrapRow, int column);
	
	/** returns the number of text glyphs at the specific wrap row */
	public abstract int getGlyphCountAtRow(int wrapRow);
	
	/** 
	 * returns a glyph index (>=0),
	 * or a negative value that corresponds to either
	 * -glyphIndex for a tab, or
	 * a value that can be checked with GlyphIndex.isEOL(x) or GlyphIndex.isEOF(x)
	 */
	protected abstract int getGlyphIndex(int row, int column);
	
	//
	
	public static final WrapInfo EMPTY = new EmptyWrapInfo();
	
	
	protected WrapInfo()
	{
	}
	
	
	public static WrapInfo create(FlowLine fline, ITabPolicy tabPolicy, int width, boolean wrapLines)
	{
		// TODO move to caller?
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
	
	
	/** returns the type of a glyph at the specified row and column */
	public GlyphType getGlyphType(int row, int column)
	{
		int gix = getGlyphIndex(row, column);
		if(gix >= 0)
		{
			return GlyphType.REG;
		}
		
		if(GlyphIndex.isEOL(gix))
		{
			return GlyphType.EOL;
		}
		else if(GlyphIndex.isEOF(gix))
		{
			return GlyphType.EOF;
		}
		else
		{
			return GlyphType.TAB;
		}
	}
	
	/** 
	 * returns tab span (distance to the next glyph), or throws an Error if it is not a tab.
	 * must always be preceded by a call to getGlyphTypeAtColumn() and a chech against GlyphType.TAB.
	 */
	public int getTabSpan(int row, int column)
	{
		int gix = getGlyphIndex(row, column);
		if(gix >= 0)
		{
			throw new Error("not a tab at row=" + row + " col=" + column); 
		}
		
		for(int span=1; ; span++)
		{
			int next = getGlyphIndex(row + span, column);
			if(gix != next)
			{
				return span;
			}
			
			// check for overflow, should not happen
			if(span < 0)
			{
				throw new Error();
			}
		}
	}
}
