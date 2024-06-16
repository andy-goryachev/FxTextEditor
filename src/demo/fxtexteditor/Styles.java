// Copyright © 2016-2024 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import goryachev.fx.CommonStyles;
import goryachev.fx.FxStyleSheet;
import goryachev.fx.Theme;


/**
 * this is how a style sheet is generated.
 */
public class Styles
	extends FxStyleSheet
{
	public Styles()
	{
		Theme theme = Theme.current();
		
		add
		(
			// common fx styles
			new CommonStyles(),
			
			selector(StatusBar.LABEL).defines
			(
				padding(1, 1, 1, 5)
			)
		);
	}
}
