// Copyright © 2024-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxcodeeditor.internal;
import goryachev.fxcodeeditor.model.CodeParagraph;


/**
 * WrapInfo represents a CodeParagraph laid out within the view port with wrapping
 * and tab stops computed when necessary.
 * 
 * There are several type:
 * - simple, with 1:1 correspondence between characters and cells and no tabulation applied
 * - simple, wrapped
 * - complex, where tabs and/or combined characters are present.
 */
public class WrapInfo
{
	private final CodeParagraph paragraph;
	
	
	private WrapInfo(CodeParagraph p)
	{
		this.paragraph = p;
	}
	
	
	public static WrapInfo create(CodeParagraph p, int tabSize, int wrapLimit)
	{
		String text = p.getPlainText();
		// TODO tabs, complex glyphs, multiple lines
		// possible outputs: SingleLine, SimpleWrap, ComplexSingleLine, ComplexMultiLine
		return new WrapInfo(p);
	}


	/**
	 * Returns the number of visual rows in this paragraph.
	 */
	public int getRowCount()
	{
		return 0;
	}
}
