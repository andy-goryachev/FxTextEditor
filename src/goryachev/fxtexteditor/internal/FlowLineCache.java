// Copyright © 2019-2021 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.log.Log;
import goryachev.common.util.CMap;
import goryachev.common.util.text.IBreakIterator;
import goryachev.fxtexteditor.FxTextEditor;
import goryachev.fxtexteditor.ITextLine;
import java.util.Iterator;
import java.util.Random;


/**
 * Fixed Size Cache, not synchronized.
 */
public class FlowLineCache
{
	protected static final Log log = Log.get("FlowLineCache");
	private final FxTextEditor editor;
	private final int capacity;
	private final CMap<Integer,FlowLine> cache;
	private final Random random = new Random();
	private IBreakIterator breakIterator;
	
	
	public FlowLineCache(FxTextEditor editor, int capacity)
	{
		if(capacity <= 8)
		{
			throw new Error("capacity too small: " + capacity);
		}
	
		this.editor = editor;
		this.capacity = capacity;
		cache = new CMap(capacity);
	}
	
	
	public void setBreakIterator(IBreakIterator b)
	{
		breakIterator = b;
	}
	

	public FlowLine get(int key)
	{
		return cache.get(key);
	}
	
	
	protected void prune()
	{
		Iterator<FlowLine> it = cache.values().iterator();
		while(it.hasNext())
		{
			it.next();
			boolean remove = random.nextBoolean();
			if(remove)
			{
				it.remove();
			}
		}
	}
	
	
	public FlowLine insert(int key, ITextLine t)
	{
		if(cache.size() >= (capacity - 1))
		{
			prune();
		}
		
		FlowLine f = new FlowLine(t, createInfo(t));
		cache.put(key, f);
		return f;
	}
	
	
	public void clear()
	{
		cache.clear();
	}
	
	
	protected AGlyphInfo createInfo(ITextLine t)
	{
		if(t == null)
		{
			return AGlyphInfo.BLANK;
		}
		
		String text = t.getPlainText();
		return AGlyphInfo.create(text, breakIterator);
	}


	// TODO O(N) operation
	public void invalidate(int startIndex, int endIndex, int linesInserted)
	{
		log.debug("start=%d, end=%d, ins=%d", startIndex, endIndex, linesInserted);
		
		int end;
		if((endIndex - startIndex) == linesInserted)
		{
			end = endIndex;
		}
		else
		{
			end = Integer.MAX_VALUE;
		}
		
		log.trace("end=%d", end);
		
		Iterator<FlowLine> it = cache.values().iterator();
		while(it.hasNext())
		{
			FlowLine fline = it.next();
			int line = fline.getModelIndex();
			if((line >= startIndex) && (line <= end))
			{
				it.remove();
				log.trace("removed: %d", line);
			}
		}
	}
}
