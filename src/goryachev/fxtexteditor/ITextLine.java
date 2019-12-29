// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;


/**
 * Text Row in the model.
 * 
 * valid until the row is modified in any wa (such as modification of the text, 
 * changing of any attributes, or a line number.
 */
public interface ITextLine
{
	/** underlying line number, or -1 if the model line does not correspond to a text line */
	public int getLineNumber();


	/** 
	 * returns index of this text line in the model.
	 * this number may be different from line number reported by getLineNumber()
	 * when the model inserts additional text lines.
	 */
	public int getModelIndex();


	/** returns the plain text, or null */
	public String getPlainText();
	
	
	/** length of the plain text, or 0 if unknown */
	public int getTextLength();


	/** sets cell styles at the given char index */
	public void updateStyles(CellStyles styles, int charOffset);
}
