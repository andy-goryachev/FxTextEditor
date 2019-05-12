// Copyright © 2018-2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import javafx.scene.text.Font;


/**
 * Text (Cell) Metrics.
 */
public class TextMetrics
{
	public final Font font;
	public final double baseline;
	public final int cellWidth;
	public final int cellHeight;
	
	
	public TextMetrics(Font f, double baseline, int cellWidth, int cellHeight)
	{
		this.font = f;
		this.cellHeight = cellHeight;
		this.baseline = baseline;
		this.cellWidth = cellWidth;
	}
}
