// Copyright © 2019-2022 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.ITextLine;


/**
 * Text Layout on canvas.
 */
public class TLayout
{
	private final int width;
	private final ITextLine[] lines;
	private final int[] offsets;
	private boolean[] selection;
	private boolean[] carets;
	
	
	public TLayout(int width, ITextLine[] lines, int[] offsets)
	{
		this.width = width;
		this.lines = lines;
		this.offsets = offsets;
	}
}
