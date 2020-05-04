// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.Marker;
import goryachev.fxtexteditor.internal.FlowLine;
import goryachev.fxtexteditor.internal.GlyphIndex;
import goryachev.fxtexteditor.internal.NavDirection;
import goryachev.fxtexteditor.internal.NavigationAction;


/**
 * Moves Cursor(s) Left.
 */
public class MoveLeft
	extends NavigationAction
{
	public MoveLeft(Actions a)
	{
		super(a, NavDirection.LEFT);
	}
	

	protected Marker move(Marker m)
	{
		int pos = m.getCharIndex();
		int line = m.getLine();

		FlowLine fline = vflow().getTextLine(line);
		GlyphIndex gix = fline.getGlyphIndex(pos);
		
		if(gix.intValue() > 0)
		{
			gix = gix.decrement();
		}
		else
		{
			if(line > 0)
			{
				--line;
				fline = vflow().getTextLine(line); 
				gix = GlyphIndex.of(fline.getGlyphCount());
			}
			else
			{
				// no-op
			}
		}
		
		pos = fline.getCharIndex(gix);
		
		return editor().newMarker(line, pos);
	}
}
