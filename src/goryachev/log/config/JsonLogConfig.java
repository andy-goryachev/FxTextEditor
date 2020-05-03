// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.log.config;
import goryachev.common.log.AbstractLogConfig;
import goryachev.common.log.Log;
import goryachev.common.log.LogUtil;
import goryachev.common.util.CKit;
import java.io.File;
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
		
		configure(file);
		
		if(pollingPeriod > 0)
		{
			monitor = new FileMonitor(file, pollingPeriod, JsonLogConfig::configure);
			monitor.start();
		}
	}
	
	
	protected static void configure(File file)
	{
		try
		{
			String spec = CKit.readString(file);
			configure(spec);
		}
		catch(Throwable e)
		{
			LogUtil.internalError(e);
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
