// Copyright © 2017-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.FxTextEditor;
import goryachev.fxtexteditor.Marker;
import java.util.function.BiConsumer;


/**
 * Simple Word Selector uses whitespace and punctuation to delimit words.
 * Does not handle surrogate pairs.
 * 
 * FIX use break iterator, glyphs
 */
public class SimpleWordSelector
	implements BiConsumer<FxTextEditor,Marker>
{
	public SimpleWordSelector()
	{
	}
	
	
	protected boolean isWordChar(int c)
	{
		return Character.isLetterOrDigit(c);
	}
	
	
	protected int skipWordCharsForward(String text, int start)
	{
		int len = text.length();
		for(int i=start; i<len; i++)
		{
			// TODO surrogate
			char c = text.charAt(i);
			if(!isWordChar(c))
			{
				return i;
			}
		}
		return len;
	}
	
	
	protected int skipNonWordCharsForward(String text, int start)
	{
		int len = text.length();
		for(int i=start; i<len; i++)
		{
			// TODO surrogate
			char c = text.charAt(i);
			if(isWordChar(c))
			{
				return i;
			}
		}
		return len;
	}
	
	
	protected int skipWordCharsBackward(String text, int start)
	{
		for(int i=start; i>=0; i--)
		{
			// TODO surrogate
			char c = text.charAt(i);
			if(!isWordChar(c))
			{
				return i;
			}
		}
		// this is legitimate offset
		return -1;
	}
	
	
	protected int skipNonWordCharsBackward(String text, int start)
	{
		if(start < text.length())
		{
			for(int i=start; i>=0; i--)
			{
				// TODO surrogate
				char c = text.charAt(i);
				if(isWordChar(c))
				{
					return i;
				}
			}
		}
		// this is legitimate offset
		return -1;
	}
	

	@Override
	public void accept(FxTextEditor ed, Marker m)
	{
		int line = m.getLine();
		String text = ed.getPlainText(line);
		if(text == null)
		{
			return;
		}
		
		int len = ed.getTextLength(line);
		if(len == 0)
		{
			return;
		}

		int pos = m.getCharIndex();
		int start;
		int end;
		
		if(pos < text.length() && isWordChar(text.charAt(pos)))
		{
			start = skipWordCharsBackward(text, pos) + 1;
			end = skipWordCharsForward(text, pos);
			
			ed.select(line, start, line, end);
		}
	}
}
