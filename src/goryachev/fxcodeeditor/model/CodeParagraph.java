// Copyright © 2024-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxcodeeditor.model;
import javafx.scene.paint.Color;


/**
 * Code Paragraph is an immutable object that encapsulates a single paragraph of
 * text and associated text attributes.
 */
public abstract class CodeParagraph
{
	/** 
	 * Returns the model index of this paragraph.
	 */
	public abstract int getIndex();


	/**
	 * Returns the plain text (non-null) of this paragraph.
	 */
	public abstract String getPlainText();
	
	
	/**
	 * Returns the length of the paragraph plain text.
	 */
	public abstract int getTextLength();


	/** 
	 * returns cell styles at the given char index, or null if no styling exists.
	 * The styles should not include view-specific styles such as current line or cursor.
	 */
	//public TextCellStyle getCellStyle(int charOffset);
	
	
	/**
	 * Returns the background color of this paragraph, or {@code null}.
	 */
	public abstract Color getBackgroundColor();
}
