// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.ibm.icu;
import goryachev.fxtexteditor.IBreakIterator;
import java.util.Locale;
import com.ibm.icu.text.BreakIterator;


/**
 * IBreakIterator implementation based on com.ibm.icu.text.BreakIterator.
 */
public class IcuBreakIterator
	implements IBreakIterator
{
	private final BreakIterator br;
	
	
	public IcuBreakIterator(Locale locale)
	{
		br = BreakIterator.getCharacterInstance(locale);
	}


	public void setText(String text)
	{
		br.setText(text);
	}


	public int first()
	{
		return br.first();
	}


	public int next()
	{
		int rv = br.next();
		if(rv == BreakIterator.DONE)
		{
			return DONE;
		}
		return rv;
	}
	
	
	public IBreakIterator copy()
	{
		return (IBreakIterator)br.clone();
	}
}
