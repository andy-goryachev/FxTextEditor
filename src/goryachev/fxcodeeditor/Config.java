// Copyright © 2024-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxcodeeditor;


/**
 * FxCodeEditor Config.
 */
public final class Config implements Cloneable
{
	public Config()
	{
	}
	

	public static Config getDefault()
	{
		return new Config();
	}
	
	
	public Config copy()
	{
		try
		{
			return (Config)super.clone();
		}
		catch(Exception e)
		{
			// never happens
			throw new Error();
		}
	}
}
