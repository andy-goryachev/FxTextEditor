// Copyright © 2019-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.test.TF;
import goryachev.common.test.Test;
import goryachev.common.util.D;
import goryachev.fx.TextCellStyle;
import goryachev.fxtexteditor.ITabPolicy;
import goryachev.fxtexteditor.ITextLine;


/**
 * TODO test WrapInfo instead.
 * Test WrappingReflowHelper.
 */
public class TestWrappingReflowHelper
{
	public static void main(String[] args)
	{
		TF.run();
	}
	
	
	@Test
	public void test()
	{
//		VerticalScrollHelper h = new VerticalScrollHelper(null, 10, 10, 5, 0.5)
//		{
//			@Override
//			public void addEntry(int line, GlyphIndex gix)
//			{
//				D.print(line, gix);
//				super.addEntry(line, gix);
//			}
//		};
//		ITabPolicy tp = TabPolicy.create(4);
//		
//		String text = 
//			"0123456789";
////			"0123456789012345678901234567890123456789012345678901234567890123456789";
//		
//		ITextLine tline = new ITextLine()
//		{
//			public int getLineNumber()
//			{
//				return 0;
//			}
//
//
//			public int getModelIndex()
//			{
//				return 0;
//			}
//
//
//			public String getPlainText()
//			{
//				return text;
//			}
//
//
//			public int getTextLength()
//			{
//				return text.length();
//			}
//
//
//			public CellStyle getCellStyle(int charOffset)
//			{
//				return null;
//			}
//		};
//		FlowLine fline = new FlowLine(tline, TextGlyphInfo.create(tline.getPlainText(), null));
//		
//		int sz = WrappingReflowHelper.computeBreaks(h, tp, fline, 5);
//		
//		D.print(sz);
	}
}
