// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.fxtexteditor.GlyphType;
import goryachev.fxtexteditor.ITabPolicy;


/**
 * Provides mapping between text characters, glyphs, and screen cells.
 * This information is cached by FlowLine.
 */
public abstract class WrapInfo
{
	/** returns the number of screen rows occupied by the flow line */
	public abstract int getWrapRowCount();
	
	/** returns glyph index for wrapped row */
	@Deprecated // TODO may not return the right index (if in a tab)
	public abstract int getGlyphIndexForRow(int row);
	
	/** finds wrapped row for the given glyph index */
	public abstract int findRowForGlyphIndex(int glyphIndex);
	
	/** returns true if the current wrap info can be reused with the new screen configuration */
	public abstract boolean isCompatible(ITabPolicy tabPolicy, int width, boolean wrapLines);
	
	/** returns the wrapped row index for the given character */
	public abstract int getWrapRowForCharIndex(int charIndex);

	/** returns the screen column for the given character */
	public abstract int getColumnForCharIndex(int charIndex);

	/** returns the character index for the given column and wrap row */
	public abstract int getCharIndexForColumn(int wrapRow, int column);
	
	//
	
	public static final WrapInfo EMPTY = new EmptyWrapInfo();
	
	
	public WrapInfo()
	{
	}
	
	
	public static WrapInfo create(FlowLine fline, ITabPolicy tabPolicy, int width, boolean wrapLines)
	{
		// TODO move to caller?
		int lineIndex = fline.getModelIndex();
		if(lineIndex < 0)
		{
			return EMPTY;
		}
		
//		int cellIndex = 0;
//		int x = 0;
//		int startGlyphIndex = 0;
//		int glyphIndex = 0;
//		int tabDistance = 0;
		
		boolean complex = fline.hasComplexGlyphs();
		if(!complex)
		{
			if(!tabPolicy.isSimple())
			{
				complex |= fline.hasTabs();
			}
		}
		
		if(complex)
		{
			return ComplexWrapInfo.createComplexWrapInfo(fline, tabPolicy, width, wrapLines);
		}
		else
		{
			int len = fline.getGlyphCount();
			if(wrapLines)
			{
				return new SimpleWrapInfo(len, width);
			}
			else
			{
				return new SingleRowWrapInfo(len);
			}
		}
		
		/*
		// TODO move to ComplexWrapInfo.create(), also move the helpers there.
		ComplexWrapInfo wrap = new ComplexWrapInfo(tabPolicy, width, wrapLines);
		wrap.addBreak(startGlyphIndex);
		
		for(;;)
		{
			if(tabDistance > 0)
			{
				if(x >= width)
				{
					// carry on to next line, resetting tab distance
					startGlyphIndex = glyphIndex;
					tabDistance = 0;
					x = 0;
				}
				else
				{
					--tabDistance;
					x++;
				}
			}
			else if(complex)
			{
				if(x >= width)
				{
					// next row
					startGlyphIndex = glyphIndex;
					tabDistance = 0;
					x = 0;
					
					wrap.addBreak(startGlyphIndex);
				}
				else
				{
					GlyphType gt = fline.getGlyphType(glyphIndex);
					switch(gt)
					{
					case EOL:
						return wrap;
					case TAB:
						tabDistance = tabPolicy.nextTabStop(x) - x;
						--tabDistance;
						glyphIndex++;
						cellIndex++;
						x++;
						break;
					case NORMAL:
						glyphIndex++;
						cellIndex++;
						x++;
						break;
					default:
						throw new Error("?" + gt);
					}
				}
			}
			else
			{
				// simple case, cell indexes coincide with glyph indexes
				if(cellIndex + width >= fline.glyphInfo().getGlyphCount())
				{
					// end of line
					return wrap;
				}
				else
				{
					// middle of line
					glyphIndex += width;
					cellIndex += width;
					startGlyphIndex = glyphIndex;
				}
				
				x = 0;

				wrap.addBreak(startGlyphIndex);
			}
		}
		*/
	}
}
