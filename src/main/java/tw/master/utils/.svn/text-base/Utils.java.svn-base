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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * @author Thomas Welsch
 *
 * Help class with mixed utilities...
 */
public class Utils {

    //----------------------------------------------------------------------------------------------

    /**
     * Returns true if the string is empty,false otherwise.
     *
     * @param	s			String to test
     * @return  true/false.
     */
    public static boolean isEmpty(String s) {
        //		System.err.println("s = |"+s+"|");
        if (s == null) return true;
        s = s.trim();
        if (s.length() == 0) return true;
        return false;
    }

    //----------------------------------------------------------------------------------------------

    /**
     * Temporarily ceases execution of Thread for the specified number of milliseconds
     * with ignoring exceptions.
     *
     * @param ms    Millisecs to sleep
     */
    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Utils.hookIgnoredException(e);
        }
    }

    //----------------------------------------------------------------------------------------------

    /**
     * Replacing all occurrences of oldPat by newPat.
     *
     * @param org       Original string
     * @param oldPat    Pattern to search for
     * @param newPat    Test to replace
     *
     * @return  Unchanged String or String with replaced values.
     */
    public static String stringReplace(String org, String oldPat, String newPat) {

        int idx = org.indexOf(oldPat);
        if (idx == -1) return org;

        StringBuffer ret = new StringBuffer(org.length() + 100);

        int lastEnd = 0;
        while (idx != -1) {
            ret.append(org.subSequence(lastEnd, idx));
            ret.append(newPat);
            lastEnd = idx + oldPat.length();
            idx = org.indexOf(oldPat, lastEnd);
        }
        ret.append(org.subSequence(lastEnd, org.length()));

        return ret.toString();
        //		return org.replaceAll(oldPat, newPat);
    }

    //----------------------------------------------------------------------------------------------

    //	public static void showBean(JPanel panel,boolean restorePos) {
    //	......
    //	}

    public static JFrame showBean(JPanel panel) {
        return showBean(panel, "BeanTestWindow");
    }

    public static JFrame showBean(JPanel panel, String name) {
        return showBean(panel, name, false);
    }

    public static JFrame showBean(JPanel panel, String name, boolean exitOnClose) {

        panel.invalidate();

        final JFrame frame = new JFrame(name);
        frame.setName(name);

        // Default Size and position, if not restored later (FirstTime)
        frame.setLocation(30, 30);
        frame.setSize(900, 600);


        if (exitOnClose)
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        /*else
        	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);*/

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(panel, BorderLayout.CENTER);

        PreferencesUtils.restorePosition(frame);
        //		PreferencesUtils.restoreLocation(frame);


        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                frame.setVisible(true);
            }
        });

        return frame;
    }


    // ---------------------------------------------------------------------------------------------

    /**
     * Read a named attribute from given string.<p>
     * The attribute value must be enclosed in "
     *
     * @param	s		String in form xxxx a1="111" a2="222"
     * @param	attr	Name of attribute, case sensitive
     *
     * @return	Found attribute or null
     */
    public static String readAttr(String s, String attr) {
        int idx = s.indexOf(attr);
        if (idx == -1) return null;

        idx += attr.length(); //	Skip parameter name

        // Skip to text...:
        while (idx < s.length() && "\\=\" \t".indexOf(s.charAt(idx)) != -1)
            idx++;

        int endP = s.indexOf('\\', idx);
        while (endP > idx && "\\\t ".indexOf(s.charAt(endP)) != -1)
            endP--;

        String ret = s.substring(idx, endP + 1);

        if (ret.length() == 0) return null;

        return ret;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Read file into List (separated at newlines).
     * <p>
     * The lines are trimmed and potentially filtered by pat parameter.
     *
     * @param	in	InputStream to read in
     * @param	pat	Pattern; lines must contain pat (exactly...)
     * @return	String list with zero or more elements...
     */
    public static List<String> readFileInList(InputStream in, String pat) throws IOException {

        List<String> ret = new ArrayList<String>();

        BufferedReader bin = new BufferedReader(new InputStreamReader(in));

        String line;
        while ((line = bin.readLine()) != null) {
            //			Logger.Warn("Read |"+line+"|");

            if (pat != null && line.indexOf(pat) == -1) continue;
            line = line.trim();

            line = replaceUniEncoding(line);
            ret.add(line.trim());

        }
        bin.close();

        return ret;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Replace all "\Uxxxx" patterns with the right unicode chararcter.
     *
     * @param	s	String to examine, eg: "test \U20ac xxx"
     *
     * @return	Converted string or original if nothing to replace. Sample: "test € xxx"
     */
    public static String replaceUniEncoding(String s) {
        int idx = s.indexOf("\\U");
        if (idx == -1) return s;

        //		System.err.println("in = "+s);

        StringBuffer sb = new StringBuffer(s.length());
        StringBuffer hex = new StringBuffer(4);

        int startP = 0;
        while (idx != -1) {
            if (startP != idx) sb.append(s.substring(startP, idx));

            startP = idx + 2; // Skip \U

            hex.setLength(0);
            hex.append(s.charAt(startP++));
            hex.append(s.charAt(startP++));
            hex.append(s.charAt(startP++));
            hex.append(s.charAt(startP++));

            char c = (char) Integer.parseInt(hex.toString(), 16);

            sb.append(c);

            idx = s.indexOf("\\U", startP);

        }
        sb.append(s.substring(startP));

        //		System.err.println("out= "+sb);

        return sb.toString();
    }


    // ---------------------------------------------------------------------------------------------

    /**
     * Transform Exception to String stacktrace.
     *
     * @param	cause     of the stacktrace for information
     *
     * @return	Standard Java stacktrace
     */
    public static String toStacktrace(Throwable cause) {
        StringWriter sw = new StringWriter();
        cause.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Get the current stacktrace as string.
     *
     * @return	Standard Java stacktrace but without the first internal lines
     */
    public static String getStacktrace() {

        // Generate:
        String ret = toStacktrace(new Throwable());

        // Filter the first two lines:
        String cr = System.getProperty("line.separator");
        int startIdx = 0;
        for (int i = 0; i < 2; i++) {
            startIdx = ret.indexOf(cr, startIdx);
            if (startIdx == -1) startIdx = 0;
            else
                startIdx++;
        }

        return ret.substring(startIdx);
    }

    // ---------------------------------------------------------------------------------------------

    public static String getActualSourceAndLine() {
        // Generate:
        String ret = toStacktrace(new Throwable());

        // Filter the first two lines:
        String cr = System.getProperty("line.separator");
        int startIdx = 0;

        for (int i = 0; i < 2; i++) {
            startIdx = ret.indexOf(cr, startIdx);
            if (startIdx == -1) startIdx = 0;
            else
                startIdx++;
        }
        int endIdx = ret.indexOf(')', startIdx);
        if (endIdx == -1) return "UNKNOWN-A";

        startIdx = ret.indexOf('(', startIdx);
        if (startIdx == -1) return "UNKNOWN-B";

        return ret.substring(startIdx + 1, endIdx);
    }

    // ---------------------------------------------------------------------------------------------

    public static String getInputText(String title, String txt, String defaultTxt) {
        String s = (String) JOptionPane.showInputDialog(
                null,
                txt,
                title,
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                defaultTxt);
        return s;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Formats a double with the give numbers before and after the ".".<p>
     * This routine always formats with "." and use the US-Locale !<p>
     * If you need locale aware formating, use String.format() directly !
     *
     * @param d
     * 			The double to convert
     * @param vk
     * 			Numbers before "."
     * @param nk
     * 			Numbers after "."
     *
     * @return
     * 			Formated double, eg. "123.12"
     */
    @SuppressWarnings("boxing")
    public static String formatDouble(double d, int vk, int nk) {
        String ret = String.format(Locale.US, "%" + vk + "." + nk + "f", d);
        ret = stringReplace(ret, ",", ".");
        return ret;
    }

    // ---------------------------------------------------------------------------------------------

    private static Map<Key, Object> renderMap;

    public static synchronized void setQuality(Graphics2D g2) {

        if (renderMap == null) {
            renderMap = new HashMap<Key, Object>();
            renderMap.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            renderMap.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            renderMap.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            renderMap.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            renderMap.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            renderMap.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        }
        g2.setRenderingHints(renderMap);

    }

    @SuppressWarnings("boxing")
    public static long showTimeUsed(long ms, String txt) {
        long now = System.currentTimeMillis();
        long used = now - ms;

        System.err.println(String.format("%6d", used) + "ms  for " + txt);
        return now;
    }

    // ---------------------------------------------------------------------------------------------

    private static HashMap<Color, String> colToName;

    /**
     * Returns the String for a color, eg. "red" for (255,0,0). If unknown (not one of the pre-
     * defined colors in Color, then the normal toString() for that color is return.
     *
     * @param c		Color
     *
     * @return		String representation of that color
     */
    public static String colorToName(Color c) {
        if (c == null) return "null";

        if (colToName == null) {
            colToName = new HashMap<Color, String>();

            colToName.put(Color.white, "white");
            colToName.put(Color.lightGray, "lightGray");
            colToName.put(Color.gray, "gray");
            colToName.put(Color.darkGray, "darkGray");
            colToName.put(Color.black, "black");
            colToName.put(Color.red, "red");
            colToName.put(Color.pink, "pink");
            colToName.put(Color.orange, "orange");
            colToName.put(Color.yellow, "yellow");
            colToName.put(Color.green, "green");
            colToName.put(Color.magenta, "magenta");
            colToName.put(Color.cyan, "cyan");
            colToName.put(Color.blue, "blue");
        }

        String ret = colToName.get(c);
        if (ret == null) ret = c.toString();

        return ret;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Strips all given characters from a string.
     *
     * @param s			Original string
     * @param toStrip	String with all charactes to remove from original
     *
     * @return	If nothing was delete, the original is give back, else a new string
     */
    public static String stripChars(String s, String toStrip) {

        String ret = s;

        StringBuffer sb = null;
        for (int i = 0; i < toStrip.length(); i++) {
            char c = toStrip.charAt(i);
            if (s.indexOf(c) == -1) continue;

            if (sb == null) sb = new StringBuffer(s.length());
            else
                sb.setLength(0);
            for (int j = 0; j < s.length(); j++) {
                char oc = s.charAt(j);
                if (oc != c) sb.append(oc);
            }
            ret = sb.toString();
        } // toStrip loop

        return ret;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Counts the number of the given chars.
     *
     * @param s     String to examine
     * @param c     Char to count
     *
     * @return  the number if found chars, or 0 if nothing is foundÏ
     */
    public static int charCount(String s, char c) {
        if (s == null || s.length() == 0) return 0;

        int n = 0;
        int l = s.length();
        for (int i = 0; i < l; i++)
            if (s.charAt(i) == c) n++;

        return n;
    }


    /**
     * Give a textual description of the given image, with size,depth, buffers.....
     *
     * @param img	Image
     *
     * @return	String with description
     */
    public static String describeImage(BufferedImage img) {
        if (img == null) return "<no image>";
        return "image(" +
        img.getWidth() +
        "*" +
        img.getHeight() +
        "*" +
        img.getColorModel().getPixelSize() +
        "," +
        " Type=" +
        typeName(img.getType()) +
        " Color=" +
        typeName(img.getColorModel().getColorSpace()) +
        " Buffer=" +
        transferTypeName(img.getRaster().getTransferType()) +
        " Bands=" +
        img.getRaster().getNumBands() +
        ")";
    }


    // The following image description stuff is extracted from Greg Guerin's
    // ImagerTrials stuff at "http://www.amug.org/~glguerin/other/index.html#ImagerTrials"

    protected static final String[] IMAGE_TYPE_NAMES =
    {
        "TYPE_CUSTOM",
        "TYPE_INT_RGB",
        "TYPE_INT_ARGB",
        "TYPE_INT_ARGB_PRE",
        "TYPE_INT_BGR",
        "TYPE_3BYTE_BGR",
        "TYPE_4BYTE_ABGR",
        "TYPE_4BYTE_ABGR_PRE",
        "TYPE_USHORT_565_RGB",
        "TYPE_USHORT_555_RGB",
        "TYPE_BYTE_GRAY",
        "TYPE_USHORT_GRAY",
        "TYPE_BYTE_BINARY",
        "TYPE_BYTE_INDEXED",
    };

    /** Indexes correspond to DataBuffer.TYPE_xxx values, except TYPE_UNDEFINED. */
    protected static final String[] BUFFER_TYPE_NAMES  =
    { "UBYTE", "USHORT", "short", "int", "float", "double" };


    /** Indexes correspond to some ColorSpace.TYPE_xxx values. */
    protected static final String[] SPACE_TYPE_NAMES   =
    { "XYZ", "Lab", "Luv", "YCbCr", "Yxy", "RGB", "Grayscale", "HSV",
        "HLS", "CMYK", "type-10", "CMY" };

    /**
     * Return name for given BufferedImage type.
     *
     * @param   imageType from BufferedImage.getType()
     *
     * @return  The type as String
     */
    public static String typeName(int imageType) {
        return IMAGE_TYPE_NAMES[imageType];
    }

    /**
     * <p>Return name for integer type and/or for BufferedImage's actual type.</p>
     * <p>If image is null, then name is for imageType alone.</p>
     * <p>If image is non-null, and its type matches imageType, then name is for imageType alone.</p>
     * <p>If image's type doesn't match imageType, then name is first for imageType,
     * followed by image's actual type name in parentheses.</p>
     *
     * @param	imageType
     * @param	image
     * @return			String name
     */
    public static String typeName(int imageType, BufferedImage image) {
        String typeName = typeName(imageType);
        if (image == null) { return typeName; }
        // Evaluate image's actual type.
        int actualType = image.getType();
        if (actualType == imageType) { return typeName; }
        return typeName + " (" + typeName(actualType) + ")";
    }

    /*
     * <p>Return name for DataBuffer or other transfer-type.</p>
     *
     * @param	bufferType
     * @return			String name
     */
    public static String transferTypeName(int bufferType) {
        if (bufferType < 0 || bufferType >= BUFFER_TYPE_NAMES.length) return "UNKNOWN";
        else
            return BUFFER_TYPE_NAMES[bufferType];
    }

    /**
     * <p>Return name for its color-space type.</p>
     *
     * @param	space
     * @return		String name
     */
    public static String typeName(ColorSpace space) {
        if (space == null) { return "NULL"; }
        int type = space.getType();
        if (type < 0 || type >= SPACE_TYPE_NAMES.length) { return "UNKNOWN"; }
        if (type >= SPACE_TYPE_NAMES.length) { return String.valueOf(type - 10) + "-color space"; }
        return SPACE_TYPE_NAMES[type];
    }


    public static BufferedImage toImage(short[] pixels, int w, int h) {

        DataBuffer db = new DataBufferUShort(pixels, w * h);
        WritableRaster raster = Raster.createInterleavedRaster(db, w, h, w, 1, new int[] { 0 }, null);

        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorModel cm = new ComponentColorModel(cs, false, false, Transparency.OPAQUE, DataBuffer.TYPE_USHORT);
        return new BufferedImage(cm, raster, false, null);
    }

    public static BufferedImage toImage(byte[] pixels, int w, int h) {
        DataBuffer db = new DataBufferByte(pixels, w * h);
        WritableRaster raster = Raster.createInterleavedRaster(db, w, h, w, 1, new int[] { 0 }, null);
        BufferedImage sampleWithIndexColorModel = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED);

//		System.out.println(sampleWithIndexColorModel.getSampleModel());

        ColorModel cm = sampleWithIndexColorModel.getColorModel();
        return new BufferedImage(cm, raster, false, null);
    }

    public static BufferedImage toImage2(byte[] pixels, int w, int h) {
        DataBuffer db = new DataBufferByte(pixels, w * h);
        WritableRaster raster = Raster.createInterleavedRaster(db, w, h, w, 1, new int[] { 0 }, null);
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorModel cm = new ComponentColorModel(cs, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        return new BufferedImage(cm, raster, false, null);
    }

    public static BufferedImage toImage(int[] pixels, int w, int h) {
        DataBuffer db = new DataBufferInt(pixels, w * h);
        WritableRaster raster = Raster.createPackedRaster(db, w, h, w, new int[] { 0xff0000, 0xff00, 0xff }, null);
        ColorModel cm = new DirectColorModel(24, 0xff0000, 0xff00, 0xff);
        return new BufferedImage(cm, raster, false, null);
    }


    public static final boolean skipPaint(Component c) {
        String name = c.getName();
        if (name == null || name.length() == 0)
            throw new IllegalArgumentException("skipPaint: Need component name, got " + name);

        if ("LDP-InnerDrawer".equals(name)) return false;

        if ("ClientImage".equals(name)) return false;
        if ("ServerImage".equals(name)) return false;
        if ("TracePanel".equals(name)) return false;
        //if ("DrawPanel".equals(name)) return false;

        if ("ArrayPanel".equals(name)) return false;
        if ("OpenGL3dImplementation".equals(name)) return false;
        if ("Brain3dDisplay".equals(name)) return false;

        System.err.println(System.currentTimeMillis() + " SP: " + name + ":   " + c);

        return false;
    }

    public static final void repaintCtrl(Component c, long ms) {
        String name = c.getName();
        if (name == null || name.length() == 0)
            throw new IllegalArgumentException("repaintCtrl: Need component name, got " + name);

        if ("LDP-InnerDrawer".equals(name)) {
            c.repaint(ms);
            return;
        }
        if ("ArrayPanel".equals(name)) {
            // System.err.println(System.currentTimeMillis() + " RC: " + name +
            // ":  " + ms + "  " + c);
            // System.err.println(Utils.getStacktrace());

            c.repaint(ms);
            return;
        }
        if ("BestCrawlerPanel".equals(name)) {
            c.repaint(ms);
            // System.err.println(System.currentTimeMillis() + " RC: " + name +
            // ":  " + ms + "  " + c);
            // System.err.println(Utils.getStacktrace());
            // System.exit(0);
            return;
        }

        if ("ClientImage".equals(name) || "ServerImage".equals(name) || "TracePanel".equals(name)) {
            c.repaint(ms);
            // System.err.println(System.currentTimeMillis() + " RC: " + name +
            // ":  " + ms + "  " + c);
            // System.err.println(Utils.getStacktrace());
            // System.exit(0);
            return;
        }

        System.err.println("NOT REPAINT!: RC: " + name + ":  " + ms + "  " + c);
        // c.repaint(ms);
    }

    public static void writeImage(BufferedImage img, String name) {
        try {
            ImageIO.write(img, "png", new File(name + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isFrontmostApplication() {
        Frame[] frames = Frame.getFrames();

        // for (Frame frame : frames)  System.out.println(String.format("FR:  %20s  %s",frame.getName(),""+frame.isActive()));
        for (Frame frame : frames)
            if (frame.isActive()) return true;

        return false;
    }

    public static BufferedImage makeBlureImage(BufferedImage img) {

        long start_ms = System.currentTimeMillis();

        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage ret = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        int r = 3;
        int wl = w - r;
        int hl = h - r;

        Graphics2D g = ret.createGraphics();

        Color c = new Color(255, 0, 0, 10);
        g.setColor(c);

        final int r2 = 2 * r + 1;

        for (int y = r; y < hl; y++) {
            for (int x = r; x < wl; x++) {
                int p = img.getRGB(x, y) & 0x00ffffff;

                if (p != 0) {
                    g.fillRect(x - r, y - r, r2, r2);
                }
            }
        }

        g.dispose();

        long stop_ms = System.currentTimeMillis();
        System.err.println("Used = " + (stop_ms - start_ms) + "ms");

        return ret;
    }

    /**
     * This is a method that can be called from catch-block in cases where the exeption should be
     * ignored. This solves PMD-Warnings and provide a hook for debug purpose.
     *
     * @param e Exception
     */
    public static void hookIgnoredException(Exception e) {
        // Do nothing
    }
}
