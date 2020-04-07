// Copyright © 2020 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import goryachev.fx.CssStyle;
import goryachev.fx.FX;
import goryachev.fx.HPane;
import goryachev.fxtexteditor.FxTextEditor;
import goryachev.fxtexteditor.Marker;
import goryachev.fxtexteditor.SelectionSegment;
import java.util.List;
import javafx.beans.binding.StringBinding;
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
		// FIX use segments instead
		caret.textProperty().bind(new StringBinding()
		{
			{
				bind(ed.selectionSegmentsProperty());
			}
			
			
			protected String computeValue()
			{
				List<SelectionSegment> sel = ed.selectionSegmentsProperty();
				int sz = sel.size();
				if(sz == 0)
				{
					return null;
				}
				
				// TODO format
				// TODO glyph index?
				SelectionSegment seg = sel.get(sz - 1);
				Marker m = seg.getCaret();
				return m.getLine() + " : " + m.getCharIndex();
			}
		});
	}
}
