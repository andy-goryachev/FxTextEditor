// Copyright © 2024-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxcodeeditor.model;
import javafx.scene.paint.Color;


/**
 * Collects individual cell attributes.
 */
public interface CellStyle
{
	public Color getTextColor();
	
	public Color getBackgroundColor();
	
	public boolean isUnderline();
	
	public boolean isStrikeThrough();
	
	public boolean isBold();
	
	public boolean isItalic();
	
	// TODO squiggle color
	// TODO user data?
}
