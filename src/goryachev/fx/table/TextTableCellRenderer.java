// Copyright © 2021-2022 Andy Goryachev <andy@goryachev.com>
package goryachev.fx.table;
import goryachev.fx.CPane;
import goryachev.fx.IStyledText;
import goryachev.fx.util.TextPainter;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.text.Font;


/**
 * FxTextTable Cell Renderer.
 */
public class TextTableCellRenderer
	extends CPane
{
	protected final TextPainter painter = new TextPainter();
	protected final Object value;
	private final ObjectBinding binding;
	
	
	public TextTableCellRenderer(FxTextTable parent, Object value)
	{
		this.value = value;

		// using object binding callback to create and paint the canvas 
		binding = Bindings.createObjectBinding
		(
			() ->
			{
				Font f = parent.getFont();
				return updateCanvas(f);	
			}, 
			parent.fontProperty(), 
			widthProperty(), 
			heightProperty()
		);
		binding.addListener((c,p,v) -> { }); // TODO lambda can be used to updateCanvas
		
		Font f = parent.getFont();
		updateCanvas(f);
	}
	
	
	protected double computePrefHeight(double width)
	{
//		double h;
//		Canvas c = painter.getCanvas();
//		if(c == null)
//		{
//			h = 0.0; 
//		}
//		else
//		{
//			h = c.getHeight();
//		}
//		
//		Insets m = getInsets();
//		return m.getTop() + h + m.getBottom();
		
		return 10;
	}


	protected double computePrefWidth(double height)
	{
		double w;
		Canvas c = painter.getCanvas();
		if(c == null)
		{
			w = 0.0; 
		}
		else
		{
			w = c.getWidth();
		}
		
		Insets m = getInsets();
		return m.getLeft() + w + m.getRight();
	}
	
	
	protected void layoutChildren()
	{
		super.layoutChildren();
	}
	
	
	protected Object updateCanvas(Font f)
	{
		Canvas canvas = painter.createCanvas(this);
		setCenter(canvas);
		
		painter.clear();
		painter.setFont(f);
		
		if(value instanceof IStyledText)
		{
			painter.paint((IStyledText)value);
		}
		else
		{
			if(value == null)
			{
				// TODO paint background?
			}
			else
			{
				String text = value.toString();
				painter.paint(text);
			}
		}
		
		// will be ignored
		return canvas;
	}
}
