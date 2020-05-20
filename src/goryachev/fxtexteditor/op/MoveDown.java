// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.common.log.Log;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.Marker;
import goryachev.fxtexteditor.WrapPos;
import goryachev.fxtexteditor.internal.NavigationAction;
import goryachev.fxtexteditor.internal.WrapInfo;


/**
 * Moves the cursor down.
 */
public class MoveDown
	extends NavigationAction
{
	protected static final Log log = Log.get("MoveDown");
	
	
	public MoveDown(Actions a)
	{
		super(a);
	}
	

	protected Marker move(Marker m)
	{
		int pos = m.getCharIndex();
		int line = m.getLine();
		int col = updatePhantomColumn(line, pos);
		
		WrapInfo wr = wrapInfo(line);
		int wrapRow = wr.getWrapRowForCharIndex(pos);
		
		WrapPos wp = vflow().navigate(line, wrapRow, 1, true);
		
		int newLine = wp.getLine();
		int newWrapRow = wp.getRow();
		
		wr = wrapInfo(newLine);
		int newPos = wr.getCharIndexForColumn(newWrapRow, col);
		
		log.debug("col=%d line=%d pos=%d", col, line, pos);
		
		return editor().newMarker(newLine, newPos);
	}
}
