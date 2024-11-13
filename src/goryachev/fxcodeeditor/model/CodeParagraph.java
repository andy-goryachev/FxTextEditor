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
	 * Returns the background color of this paragraph, or {@code null}.
	 */
	public abstract Color getBackgroundColor();


	/**
	 * Returns the plain text (non-null) of this paragraph.
	 */
	public abstract String getPlainText();
	
	
	/**
	 * Returns the length of the paragraph plain text.
	 */
	public abstract int getTextLength();
	
	
	/**
	 * Returns the number of cells.
	 */
	public abstract int getCellCount();


	/**
	 * Retrieves the text cell into the specified instance of
	 * {@link CellInfo}.
	 * 
	 * @implNote
	 * The implementation must not cache the instance passed to it, because
	 * the same instance is likely to be used for performance reasons.
	 */
	public abstract void getCell(int cellIndex, CellInfo a);


	public static CodeParagraph of(int index, String text)
	{
		return new CodeParagraph()
		{
			@Override
			public int getTextLength()
			{
				return text.length();
			}
			
			
			@Override
			public int getCellCount()
			{
				return text.length();
			}
			
			
			@Override
			public String getPlainText()
			{
				return text;
			}
			
			
			@Override
			public int getIndex()
			{
				return index;
			}
			
			
			@Override
			public void getCell(int offset, CellInfo a)
			{
			}
			
			
			@Override
			public Color getBackgroundColor()
			{
				return null;
			}
		};
	}
}
