// Copyright © 2017-2020 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import goryachev.common.util.Parsers;
import goryachev.fx.CPane;
import goryachev.fx.FX;
import goryachev.fx.FxAction;
import goryachev.fx.FxComboBox;
import goryachev.fx.FxDump;
import goryachev.fx.FxMenuBar;
import goryachev.fx.FxPopupMenu;
import goryachev.fx.FxToolBar;
import goryachev.fx.FxWindow;
import goryachev.fx.internal.LocalSettings;
import goryachev.fxtexteditor.Actions;
import goryachev.fxtexteditor.FxTextEditor;
import goryachev.fxtexteditor.FxTextEditorModel;
import demo.fxtexteditor.res.DemoModels;
import javafx.scene.Node;
import javafx.scene.control.Label;


/**
 * FxEditor Demo Window.
 */
public class MainWindow
	extends FxWindow
{
	public final FxAction prefsAction = new FxAction(this::preferences);
	public final MainPane mainPane;
	public final CPane content;
	public final StatusBar statusBar;
	protected final FxComboBox modelSelector = new FxComboBox();
	protected final FxComboBox fontSelector = new FxComboBox();
	
	public MainWindow()
	{
		super("MainWindow");
		
		modelSelector.setItems((Object[])DemoModels.getAll());
		modelSelector.valueProperty().addListener((s,p,c) -> onModelSelectionChange(c));
		
		fontSelector.setItems
		(
			"9",
			"12",
			"18",
			"24"
		);
		fontSelector.valueProperty().addListener((s,p,c) -> onFontChange(c));
		
		mainPane = new MainPane();
		
		content = new CPane();
		content.setTop(createToolbar());
		content.setCenter(mainPane);
		
		statusBar = new StatusBar();
		
		setTitle("FxTextEditor");
		setTop(createMenu());
		setCenter(content);
		setBottom(statusBar);
		setSize(600, 700);
		
		fontSelector.setEditable(true);
		fontSelector.select("12");

		LocalSettings.get(this).
			add("LINE_WRAP", editor().wrapLinesProperty()).
			add("SHOW_LINE_NUMBERS", editor().showLineNumbersProperty()).
			add("MODEL", modelSelector).
			add("FONT_SIZE", fontSelector);
		
//		FX.setPopupMenu(editor(), this::createPopupMenu);
		
		statusBar.attach(editor());
		
		// debug
		FxDump.attach(this);
	}
	
	
	protected FxTextEditor editor()
	{
		return mainPane.editor;
	}
	
	
	protected FxPopupMenu createPopupMenu()
	{
		FxPopupMenu m = new FxPopupMenu();
		m.item("Cut");
		m.item("Copy");
		m.item("Paste");
		return m;
	}
	
	
	protected Node createMenu()
	{
		FxMenuBar m = new FxMenuBar();
		Actions a = editor().actions;
		
		// file
		m.menu("File");
//		m.item("Growing Model", tailMode);
		m.item("New Window, Same Model", new FxAction(this::newWindow));
		m.separator();
		m.item("Preferences", prefsAction);
		m.separator();
		m.item("Exit", FX.exitAction());
		
//		// edit
		m.menu("Edit");
		m.item("Undo");
		m.item("Redo");
		m.separator();
		m.item("Cut");
		m.item("Copy", a.copy);
		m.item("Paste");
		m.separator();
		m.item("Select All", a.selectAll);
		m.item("Select Line");
		m.item("Split Selection into Lines");
		m.separator();
		m.item("Indent");
		m.item("Unindent");
		m.item("Duplicate");
		m.item("Delete Line");
		m.item("Move Line Up");
		m.item("Move Line Down");

//		// find
		m.menu("Find");
		m.item("Find");
		m.item("Regex");
		m.item("Replace");
		m.separator();
		m.item("Find Next");
		m.item("Find Previous");
		m.item("Find and Select");
		m.separator();
		m.item("Go to Line");
		
		// view
		m.menu("View");
		m.item("Show Line Numbers", editor().showLineNumbersProperty());
		m.item("Wrap Lines", editor().wrapLinesProperty());
		
		// help
		m.menu("Help");
		m.item("About");
		
		return m;
	}
	
	
	protected Node createToolbar()
	{
		FxToolBar t = new FxToolBar();
		t.toggleButton("wrap", editor().wrapLinesProperty());
		t.toggleButton("num", editor().showLineNumbersProperty());
		t.fill();
		t.add(new Label("Font:"));
		t.add(fontSelector);
		t.add(new Label("Model:"));
		t.add(modelSelector);
		return t;
	}
	
	
	protected void preferences()
	{
	}
	
	
	protected void newWindow()
	{
		MainWindow w = new MainWindow();
		w.mainPane.setModel(mainPane.getModel());
		w.open();
	}
	
	
	protected void onModelSelectionChange(Object x)
	{
		FxTextEditorModel m = DemoModels.getModel(x);
		mainPane.setModel(m);
	}
	
	
	protected void onFontChange(Object x)
	{
		int sz = Parsers.parseInt(x, 12);
		mainPane.editor.setFontSize(sz);
	}
}