// Copyright © 2024-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxcodeeditor.model;
import javafx.scene.paint.Color;


/**
 * Collects individual cell attributes.
 */
public abstract class CellInfo
{
	public abstract void setText(String text);
	
	public abstract void setColor(Color fg);
	
	public abstract void setBackground(Color bg);
	
	public abstract void setUnderline(boolean on);
	
	public abstract void setStrikeThrough(boolean on);
	
	public abstract void setBold(boolean on);
	
	public abstract void setItalic(boolean on);
	
	// TODO squiggle color
	// TODO user object?
}
