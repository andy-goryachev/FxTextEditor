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
		
		WrapPos wp = vflow().advance(line, wrapRow, 1);
		int newLine = wp.getLine();
		int newWrapRow = wp.getRow();
		
		WrapInfo wr2 = wrapInfo(newLine);
		int newPos = wr2.getCharIndexForColumn(newWrapRow, col);
		
		log.debug("col=%d line=%d pos=%d", col, newLine, newPos);
		
		return editor().newMarker(newLine, newPos);
	}
}
