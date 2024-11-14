// Copyright © 2024-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.codepad.internal;
import goryachev.codepad.SelectionRange;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;


/**
 * Selection Model.
 */
public final class SelectionModel
{
    private final ReadOnlyObjectWrapper<SelectionRange> range = new ReadOnlyObjectWrapper<>();
    
    
	public SelectionModel()
	{
	}


	public void clear()
	{
	}


	public ReadOnlyProperty<SelectionRange> selectionProperty()
	{
		return range.getReadOnlyProperty();
	}


	public SelectionRange getSelection()
	{
		return range.get();
	}
}
