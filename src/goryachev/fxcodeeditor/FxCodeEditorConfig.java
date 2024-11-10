// Copyright © 2024-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxcodeeditor;

/**
 * FxCodeEditor Config.
 */
public final class FxCodeEditorConfig implements Cloneable
{
	public FxCodeEditorConfig()
	{
	}
	
	
	public FxCodeEditorConfig copy()
	{
		try
		{
			return (FxCodeEditorConfig)super.clone();
		}
		catch(Exception e)
		{
			// never happens
			throw new Error();
		}
	}
}
