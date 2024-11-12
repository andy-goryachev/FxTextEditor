// Copyright © 2024-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxcodeeditor.internal;
import goryachev.fxcodeeditor.model.CodeModel;


/**
 * Paragraph Arrangement contains a map of pre-computed FlowPars.
 * The arrangement is valid
 */
public class Arrangement
{
	private final CodeModel model;
	private final int columnCount;
	
	
	public Arrangement(CodeModel m, int columnCount)
	{
		this.model = m;
		this.columnCount = columnCount;
	}


	/**
	 * Lays out {@code rowCount} paragraphs.
	 * Returns the number of paragraphs actually laid out.
	 */
	public int layout(int rowCount, int startIndex, int startCellIndex)
	{
		// TODO
		return 0;
	}


	public boolean isVsbNeeded()
	{
		// TODO
		return false;
	}
	
	
	public boolean isHsbNeeded()
	{
		// TODO
		return false;
	}
	
	
	public int getLastIndex()
	{
		// TODO
		return 0;
	}
}
