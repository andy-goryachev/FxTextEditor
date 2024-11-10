// Copyright © 2024-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxcodeeditor.internal;
import goryachev.fxcodeeditor.SelectionRange;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;


/**
 * Selection Model.
 */
public class SelectionModel
{
    private final ReadOnlyObjectWrapper<SelectionRange> segment = new ReadOnlyObjectWrapper<>();
    
    
	public SelectionModel()
	{
	}


	public void clear()
	{
	}


	public ReadOnlyProperty<SelectionRange> selectionProperty()
	{
		return null;
	}


	public SelectionRange getSelection()
	{
		return null;
	}
}
