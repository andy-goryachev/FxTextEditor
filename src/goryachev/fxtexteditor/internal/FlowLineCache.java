// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.util.CList;
import goryachev.common.util.CMap;
import goryachev.common.util.text.IBreakIterator;
import goryachev.fxtexteditor.FxTextEditor;
import goryachev.fxtexteditor.ITextLine;
import java.util.Random;


/**
 * Fixed Size Cache, not synchronized.
 */
public class FlowLineCache
{
	private final FxTextEditor editor;
	private final int capacity;
	private final CMap<Integer,FlowLine> cache;
	private final CList<Integer> keys;
	
	
	public FlowLineCache(FxTextEditor editor, int capacity)
	{
		if(capacity <= 8)
		{
			throw new Error("capacity too small: " + capacity);
		}
	
		this.editor = editor;
		this.capacity = capacity;
		cache = new CMap(capacity);
		keys = new CList(capacity);
	}
	
	
	public int size()
	{
		return cache.size();
	}
	

	public FlowLine get(int key)
	{
		return cache.get(key);
	}
	
	
	protected void evict()
	{
		int ix = new Random().nextInt(size());
		Integer key = keys.get(ix);
		Integer moved = keys.removeLast();
		if(moved != null)
		{
			if(ix == (size() - 1))
			{
				keys.add(moved);
			}
			else
			{
				keys.set(ix, moved);
			}
		}
		cache.remove(key);
	}
	
	
	public FlowLine insert(int key, ITextLine t)
	{
		if(size() >= (capacity - 1))
		{
			evict();
		}
		
		keys.add(key);
		
		FlowLine f = new FlowLine(t, createInfo(t));
		cache.put(key, f);
		return f;
	}
	
	
	public void clear()
	{
		keys.clear();
		cache.clear();
	}
	
	
	protected TextGlyphInfo createInfo(ITextLine t)
	{
		if(t == null)
		{
			return TextGlyphInfo.BLANK;
		}
		
		String text = t.getPlainText();
		return TextGlyphInfo.create(text, breakIterator());
	}


	protected IBreakIterator breakIterator()
	{
		// TODO if model is simple, return null
		// else get iterator (from model?)
		return null;
	}
}
