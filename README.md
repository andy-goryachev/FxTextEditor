# FxTextEditor

![screenshot](https://github.com/andy-goryachev/FxTextEditor/blob/master/doc/screenshot.png)


## Why ##

Nearly all Java text editors, Swing and JavaFX alike, suffer from one deficiency: inability to work with large 
data models such as logs or query results.

The goal of this project is to provide a professional FX text component capable of handling billions of 
lines of styled unicode text, possibly with very long lines, by using monospaced font and rendering model - 
unlike its sibling, [FxEditor](https://github.com/andy-goryachev/FxEditor).

This component is being developed for the [AccessPanel](https://github.com/andy-goryachev/AccessPanelPublic) project.


## How

Please refer to a simple demo application: [FxTextEditorDemoApp.java](https://github.com/andy-goryachev/FxTextEditor/blob/master/src/demo/fxtexteditor/FxTextEditorDemoApp.java).


## Main Features

* supports up to 2^31 lines of text
* efficiently handles very long lines
* supports syntax highlight
* basic text attributes: bold, italic, underline, strikethrough
* variable tab width


## Warning

This project is currently in the early development stage.  Do not even think of using it in production.


## Requirements

Requires JavaFX 17+.


## License

This project and its source code is licensed under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0) and you should feel free to make adaptations of this work. Please see the included LICENSE file for further details.
