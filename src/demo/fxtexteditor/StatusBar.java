// Copyright © 2020-2024 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import goryachev.fx.CssStyle;
import goryachev.fx.FX;
import goryachev.fx.Formatters;
import goryachev.fx.FxFormatter;
import goryachev.fx.HPane;
import goryachev.fxtexteditor.FxTextEditor;
import goryachev.fxtexteditor.Marker;
import goryachev.fxtexteditor.SelectionSegment;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Label;


/**
 * FxTextEditor Status Bar.
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
		
		trailing = FX.label(LABEL_TRAILING, Pos.CENTER_RIGHT, FxTextEditorDemoApp.COPYRIGHT);
		
		add(leading);
		fill();
		add(trailing);
	}


	public void attach(FxTextEditor ed)
	{
		leading.textProperty().bind(Bindings.createStringBinding
		(
			() ->
			{
				SelectionSegment seg = ed.getSelectedSegment();
				if(seg == null)
				{
					return null;
				}
				
				FxFormatter fmt = Formatters.integerFormatter();
				
				Marker m = seg.getCaret();
				String line = fmt.format(m.getLine() + 1);
				String col = fmt.format(ed.getColumnAt(m) + 1);
				String ix = fmt.format(m.getCharIndex());
				
				return String.format("line: %s  column: %s  char: %s", line, col, ix);  
			},
			ed.selectionSegmentProperty()
		));
	}
}
