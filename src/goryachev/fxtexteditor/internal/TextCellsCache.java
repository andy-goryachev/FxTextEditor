// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.util.CList;
import goryachev.common.util.CMap;
import java.util.Random;


/**
 * Fixed Size TextCells Cache, not synchronized.
 */
public class TextCellsCache
{
	private final int capacity;
	private final CMap<Integer,TextCells> cache;
	private final CList<Integer> keys;
	
	
	public TextCellsCache(int capacity)
	{
		if(capacity <= 8)
		{
			throw new Error("capacity too small: " + capacity);
		}
		
		this.capacity = capacity;
		cache = new CMap(capacity);
		keys = new CList(capacity);
	}
	
	
	public int size()
	{
		return cache.size();
	}
	

	public TextCells get(int key)
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
			keys.set(ix, moved);
		}
		cache.remove(key);
	}
	
	
	public TextCells put(int key, TextCells value)
	{
		if(size() >= (capacity - 1))
		{
			evict();
		}
		
		keys.add(key);
		return cache.put(key, value);
	}
	
	
	public void clear()
	{
		keys.clear();
		cache.clear();
	}
}
