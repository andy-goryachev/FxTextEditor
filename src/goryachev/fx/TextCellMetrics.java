// Copyright © 2018-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fx;
import javafx.scene.text.Font;


/**
 * Monospaced Text Cell Metrics.
 */
public final class TextCellMetrics
{
	public final Font font;
	public final double baseline;
	public final double cellWidth;
	public final double cellHeight;
	
	
	public TextCellMetrics(Font font, double baseline, double cellWidth, double cellHeight)
	{
		this.font = font;
		this.cellHeight = cellHeight;
		this.baseline = baseline;
		this.cellWidth = cellWidth;
	}
}
