// Copyright © 2020-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal.rtf;
import goryachev.common.util.CList;
import goryachev.common.util.CMap;
import java.util.List;
import javafx.scene.paint.Color;


/**
 * Color Table.
 */
public class ColorTable
{
	private final CList<Color> colors = new CList();
	private final CMap<Color,String> indexes = new CMap();
	
	
	public ColorTable()
	{
	}
	
	
	public void add(Color c)
	{
		if(!indexes.containsKey(c))
		{
			colors.add(c);
			indexes.put(c, String.valueOf(colors.size()));
		}
	}


	public String getIndexFor(Color c)
	{
		return indexes.get(c);
	}
	
	
	public List<Color> getColors()
	{
		return colors;
	}
}