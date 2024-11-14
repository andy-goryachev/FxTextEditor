// Copyright © 2024-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.codepad;


/**
 * Selection Range.
 */
public final class SelectionRange
{
    private final TextPos min;
    private final TextPos max;
    private final boolean caretAtMin;
    
    
    private SelectionRange(TextPos min, TextPos max, boolean caretAtMin)
    {
    	this.min = min;
    	this.max = max;
    	this.caretAtMin = caretAtMin;
    }
    
    
    public TextPos getAnchor()
    {
    	return caretAtMin ? max : min;
    }
    
    
    public TextPos getCaret()
    {
    	return caretAtMin ? min : max;
    }
    
    
    public TextPos getMax()
    {
    	return max;
    }
    
    
    public TextPos getMin()
    {
    	return min;
    }
}
