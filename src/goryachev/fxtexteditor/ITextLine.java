// Copyright © 2019-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.fx.TextCellStyle;
import javafx.scene.paint.Color;


/**
 * Text Row in the model.
 * 
 * valid until the row is modified in any way (such as modification of the text, 
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


	/** 
	 * returns cell styles at the given char index, or null if no styling exists.
	 * The styles should not include view-specific styles such as current line or cursor.
	 */
	public TextCellStyle getCellStyle(int charOffset);
	
	
	/** returns a line color or null */
	public Color getLineColor();
}
