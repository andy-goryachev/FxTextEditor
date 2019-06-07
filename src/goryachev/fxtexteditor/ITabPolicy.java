// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;


/**
 * Tab Policy Interface.
 */
public interface ITabPolicy
{
	public int distanceToNextTabStop(int offset);
}
