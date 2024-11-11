// Copyright © 2017-2024 Andy Goryachev <andy@goryachev.com>
package demo.fxcodeeditor;
import goryachev.fx.FX;
import goryachev.fx.FxAction;
import goryachev.fx.FxComboBox;
import goryachev.fx.FxDump;
import goryachev.fx.FxFramework;
import goryachev.fx.FxMenuBar;
import goryachev.fx.FxToolBar;
import goryachev.fx.FxWindow;
import goryachev.fxcodeeditor.FxCodeEditor;
import goryachev.fxcodeeditor.model.CodeModel;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;


/**
 * FxEditor Demo Window.
 */
public class FxCodeEditorDemoWindow
	extends FxWindow
{
	public final FxCodeEditorDemoPane mainPane;
	public final BorderPane content;
	public final StatusBar statusBar;
	protected final FxComboBox<DemoModels> modelSelector = new FxComboBox();
	protected final FxComboBox fontSelector = new FxComboBox();
	
	
	public FxCodeEditorDemoWindow()
	{
		super("FxCodeEditorDemoWindow");
		
		modelSelector.setItems(DemoModels.values());
		modelSelector.valueProperty().addListener((s,p,c) -> onModelSelectionChange(c));
		FX.setName(modelSelector, "modelSelector");

		fontSelector.setItems
		(
			"9",
			"10",
			"11",
			"12",
			"13",
			"14",
			"16",
			"18",
			"20",
			"24",
			"28",
			"32"
		);
		fontSelector.valueProperty().addListener((s,p,c) -> onFontChange(c));
		FX.setName(fontSelector, "fontSelector");
		
		mainPane = new FxCodeEditorDemoPane();
		
		content = new BorderPane();
		content.setTop(createToolbar());
		content.setCenter(mainPane);
		
		statusBar = new StatusBar();
		
		setTitle("FxCodeEditor Demo");
		setTop(createMenu());
		setCenter(content);
		setBottom(statusBar);
		setSize(600, 700);
		
		fontSelector.setEditable(true);
		fontSelector.select("12");

//		LocalSettings.get(this).
//			add("LINE_WRAP", editor().wrapTextProperty());
//			add("SHOW_LINE_NUMBERS", editor().showLineNumbersProperty());
		
		statusBar.attach(editor());
		
		// debug
		FxDump.attach(this);
	}
	
	
	protected FxCodeEditor editor()
	{
		return mainPane.editor;
	}
	
	
	protected Node createMenu()
	{
		FxMenuBar m = new FxMenuBar();
//		Actions a = editor().actions;
		
		// file
		m.menu("File");
		m.item("New Window, Same Model", new FxAction(this::newWindow));
		m.separator();
		m.item("Preferences");
		m.separator();
		m.item("Exit", FxFramework::exit);
		
		// edit
		m.menu("Edit");
		m.item("Undo");
		m.item("Redo");
		m.separator();
		m.item("Cut");
//		m.item("Copy", a.copy());
		m.item("Paste");
		m.separator();
//		m.item("Select All", a.selectAll());
		m.item("Select Line");
		m.item("Split Selection into Lines");
		m.separator();
		m.item("Indent");
		m.item("Unindent");
		m.item("Duplicate");
		m.item("Delete Line");
		m.item("Move Line Up");
		m.item("Move Line Down");

		// find
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
//		m.menu("View");
//		m.item("Show Line Numbers", editor().showLineNumbersProperty());
//		m.item("Wrap Lines", editor().wrapLinesProperty());
		
		// help
		m.menu("Help");
		m.item("About");
		
		return m;
	}
	
	
	protected Node createToolbar()
	{
		FxToolBar t = new FxToolBar();
//		t.addToggleButton("wr", "wrap lines", editor().wrapTextProperty());
//		t.addToggleButton("ln", "line numbers", editor().showLineNumbersProperty());
		t.fill();
		t.add(new Label("Font:"));
		t.add(fontSelector);
		t.space();
		t.add(new Label("Model:"));
		t.space(2);
		t.add(modelSelector);
		return t;
	}
	
	
	protected void preferences()
	{
	}
	
	
	protected void newWindow()
	{
		FxCodeEditorDemoWindow w = new FxCodeEditorDemoWindow();
		w.mainPane.setModel(mainPane.getModel());
		w.open();
	}
	
	
	protected void onModelSelectionChange(DemoModels x)
	{
		CodeModel m = DemoModels.getModel(x);
		mainPane.setModel(m);
	}
	
	
	protected void onFontChange(Object x)
	{
//		int sz = Parsers.parseInt(x, 12);
//		mainPane.editor.setFontSize(sz);
	}
}