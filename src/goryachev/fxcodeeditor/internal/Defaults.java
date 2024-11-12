// Copyright © 2024-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fxcodeeditor.internal;
import javafx.geometry.Insets;
import javafx.scene.text.Font;


/**
 * Defaults.
 */
public class Defaults
{
	public static final Insets CONTENT_PADDING = null;
	
	public static final Font FONT = Font.font("Monospaced", -1);
	
	public static final double MIN_HEIGHT = 20;
	
	public static final double MIN_WIDTH = 20;
	
	public static final double PREF_HEIGHT = 150;
	
	public static final double PREF_WIDTH = 100;
	
	/**
	 * Number of paragraphs to lay out before and after the view port
	 * to form a sliding window, for the purpose of smoother scrolling.
	 */
	public static final int SLIDING_WINDOW_HALF = 100;
	
	public static final int TAB_SIZE = 8;
	
	public static final int TAB_SIZE_MAX = 32;
	
	public static final boolean WRAP_TEXT = false;
}
