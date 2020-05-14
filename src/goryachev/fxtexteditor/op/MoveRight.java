// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.Marker;
import goryachev.fxtexteditor.internal.FlowLine;
import goryachev.fxtexteditor.internal.GlyphIndex;
import goryachev.fxtexteditor.internal.NavigationAction;


/**
 * Moves cursor right.
 */
public class MoveRight
	extends NavigationAction
{
	public MoveRight(Actions a)
	{
		super(a);
	}
	

	protected Marker move(Marker m)
	{
		int pos = m.getCharIndex();
		int line = m.getLine();

		FlowLine fline = vflow().getTextLine(line);
		int gix = fline.getGlyphIndex(pos);
		
		if(gix < fline.getGlyphCount())
		{
			gix++;
		}
		else
		{
			if(line < vflow().getModelLineCount())
			{
				line++;
				gix = 0;
			}
			else
			{
				line = vflow().getModelLineCount();
				gix = 0;
			}
		}
		
		pos = fline.getCharIndex(gix);
		
		return editor().newMarker(line, pos);
	}
}
