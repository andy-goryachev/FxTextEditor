// Copyright © 2020-2021 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.Marker;
import goryachev.fxtexteditor.internal.FlowLine;
import goryachev.fxtexteditor.internal.GlyphIndex;
import goryachev.fxtexteditor.internal.NavigationAction;


/**
 * Moves cursor Left.
 */
public class MoveLeft
	extends NavigationAction
{
	public MoveLeft(Actions a)
	{
		super(a);
	}
	

	protected Marker move(Marker m)
	{
		int pos = m.getCharIndex();
		int line = m.getLine();

		FlowLine fline = vflow().getTextLine(line);
		int gix = fline.getGlyphIndex(pos);
		
		if(gix > 0)
		{
			gix--;
		}
		else
		{
			if(line > 0)
			{
				--line;
				fline = vflow().getTextLine(line); 
				gix = fline.getGlyphCount();
			}
			else
			{
				// no-op
			}
		}
		
		pos = fline.getCharIndex(gix);

		setPhantomColumn(line, pos);
		
		return editor().newMarker(line, pos);
	}
}
