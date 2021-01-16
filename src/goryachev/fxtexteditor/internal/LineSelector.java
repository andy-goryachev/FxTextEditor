// Copyright © 2020-2021 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.FxTextEditor;
import goryachev.fxtexteditor.Marker;
import java.util.function.BiConsumer;


/**
 * Line Selector.
 */
public class LineSelector
	implements BiConsumer<FxTextEditor,Marker>
{
	public LineSelector()
	{
	}
	
	
	public void accept(FxTextEditor ed, Marker m)
	{
		selectLine(ed, m);
	}
	

	public void selectLine(FxTextEditor ed, Marker m)
	{
		if(m != null)
		{
			int line = m.getLine();
			
			int endLine = line + 1;
			int endPos;
			
			if(endLine >= ed.getLineCount())
			{
				endPos = ed.getTextLength(line);
				endLine = line;
			}
			else
			{
				endPos = 0;
			}
			
			ed.select(line, 0, endLine, endPos);
		}
	}
}
