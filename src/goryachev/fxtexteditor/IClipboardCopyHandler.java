// Copyright © 2020-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.fxtexteditor;


/**
 * Clipboard Copy Handler Interface.
 */
@FunctionalInterface
public interface IClipboardCopyHandler
{
	public Object copy(FxTextEditorModel fxTextEditorModel, int startLine, int startPos, int endLine, int endPos) throws Exception;
}
