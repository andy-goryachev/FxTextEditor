// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.op;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.EditorSelection;
import goryachev.fxtexteditor.Marker;
import goryachev.fxtexteditor.SelectionSegment;
import goryachev.fxtexteditor.internal.FlowLine;
import goryachev.fxtexteditor.internal.GlyphIndex;


/**
 * Moves Cursor(s) Right.
 */
public class MoveRight
	extends EditorAction
{
	public MoveRight(Actions a)
	{
		super(a);
	}
	
	
	protected void action()
	{
		moveRight();
	}
	
	
	public void moveRight()
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
				Marker to = moveRight(from);
				
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


	protected Marker moveRight(Marker m)
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
