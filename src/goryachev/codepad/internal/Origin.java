// Copyright © 2024-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.codepad.internal;


/**
 * Viewport Origin.
 */
public record Origin(int index, int glyphIndex)
{
	public static final Origin ZERO = new Origin(0, 0);
}
