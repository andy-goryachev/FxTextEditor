// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.ITabPolicy;


/**
 * Tab Policy.
 */
public class TabPolicy
	implements ITabPolicy
{
	private final int tabWidth;
	
	
	public TabPolicy(int tabWidth)
	{
		this.tabWidth = tabWidth;
	}
	
	
	public static TabPolicy create(int tabWidth)
	{
		return new TabPolicy(tabWidth);
	}


	public int distanceToNextTabStop(int offset)
	{
		int rv = tabWidth - (offset % tabWidth);
		return rv == 0 ? tabWidth : rv;
	}
}
