// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
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
import javafx.scene.control.Label;


/**
 * Status Bar.
 */
public class StatusBar
	extends HPane
{
	public static final CssStyle PANE = new CssStyle("StatusBar_PANE");
	public static final CssStyle LABEL = new CssStyle("StatusBar_LABEL");
	public final Label caret;
	
	
	public StatusBar()
	{
		FX.style(this, PANE);
		
		caret = FX.label(LABEL);
		
		add(caret);
		fill();
	}


	public void attach(FxTextEditor ed)
	{
		caret.textProperty().bind(Bindings.createStringBinding
		(
			() ->
			{
				SelectionSegment seg = ed.getSelectedSegment();
				if(seg == null)
				{
					return null;
				}
				
				FxFormatter fmt = Formatters.getIntegerFormatter();
				
				// TODO glyph index?
				Marker m = seg.getCaret();
				
				return 
					"line: " +
					fmt.format(m.getLine() + 1) + 
					", char: " + 
					fmt.format(m.getCharIndex());
			},
			ed.selectionSegmentProperty()
		));
	}
}
