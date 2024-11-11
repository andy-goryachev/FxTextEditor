// Copyright © 2024-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxcodeeditor.internal;
import goryachev.common.util.text.IBreakIterator;
import goryachev.fxcodeeditor.model.CodeParagraph;


/**
 * Flow Cell contains information necessary for the paragraph presentation,
 * such as conversion between the paragraph's characters and glyph cells,
 * wrapping lines
 */
public class FlowCell
{
	private final CodeParagraph paragraph;
	
	
	private FlowCell(CodeParagraph p)
	{
		this.paragraph = p;
	}
	
	
	public static FlowCell create(CodeParagraph p, int tabSize, int wrapLimit, IBreakIterator bi)
	{
		String text = p.getPlainText();
		// TODO tabs, complex glyphs, multiple lines
		// possible outputs: SingleLine, SimpleWrap, ComplexSingleLine, ComplexMultiLine
		return new FlowCell(p);
	}
}
