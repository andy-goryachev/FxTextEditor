// Copyright © 2024-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.codepad.model;


/**
 * CodePad Text Model.
 */
public abstract class CodeModel
{
	/**
	 * Returns the number of paragraphs. 
	 */
	public abstract int size();
	
	
	/**
	 * Returns the {@link CodeParagraph} at the specified {@code index}.
	 * <p>
	 * This index should never go beyond the number of paragraphs as determined by {@link #size()}.
	 * Doing so might result in an undetermined behavior (most likely an exception).
	 */
	public abstract CodeParagraph getParagraph(int index);

	
	public CodeModel()
	{
	}
	
	
	/**
	 * Returns the plain text (always non-null) of the paragraph at the specified {@code index}.
	 * <p>
	 * This index should never go beyond the number of paragraphs as determined by {@link #size()}.
	 * Doing so might result in an undetermined behavior (most likely an exception).
	 * @implNote
	 * The default implementation retrieves the {@link CodeParagraph} and obtains the plain text from it.
	 * The subclasses may override this method if a more efficient way of obtaining the plain text exist. 
	 */
	public String getPlainText(int index)
	{
		CodeParagraph p = getParagraph(index);
		return p.getPlainText();
	}
}
