// Copyright © 2020-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.util.CList;
import goryachev.common.util.SB;
import goryachev.fx.TextCellStyle;
import javafx.scene.paint.Color;


/**
 * Appendable Styled ITextLine.
 */
public class StyledTextLine
	implements ITextLine
{
	private final int lineNumber;
	private final SB sb;
	private Color lineColor;
	private final CList<TextCellStyle> styles;
	
	
	public StyledTextLine(int lineNumber)
	{
		this.lineNumber = lineNumber;
		this.sb = new SB();
		this.styles = new CList();
	}


	@Override
	public int getTextLength()
	{
		return sb.length();
	}
	
	
	@Override
	public String getPlainText()
	{
		return sb.toString();
	}
	
	
	@Override
	public int getModelIndex()
	{
		return lineNumber;
	}
	
	
	@Override
	public int getLineNumber()
	{
		return lineNumber;
	}
	
	
	@Override
	public TextCellStyle getCellStyle(int charOffset)
	{
		return styles.get(charOffset);
	}


	@Override
	public Color getLineColor()
	{
		return lineColor;
	}
	
	
	public void setLineColor(Color c)
	{
		lineColor = c;
	}
	
	
	public void append(TextCellStyle st, String text)
	{
		for(int i=0; i<text.length(); i++)
		{
			styles.add(st);
		}
		
		sb.append(text);
	}
	
	
	public void append(String text)
	{
		for(int i=0; i<text.length(); i++)
		{
			styles.add(null);
		}
		
		sb.append(text);
	}
	
	
	public void append(TextCellStyle st, char ch)
	{
		styles.add(st);
		sb.append(ch);
	}
	
	
	public void append(char ch)
	{
		styles.add(null);
		sb.append(ch);
	}
}