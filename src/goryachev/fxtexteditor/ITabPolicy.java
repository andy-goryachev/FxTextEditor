// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;


/**
 * Tab Policy Interface.
 */
public interface ITabPolicy
{
	/** returns true if no tab expansion is required (tab width == 1) */
	public boolean isSimple();
	
	
	// TODO or this?
	//public int distanceToNextTabStop(int offset);
	
	
	/** returns the next tab stop position */
	public int nextTabStop(int position);
}
