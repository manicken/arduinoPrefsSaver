
package com.manicken;

import cc.arduino.Constants;
import cc.arduino.i18n.Languages;
import org.apache.commons.compress.utils.IOUtils;
import processing.app.BaseNoGui;

import processing.app.helpers.PreferencesHelper;
import processing.app.helpers.PreferencesMap;
import processing.app.legacy.PApplet;
import processing.app.legacy.PConstants;
import processing.app.PreferencesData;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import static processing.app.I18n.format;
import static processing.app.I18n.tr;

public class MyPreferencesData {

    public File preferencesFile;
    public PreferencesMap prefs = new PreferencesMap();

    public boolean init(File file) {
        if (file == null)
        {
            System.err.println("Error File cannot be null @ MyPreferencesData.init(File");
            return false;
        }
        preferencesFile = file;
    
        try {
            BaseNoGui.getPlatform().fixPrefsFilePermissions(preferencesFile);
        } catch (Exception e) {
            //ignore
        }
    
        // Start with a clean slate
        prefs = new PreferencesMap();
    
        if (preferencesFile.exists()) {
            // load the previous preferences file
            try {
                prefs.load(preferencesFile);
            } catch (IOException ex) {
                BaseNoGui.showError(tr("Error reading sketch preferences"),
                "Error reading the sketch preferences file. "+
                    preferencesFile.getAbsolutePath(), ex);
                    return false;
            }
        }
        else
        {
            System.out.println("Skect Pref file not found!");
            return false;
        }
        fixPreferences();
        return true;
    }
    
    public File getPreferencesFile() {
        return preferencesFile;
    }
    
    private void fixPreferences() {
        String baud = get("serial.debug_rate");
        if ("14400".equals(baud) || "28800".equals(baud)) {
          set("serial.debug_rate", "9600");
        }
    }
    public String get(String attribute) {
        return prefs.get(attribute);
    }
    public void set(String attribute, String value) {
        prefs.put(attribute, value);
      }

    public void mergeIntoGlobalPreferences(boolean printDebug)
    {
        System.out.println("Merging Sketch Pref. into Global Pref.\n");
        String[] keys = prefs.keySet().toArray(new String[0]);
        for (String key : keys) {
            String value = prefs.get(key);
            if (printDebug)
            System.out.println("merging: " + key +"="+ value);
            PreferencesData.set(key, value);
        }
        System.out.println("");
    }
    
    /**
     * this is not used at the moment
     */
    public void save() { // 

        // on startup, don't worry about it
        // this is trying to update the prefs for who is open
        // before Preferences.init() has been called.
        if (preferencesFile == null) return;
    
        // Fix for 0163 to properly use Unicode when writing preferences.txt
        PrintWriter writer = null;
        try {
            writer = PApplet.createWriter(preferencesFile);
        
            String[] keys = prefs.keySet().toArray(new String[0]);
            Arrays.sort(keys);
            for (String key : keys) {
                if (key.startsWith("runtime."))
                continue;
                writer.println(key + "=" + prefs.get(key));
            }
        
            writer.flush();
        } catch (Throwable e) {
            System.err.println(format(tr("Could not write preferences file: {0}"), e.getMessage()));
            return;
        } finally {
            IOUtils.closeQuietly(writer);
        }
    
        try {
            BaseNoGui.getPlatform().fixPrefsFilePermissions(preferencesFile);
        } catch (Exception e) {
          //ignore
        }
    }
}
