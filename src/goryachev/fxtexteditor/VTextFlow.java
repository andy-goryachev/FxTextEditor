// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.util.CKit;
import goryachev.common.util.D;
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
public class VTextFlow
	extends CPane
{
	public static final CssStyle PANE = new CssStyle("FxTermView_PANE");
	protected final FxTextEditor editor;
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
	private Color textColor = Color.BLACK;
	private int topLine;
	private int topOffset;
	private FxTextEditorLayout layout;
	
	
	public VTextFlow(FxTextEditor ed)
	{
		this.editor = ed;
		
		FX.style(this, PANE);
		
		setMinWidth(0);
		setMinHeight(0);
		
		cursorAnimation = createCursorAnimation();
		
		setFocusTraversable(true);
		
		FX.listen(this::handleSizeChange, widthProperty());
		FX.listen(this::handleSizeChange, heightProperty());
		
		// TODO clip rect
	}
	
	
	public FxTextEditor getEditor()
	{
		return editor;
	}
	
	
	public int getTopLine()
	{
		return topLine;
	}
	
	
	public void setTopLine(int y)
	{
		topLine = y;
	}
	
	
	public int getTopOffset()
	{
		return topOffset;
	}
	
	
	public void setTopOffset(int off)
	{
		topOffset = off;
	}
	
	
	public int getVisibleColumnCount()
	{
		return colCount;
	}
	
	
	public int getVisibleRowCount()
	{
		return rowCount;
	}
	
	
	public int getMaxColumnCount()
	{
		return layout.getMaxColumnCount();
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
		if(font == null)
		{
			font = Font.font("Monospace", 12);
		}
		return font;
	}
	
	
	public void setBackgroundColor(Color c)
	{
		backgroundColor = c;
		repaint();
	}
	
	
	public Color getBackgroundColor()
	{
		return backgroundColor;
	}
	
	
	public void setTextColor(Color c)
	{
		textColor = c;
		repaint();
	}
	
	
	public Color getTextColor()
	{
		return textColor;
	}
	
	
	protected TextMetrics textMetrics()
	{
		if(metrics == null)
		{
			Font f = getFont();
			
			proto.setText("8");
			proto.setFont(f);
			
			Bounds b = proto.getBoundsInLocal();
			int w = FX.round(b.getWidth());
			int h = FX.round(b.getHeight());
			
			metrics = new TextMetrics(f, b.getMinY(), w, h);
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
		canvas = createCanvas();
		setCenter(canvas);
		gx = canvas.getGraphicsContext2D();
		gx.setFontSmoothingType(FontSmoothingType.GRAY);
		
		gx.setFill(getBackgroundColor());
		gx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		repaint();
	}
	
	
	protected GraphicsContext getGraphicsContext()
	{
		return gx;
	}
	
	
	// TODO create canvas +1 size
	protected Canvas createCanvas()
	{
		TextMetrics tm = textMetrics();
		Insets m = getInsets();
		
		double w = getWidth() - m.getLeft() - m.getRight();
		double h = getHeight() - m.getTop() - m.getBottom();
		
		colCount = CKit.floor(w / tm.cellWidth);
		rowCount = CKit.floor(h / tm.cellHeight);
		
		D.print(rowCount); // FIX
		return new Canvas(w + 1, h + 1);
	}
	
	
	public void invalidate()
	{
		layout = null;
	}
	
	
	protected FxTextEditorLayout createLayout()
	{
		int sz = getVisibleRowCount() + 1;
		int[] offsets = new int[sz];
		ITextCells[] cells = new ITextCells[sz];
		FxTextEditorModel m = editor.getModel();
		
		int ix = getTopLine();
		int y = 0;
		int max;
		
		if(editor.isWrapLines())
		{
			int colCount = getVisibleColumnCount();
			max = colCount;
			
			for(;;)
			{
				ITextCells tc = m.getTextCells(ix);
				if(tc == null)
				{
					break;
				}
				
				int len = tc.getCellCount();
				int off = getTopOffset();
				while(off < len)
				{
					if(y >= sz)
					{
						break;
					}
					
					cells[y] = tc;
					offsets[y] = off;
					
					off += colCount;
					y++;
				}
				
				ix++;
			}
		}
		else
		{
			int off = getTopOffset();
			max = 0;

			for(;;)
			{
				ITextCells tc = m.getTextCells(ix);
				if(tc == null)
				{
					break;
				}
				
				cells[y] = tc;
				offsets[y] = off;
				int w = tc.getCellCount();
				if(max < w)
				{
					max = w;
				}
					
				y++;
					
				if(y >= sz)
				{
					break;
				}
				
				ix++;
			}
		}
		
		return new FxTextEditorLayout(cells, offsets, max);
	}
	
	
	// TODO in invoke later to coalesce multilpe repaints?
	public void repaint()
	{
		D.print(getVisibleRowCount()); // FIX 

		if((colCount == 0) || (rowCount == 0))
		{
			return;
		}
		
		if(editor.getModel() == null)
		{
			return;
		}
		
		if(layout == null)
		{
			layout = createLayout();
		}
		
		boolean wrap = editor.isWrapLines();
		int x = 0;
		int y = 0;
		int max = wrap ? colCount : colCount + 1;
		
		for(;;)
		{
			TCell c = layout.getCell(x, y);
			if(c == null)
			{
				clearToEndOfLine(x, y);
			}
			else
			{
				paintCell(x, y, c);
			}
			
			x++;
			if(x >= max)
			{
				if(wrap)
				{
					clearToEndOfLine(x, y);
				}
				
				x = 0;
				y++;
				if(y > rowCount)
				{
					break;
				}
			}
		}
	}
	
	
	protected void clearToEndOfLine(int x, int y)
	{
		TextMetrics m = textMetrics();
		double px = x * m.cellWidth;
		double py = y * m.cellHeight;
		
		// TODO selection color, line color
		Color bg = backgroundColor;
		gx.setFill(bg);
		gx.fillRect(px, py,canvas.getWidth() - px, m.cellHeight);
	}
	
	
	protected void paintCell(int x, int y, TCell cell)
	{		
		TextMetrics m = textMetrics();
		double px = x * m.cellWidth;
		double py = y * m.cellHeight;
		
		// TODO line bg
		// TODO selection bg
		// TODO highlight bg
	
		// background
		Color bg = cell.getBackgroundColor();
		if(bg == null)
		{
			bg = backgroundColor;
		}
		gx.setFill(bg);
		gx.fillRect(px, py, m.cellWidth, m.cellHeight);
		
		Color fg = cell.getTextColor();
		if(fg == null)
		{
			fg = textColor;
		}
		
		String text = cell.getText();
		// TODO font attributes: bold, italic, underline, strikethrough
		gx.setFont(getFont());
		gx.setFill(fg);
		gx.fillText(text, px, py - m.baseline, m.cellWidth);
	}
}
