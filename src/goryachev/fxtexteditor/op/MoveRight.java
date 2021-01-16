// Copyright © 2020-2021 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.FxTextEditor;
import goryachev.fxtexteditor.Marker;
import goryachev.fxtexteditor.internal.FlowLine;
import goryachev.fxtexteditor.internal.NavigationAction;


/**
 * Moves cursor right.
 */
public class MoveRight
	extends NavigationAction
{
	public MoveRight(FxTextEditor ed)
	{
		super(ed);
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
			if(line < (vflow().getModelLineCount() - 1))
			{
				line++;
				gix = 0;
			}
			else
			{
				line = vflow().getModelLineCount() - 1;
				gix = fline.getGlyphCount();
			}
		}
		
		pos = fline.getCharIndex(gix);
		
		setPhantomColumn(line, pos);
		
		return editor().newMarker(line, pos);
	}
}
