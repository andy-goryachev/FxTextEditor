// Copyright © 2017-2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.util.WeakList;
import goryachev.fxtexteditor.Marker;


/**
 * Maintains weak list of Markers.
 * This editor-specific class is needed to allow for marker adjustment after an editing operation.
 */
public class Markers
{
	private final WeakList<Marker> markers;
	
	
	public Markers(int size)
	{
		markers = new WeakList<Marker>();
	}


	public Marker newMarker(int lineNumber, int charIndex)
	{
		Marker m = new Marker(this, lineNumber, charIndex);
		markers.add(m);
		
		if(markers.size() > 1_000_000)
		{
			markers.gc();
			
			if(markers.size() > 1_000_000)
			{	
				throw new Error("too many markers");
			}
		}
		
		return m;
	}
	
	
	public void clear()
	{
		markers.clear();
	}
	
	
	public void update(int startLine, int startPos, int startCharsInserted, int linesInserted, int endLine, int endPos, int endCharsInserted)
	{
		for(int i=markers.size()-1; i>=0; --i)
		{
			Marker m = markers.get(i);
			if(m == null)
			{
				markers.remove(i);
			}
			else
			{
				if(m.isBefore(startLine, startPos))
				{
					// unchanged
				}
				else if(m.isAfter(endLine, endPos))
				{
					// shift
					if(endLine == m.getLine())
					{
						// marker on the end line 
						int charDelta = endCharsInserted - (endPos - startPos);
						m.movePosition(charDelta);
					}

					int lineDelta = linesInserted - (endLine - startLine);
					m.moveLine(lineDelta);
				}
				else
				{
					// reset to start
					m.reset(startLine, startPos);
				}
			}
		}
	}
}
