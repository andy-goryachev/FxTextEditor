// Copyright © 2020-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;


/**
 * Text Source Interface for Copy/Save Operations.
 */
public interface ITextSource
{
	/** 
	 * returns next text line, also setting start and end indexes.
	 * returns null when no more text is available (start and end values will be undefined)
	 */
	public ITextLine nextLine();
	
	/** 
	 * returns next plain text line, also setting start and end indexes.
	 * returns null when no more text is available (start and end values will be undefined)
	 */
	public String nextPlainTextLine();
	
	/** after a non-null nextLine(), returns the text line start char index */
	public int getStart();
	
	/** after a non-null nextLine(), returns the text line end char index */
	public int getEnd();
}
