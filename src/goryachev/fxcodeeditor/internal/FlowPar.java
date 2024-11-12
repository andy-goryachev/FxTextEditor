// Copyright © 2024-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxcodeeditor.internal;
import goryachev.fxcodeeditor.model.CodeParagraph;


/**
 * Flow Paragraph contains information necessary for the paragraph presentation,
 * such as conversion between the paragraph's characters and glyph cells,
 * wrapping lines
 */
public class FlowPar
{
	private final CodeParagraph paragraph;
	
	
	private FlowPar(CodeParagraph p)
	{
		this.paragraph = p;
	}
	
	
	public static FlowPar create(CodeParagraph p, int tabSize, int wrapLimit)
	{
		String text = p.getPlainText();
		// TODO tabs, complex glyphs, multiple lines
		// possible outputs: SingleLine, SimpleWrap, ComplexSingleLine, ComplexMultiLine
		return new FlowPar(p);
	}
}
