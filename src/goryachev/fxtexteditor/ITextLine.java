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
	
	/** returns the plain text, null permitted */
	public String getPlainText();
	
	public int getCellCount();
	
	public String getCellText(int offset);
	
	public void getStyle(StyleInfo s, int offset);
}
