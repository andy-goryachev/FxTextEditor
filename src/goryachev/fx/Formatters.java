// Copyright © 2017-2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fx;


/**
 * Standard Formatters.
 */
public class Formatters
{
	private static FxDecimalFormatter integerFormatter;
	
	
	public static FxDecimalFormatter integerFormatter()
	{
		if(integerFormatter == null)
		{
			integerFormatter = new FxDecimalFormatter("#,##0");
		}
		return integerFormatter;
	}
}
