// Copyright © 2019-2024 Andy Goryachev <andy@goryachev.com>
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
	 * This methis is called *after* the necessary changes are made within the model.
	 * The change can be explained by the following steps: 
	 * 
	 * <pre>
	 * 1. the two positions in the model are noted: (line1, charIndex1) and (line2, charIndex2).
	 * 2. all characters between these two positions are deleted.
	 * 3. new characters are added, as described by (charsAdded1, linesAdded, charsAdded2).
	 * 
	 * Before:
	 *       line1 ->  TTTTTTT|DDDDD                      | charIndex1 = 7
	 *                 DDDDDDDDDD                         |
	 *       line2 ->  DDDD|TTTTTTTTTTTT                  | charIndex2 = 4
	 * 
	 * After:
	 *       line1 ->  TTTTTTT|II                         | charsAdded1 = 2
	 *                 IIII                               | linesAdded = 2
	 *     endLine ->  I|TTTTTTTTTTTT                     | charsAdded2 = 1
	 *     
	 * where endline = line1 + linesAdded
	 * </pre>
	 * 
	 * @param line1 - first marker line
	 * @param charIndex1 - first marker position (0 ... length)
	 * @param line2 - second marker line
	 * @param charIndex2 - second marker position
	 * @param charsAdded1 - number of characters inserted after charIndex1 on line1
	 * @param linesAdded - number of lines inserted between (and not counting) line1 and line2
	 * @param charsAdded2 - number of characters inserted before (original) charIndex2 on line2
	 */
	public void eventTextAltered(int line1, int charIndex1, int line2, int charIndex2, int charsAdded1, int linesAdded, int charsAdded2);

	/** 
	 * All lines in the editor have changed.  
	 * The clients should re-query the model and rebuild everything 
	 */
	public void eventAllLinesChanged();
}
