// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.log.Log;
import goryachev.fxtexteditor.GlyphPos;
import goryachev.fxtexteditor.VFlow;


/**
 * Assists with vertical movement in wrapping mode.
 */
@Deprecated // use VFlow.navigate 
public class WrapAssist
{
	protected static final Log log = Log.get("WrapAssist");
	private final VFlow vflow;
	private final int startLine;
	private final int charIndex;
	
	
	public WrapAssist(VFlow vflow, int line, int charIndex)
	{
		this.vflow = vflow;
		this.startLine = line;
		this.charIndex = charIndex;
	}
	

	/** returns new origin */ 
	public GlyphPos move(int delta)
	{
		int line = startLine;
		FlowLine fline = vflow.getTextLine(line);
		int gix = fline.getGlyphIndex(charIndex);
		WrapInfo wr = vflow.getWrapInfo(fline);
		int row = wr.findRowForGlyphIndex(gix);
		
		int toSkip = Math.abs(delta);
		int pos = 0;
		
		if(delta < 0)
		{
			while(toSkip > 0)
			{
				if(row < toSkip)
				{
					toSkip -= row;
					line--;
					
					fline = vflow.getTextLine(line);
					wr = vflow.getWrapInfo(fline);
					row = wr.getWrapRowCount() - 1;
				}
				else
				{
					pos = wr.getGlyphIndexForRow_DELETE(row - toSkip);
					break;
				}
			}
		}
		else
		{
			int ct = wr.getWrapRowCount() - row;
			
			while(toSkip > 0)
			{
				if(ct < toSkip)
				{
					toSkip -= ct;
					line++;
					
					fline = vflow.getTextLine(line);
					wr = vflow.getWrapInfo(fline);
					ct = wr.getWrapRowCount();
				}
				else
				{
					pos = wr.getGlyphIndexForRow_DELETE(toSkip);
					break;
				}
			}
		}
		
		log.debug("start=%d,%s delta=%d, move: %d,%s", startLine, gix, delta, line, pos);
		
		return new GlyphPos(line, pos);
	}
}
