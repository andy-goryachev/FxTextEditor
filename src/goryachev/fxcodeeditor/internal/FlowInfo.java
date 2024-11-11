// Copyright © 2024-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxcodeeditor.internal;


/**
 * Flow Info: the FlowCell arrangement around the view port.
 */
public class FlowInfo
{
	public FlowInfo()
	{
	}

	
	public boolean isCanvasDifferent(FlowInfo previous)
	{
		if(previous == null)
		{
			return true;
		}
		
		// TODO is w,h different?
		return true;
	}
}
