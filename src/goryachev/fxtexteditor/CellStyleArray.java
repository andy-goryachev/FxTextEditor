// Copyright © 2020-2021 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.fx.FX;
import javafx.scene.paint.Color;


/**
 * Cell Style Array manages allocation of cell styles
 * as well as adding highlights (i.e. mixing color to an existing background).
 */
public class CellStyleArray
{
	private final CellStyle[] cells;
	private Color lineColor;

	
	public CellStyleArray(int size)
	{
		cells = new CellStyle[size];
	}
	
	
	public CellStyle get(int ix)
	{
		return cells[ix];
	}


	public void setStyle(CellStyle style, int start, int end)
	{
		for(int i=start; i<end; i++)
		{
			cells[i] = style;
		}
	}
	
	
	public void setLineColor(Color c)
	{
		lineColor = c;
	}
	
	
	public Color getLineColor()
	{
		return lineColor;
	}


	public void addHighlight(Color color, int start, int end)
	{
		CellStyle style = null;
		
		for(int i=start; i<end; i++)
		{
			CellStyle old = cells[i];
			if(old == null)
			{
				if(style == null)
				{
					style = new CellStyle(null, color, false, false, false, false);
				}
				cells[i] = style;
			}
			else
			{
				cells[i] = new CellStyle
				(
					old.getTextColor(),
					FX.mix(old.getBackgroundColor(), color, 0.85),
					old.isBold(),
					old.isItalic(),
					old.isStrikeThrough(),
					old.isUnderscore()
				);
			}
		}
	}
}
