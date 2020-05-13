// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.common.log.Log;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.Marker;
import goryachev.fxtexteditor.internal.FlowLine;
import goryachev.fxtexteditor.internal.GlyphIndex;
import goryachev.fxtexteditor.internal.NavigationAction;
import goryachev.fxtexteditor.internal.WrapInfo;


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
		WrapInfo wr = wrapInfo(line);
		
		int col = updatePhantomColumn(line, pos);
		
		if(isWrapLines())
		{
			int wrapRow = wr.getWrapRowForCharIndex(pos);
			if(wrapRow > 0)
			{
				pos = wr.getCharIndexForColumn(wrapRow - 1, col);
			}
			else
			{
				if(line == 0)
				{
					return null;
				}
				
				--line;
				wr = wrapInfo(line);
				wrapRow = wr.getWrapRowCount() - 1;
				pos = wr.getCharIndexForColumn(wrapRow, col);
			}
		}
		else
		{
			if(line == 0)
			{
				return null;
			}
			
			--line;
			wr = wrapInfo(line);
			pos = wr.getCharIndexForColumn(0, col);
		}

		log.debug("col=%d line=%d pos=%d", col, line, pos);
		
		return editor().newMarker(line, pos);
	}
}
