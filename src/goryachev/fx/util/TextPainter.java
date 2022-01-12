// Copyright © 2022 Andy Goryachev <andy@goryachev.com>
package goryachev.fx.util;
import goryachev.common.util.CKit;
import goryachev.fx.IStyledText;
import goryachev.fx.TextCellMetrics;
import goryachev.fx.TextCellStyle;
import goryachev.fx.internal.GlyphCache;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;


/**
 * Canvas-based Monospaced Text Painter.
 */
public class TextPainter
{
	protected final static Text proto = new Text();
	private TextCellMetrics metrics;
	private Font font;
	private Font boldFont;
	private Font boldItalicFont;
	private Font italicFont;
	private Canvas canvas;
	private GraphicsContext gx;
	private Color textColor = Color.BLACK; // TODO property?

	
	public TextPainter()
	{
		setFont(null);
	}
	
	
	public void setFont(Font f)
	{
		if(f == null)
		{
			f = Font.font("Monospace", 12);
		}
		
		this.font = f; 
		metrics = null;
		boldFont = null;
		boldItalicFont = null;
		italicFont = null;
	}
	
	
	public Canvas createCanvas(Region r)
	{
		Insets m = r.getInsets();
		double w = r.getWidth() - m.getLeft() - m.getRight();
		double h = r.getHeight() - m.getTop() - m.getBottom();

		canvas = new Canvas(w, h);
		
		gx = canvas.getGraphicsContext2D();
		gx.setFontSmoothingType(FontSmoothingType.GRAY);
		
		return canvas;
	}
	
	
	public Canvas getCanvas()
	{
		return canvas;
	}
	
	
	public TextCellMetrics textMetrics()
	{
		if(metrics == null)
		{
			proto.setText("8");
			proto.setFont(font);
			
			Bounds b = proto.getBoundsInLocal();
			int w = CKit.round(b.getWidth());
			int h = CKit.round(b.getHeight());
			
			metrics = new TextCellMetrics(font, b.getMinY(), w, h);
		}
		return metrics;
	}
	
	
	protected Font getFont(TextCellStyle st)
	{
		if(st.isBold())
		{
			if(st.isItalic())
			{
				if(boldItalicFont == null)
				{
					boldItalicFont = Font.font(font.getFamily(), FontWeight.BOLD, FontPosture.ITALIC, font.getSize());
				}
				return boldItalicFont;
			}
			else
			{
				if(boldFont == null)
				{
					boldFont = Font.font(font.getFamily(), FontWeight.BOLD, FontPosture.REGULAR, font.getSize());
				}
				return boldFont;
			}
		}
		else
		{
			if(st.isItalic())
			{
				if(italicFont == null)
				{
					italicFont = Font.font(font.getFamily(), FontWeight.NORMAL, FontPosture.ITALIC, font.getSize());
				}
				return italicFont;
			}
			else
			{
				return font;
			}
		}
	}
	
	
	// attempt to limit the canvas queue
	// https://bugs.java.com/bugdatabase/view_bug.do?bug_id=8092801
	// https://github.com/kasemir/org.csstudio.display.builder/issues/174
	// https://stackoverflow.com/questions/18097404/how-can-i-free-canvas-memory
	// https://bugs.openjdk.java.net/browse/JDK-8103438
	public void clear()
	{
		gx.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}
	
	
	public void fill(Color c)
	{
		gx.setFill(c);
		gx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}
	

	protected void paintCell(IStyledText styledText, TextCellMetrics tm, int ix)
	{
		double cx = ix * tm.cellWidth;
		
		// style
		TextCellStyle style = styledText.getCellStyle(ix);
		if(style == null)
		{
			style = TextCellStyle.NONE;
		}
		
		// background
//		Color bg = backgroundColor(caretLine, selected, row.getLineColor(), style.getBackgroundColor());
//		gx.setFill(bg);
//		gx.fillRect(cx, cy, tm.cellWidth, tm.cellHeight);
		
		if(style.isUnderscore())
		{
			// TODO special property, mix with background
			gx.setFill(textColor);
			gx.fillRect(cx, tm.cellHeight - 1, tm.cellWidth, 1);
		}
		
		// text
		char c = styledText.charAt(ix);
		String s = GlyphCache.get(c);
		if(s != null)
		{
			Color fg = style.getTextColor();
			if(fg == null)
			{
				fg = textColor;
			}
			
			Font f = getFont(style);
			gx.setFont(f);
			gx.setFill(fg);
			gx.fillText(s, cx, -tm.baseline, tm.cellWidth);
		
			if(style.isStrikeThrough())
			{
				// TODO special property, mix with background
				gx.setFill(textColor);
				gx.fillRect(cx, tm.cellHeight/2, tm.cellWidth, 1);
			}
		}
	}
	
	
	protected void paintText(String text, TextCellMetrics tm, int ix)
	{
		double cx = ix * tm.cellWidth;
		
		// background
//		Color bg = backgroundColor(caretLine, selected, row.getLineColor(), style.getBackgroundColor());
//		gx.setFill(bg);
//		gx.fillRect(cx, cy, tm.cellWidth, tm.cellHeight);
		
		// text
		char c = text.charAt(ix);
		String s = GlyphCache.get(c);
		if(s != null)
		{
			gx.fillText(s, cx, -tm.baseline, tm.cellWidth);
		}
	}
	
	
	public void paint(String text)
	{
		TextCellMetrics tm = textMetrics();
		gx.setFont(font);
		gx.setFill(textColor);
		
		int sz = text.length();
		for(int i=0; i<sz; i++)
		{
			char c = text.charAt(i);
			paintText(text, tm, i);
		}
	}
	
	
	public void paint(IStyledText text)
	{
		TextCellMetrics tm = textMetrics();
		
		int sz = text.getTextLength();
		for(int i=0; i<sz; i++)
		{
			paintCell(text, tm, i);
		}
	}
}
