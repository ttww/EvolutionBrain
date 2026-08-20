/*
 *  This file is part of the EvolutionBrain project.
 *
 *  Copyright (c) 2011 by Thomas Welsch (ttww@gmx.de). All rights reserved.
 *
 *  EvolutionBrain is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  EvolutionBrain is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with EvolutionBrain.  If not, see <http://www.gnu.org/licenses/>.
 */

package tw.master.utils;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Window;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.zip.CRC32;

import javax.swing.JSplitPane;

/**
 * @author Thomas Welsch
 * 
 *         This Class handles preferences.
 */
public class PreferencesUtils {

    /**
     * After so much days old window and split positions are removed from the prefs.
     */
    static final int			MAX_SCREEN_PREF_AGE_DAYS	= 200;

    /**
     * CRC of screen configuration (number, sizes and positions of screens).
     */
    static String				displayId					= "";

    /**
     * The root path for the Spontech applications in the property file.
     */
    private static String		ROOT_PATH					= "com.st";

    /**
     * The root path for the Spontech applications in the property file.
     */
    public static final String	USER_PATH					= "users";

    /**
     * Preferences storage starting with com.st.
     *
     * Preferences will stored in the user directory, for example with mac os: ~/Library/Preferences/com.apple.java.util.prefs.plist in an
     * XML file.
     *
     */
    private static Preferences	prefsInstance				= null;

    private static Preferences getPreferences() {
//		System.err.println("get:  "+Utils.getStacktrace());
        if (prefsInstance == null) {
            prefsInstance = Preferences.userRoot().node(ROOT_PATH);
        }
        return prefsInstance;
    }

    /**
     * Your application may wanted to set its own property root.<BR>
     * If you wants the properties to be stored in its own file, your have to set a root with at least four components (<code>a.b.c.d</code>
     * ). The root could not be set when the {@link #prefsInstance} has already been instanced.
     *
     * @param root
     */
    public static void setPreferencesRoot(String root) {
//		System.err.println(Utils.getStacktrace());
        if (prefsInstance != null) {
            throw new IllegalStateException("Can't set property root to " + root + " because it has already been set to " + ROOT_PATH);
        }
        ROOT_PATH = root;
    }

    // ---------------------------------------------------------------------------------------------

    // static {
    // getScreenId();
    // }

    // ---------------------------------------------------------------------------------------------

    /**
     * Last time the displayId was updated.
     */
    static long	lastCalcScreenId	= 0;

    /**
     * Calculates the screen identifier.
     */
    private static synchronized String getScreenId() {
        // -----------------------------------------------------------------------------------------
        // Calculate the ID if we don't have one or if the ID is to old (against changing monitor
        // configurations:
        // -----------------------------------------------------------------------------------------
        if (displayId != null && System.currentTimeMillis() - lastCalcScreenId < 1000) return displayId;

        // -----------------------------------------------------------------------------------------
        // Collect all Screens and build an CRC for identify the actual screen configuration later:
        // -----------------------------------------------------------------------------------------
        GraphicsDevice[] gda = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

        StringBuffer sb = new StringBuffer();
        for (GraphicsDevice gd : gda) {
            sb.append(gd.getIDstring());
            for (GraphicsConfiguration cc : gd.getConfigurations()) {
                sb.append(cc.getBounds());
            } // for

            sb.append(gd.getDefaultConfiguration().getBounds());

            // Logger.Log("#"+d+": "+gd.getIDstring()+"  "+gd.getDefaultConfiguration().getBounds());
        }

        CRC32 c = new CRC32();
        c.update(sb.toString().getBytes());
        displayId = String.format("%x", new Long(c.getValue()));

        lastCalcScreenId = System.currentTimeMillis();

        return displayId;
    }

    // ---------------------------------------------------------------------------------------------

    private static String replaceToUL(String s, char oldC) {
        if (s.indexOf(oldC) != -1) s = s.replace(oldC, '_');
        return s;
    }

    private static String cleanup(String s) {
        s = replaceToUL(s, ' ');
        s = replaceToUL(s, '"');
        s = replaceToUL(s, '\'');
        s = replaceToUL(s, '\\');
        s = replaceToUL(s, '=');
        s = replaceToUL(s, '|');
        return s;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Cleanup the preference key string and ensure proper max. length.
     * <p>
     * If the key is trimmed, then a CRC is added to the key.
     *
     * @param pref
     *            Key for preferences
     * @param sub
     *            Additional part of key, used for length calculation
     *
     * @return Result, cleaned up and < Preferences.MAX_KEY_LENGTH (=80)
     */
    private static String toPref(String pref, String sub) {

        pref = cleanup(pref);

        int subLen = sub != null ? sub.length() : 0;
        int hasLen = pref.length() + subLen + 2;
        int toMuch = hasLen - Preferences.MAX_KEY_LENGTH;

        if (toMuch > 0) {

            CRC32 c = new CRC32();
            c.update(pref.getBytes());
            String crc = String.format("%x", new Long(c.getValue()));

            // System.out.println("OLD Pref=|"+pref);

            pref = pref.substring(0, pref.length() - toMuch - crc.length() - 1) + "." + crc;
            // System.out.println("NEW Pref=|"+pref);
        }
        return pref;
    }

    private static String toPref(String pref) {
        return toPref(pref, null);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * This method adds substrings to the preferences.
     *
     * @param pref
     * @param sub
     */
    private static String addSubString(String pref, String sub) {
        pref = toPref(pref);
        if (sub != null && sub.length() != 0) return pref + "." + sub;

        return pref;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Loading a string from the preferences.
     *
     * @param pref
     *            Full preference key
     * @param def
     *            default value
     *
     * @return Loaded value or default value
     */
    public static String get(String pref, String def) {

        String ret = getPreferences().get(toPref(pref), def);

        return ret;
    }

    // ---------------------------------------------------------------------------------------------

    public static void removeProperty(String pref) {
        getPreferences().remove(toPref(pref));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Saving a string to the preferences.
     * <p>
     * Don't forget to call flushPreferences() when you are finished !
     * <p>
     *
     * @param pref
     *            Full preference key
     * @param value
     *            New value
     */
    public static void put(String pref, String value) {
        getPreferences().put(toPref(pref), value);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Loading a string from the preferences.
     *
     * @param pref
     *            Main preference key
     * @param sub
     *            Sub preference key
     * @param def
     *            default value
     *
     * @return Loaded value or default value
     */
    public static String get(String pref, String sub, String def) {

        String ret = getPreferences().get(addSubString(pref, sub), def);

        // Logger.Log("Load "+pref + "." + sub+" : "+ret);

        return ret;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Saving a string to the preferences.
     * <p>
     * Don't forget to call flushPreferences() when you are finished !
     * <p>
     *
     * @param pref
     *            Main preference key
     * @param sub
     *            Sub preference key
     * @param value
     *            New value
     */
    public static void put(String pref, String sub, String value) {
        // System.out.println("Save "+addSubString(pref,sub)+" : "+value);
        getPreferences().put(addSubString(pref, sub), value);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Loading an integer from the preferences.
     *
     * @param pref
     *            Main preference key
     * @param sub
     *            Sub preference key
     * @param def
     *            default value
     *
     * @return Loaded value or default value
     */
    public static int getInt(String pref, String sub, int def) {
        int ret = getPreferences().getInt(addSubString(pref, sub), def);

        // Logger.Log("Load "+addSubString(pref,sub)+" : "+ret);

        return ret;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Saving an integer to the preferences.
     *
     * @param pref
     *            Main preference key
     * @param sub
     *            Sub preference key
     * @param value
     *            New value
     */
    public static void putInt(String pref, String sub, int value) {
        // System.out.println("Save "+addSubString(pref,sub)+" : "+value);
        getPreferences().putInt(addSubString(pref, sub), value);
    }

    // ---------------------------------------------------------------------------------------------


    /**
     * Loading an float from the preferences.
     *
     * @param pref
     *            Main preference key
     * @param sub
     *            Sub preference key
     * @param def
     *            default value
     *
     * @return Loaded value or default value
     */
    public static float getFloat(String pref, String sub, float def) {
        float ret = getPreferences().getFloat(addSubString(pref, sub), def);

        // Logger.Log("Load "+addSubString(pref,sub)+" : "+ret);

        return ret;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Saving an float to the preferences.
     *
     * @param pref
     *            Main preference key
     * @param sub
     *            Sub preference key
     * @param value
     *            New value
     */
    public static void putFloat(String pref, String sub, float value) {
        // System.out.println("Save "+addSubString(pref,sub)+" : "+value);
        getPreferences().putFloat(addSubString(pref, sub), value);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Saving a date to the preferences.
     *
     * @param pref
     *            Main preference key
     * @param sub
     *            Sub preference key
     * @param value
     *            New value
     */
    private static void putDate(String pref, String sub, SimpleDate value) {
        getPreferences().put(addSubString(pref, sub), value.toString());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Loading a date from the preferences.
     *
     * @param pref
     *            Main preference key
     * @param sub
     *            Sub preference key
     * @param def
     *            default value
     *
     * @return Loaded value or default value
     */
    public static SimpleDate getDate(String pref, String sub, SimpleDate def) {
        String s = getPreferences().get(addSubString(pref, sub), def != null ? def.toString() : null);

        if (s == null) return null;
        // Logger.Log("Load "+addSubString(pref,sub)+" : "+ret);

        return new SimpleDate(s);
    }

    // ---------------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------

    private static void ensureInsideOneScreens(Rectangle orgRect, Rectangle prefRect) {

        GraphicsDevice[] gs = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

        // Get size of each screen
        for (GraphicsDevice gd : gs) {
            for (GraphicsConfiguration cc : gd.getConfigurations()) {

                Rectangle screen = cc.getBounds();
                // Full inside, so it's ok....
                // System.err.println("Screen = "+screen+"  r = "+prefRect+"  in = "+screen.contains(prefRect));
                if (screen.contains(prefRect)) return;
            } // for
        } // for

        // Not full in one screen, use the defaults....
        prefRect.x = orgRect.x;
        prefRect.y = orgRect.y;
        prefRect.width = orgRect.width;
        prefRect.height = orgRect.height;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Try to initialize the given window to the last saved position and window size.
     * <p>
     * This method installs an WindowListener and ShutdownHool to automatically saving the window properties.
     *
     * @param window
     *            The chosen window...
     * @param withSize
     *            Restore the size also...
     */
    private static void restorePosition(Window window, boolean withSize) {

        String name = window.getName();
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("Cannot save window position without window name");
        }

        name = "window." + getScreenId() + "." + name;

        //System.err.println("Restore Window |"+window+"|");

        int x = window.getX();
        int y = window.getY();
        int w = window.getWidth();
        int h = window.getHeight();

        Rectangle orgRect = new Rectangle(x, y, w, h);

        x = getInt(name, "x", x);
        y = getInt(name, "y", y);
        w = getInt(name, "w", w);
        h = getInt(name, "h", h);

        // Window-Size to small, don't use it !
        if (w < 5 || h < 5) {
            w = orgRect.width;
            h = orgRect.height;
        }
        // System.out.println("Location = " + x + "/" + y);

        // Bring window into screen (Maybe to simple fix for multiscreens...)
        // To simple.... simple fix: disable x check ;-)
        // if (y < 0) y = 0;
        // if (x < 0) x = 0;

        Rectangle prefRect = new Rectangle(x, y, w, h);

        ensureInsideOneScreens(orgRect, prefRect);

        if (true) {
            window.setLocation(prefRect.x, prefRect.y);

            if (withSize) {
                window.setSize(prefRect.width, prefRect.height);
                window.setPreferredSize(new Dimension(prefRect.width, prefRect.height));
            }
        }
        window.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                PreferencesUtils.savePosition(e.getWindow());
            }
        });

        Runtime.getRuntime().addShutdownHook(new ShutdownWindowHook(window));

    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Try to initialize the given window to the last saved position and window size.
     * <p>
     * This method installs an WindowListener and ShutdownHool to automatically saving the window properties.
     *
     * @param window
     *            The chosen window...
     */
    public static void restorePosition(Window window) {
        restorePosition(window, true);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Try to initialize the given window to the last saved position.
     * <p>
     * This method installs an WindowListener and ShutdownHool to automatically saving the window properties.
     *
     * @param window
     *            The chosen window...
     */
    public static void restoreLocation(Window window) {
        restorePosition(window, false);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Saves the window properties in the preferences.
     *
     * @param window
     */
    public static void savePosition(Window window) {
        String name = "window." + getScreenId();

        putDate(name, "date", new SimpleDate());

        name += "." + window.getName();

        // System.err.println("Save Window |"+window+"|");

        putInt(name, "x", window.getX());
        putInt(name, "y", window.getY());
        putInt(name, "w", window.getWidth());
        putInt(name, "h", window.getHeight());

        flushPreferences();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Method restores last used position for the component and all children. Component registers to save the value at shutdown.
     *
     * @param split
     * @param defaultLocation
     */
    public static void restoreSplit(JSplitPane split, int defaultLocation) {
        String name = split.getName();
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("Cannot save split position without split name");
        }

        name = "split." + getScreenId() + "." + name;

        int location = split.getDividerLocation();
        if (location == 0) location = defaultLocation;

        // System.err.println("get split |" + name + "| : " + location);

        location = getInt(name, "location", location);

        if (location == -1) location = defaultLocation;
        // System.err.println("   -->  |" + name + "| : " + location);

        // System.out.println("location (" + name + ") = " + location);
        split.setDividerLocation(location);
        // save split panel position on exit of VM
        Runtime.getRuntime().addShutdownHook(new ShutdownSplitHook(split));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Saves the split location properties in the preferences.
     *
     */
    public static void saveSplitLocation(JSplitPane split) {

        int location = split.getLastDividerLocation();

        if (location < 0) return; // Panel was not on screen, don't save

        String name = "split." + getScreenId() + "." + split.getName();
        putDate(name, "date", new SimpleDate());

        name += "." + split.getName();

        // System.err.println("Save split |"+name+"| : "+location);
        putInt(name, "location", location);

        flushPreferences();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Flushes the preferences.
     */
    public static void flushPreferences() {
        if (prefsInstance == null) return;
        try {
            cleanupOldEntrys();
            getPreferences().flush();
        }
        catch (BackingStoreException e) {
            e.printStackTrace();
            System.err.println("Can't store preferences...");
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Collects all properties with the specified start name.
     *
     * @param startsWith
     *            Match string, or null / "" for all
     *
     * @return List of properties
     *
     */
    public static List<String> getProperties(String startsWith) {
        String[] keys;
        try {
            keys = getPreferences().keys();
            ArrayList<String> ret = new ArrayList<String>();

            if (startsWith.length() == 0) startsWith = null;

            for (String key : keys) {
                if (startsWith == null || key.startsWith(startsWith)) ret.add(key);
            }

            return ret;
        }
        catch (BackingStoreException e) {
            throw new IllegalArgumentException(e);
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Cleanup old split and window entrys.
     */
    private static void cleanupOldEntrys() {
        try {
            HashSet<String> checkList = new HashSet<String>();

            String[] keys = getPreferences().keys();

            String window = "window.";
            String split = "split.";

            // -------------------------------------------------------------------------------------
            // Collect all preferences for window/split:
            // -------------------------------------------------------------------------------------
            for (String key : keys) {
                if (key.startsWith(window)) {
                    int end = key.indexOf('.', window.length());
                    if (end != -1) checkList.add(key.substring(0, end));
                }
                if (key.startsWith(split)) {
                    int end = key.indexOf('.', split.length());
                    if (end != -1) checkList.add(key.substring(0, end));
                }
            }

            // -------------------------------------------------------------------------------------
            // Check for date:
            // -------------------------------------------------------------------------------------
            for (String key : checkList) {
                // System.err.println("Look |"+key+"|");
                SimpleDate d = getDate(key, "date", null);

                boolean toOld = false;

                if (d != null) {
                    int age = (int) ((System.currentTimeMillis() - d.toMillis()) / (1000L * 60 * 60 * 24));
                    if (age > MAX_SCREEN_PREF_AGE_DAYS) toOld = true;
                    // System.err.println("Found age for "+key+":  "+d+"  = "+age);
                }
                else {
                    toOld = true;
                    // System.err.println("NO    age for "+key+":  "+d);
                }

                // ---------------------------------------------------------------------------------
                // Remove old entries:
                // ---------------------------------------------------------------------------------
                if (toOld) {
                    // System.err.println("Want delete |"+key+"|");

                    for (String delKey : keys) {
                        if (delKey.startsWith(key)) {
                            getPreferences().remove(delKey);
                            // System.err.println("   delKey |"+delKey+"|");
                        }
                    } // for
                }

            }

        }
        catch (BackingStoreException e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------------------------------------------------

} // of class

// -------------------------------------------------------------------------------------------------

/**
 * @author Thomas Welsch
 * 
 *         Helper class for storing the window positions on application exit.
 */
class ShutdownWindowHook extends Thread {

    Window	window;

    /**
     * Create new shutdown hook for a window.
     *
     * @param window
     */
    public ShutdownWindowHook(Window window) {
        this.window = window;
        setName("ShutdownWindowHook");

    }

    // ----------------------------------------------------------------------------------------------

    /**
     * run() method getting started by ShutdownHook.
     *
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        PreferencesUtils.savePosition(window);
    }

}

// -------------------------------------------------------------------------------------------------

/**
 * @author Thomas Welsch
 * 
 *         Helper class for storing the split positions on application exit.
 */
class ShutdownSplitHook extends Thread {

    JSplitPane	split;

    /**
     * Create new shutdown hook for a split pane.
     *
     * @param split
     */
    public ShutdownSplitHook(JSplitPane split) {

        setName("ShutdownSplitHook");
        this.split = split;
    }

    // ----------------------------------------------------------------------------------------------

    /**
     * run() method getting started by ShutdownHook.
     *
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        PreferencesUtils.saveSplitLocation(split);
    }

}
