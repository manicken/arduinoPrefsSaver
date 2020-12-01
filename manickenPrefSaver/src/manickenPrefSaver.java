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
import java.util.prefs.Preferences;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JMenuBar;
import javax.swing.MenuElement;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.nio.file.Path;

import processing.app.Base;
import processing.app.BaseNoGui;
import processing.app.Editor;
import processing.app.tools.Tool;
import processing.app.Sketch;
import processing.app.PreferencesData;
import processing.app.helpers.PreferencesMap;
import java.util.Map;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;


import static processing.app.I18n.tr;

import com.manicken.MyPreferencesData;
import com.manicken.SelectionDialog;
import com.manicken.Reflect;

/**
 * Example Tools menu entry.
 */
public class manickenPrefSaver implements Tool {
	boolean debugPrint = false;

	boolean useSeparateExtensionsMainMenu = true; // good for development for quick access

	Base base;// for the API uses reflection to get
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
		

		editor.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
			  init();
			}
		});
		
	}

	private void PrintCurrentPreferencesMap()
	{
		PreferencesMap pm = PreferencesData.getMap();
		String[] keys = pm.keySet().toArray(new String[0]);
		Arrays.sort(keys);
		for (String key : keys) {
			
			System.out.println(key + "=" + pm.get(key));
		}
	}

	public void run() {// required by tool loader
		// this is not used when using custom menu (see down @initMenu())
	}

	public String getMenuTitle() {// required by tool loader
		return thisToolMenuTitle;
	}

	private void Activate()
	{
		if (!RenameFile(prefsFileNameInactive, prefsFileName)) System.out.println("@Activate codsn not rname");
		//PrintCurrentPreferencesMap();
		PreferencesMap pm = PreferencesData.getMap();
		String[] keys = pm.keySet().toArray(new String[0]);
		Arrays.sort(keys);
		StringBuilder sb = new StringBuilder();
		for (String key : keys) {
			if (key.startsWith("serial.") || 
				key.startsWith("target_") ||
				key.startsWith("custom_") ||
				key.equals("board"))
				sb.append(key + "=" + pm.get(key) + "\r\n");
		}
		saveFile(prefsFileName, sb.toString());
	}

	private void Deactivate()
	{
		if (!RenameFile(prefsFileName, prefsFileNameInactive)) System.out.println("@Activate codsn not rname");
	}

	private boolean FileExists(String pathname)
	{
		return new File(pathname).exists();
	}

	private void init() {
		System.out.println("init manicken Preferences Saver");

		try{
			
			base = (Base) Reflect.GetField("base", this.editor);
			
			if (useSeparateExtensionsMainMenu)
				initAtSeparateExtensionsMenu();
			else
				initAtToolsMenu();

				
				sketch = this.editor.getSketch();

				myPrefs = new MyPreferencesData();

				LoadPrevPreferences();



			started = true;

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("manickenPrefSaver could not start!!!");
			return;
		}
	}

	private void LoadPrevPreferences()
	{
		File file = new File(sketch.getFolder() + "/" + prefsFileName);
		if (file.exists())
		{
			System.out.println("sketch pref file exists");
			myPrefs.init(file);
			myPrefs.mergeIntoGlobalPreferences();
			//toolsMenu = (JMenu) Reflect.GetField("toolsMenu", this.editor);
			//base.getBoardsCustomMenus().stream().forEach(toolsMenu::add);
			try {
				base.rebuildBoardsMenu(); // throws exception

				base.onBoardOrPortChange();
				base.rebuildImportMenu((JMenu)Reflect.GetStaticField("importMenu", Editor.class));
        		base.rebuildExamplesMenu((JMenu)Reflect.GetStaticField("examplesMenu", Editor.class));
       			base.rebuildProgrammerMenu();
			}
			catch (Exception e)
			{

			}
		}
	}


	private void initAtSeparateExtensionsMenu()
	{
		JMenuBar menubar = editor.getJMenuBar();
		int existingExtensionsMenuIndex = GetMenuBarItemIndex(menubar, tr("Extensions"));
		int toolsMenuIndex = GetMenuBarItemIndex(menubar, tr("Tools"));
		JMenu extensionsMenu = null;
		
		if (existingExtensionsMenuIndex == -1)
			extensionsMenu = new JMenu(tr("Extensions"));
		else
			extensionsMenu = (JMenu)menubar.getSubElements()[existingExtensionsMenuIndex];

		JMenu thisToolMenu = new JMenu(thisToolMenuTitle);	

		if (existingExtensionsMenuIndex == -1)
			menubar.add(extensionsMenu, toolsMenuIndex+1);
		menubar.revalidate(); // "repaint" menu bar with the new item
		extensionsMenu.add(thisToolMenu);
		// create new special menu
		CreatePluginMenu(thisToolMenu);

		// remove original menu at the moment sometimes buggy
		//JMenu toolsMenu = (JMenu) Reflect.GetField("toolsMenu", this.editor);
		//int thisToolMenuIndex = GetMenuItemIndex(toolsMenu, thisToolMenuTitle);
		//toolsMenu.remove(thisToolMenuIndex);
		//toolsMenu.revalidate();
	}

	private void initAtToolsMenu()
	{
		toolsMenu = (JMenu) Reflect.GetField("toolsMenu", this.editor);
		int thisToolIndex = GetMenuItemIndex(toolsMenu, thisToolMenuTitle);
		JMenu thisToolMenu = new JMenu(thisToolMenuTitle);
		// create new special menu
		CreatePluginMenu(thisToolMenu);
		// replace original menu
		toolsMenu.remove(thisToolIndex);
		toolsMenu.insert(thisToolMenu, thisToolIndex);
	}

	private void CreatePluginMenu(JMenu thisToolMenu)
	{
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

	/**
	 * Experimental way of getting tools menu, not working at the moment
	 * @param menuBar
	 * @param name
	 * @return
	 */
	public int GetMenuBarItemIndex(JMenuBar menuBar, String name) {
		//System.out.println("try get menu: " + name);
		MenuElement[] items = menuBar.getSubElements();
		for ( int i = 0; i < items.length; i++) {
			//System.out.println("try get menu item @ " + i);
			JMenu menu = (JMenu)items[i];
			if (items[i] == null) continue; // happens on seperators

			//System.out.println("menu.getText(): "+ menu.getText());

			if (menu.getText() == name)
				return i;
		}
		return -1;
	}

	/**
	 * Just a simplifier to load files from the sketch folder
	 * @param name
	 * @param contents
	 */
	public void saveFile(String name, String contents) {
		try {
            // Constructs a FileWriter given a file name, using the platform's default charset
            FileWriter file = new FileWriter(sketch.getFolder() + "/" + name);
			file.write(contents);
			file.close();
        } catch (IOException e) { e.printStackTrace(); }
	}

	public boolean RenameFile(String fromName, String toName)
	{
		// File (or directory) with old name
		File file = new File(sketch.getFolder() + "/" + fromName);

		if (!file.exists()){ System.out.println("RenameFile first file dont"); return false;}

		// File (or directory) with new name
		File file2 = new File(sketch.getFolder() + "/" + toName);

		if (file2.exists()){ System.out.println("RenameFile second file exist"); return false; }

		// Rename file (or directory)
		return file.renameTo(file2);
	}
}
