// Copyright © 2020-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.FxTextEditorModel;
import goryachev.fxtexteditor.ITextLine;
import goryachev.fxtexteditor.ITextSource;


/**
 * Contiguous Selected Text Source.  
 */
public class SelectedTextSource
	implements ITextSource
{
	private final FxTextEditorModel model;
	private final int startLine;
	private final int startPos;
	private final int endLine;
	private final int endPos;
	private int current;
	private int start;
	private int end;
	
	
	public SelectedTextSource(FxTextEditorModel m, int startLine, int startPos, int endLine, int endPos)
	{
		this.model = m;
		this.startLine = startLine;
		this.startPos = startPos;
		this.endLine = endLine;
		this.endPos = endPos;
		this.current = startLine;
	}
	
	
	public String nextPlainTextLine()
	{
		if(current > endLine)
		{
			start = -1;
			end = -1;
			return null;
		}
		
		String t = model.getPlainText(current);
		if(t == null)
		{
			t = "";
		}
		
		if(current == startLine)
		{
			if(startLine == endLine)
			{
				start = startPos;
				end = endPos;
			}
			else
			{
				start = startPos;
				end = t.length();
			}
		}
		else if(current < endLine)
		{
			start = 0;
			end = t.length();
		}
		else
		{
			start = 0;
			end = endPos;
		}
		
		current++;
		return t;
	}
	
	
	public ITextLine nextLine()
	{
		if(current > endLine)
		{
			start = -1;
			end = -1;
			return null;
		}
		
		ITextLine t = model.getTextLine(current);
		
		if(current == startLine)
		{
			if(startLine == endLine)
			{
				start = startPos;
				end = endPos;
			}
			else
			{
				start = startPos;
				end = t.getTextLength();
			}
		}
		else if(current < endLine)
		{
			start = 0;
			end = t.getTextLength();
		}
		else
		{
			start = 0;
			end = endPos;
		}
		
		current++;
		return t;
	}
	

	public int getStart()
	{
		return start;
	}


	public int getEnd()
	{
		return end;
	}
}
