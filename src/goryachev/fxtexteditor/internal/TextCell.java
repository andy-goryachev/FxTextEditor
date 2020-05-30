// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.GlyphType;


/**
 * Text Cell class (preliminary name) contains all information
 * required to paint a cell and obtain the insert position.
 * For example, if this cell falls within a tab span, it also
 * provides position of the leading edge.
 */
public class TextCell
{
	private static final TextCell globalInstance = new TextCell();
	private GlyphType type;
	private int caretCharIndex;
	private int leadingCharIndex;
	private int insertCharIndex;
	private int glyphIndex;
	
	
	public TextCell(GlyphType type, int caretCharIndex, int leadingCharIndex, int insertCharIndex, int glyphIndex)
	{
		this.type = type;
		this.caretCharIndex = caretCharIndex;
		this.leadingCharIndex = leadingCharIndex;
		this.insertCharIndex = insertCharIndex;
		this.glyphIndex = glyphIndex;
	}
	
	
	public TextCell()
	{
	}
	
	
	public String toString()
	{
		return
			"TextCell[" +
			"type=" + type +
			", caret=" + caretCharIndex +
			", leading=" + leadingCharIndex +
			", insert=" + insertCharIndex +
			", glyph=" + glyphIndex +
			"]";
	}
	
	
	public void set(GlyphType type, int caretCharIndex, int leadingCharIndex, int insertCharIndex, int glyphIndex)
	{
		this.type = type;
		this.caretCharIndex = caretCharIndex;
		this.leadingCharIndex = leadingCharIndex;
		this.insertCharIndex = insertCharIndex;
		this.glyphIndex = glyphIndex;
	}
	
	
	/** method for testing */
	protected void set(TextCell c)
	{
		this.type = c.type;
		this.caretCharIndex = c.caretCharIndex;
		this.leadingCharIndex = c.leadingCharIndex;
		this.insertCharIndex = c.insertCharIndex;
		this.glyphIndex = c.glyphIndex;
	}
	
	
	public static TextCell globalInstance()
	{
		return globalInstance;
	}
	
	
	public GlyphType getGlyphType()
	{
		return type;
	}
	
	
	/** 
	 * returns a non-negative char index if a caret may be placed at the leading edge of this cell.
	 * returns -1 if this cell is beyond EOL, or inside of a tab span.
	 */
	public int getCaretCharIndex()
	{
		return caretCharIndex;
	}


	/** 
	 * returns a non-negative char index of the glyph that corresponds to this cell.
	 * normally, this will be the same value as returned by getCaretCharIndex(), 
	 * except for when the cell is inside a tab span, in which case this method
	 * returns the start of the tab.
	 */
	public int getLeadingEdgeCharIndex()
	{
		return leadingCharIndex;
	}
	
	
	/**
	 * returns a non-negative index of a glyph displayed in this cell,
	 * or GlyphIndex.EOL_INDEX if the cell is beyond EOL,
	 * or GlyphIndex.EOF_INDEX if the cell is beyond EOF.
	 */
	public int getGlyphIndex()
	{
		return glyphIndex;
	}
	
	
	/** returns a char index where a caret must be positioned if a mouse click lands on this cell */
	public int getInsertCharIndex()
	{
		return insertCharIndex;
	}
	
	
	public int getTabSpan()
	{
		// only in a Tab
		throw new Error();
	}
}
