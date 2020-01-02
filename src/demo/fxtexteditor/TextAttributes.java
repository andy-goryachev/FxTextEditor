// Copyright © 2019-2020 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import goryachev.fxtexteditor.CellStyles;
import java.util.BitSet;
import javafx.scene.paint.Color;


/**
 * Uncompressed Text Attributes for the whole line.
 */
public class TextAttributes
{
	private final Color[] fg;
	private final Color[] bg;
	private BitSet bold;
	
	
	public TextAttributes(int size)
	{
		fg = new Color[size];
		bg = new Color[size];
		bold = new BitSet(size);
	}
	
	
	public int size()
	{
		return fg.length;
	}


	public void addSegment(int start, Segment seg)
	{
		for(int i=0; i<seg.length(); i++)
		{
			int ix = start + i;
			fg[ix] = seg.textColor;
			bg[ix] = seg.backgroundColor;
			bold.set(ix, seg.bold);
		}
	}


	public void update(CellStyles styles, int charIndex)
	{
		if(charIndex < size())
		{
			styles.update
			(
				bg[charIndex], 
				fg[charIndex],
				bold.get(charIndex),
				false, // italic
				false, // strikeThrough
				false  // s.underscore
			);
		}
	}
}
