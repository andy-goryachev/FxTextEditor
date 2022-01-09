// Copyright © 2019-2022 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import goryachev.common.test.TF;
import goryachev.common.test.Test;


/**
 * Text Line Attributes.
 */
public class TestTAttributes
{
	public static void main(String[] args)
	{
		TF.run();
	}
	
	
	@Test
	public void test()
	{
		TAttributes a = new TAttributes();
		prep(a, 0, 1, 10, 20, 99, 100);
		
		t(a, 0, 0);
		t(a, 1, 1);
		t(a, 3, 1);
		t(a, 10, 10);
		t(a, 11, 10);
		t(a, 19, 10);
		t(a, 20, 20);
		t(a, 21, 20);
		t(a, 98, 20);
		t(a, 99, 99);
		
		tnull(a, -1);
		tnull(a, 100);
		tnull(a, 1001);
	}


	protected void prep(TAttributes a, int ... offsets)
	{
		int sz = offsets.length - 1;
		for(int i=0; i<sz; i++)
		{
			int start = offsets[i];
			int end = offsets[i + 1];
			TSegment s = new TSegment(null, start, end, null);
			a.addSegment(s);
		}
	}
	
	
	protected void t(TAttributes a, int off, int expected)
	{
		TSegment s = a.getSegmentAt(off);
		TF.eq(s.start, expected);
	}
	
	
	protected void tnull(TAttributes a, int off)
	{
		TSegment s = a.getSegmentAt(off);
		TF.eq(s, null);
	}
}
