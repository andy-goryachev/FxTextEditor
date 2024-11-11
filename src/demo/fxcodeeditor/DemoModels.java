// Copyright © 2019-2024 Andy Goryachev <andy@goryachev.com>
package demo.fxcodeeditor;
import goryachev.fxcodeeditor.model.CodeModel;


/**
 * Demo Text Models.
 */
public enum DemoModels
{
	NULL;
	
	
	@Override
	public String toString()
	{
		switch(this)
		{
		case NULL:
		default:
			return "<null>";
		}
	}
	
	
	public static CodeModel getModel(Object x)
	{
		DemoModels choice;
		if(x instanceof DemoModels ch)
		{
			choice = ch;
		}
		else
		{
			choice = DemoModels.NULL;
		}
		
		switch(choice)
		{
		case NULL:
		default:
			return null;
		}
	}
}
