// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import java.text.BreakIterator;


/**
 * BreakIterator interface.
 */
public interface IBreakIterator
{
	public static final int DONE = -1;
	
	//
	
	public void setText(String text);

	public int first();

	public int next();
	
	public IBreakIterator copy();
	
	//

	/** 
	 * wraps a standard java.util.BreakIterator instance.
	 * it is recommended to use com.ibm.icu.text.BreakIterator instead because
	 * the stock java one is not complete (emoji!)
	 */
	public static IBreakIterator wrap(BreakIterator b)
	{
		return new IBreakIterator()
		{
			public void setText(String text)
			{
				b.setText(text);
			}


			public int first()
			{
				return b.first();
			}


			public int next()
			{
				int rv = b.next();
				if(rv == BreakIterator.DONE)
				{
					return DONE;
				}
				return rv;
			}
			
			
			public IBreakIterator copy()
			{
				return (IBreakIterator)b.clone();
			}
		};
	}
}
