# FxTextEditor

![screenshot](https://github.com/andy-goryachev/FxTextEditor/blob/master/doc/screenshot.png)


## Why ##

Nearly all Java text editors, Swing and JavaFX alike, suffer from one deficiency: inability to work with large 
data models such as logs or query results.

The goal of this project is to provide a professional FX text component capable of handling billions of 
lines of styled unicode text, possibly with long lines, while providing syntax highlighting, multiple carets and multiple selection segments.

This is achieved using monospaced font and rendering model, unlike its sibling, [FxEditor](https://github.com/andy-goryachev/FxEditor).


## Features

* supports up to 2^31 lines of text
* efficiently handles very long lines
* supports syntax highlight
* basic text attributes: bold, italic, underline, strikethrough
* variable tab width


## Warning

This project is currently in the early development stage.  Do not even think of using it in production.
 

## License

This project and its source code is licensed under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0) and you should feel free to make adaptations of this work. Please see the included LICENSE file for further details.
