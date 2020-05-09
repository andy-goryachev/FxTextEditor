// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.Marker;
import goryachev.fxtexteditor.internal.FlowLine;
import goryachev.fxtexteditor.internal.GlyphIndex;
import goryachev.fxtexteditor.internal.NavDirection;
import goryachev.fxtexteditor.internal.NavigationAction;


/**
 * Moves cursor right.
 */
public class MoveRight
	extends NavigationAction
{
	public MoveRight(Actions a)
	{
		super(a, NavDirection.RIGHT);
	}
	

	protected Marker move(Marker m)
	{
		int pos = m.getCharIndex();
		int line = m.getLine();

		FlowLine fline = vflow().getTextLine(line);
		GlyphIndex gix = fline.getGlyphIndex(pos);
		
		if(gix.intValue() < fline.getGlyphCount())
		{
			gix = gix.increment();
		}
		else
		{
			if(line < vflow().getModelLineCount())
			{
				line++;
				gix = GlyphIndex.ZERO;
			}
			else
			{
				line = vflow().getModelLineCount();
				gix = GlyphIndex.ZERO;
			}
		}
		
		pos = fline.getCharIndex(gix);
		
		return editor().newMarker(line, pos);
	}
}
