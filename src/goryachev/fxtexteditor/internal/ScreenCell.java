// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import javafx.scene.paint.Color;


/**
 * Screen Buffer Cell.
 * Usually contains a grapheme, but not always - inside a tab, for example,
 * or after an end of line, or after an end of file.
 */
@Deprecated // TODO replace
public class ScreenCell
{
	private int line;
	private int offset;
	private boolean validLine;
	private boolean validCaret;
	private Grapheme grapheme;
	
	
	public ScreenCell()
	{
	}
	
	
	public String getText()
	{
		return grapheme == null ? null : grapheme.getText();
	}
	
	
	public Color getBackgroundColor()
	{
		return grapheme == null ? null : grapheme.getBackgroundColor();
	}
	
	
	public Color getTextColor()
	{
		return grapheme == null ? null : grapheme.getTextColor();
	}
	
	
	public int getLine()
	{
		return line;
	}
	
	
	public void setLine(int line)
	{
		this.line = line;
	}
	
	
	public boolean isValidLine()
	{
		return validLine;
	}
	
	
	public void setValidLine(boolean on)
	{
		this.validLine = on;
	}
	
	
	public boolean isValidCaret()
	{
		return validCaret;
	}
	
	
	public void setValidCaret(boolean on)
	{
		this.validCaret = on;
	}
	
	
	public int getOffset()
	{
		return offset;
	}
	
	
	public void setOffset(int offset)
	{
		this.offset = offset;
	}
	

	public void setCell(Grapheme gr)
	{
		this.grapheme = gr;
	}
	
	
	public boolean isBold()
	{
		return grapheme.isBold();
	}
	
	
	public boolean isItalic()
	{
		return grapheme.isItalic();
	}
	
	
	public boolean isStrikeThrough()
	{
		return grapheme.isStrikeThrough();
	}
	
	
	public boolean isUnderscore()
	{
		return grapheme.isUnderscore();
	}
}
