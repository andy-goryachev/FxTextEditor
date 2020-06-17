// Copyright © 2017-2020 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;


/**
 * An Edit.
 */
public class Edit
{
	private final SelectionSegment selection;
	private final CharSequence replaceText;
	
	
	// TODO multiple lines
	
	
	public Edit(SelectionSegment sel, CharSequence replaceText)
	{
		this.selection = sel;
		this.replaceText = replaceText;
	}
	
	
	public SelectionSegment getSelection()
	{
		return selection;
	}
	
	
	public CharSequence getReplaceText()
	{
		return replaceText;
	}
}
