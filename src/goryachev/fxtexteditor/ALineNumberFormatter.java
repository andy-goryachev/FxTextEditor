// Copyright © 2020-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.fx.Formatters;
import goryachev.fx.FxFormatter;


/**
 * Line Number Formatter Base Class.
 * 
 * This class implements formatting of the left margin ("line numbers")
 * of the editor.  In its simplest form line numbers are formatted. 
 * 
 * The class can be extended to display more than one value - for instance
 * line number and timestamp, for example:
 * 
 * <pre>
 * |  1      0.00|
 * |  2     12.34|
 * | 10   1:23.45|
 * |999  35:00.45|
 * </pre>   
 */
public abstract class ALineNumberFormatter
{
	/**
	 * Formats the line number when hasMultipleColumns() returns false (i.e. this is a single-value formatter).
	 * @param lineNumber - line number, starting with 1
	 * @param maxWidth - total number of cells for line number, obtained by calling this method with all visible rows
	 * @return a non-null String 
	 */
	public abstract String formatLineNumber(int lineNumber);
	
	
	/** 
	 * Returns 1 or the the number of data points (such as line number + timestamp) to be shown in the 
	 * line number margin of the editor.
	 */
	public int getColumnCount() { return 1; }
	
	
	/**
	 * Formats multiple columns.  Individual data columns may have their own alignment (left/right),
	 * the left margin of the editor that displays the data will be the maximal value of the sum of
	 * column widths for each displayed row.
	 * Base class throws an error.
	 */
	public String[] formatMultiColumn(int lineNumber) { throw new Error(); }
	
	
	/** returns true if the specifed column should be right aligned */
	public boolean isRightAlignmentForColumn(int column) { return true; }
	
	
	//
	
	
	public static ALineNumberFormatter getDefault()
	{
		return new ALineNumberFormatter()
		{
			FxFormatter f = Formatters.integerFormatter();
			
			
			@Override
			public String formatLineNumber(int lineNumber)
			{
				return f.format(lineNumber);
			}
		};
	}
}
