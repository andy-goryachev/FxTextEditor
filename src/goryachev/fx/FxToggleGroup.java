// Copyright © 2020-2021 Andy Goryachev <andy@goryachev.com>
package goryachev.fx;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;


/**
 * Slightly more convenient ToggleGroup.
 */
public class FxToggleGroup
	extends ToggleGroup
{
	public FxToggleGroup(ToggleButton ... buttons)
	{
		for(ToggleButton b: buttons)
		{
			b.setToggleGroup(this);
		}
	}

	
	public FxToggleGroup()
	{
	}
	
	
	public void add(ToggleButton b)
	{
		b.setToggleGroup(this);
	}
}
