// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.log.config;
import goryachev.common.log.AbstractLogConfig;
import goryachev.common.log.Log;
import goryachev.common.log.LogUtil;
import goryachev.common.util.CKit;
import java.io.File;
import java.io.FileNotFoundException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * This facility configures logging facade provided by the Log class,
 * using JSON configuration files (requires GSON dependency).
 */
public class JsonLogConfig
{
	private static Gson gson;
	private static FileMonitor monitor;

	
	public static void configure(String spec)
	{
		AbstractLogConfig cf;
		try
		{
			cf = parseLogConfig(spec);
		}
		catch(Throwable e)
		{
			LogUtil.internalError(e);
			return;
		}
		
		Log.setConfig(cf);
	}
	

	/** 
	 * configures logger from a file.  
	 * when pollingPeriod > 0, starts watching the specified file for changes.
	 */
	public static synchronized void configure(File file, long pollingPeriod)
	{
		if(monitor != null)
		{
			monitor.cancel();
			monitor = null;
		}
		
		configure(file, false);
		
		if(pollingPeriod > 0)
		{
			monitor = new FileMonitor(file, pollingPeriod, (f) -> configure(f, true));
			monitor.start();
		}
	}
	
	
	protected static void configure(File file, boolean stderr)
	{
		try
		{
			String spec = CKit.readString(file);
			configure(spec);
			
			if(stderr)
			{
				System.err.println("Log config reloaded: " + file); 
			}
		}
		catch(FileNotFoundException e)
		{
			if(stderr)
			{
				System.err.println("Log config not found: " + file);
			}
		}
		catch(Throwable e)
		{
			if(stderr)
			{
				System.err.print("Failed to reload log config: " + file);
				e.printStackTrace();
			}
			else
			{
				LogUtil.internalError(e);
			}
		}
	}
	
	
	private static LogConfig parseLogConfig(String spec) throws Exception
	{
		return gson().fromJson(spec, LogConfig.class);
	}
	
	
	private static Gson gson()
	{
		if(gson == null)
		{
			gson = new GsonBuilder().
				setLenient().
				setPrettyPrinting().
				create();
		}
		return gson;
	}
}
