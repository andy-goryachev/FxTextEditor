// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;


/**
 * Text Layout on canvas.
 */
public class TLayout
{
	private final int width;
	private final TextCells[] lines;
	private final int[] offsets;
	private boolean[] selection;
	private boolean[] carets;
	
	
	public TLayout(int width, TextCells[] lines, int[] offsets)
	{
		this.width = width;
		this.lines = lines;
		this.offsets = offsets;
	}
}
