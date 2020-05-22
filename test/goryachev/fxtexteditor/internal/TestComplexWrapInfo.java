// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor.internal;
import goryachev.common.test.TF;
import goryachev.common.test.Test;
import goryachev.common.util.CList;
import goryachev.common.util.D;
import goryachev.common.util.SB;
import goryachev.fxtexteditor.ITabPolicy;
import goryachev.fxtexteditor.ITextLine;


/**
 * Tests ComplexWrapInfo
 */
public class TestComplexWrapInfo
{
	public static void main(String[] args)
	{
		TF.run();
	}
	
	
	@Test
	public void testTabIssue()
	{
		ITextLine tline = new MockTextLine("\t\t"); 
		ITabPolicy tp = TabPolicy.create(4);
		FlowLine fline = new FlowLine(tline, AGlyphInfo.create(tline.getPlainText(), null));
		ComplexWrapInfo wr = ComplexWrapInfo.createComplexWrapInfo(fline, tp, 100, true);
		TextCell c1 = wr.getCell(0, 4);
		TextCell c2 = wr.getCell(0, 8); // FIX wrong caretCharIndex
		
		D.print(c1, c2);
	}
	
	
	@Test
	public void test()
	{
		test
		(
			4,
			4,
			true,
			"123\t", new int[] { 0, 1, 2, -4 },
			"456",   new int[] { 4, 5, 6 }
		);
	}
	
	
	@Test
	public void regression()
	{
		test
		(
			4,
			4,
			true,
			"123\t", new int[] { 0, 1, 2, -4 },
			"456",   new int[] { 4, 5, 6 }
		);

		test
		(
			8,
			4,
			true,
			"\t1234", new int[] { -1, -1, -1, -1, 1, 2, 3, 4 },
			"56",     new int[] { 5, 6 }
		);
		
		test
		(
			4,
			1,
			true,
			"1234", new int[] { 0, 1, 2, 3 }
		);
		
		test
		(
			3,
			1,
			true,
			"123", new int[] { 0, 1, 2 },
			"4",   new int[] { 3 }
		);
	}
	
	
	protected void test(int width, int tabSize, boolean wrapLines, Object ... args)
	{
		CList<int[]> cells = new CList();
		
		SB sb = new SB();
		for(Object x: args)
		{
			if(x instanceof String)
			{
				sb.append((String)x);
			}
			else if(x instanceof int[])
			{
				cells.add((int[])x);
			}
			else
			{
				throw new Error("?" + x);
			}
		}
		
		String text = sb.toString();
		
		int[][] expected = new int[cells.size()][];
		cells.toArray(expected);
		
		ITextLine tline = new MockTextLine(text); 
		ITabPolicy tp = TabPolicy.create(tabSize);
		FlowLine fline = new FlowLine(tline, AGlyphInfo.create(tline.getPlainText(), null));
		ComplexWrapInfo wr = ComplexWrapInfo.createComplexWrapInfo(fline, tp, width, wrapLines);
		
		TF.eq(wr.cells, expected, "cells");
	}
}
