// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.common.log.Log;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.Marker;
import goryachev.fxtexteditor.internal.FlowLine;
import goryachev.fxtexteditor.internal.GlyphIndex;
import goryachev.fxtexteditor.internal.NavigationAction;


/**
 * Moves cursor up.
 */
public class MoveUp
	extends NavigationAction
{
	protected static final Log log = Log.get("MoveUp");

	
	public MoveUp(Actions a)
	{
		super(a);
	}
	

	protected Marker move(Marker m)
	{
		int pos = m.getCharIndex();
		int line = m.getLine();
		
		int col = updatePhantomColumn(line, pos);
		
		if(isWrapLines())
		{
			// TODO
//			FlowLine fline = vflow().getTextLine(line);
//			GlyphIndex gix = fline.getGlyphIndex(pos);
//			
//			// TODO phantom column
//			
//			// compute line + row.start
//			WrapInfo wr = vflow().getWrapInfo(fline);
		}
		else
		{
			if(line > 0)
			{
				--line;
			}
			
			int x = col - getTopCellIndex();
			
			// FIX scroll to visible may be needed here
			GlyphIndex gix = vflow().screenColumnToGlyphIndex(line, col);
			
			FlowLine fline = vflow().getTextLine(line);
			pos = fline.getCharIndex(gix);
		}

		log.debug("col=%d line=%d pos=%d", col, line, pos);
		
		return editor().newMarker(line, pos);
	}
}
