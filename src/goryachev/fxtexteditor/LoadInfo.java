// Copyright © 2019-2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;


/**
 * Encapsulates the loading progress.
 */
public class LoadInfo
{
	public final double progress;
	public final int lineCount;
	public final long startTime;
	public final long currentTime;
	// TODO tailing?
	
	
	public LoadInfo(double progress, int lineCount, long startTime, long currentTime)
	{
		this.progress = progress;
		this.lineCount = lineCount;
		this.startTime = startTime;
		this.currentTime = currentTime;
	}
}