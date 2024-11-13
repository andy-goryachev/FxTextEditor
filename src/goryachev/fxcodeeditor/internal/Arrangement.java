// Copyright © 2024-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxcodeeditor.internal;
import goryachev.common.util.CList;
import goryachev.fxcodeeditor.model.CodeModel;
import goryachev.fxcodeeditor.model.CodeParagraph;
import javafx.scene.canvas.GraphicsContext;


/**
 * Paragraph Arrangement contains a map of pre-computed FlowPars.
 * The arrangement is valid
 */
public class Arrangement
{
	private final CodeModel model;
	private final int viewCols;
	private final int viewRows;
	private final int tabSize;
	private final boolean wrap;
	private final CList<WrapInfo> flows = new CList<>();
	
	
	public Arrangement(CodeModel m, int viewCols, int viewRows, int tabSize, boolean wrap)
	{
		this.model = m;
		this.viewCols = viewCols;
		this.viewRows = viewRows;
		this.tabSize = tabSize;
		this.wrap = wrap;
	}


	/**
	 * Lays out {@code rowCount} paragraphs.
	 * Returns the number of paragraphs actually laid out.
	 */
	// TODO why do we need startCellIndex here?
	public int layout(int rowCount, int startIndex, int startCellIndex)
	{
		int wrapLimit = wrap ? viewCols : -1;
		int i = 0;
		int rows = 0;
		for( ; i<rowCount; i++)
		{
			CodeParagraph par = model.getParagraph(startIndex + i);
			WrapInfo p = WrapInfo.create(par, tabSize, wrapLimit);
			rows += p.getRowCount();
			if(rows > rowCount)
			{
				break;
			}
		}
		// TODO need to save [rows]
		return i;
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


	public void paintAll(GraphicsContext gx)
	{
		// TODO
	}
}
