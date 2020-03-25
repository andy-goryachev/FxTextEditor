// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package goryachev.common.util;
import goryachev.common.log.Log;
import java.util.Timer;
import java.util.TimerTask;


/**
 * System Task is a kind of TimerTask designed for general purpose, rare, and short lived tasks.
 * Unlike a regular java.util.TimerTask, a thrown exception does not kill the timer.
 * SystemTasks are being executed by a singleton daemon Timer instance.
 */
public abstract class SystemTask
	extends TimerTask
{
	protected abstract void systemTaskBody() throws Exception;
	
	//

	protected static final Log log = Log.get("SystemTask");
	private static Timer timer;

	
	public SystemTask()
	{
	}
	
	
	public static void schedule(long delay, long period, Runnable r)
	{
		new SystemTask()
		{
			protected void systemTaskBody() throws Exception
			{
				r.run();
			}
		}.schedule(delay, period);
	}
	
	
	public static void schedule(long delay, Runnable r)
	{
		new SystemTask()
		{
			protected void systemTaskBody() throws Exception
			{
				r.run();
			}
		}.schedule(delay);
	}
	
	
	public void schedule(long delay, long period)
	{
		timer().schedule(this, delay, period);
	}
	
	
	public void schedule(long delay)
	{
		timer().schedule(this, delay);
	}
	
	
	protected Timer timer()
	{
		if(timer == null)
		{
			synchronized(SystemTask.class)
			{
				if(timer == null)
				{
					timer = new Timer("SystemTask", true);
				}
			}
		}
		return timer;
	}
	
	
	public final void run()
	{
		try
		{
			systemTaskBody();
		}
		catch(Throwable e)
		{
			log.error(e);
		}
	}
}