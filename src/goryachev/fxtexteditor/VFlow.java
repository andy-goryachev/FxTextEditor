// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.util.CKit;
import goryachev.fx.CPane;
import goryachev.fx.CssStyle;
import goryachev.fx.FX;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.util.Duration;


/**
 * Paints the text on canvas. 
 */
public class VFlow
	extends CPane
{
	public static final CssStyle PANE = new CssStyle("FxTermView_PANE");
	private Font font;
	private Canvas canvas;
	private Timeline cursorAnimation;
	private boolean cursorEnabled = true;
	private boolean cursorOn = true;
	private GraphicsContext gx;
	private int colCount;
	private int rowCount;
	private TextMetrics metrics;
	protected final Text proto = new Text();
	private Color backgroundColor = Color.WHITE;
	private Color foregroundColor = Color.BLACK;
	private String demoText;
	
	
	public VFlow()
	{
		FX.style(this, PANE);
		
		setMinWidth(0);
		setMinHeight(0);
		
		cursorAnimation = createCursorAnimation();
		
		setFocusTraversable(true);
		
		FX.listen(this::handleSizeChange, widthProperty());
		FX.listen(this::handleSizeChange, heightProperty());
	}
	
	
	public void setFont(Font f)
	{
		if(f == null)
		{
			throw new NullPointerException("font");
		}
		
		this.font = f;
		metrics = null;
	}
	
	
	public Font getFont()
	{
		return font;
	}
	
	
	public void setBackgroundColor(Color c)
	{
		backgroundColor = c;
		// TODO repaint
	}
	
	
	public Color getBackgroundColor()
	{
		return backgroundColor;
	}
	
	
	public void setForegroundColor(Color c)
	{
		foregroundColor = c;
		// TODO repaint
	}
	
	
	public Color getForegroundColor()
	{
		return foregroundColor;
	}
	
	
	protected TextMetrics textMetrics()
	{
		if(metrics == null)
		{
			proto.setText("8");
			proto.setFont(font);
			
			Bounds b = proto.getBoundsInLocal();
			int w = FX.round(b.getWidth());
			int h = FX.round(b.getHeight());
			
			metrics = new TextMetrics(font, b.getMinY(), w, h);
		}
		return metrics;
	}
	
	
	protected Timeline createCursorAnimation()
	{
		Timeline t = new Timeline(new KeyFrame(Duration.millis(500), (ev) -> blinkCursor()));
		t.setCycleCount(Timeline.INDEFINITE);
		t.play();
		return t;
	}
	
	
	protected void blinkCursor()
	{
		cursorOn = !cursorOn;
		refreshCursor();
	}
	
	
	protected void refreshCursor()
	{
		// TODO
//		refreshLine(curx, 1, cury);
	}
	
	
	protected void handleSizeChange()
	{
		Canvas cv = createCanvas();
		if(canvas != null)
		{
			getChildren().remove(canvas);
		}
		
		canvas = cv;
		setCenter(cv);
		gx = canvas.getGraphicsContext2D();
		
		gx.setFill(getBackgroundColor());
		gx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		// FIX
		demoPaint();
	}
	
	
	protected GraphicsContext getGraphicsContext()
	{
		return gx;
	}
	
	
	protected Canvas createCanvas()
	{
		TextMetrics tm = textMetrics();
		Insets m = getInsets();
		
		double w = getWidth() - m.getLeft() - m.getRight();
		double h = getHeight() - m.getTop() - m.getBottom();
		
		if((w < 1) || (h < 1))
		{
			setBufferSize(80, 25);
		}
		else
		{
			setBufferSize(CKit.floor(w / tm.cellWidth), CKit.floor(h / tm.cellHeight));
		}
		
		return new Canvas(w, h);
	}
	
	
	protected void setBufferSize(int w, int h)
	{
		// TODO update cursor!
		//setCursorPrivate(curx, cury);
		
		colCount = w;
		rowCount = h;

//		buffer.updateSize(colCount, rowCount);
	}
	
	
	public void invalidateLayout()
	{
		// TODO
	}
	
	
	@Deprecated // FIX
	protected void demoPaint()
	{
		if(demoText == null)
		{
			demoText = CKit.readStringQuiet(getClass(), "demo.txt");
		}
		
		int ix = 0;
		for(int y=0; y<rowCount; )
		{
			for(int x=0; x<colCount; )
			{
				if(ix >= demoText.length())
				{
					return;
				}
				
				char c = demoText.charAt(ix++);
				switch(c)
				{
				case '\n':
					x = 0;
					y++;
					continue;
				case '\r':
					continue;
				}
				
				paintCell(x, y, c);
				x++;
				if(x >= colCount)
				{
					x = 0;
					y++;
				}
			}
		}
	}
	
	
	protected void paintCell(int x, int y, char c)
	{
		String s = String.valueOf(c);
		
		TextMetrics m = textMetrics();
		double px = x * m.cellWidth;
		double py = y * m.cellHeight;
		
		Color bg = FX.gray(255 - ((c & 0xff)/8));
		gx.setFill(bg);
		gx.fillRect(px, py, m.cellWidth, m.cellHeight);
		
		gx.setFontSmoothingType(FontSmoothingType.GRAY);
		gx.setFont(getFont());
		gx.setFill(Color.BLACK);
		gx.fillText(s, px, py - m.baseline, m.cellWidth);
	}
}
