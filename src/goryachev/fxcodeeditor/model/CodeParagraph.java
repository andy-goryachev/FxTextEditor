// Copyright © 2024-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxcodeeditor.model;
import java.util.concurrent.atomic.AtomicReference;
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
	 * This method may return a non-opaque color in which case it will be mixed
	 * with the view port background.
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
	 * Returns true when the text contains tab characters.
	 */
	public abstract boolean containsTabs();


	/**
	 * This method is called by the view to retrieve the cell content: text and style.
	 * The two references are set to {@code null} value before calling this method.  
	 */
	public abstract void updateCell(int cellIndex, AtomicReference<String> symbol, AtomicReference<CellStyle> style);


	// TODO provide several methods:
	// 1. simple (1:1 chars to cells)
	// 2. complex (with the break iterator)
	// 3. standard with the platform break iterator
	public static CodeParagraph fast(int index, String text)
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
			public void updateCell(int cellIndex, AtomicReference<String> symbol, AtomicReference<CellStyle> style)
			{
				char c = text.charAt(cellIndex);
				symbol.set(String.valueOf(c));
			}
			
			
			@Override
			public Color getBackgroundColor()
			{
				return null;
			}


			@Override
			public boolean containsTabs()
			{
				return false;
			}
		};
	}
}
