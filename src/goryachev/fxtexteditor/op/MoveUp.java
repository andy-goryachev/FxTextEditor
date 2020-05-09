// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.Marker;
import goryachev.fxtexteditor.internal.NavigationAction;


/**
 * Moves cursor Up.
 */
public class MoveUp
	extends NavigationAction
{
	public MoveUp(Actions a)
	{
		super(a);
	}
	

	protected Marker move(Marker m)
	{
		int pos = m.getCharIndex();
		int line = m.getLine();

		// TODO
//		FlowLine fline = vflow().getTextLine(line);
//		GlyphIndex gix = fline.getGlyphIndex(pos);
//		
//		// TODO phantom column
//		
//		// compute line + row.start
//		WrapInfo wr = vflow().getWrapInfo(fline);
		
		return editor().newMarker(line, pos);
	}
}
