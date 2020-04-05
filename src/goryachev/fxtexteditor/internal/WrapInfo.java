// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.util.CKit;
import goryachev.common.util.ElasticIntArray;
import goryachev.fxtexteditor.GlyphType;
import goryachev.fxtexteditor.ITabPolicy;


/**
 * retains wrapping information (screen text rows) for the given FlowLine.
 */
public abstract class WrapInfo
{
	public abstract int getRowCount();
	
	public abstract GlyphIndex getIndexForRow(int row);
	
	public abstract boolean isCompatible(ITabPolicy tabPolicy, int width);
	
	//
	
	public static final WrapInfo EMPTY = new Empty();
	
	
	public WrapInfo()
	{
	}
	
	
	public static WrapInfo create(FlowLine fline, ITabPolicy tabPolicy, int width)
	{
		// TODO move to caller?
		int lineIndex = fline.getModelIndex();
		if(lineIndex < 0)
		{
			return EMPTY;
		}
		
		int cellIndex = 0;
		int x = 0;
		int startGlyphIndex = 0;
		int glyphIndex = 0;
		int tabDistance = 0;
		
		boolean complex = fline.hasComplexGlyphs();
		if(!complex)
		{
			if(!tabPolicy.isSimple())
			{
				complex |= fline.hasTabs();
			}
		}
		
		if(!complex)
		{
			int len = fline.getGlyphCount();
			return new Simple(len, width);
		}
		
		Complex wrap = new Complex(tabPolicy, width);
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
	}
	
	
	//
	
	
	public static class Empty extends WrapInfo
	{
		public int getRowCount() { return 1; }
		public GlyphIndex getIndexForRow(int row) { return GlyphIndex.ZERO; }
		public boolean isCompatible(ITabPolicy tabPolicy, int width) { return true; }
	}
	
	
	//
	
	
	public static class Simple extends WrapInfo
	{
		private final int width;
		private final int length;
		
		
		public Simple(int length, int width)
		{
			this.length = length;
			this.width = width;
		}


		public int getRowCount()
		{
			return CKit.binCount(length, width);
		}


		public GlyphIndex getIndexForRow(int row)
		{
			return new GlyphIndex(row * width);
		}


		public boolean isCompatible(ITabPolicy tabPolicy, int width)
		{
			return (this.width == width);
		}
	}
	
	
	//
	
	
	public static class Complex extends WrapInfo
	{
		private final ITabPolicy tabPolicy;
		private final int width;
		private final ElasticIntArray breaks = new ElasticIntArray();
		
		
		public Complex(ITabPolicy tabPolicy, int width)
		{
			this.tabPolicy = tabPolicy;
			this.width = width;
		}
		
		
		protected void addBreak(int start)
		{
			breaks.add(start);
		}
		
		
		public int getRowCount()
		{
			return breaks.size();
		}


		public GlyphIndex getIndexForRow(int row)
		{
			return new GlyphIndex(breaks.get(row));
		}
		
		
		public boolean isCompatible(ITabPolicy tabPolicy, int width)
		{
			return 
				(this.width == width) &&
				(this.tabPolicy == tabPolicy);
		}
	}
}
