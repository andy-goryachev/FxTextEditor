// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.DELETE;
import goryachev.fxtexteditor.internal.TAttrs;
import javafx.scene.paint.Color;


/**
 * Grapheme Cluster fits into one ScreenCell.
 */
@Deprecated // TODO remove
public class Grapheme
{
	public final int start;
	public final int end;
	public final String text;
	private static final TAttrs EMPTY = new TAttrs();
	private TAttrs attrs = EMPTY;
	
	
	public Grapheme(int start, int end, String text)
	{
		this.start = start;
		this.end = end;
		this.text = text;
	}


	public String getText()
	{
		return text;
	}


	public Color getBackgroundColor()
	{
		return attrs == null ? null : attrs.getBackgroundColor();
	}
	
	
	public Color getTextColor()
	{
		return attrs == null ? null : attrs.getTextColor();
	}


	public void setStyle(TAttrs a)
	{
		attrs = a;
	}
	
	
	public boolean isBold()
	{
		return attrs.isBold();
	}
	
	
	public boolean isItalic()
	{
		return attrs.isItalic();
	}
	
	
	public boolean isStrikeThrough()
	{
		return attrs.isStrikeThrough();
	}
	
	
	public boolean isUnderscore()
	{
		return attrs.isUnderscore();
	}
}