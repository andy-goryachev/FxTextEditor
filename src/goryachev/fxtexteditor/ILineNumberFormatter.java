// Copyright © 2020-2021 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.fx.Formatters;
import goryachev.fx.FxFormatter;


/**
 * Line Number Formatter Interface.
 * 
 * This facility can also be used for rendering more than just the line numbers.
 */
public interface ILineNumberFormatter
{
	/**
	 * @param lineNumber - line number, starting with 1
	 * @return a non-null String 
	 */
	public String formatLineNumber(int lineNumber);
	
	
	//
	
	
	public static ILineNumberFormatter getDefault()
	{
		return new ILineNumberFormatter()
		{
			FxFormatter f = Formatters.integerFormatter();
			
			
			public String formatLineNumber(int lineNumber)
			{
				return f.format(lineNumber);
			}
		};
	}
}
