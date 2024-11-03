// Copyright © 2016-2024 Andy Goryachev <andy@goryachev.com>
package demo.fxtexteditor;
import goryachev.common.util.ASettingsStore;
import goryachev.common.util.FileSettingsProvider;
import goryachev.common.util.GlobalSettings;
import goryachev.fx.FxFramework;
import goryachev.fx.settings.FxSettingsSchema;
import goryachev.log.config.JsonLogConfig;
import java.io.File;
import javafx.application.Application;
import javafx.stage.Stage;


/**
 * FxTextEditor Demo Application.
 */
public class FxTextEditorDemoApp
	extends Application
{
	public static void main(String[] args)
	{
		JsonLogConfig.configure(new File("log-conf.json"), 1000);
		launch(args);
	}


	@Override
	public void init() throws Exception
	{
		// TODO change to something visible in Documents? platform-specific?
		File baseDir = new File(System.getProperty("user.home"), ".goryachev.com/FxTextEditorDemoApp");
			
//		File logFolder = new File(baseDir, "logs"); 
//		Log.init(logFolder);
		
		File settingsFile = new File(baseDir, "settings.conf");
		FileSettingsProvider p = new FileSettingsProvider(settingsFile);
		GlobalSettings.setProvider(p);
		p.loadQuiet();
	}


	@Override
	public void start(Stage stage) throws Exception
	{
		// generate stylesheet
		FxFramework.setStyleSheet(Styles::new);

		// support multiple windows
		ASettingsStore store = GlobalSettings.instance();
		FxFramework.openLayout(new FxSettingsSchema(store)
		{
			@Override
			public Stage createDefaultWindow()
			{
				return new MainWindow();
			}

			@Override
			protected Stage createWindow(String name)
			{
				return new MainWindow();
			}
		});		
	}
}
