// Copyright © 2020-2024 Andy Goryachev <andy@goryachev.com>
package demo.codepad;
import goryachev.codepad.CodePad;
import goryachev.codepad.SelectionRange;
import goryachev.codepad.TextPos;
import goryachev.fx.CssStyle;
import goryachev.fx.FX;
import goryachev.fx.Formatters;
import goryachev.fx.FxFormatter;
import goryachev.fx.HPane;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Label;


/**
 * CodePad Status Bar.
 */
public class StatusBar
	extends HPane
{
	public static final CssStyle PANE = new CssStyle("StatusBar_PANE");
	public static final CssStyle LABEL_LEADING = new CssStyle("StatusBar_LABEL");
	public static final CssStyle LABEL_TRAILING = new CssStyle("StatusBar_LABEL");

	private final Label leading;
	private final Label trailing;
	
	
	public StatusBar()
	{
		FX.style(this, PANE);
		
		leading = FX.label(LABEL_LEADING);
		
		trailing = FX.label(LABEL_TRAILING, Pos.CENTER_RIGHT, CodePadDemoApp.COPYRIGHT);
		
		add(leading);
		fill();
		add(trailing);
	}


	public void attach(CodePad ed)
	{
		leading.textProperty().bind(Bindings.createStringBinding
		(
			() ->
			{
				SelectionRange sel = ed.getSelection();
				if(sel == null)
				{
					return null;
				}
				
				FxFormatter fmt = Formatters.integerFormatter();
				
				TextPos p = sel.getCaret();
				String line = fmt.format(p.getLineNumber() + 1);
				String col = "?"; //fmt.format(ed.getColumnAt(m) + 1);
				String ix = fmt.format(p.getColumn());
				
				return String.format("line: %s  column: %s  char: %s", line, col, ix);  
			},
			ed.selectionProperty()
		));
	}
}
