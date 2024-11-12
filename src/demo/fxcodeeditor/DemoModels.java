// Copyright © 2019-2024 Andy Goryachev <andy@goryachev.com>
package demo.fxcodeeditor;
import goryachev.fxcodeeditor.model.CodeModel;
import goryachev.fxcodeeditor.model.UndecoratedCodeModel;


/**
 * Demo Text Models.
 */
public enum DemoModels
{
	PLAIN_TEXT,
	NULL;
	
	
	@Override
	public String toString()
	{
		switch(this)
		{
		case PLAIN_TEXT:
			return "Plain Text";
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
		case PLAIN_TEXT:
			return UndecoratedCodeModel.of
			(
				"""
				Line one.
				Line two, slightly longer.
				Line three, Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
				Line four.
				The End.
				"""
			);
		case NULL:
		default:
			return null;
		}
	}
}
