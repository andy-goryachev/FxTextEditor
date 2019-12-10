// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;


/**
 * Nearest Position.
 */
public class NearestPos
{
	public final int charIndex;
	public final boolean leading;
	
	
	public NearestPos(int charIndex, boolean leading)
	{
		this.charIndex = charIndex;
		this.leading = leading;
	}
	
	
	public int getCharOffset()
	{
		return leading ? charIndex : charIndex + 1;
	}
}
