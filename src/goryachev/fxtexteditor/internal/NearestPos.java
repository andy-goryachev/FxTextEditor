// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;


/**
 * Nearest Position.
 */
public class NearestPos
{
	public final int offset;
	public final boolean leading;
	
	
	public NearestPos(int offset, boolean leading)
	{
		this.offset = offset;
		this.leading = leading;
	}
}
