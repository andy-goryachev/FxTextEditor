// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;


/**
 * retains wrapping information (screen text rows) for the given FlowLine.
 */
public class WrapInfo
{
	private final int width;
	
	
	public WrapInfo(int width)
	{
		this.width = width;
	}
	
	
	public int getWidth()
	{
		return width;
	}
}
