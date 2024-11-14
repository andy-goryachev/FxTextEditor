// Copyright © 2024-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.codepad.model;
import goryachev.common.util.CKit;


/**
 * Undecorated CodeModel.
 * 
 * For testing only.
 */
public class UndecoratedCodeModel extends CodeModel
{
	private final String[] lines;


	public UndecoratedCodeModel(String[] lines)
	{
		this.lines = lines;
	}
	
	
	public static UndecoratedCodeModel of(String text)
	{
		String[] lines = CKit.split(text);
		return new UndecoratedCodeModel(lines);
	}
	

	@Override
	public int size()
	{
		return lines.length;
	}
	

	@Override
	public CodeParagraph getParagraph(int index)
	{
		String text = lines[index];
		return CodeParagraph.fast(index, text);
	}
}
