// Copyright © 2019-2021 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;


/**
 * FxTextEditor Model Listener.
 * 
 * The idea is that after each event, the model indexes change.
 * The clients should query the model for new information, using new text row indexes.
 */
public interface FxTextEditorModelListener
{
	/**
	 * The text between two positions has changed: either deleted, replaced, or inserted.
	 * <pre>
	 * Before:
	 *   startLine ->  TTTTTTT|DDDDD                      | startPos = 7
	 *                 DDDDDDDDDD                         |
	 *     endLine ->  DDDD|TTTTTTTTTTTT                  | endPos = 4
	 * 
	 * After:
	 *   startLine ->  TTTTTTT|II                         | startCharsAdded = 2
	 *                 IIII                               | linesAdded = 2
	 *     endLine ->  I|TTTTTTTTTTTT                     | endCharsAdded = 1
	 * </pre>
	 * 
	 * @param startLine - first marker line
	 * @param startPos - first marker position (0 ... length)
	 * @param startCharsAdded - number of characters inserted after startPos on the startLine
	 * @param linesAdded - number of lines inserted between (and not counting) startLine and endLine
	 * @param endLine - second marker line
	 * @param endPos - second marker position
	 * @param endCharsAdded - number of characters inserted before endPos on the endLine
	 */
	public void eventTextUpdated(int startLine, int startPos, int startCharsAdded, int linesAdded, int endPos, int endCharIndex, int endCharsAdded);

	/** 
	 * All lines in the editor have changed.  
	 * The clients should re-query the model and rebuild everything 
	 */
	public void eventAllLinesChanged();
}
