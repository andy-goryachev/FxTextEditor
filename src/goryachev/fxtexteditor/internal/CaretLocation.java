// Copyright © 2016-2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;


/**
 * Enapsulates local caret coordinates.  
 */
public class CaretLocation
{
	public final int x;
	public final int y;
	

	public CaretLocation(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	
	public String toString()
	{
		return "(" + x + "," + y + ")";
	}
}
