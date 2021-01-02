# arduinoPrefsSaver
Plugin for Arduino IDE that make it possible to have "sketch site" selectable settings (preferences)

## Install

* global:<br>
&nbsp;&nbsp;download this repository by either Code-Download Zip or<br>
&nbsp;&nbsp;&nbsp;&nbsp;by git clone https://github.com/manicken/arduinoPrefsSaver.git<br>
&nbsp;&nbsp;then extract/open the repository<br>

* global (into sketchbook folder (defined in Arduino IDE - Preferenses):<br>
&nbsp;&nbsp;make a new folder in the above defined sketchbook folder<br>
&nbsp;&nbsp;called tools<br>
&nbsp;&nbsp;then copy the folder manickenPrefSaver from the repository into this new "tools" folder.<br>

### Alternative install

* on windows / linux (into Arduino IDE install dir):<br>
&nbsp;&nbsp;copy folder manickenPrefSaver to [Arduino IDE install location]/tools directory<br>
&nbsp;&nbsp;ex: /Arduino-1.8.13/tools<br>

* on mac (into Arduino IDE package):<br>
&nbsp;&nbsp;In Applications right click and click on "Show Package Contents", then browse Contents -> Java -> tools<br>
&nbsp;&nbsp;by holding the Option key(copy) drag folder manickenPrefSaver from the downloaded repository to the open tools folder above<br>
&nbsp;&nbsp;select replace it you allready have an older version<br>

## Compiling (optional)

Download and Install Java SDK8 (1.8) 32bit<br>
(Arduino IDE uses Java 8 (1.8))<br>

two script is provided:<br>
&nbsp;&nbsp;for windows the .bat file<br>
&nbsp;&nbsp;for linux/mac the .sh file<br>

## Features

* saves board settings in sketch folder<br>
that is autoloaded when sketch is reloaded<br>

* auto close other editors when open new or existing sketch

## Settings 

available at MainMenu-bar:<br>
Extensions-"Manicken Pref Saver"-Settings


* Activate some debug output
* Rebuild Examples Menu (check this if something is missing or if current board examples should only be visible)
* Rebuild Library Menu (check this if something is missing or if current board examples should only be visible)
* Close Other Editors

* Select items to save (this function is for a future release, as it involves alot more coding and testing)

<br>note. "Rebuild Examples Menu" and "Rebuild Library Menu" makes the switching between sketches a bit slower.
<br>the extension settings are saved in Arduino global preferences.txt

## Requirements

none

## Known Issues

none

## Release Notes

### 1.0.0

First release<br>

issues:
* have double items on tools-menu

### 1.0.1

* fix double items at tools-menu
* add setting, that auto close other editors when open new or existing sketch

-----------------------------------------------------------------------------------------------------------