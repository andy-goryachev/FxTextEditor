// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;
import goryachev.common.util.CList;
import javafx.scene.paint.Color;


/**
 * Text Cells.
 */
public class SimpleTextCells
	implements ITextLine
{
	protected final CList<TCell> cells = new CList();
	private Color lineBackground;
	private Color backgroundColor;
	private Color textColor;
	private boolean hasRTL;
	private boolean bold;
	private boolean italic;
	private boolean strikeThrough;
	private boolean underscore;
	
	
	public SimpleTextCells()
	{
	}
	
	
	public int getCellCount()
	{
		return cells.size();
	}
	
	
	public void setLineBackground(Color c)
	{
		lineBackground = c;
	}
	
	
	public void setBackground(Color c)
	{
		backgroundColor = c;
	}
	
	
	public void setTextColor(Color c)
	{
		textColor = c;
	}
	
	
	public void setBold(boolean on)
	{
		bold = on;
	}
	
	
	public void setItalic(boolean on)
	{
		italic = on;
	}
	
	
	public void setStrikeThrough(boolean on)
	{
		strikeThrough = on;
	}
	
	
	public void setUnderscore(boolean on)
	{
		underscore = on;
	}
	
	
	/** text must represent a complete set of glyphs, without hanging combining marks */  
	public void addText(String text)
	{
		// TODO break into strings that fit one cell
		// set RTL flag
		int start = 0;
		for(int i=0; i<text.length();)
		{
			// FIX
//			int c = text.codePointAt(i);
//			if(c < 0)
//			{
//				continue;
//			}

			i++;
			
			// TODO
//			if(isCombiningChar(c))
//			{
//				continue;
//			}
			
			String s = text.substring(start, i);
			boolean rtl = false; //isRTLChar(c);
			if(rtl)
			{
				hasRTL = true;
			}
			
			cells.add(new TCell(s, backgroundColor, textColor, bold, italic, strikeThrough, underscore));
			start = i;
		}
		
//		if(start < text.length())
//		{
//			String text = text.substring(start);
//		}
	}
	
	
	public static boolean isRTLChar(int c)
	{
		// TODO
		return false;
	}
	
	
	public static boolean isCombiningChar(int c)
	{
		// TODO
		int type = Character.getType(c);
		switch(type)
		{
		case Character.COMBINING_SPACING_MARK:
		case Character.CONNECTOR_PUNCTUATION:
		case Character.CONTROL:
		case Character.CURRENCY_SYMBOL:
		case Character.DASH_PUNCTUATION:
		case Character.DECIMAL_DIGIT_NUMBER:
		case Character.ENCLOSING_MARK:
		case Character.END_PUNCTUATION:
		case Character.FINAL_QUOTE_PUNCTUATION:
		case Character.FORMAT:
		case Character.INITIAL_QUOTE_PUNCTUATION:
		case Character.LETTER_NUMBER:
		case Character.LINE_SEPARATOR:
		case Character.LOWERCASE_LETTER:
		case Character.MATH_SYMBOL:
		case Character.MODIFIER_LETTER:
		case Character.MODIFIER_SYMBOL:
		case Character.NON_SPACING_MARK:
		case Character.OTHER_LETTER:
		case Character.OTHER_NUMBER:
		case Character.OTHER_PUNCTUATION:
		case Character.OTHER_SYMBOL:
		case Character.PARAGRAPH_SEPARATOR:
		case Character.PRIVATE_USE:
		case Character.SPACE_SEPARATOR:
		case Character.START_PUNCTUATION:
		case Character.SURROGATE:
		case Character.TITLECASE_LETTER:
		case Character.UNASSIGNED:
		case Character.UPPERCASE_LETTER:
		}
		return false;
	}


	public TCell getCell(int ix)
	{
		if(ix < cells.size())
		{
			return cells.get(ix);
		}
		return null;
	}
}
