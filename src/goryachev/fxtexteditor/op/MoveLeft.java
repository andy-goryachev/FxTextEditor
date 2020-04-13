// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.EditorSelection;
import goryachev.fxtexteditor.Marker;
import goryachev.fxtexteditor.SelectionSegment;
import goryachev.fxtexteditor.internal.FlowLine;
import goryachev.fxtexteditor.internal.GlyphIndex;


/**
 * Moves Cursor(s) Left.
 */
public class MoveLeft
	extends EditorAction
{
	public MoveLeft(Actions a)
	{
		super(a);
	}
	
	
	protected void action()
	{
		moveLeft();
	}
	
	
	public void moveLeft()
	{
		vflow().setSuppressBlink(true);
		
		try
		{
			EditorSelection sel = editor().getSelection();
			int sz = sel.getSegmentCount();
			
			selector().clear();

			for(SelectionSegment seg: sel)
			{
				Marker from = seg.getCaret();
				Marker to = moveLeft(from);
				
				selector().addSelectionSegment(to, to);
			}
			
			selector().commitSelection();
		}
		finally
		{
			vflow().setSuppressBlink(false);
			vflow().scrollSelectionToVisible();
		}
	}


	protected Marker moveLeft(Marker m)
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
