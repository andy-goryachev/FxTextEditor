// Copyright © 2017-2019 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import goryachev.common.util.Parsers;
import goryachev.fx.CPane;
import goryachev.fx.FX;
import goryachev.fx.FxAction;
import goryachev.fx.FxBoolean;
import goryachev.fx.FxComboBox;
import goryachev.fx.FxDump;
import goryachev.fx.FxMenuBar;
import goryachev.fx.FxPopupMenu;
import goryachev.fx.FxToolBar;
import goryachev.fx.FxWindow;
import goryachev.fxtexteditor.FxTextEditor;
import goryachev.fxtexteditor.FxTextEditorModel;
import demo.fxtexteditor.res.DemoText;
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
	protected FxBoolean tailMode = new FxBoolean();
	protected final FxComboBox modelSelector = new FxComboBox();
	protected final FxComboBox fontSelector = new FxComboBox();
	
	public MainWindow()
	{
		super("MainWindow");
		
		modelSelector.setValues((Object[])DemoText.getAll());
		modelSelector.valueProperty().addListener((s,p,c) -> onModelSelectionChange(c));
		
		fontSelector.setValues
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
		
		setTitle("FxTextEditor");
		setTop(createMenu());
		setCenter(content);
		setSize(600, 700);
		
		// props
		bind("LINE_WRAP", editor().wrapLinesProperty());
		bind("SHOW_LINE_NUMBERS", editor().showLineNumbersProperty());
		bind("TAIL_MODE", tailMode);
		// TODO
		//bind("MODEL", modelSelector.valueProperty());

		tailMode.addListener((s,p,c) -> updateModel());
		updateModel();
		
//		FX.setPopupMenu(editor(), this::createPopupMenu);
		
		// debug
		FxDump.attach(this);
		
		FX.later(() -> modelSelector.select(DemoText.TABS_NO_UNICODE));
	}
	
	
	protected void updateModel()
	{
//		if(tailMode.get())
//		{
//			if(growingModel == null)
//			{
//				growingModel = new DemoGrowingModel();
//			}
//			model = growingModel;
//		}
//		else
//		{
//			if(largeModel == null)
//			{
//				largeModel = new DemoColorEditorModel(2_000_000_000);
//			}
//			model = largeModel;
//		}
//		editor().setModel(model);
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
		// file
		m.menu("File");
		m.separator();
		m.item("Growing Model", tailMode);
		m.item("New Window, Same Model", new FxAction(this::newWindow));
		m.separator();
		m.item("Preferences", prefsAction);
		m.separator();
		m.item("Exit", FX.exitAction());
		
//		// edit
//		m.menu("Edit");
//		m.item("Undo");
//		m.item("Redo");
//		m.separator();
//		m.item("Cut");
////		m.item("Copy", editor().copyAction);
//		m.item("Paste");
//		m.separator();
////		m.item("Select All", editor().selectAllAction);
//		m.item("Select Line");
//		m.item("Split Selection into Lines");
//		m.separator();
//		m.item("Indent");
//		m.item("Unindent");
//		m.item("Duplicate");
//		m.item("Delete Line");
//		m.item("Move Line Up");
//		m.item("Move Line Down");
//
//		// find
//		m.menu("Find");
//		m.item("Find");
//		m.item("Regex");
//		m.item("Replace");
//		m.separator();
//		m.item("Find Next");
//		m.item("Find Previous");
//		m.item("Find and Select");
		
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
		t.toggleButton("line numbers", editor().showLineNumbersProperty());
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
		w.tailMode.set(tailMode.get());
		w.open();
	}
	
	
	protected void onModelSelectionChange(Object x)
	{
		FxTextEditorModel m = DemoText.getModel(x);
		mainPane.setModel(m);
	}
	
	
	protected void onFontChange(Object x)
	{
		int sz = Parsers.parseInt(x, 12);
		mainPane.editor.setFontSize(sz);
	}
}