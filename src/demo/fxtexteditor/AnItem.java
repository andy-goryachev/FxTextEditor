// Copyright © 2019 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;


/**
 * An Item.
 * TODO enum?
 * TODO see also Choices, figure out a good way to code UI choice set with persistence
 */
public class AnItem
{
	private final String code;
	private final String displayText;
	
	
	public AnItem(String code, String displayText)
	{
		this.code = code;
		this.displayText = displayText;
	}
	
	
	public String toString()
	{
		return getDisplayText();
	}
	
	
	public String getCode()
	{
		return code;
	}
	
	
	public String getDisplayText()
	{
		return displayText;
	}
}
