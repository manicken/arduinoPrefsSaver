/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
  Part of the Processing project - http://processing.org

  Copyright (c) 2008 Ben Fry and Casey Reas
  Copyright (c) 2020 Jannik Leif Simon Svensson (1984)- Sweden

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package com.manicken;

import java.io.IOException;
import java.io.File;
import java.io.PrintStream;
import java.io.FileWriter;
import java.io.StringWriter;

import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;


import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.nio.file.Path;

import processing.app.Base;
import processing.app.BaseNoGui;
import processing.app.Editor;
import processing.app.tools.Tool;
import processing.app.Sketch;
import processing.app.PreferencesData;

//import static processing.app.I18n.tr;

import com.manicken.MyPreferencesData;
import com.manicken.SelectionDialog;
import com.manicken.Reflect;

/**
 * Example Tools menu entry.
 */
public class manickenPrefSaver implements Tool {
	boolean debugPrint = false;

	Editor editor;// for the plugin
	Sketch sketch; // for the plugin

	MyPreferencesData myPrefs;
	
	JMenu toolsMenu; // for the plugin, uses reflection to get
	
	String thisToolMenuTitle = "Manicken Pref Saver";
	//String rootDir;
	String prefsFileName = "preferences.txt";
	String prefsFileNameInactive = "preferences.inactive.txt";
	
	boolean started = false;
	
	public void init(Editor editor) { // required by tool loader
		this.editor = editor;
		myPrefs = new MyPreferencesData();

		editor.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
			  init();
			}
		});
		
	}

	public void run() {// required by tool loader
		// this is not used when using custom menu (see down @initMenu())
	}

	public String getMenuTitle() {// required by tool loader
		return thisToolMenuTitle;
	}

	private void Activate()
	{
		
	}

	private void Deactivate()
	{

	}

	private boolean FileExists(String pathname)
	{
		return new File(pathname).exists();
	}

	private void init() {
		System.out.println("init manicken Preferences Saver");
		//System.out.println("BaseNoGui.getToolsFolder()=" + BaseNoGui.getToolsFolder());
		//System.out.println("BaseNoGui.getSketchbookFolder()=" + BaseNoGui.getSketchbookFolder());
		
		
		//rootDir = GetArduinoRootDir();
		//System.out.println("rootDir="+rootDir);
		try{
			sketch = this.editor.getSketch();
			
			initMenu();

			started = true;

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("manickenPrefSaver could not start!!!");
			return;
		}
	}

	private void initMenu()
	{
		toolsMenu = (JMenu) Reflect.GetField("toolsMenu", this.editor);

		int thisToolIndex = GetMenuItemIndex(toolsMenu, thisToolMenuTitle);
		JMenu thisToolMenu = new JMenu(thisToolMenuTitle);		
		toolsMenu.insert(thisToolMenu, thisToolIndex+1);
		toolsMenu.remove(thisToolIndex);
		
		JMenuItem newItem = null;

		newItem = new JMenuItem("Activate/SaveCurrent");
		thisToolMenu.add(newItem);
		newItem.addActionListener(event -> Activate());
		
		newItem = new JMenuItem("Deactivate");
		thisToolMenu.add(newItem);
		newItem.addActionListener(event -> Deactivate());

		newItem = new JMenuItem("Select Items To Save");
		thisToolMenu.add(newItem);
		newItem.addActionListener(event -> ShowSelectionDialog());
	}

	public void ShowSelectionDialog() { // 
		SelectionDialog sd = new SelectionDialog();
		//cd.txtServerport.setText(Integer.toString(serverPort));
		//cd.chkAutostart.setSelected(autostart);
		
	   int result = JOptionPane.showConfirmDialog(editor, sd, "manickenPrefSaver Config" ,JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			
		if (result == JOptionPane.OK_OPTION) {
			//serverPort = Integer.parseInt(cd.txtServerport.getText());
			//autostart = cd.chkAutostart.isSelected();
			debugPrint = sd.chkDebugMode.isSelected();
		} else { System.out.println("Cancelled"); }
	}

	public int GetMenuItemIndex(JMenu menu, String name) {
		//System.out.println("try get menu: " + name);
		for ( int i = 0; i < menu.getItemCount(); i++) {
			//System.out.println("try get menu item @ " + i);
			JMenuItem item = menu.getItem(i);
			if (item == null) continue; // happens on seperators
			if (item.getText() == name)
				return i;
		}
		return -1;
	}

	public String GetArduinoRootDir() {
		try {
			File file = BaseNoGui.getToolsFolder();//new File(API_WebServer.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			return file.getParentFile().getAbsolutePath();//.getParentFile().getParentFile().getParent();
		} catch (Exception e) { e.printStackTrace(); return ""; }
	}

	public String GetJarFileDir() {
		try {
			File file = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
			return file.getParent();
		}catch (Exception e) { e.printStackTrace(); return ""; }
	}

	public String loadFile(String name) {
		File file = new File(sketch.getFolder(), name);
		boolean exists = file.exists();
		if (exists) {
			
			try {
				String content = new Scanner(file).useDelimiter("\\Z").next();
				return content;
			} catch (Exception e) { e.printStackTrace(); return ""; }
		}
		else {
			System.out.println(name + " file not found!");
			return "";
		}
	}

	public void saveFile(String name, String contents) {
		try {
            // Constructs a FileWriter given a file name, using the platform's default charset
            FileWriter file = new FileWriter(sketch.getFolder() + "/" + name);
			file.write(contents);
			file.close();
        } catch (IOException e) { e.printStackTrace(); }
	}

	
}
