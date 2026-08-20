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

package tw.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import tw.master.utils.PreferencesUtils;
import tw.master.utils.Rnd;

/**
 * This class provide some GUI related utilities.
 *
 * @author Thomas Welsch
 */
public final class GuiUtils {


    // ---------------------------------------------------------------------------------------------

    /**
     * Only static methods in this class.
     */
    private GuiUtils() {
        super();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Create and add a JLabel to the given panel.
     *
     * @param p     JPanel to add to
     * @param y     Grid line
     * @param txt   Label text
     *
     * @return      the created JLabel
     */
    public static JLabel addLabels(JPanel p, int y, String txt) {
        JLabel l = new JLabel(txt);
        addGrid(p, 0, y, l);
        JLabel ret = new JLabel("");

        ret.setHorizontalAlignment(SwingConstants.RIGHT);
        addGrid(p, 1, y, ret);
        return ret;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Create and add a JCheckBox to the given panel.
     *
     * @param p     JPanel to add to
     * @param y     Grid line
     * @param txt   JCheckBox text
     *
     * @return      the created JCheckBox
     */
    public static JCheckBox addCheckBox(JPanel p, int y, String txt) {
        JCheckBox ret = new JCheckBox(txt);

//		ret.setHorizontalAlignment(SwingConstants.RIGHT);
        addGrid(p, 0, y, ret);
        return ret;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Create and add a JSlider to the given panel.
     *
     * @param p     JPanel to add to
     * @param y     Grid line
     *
     * @return      the created JSlider
     */
    public static JSlider addSlider(JPanel p, int y) {
        JSlider ret = new JSlider(SwingConstants.HORIZONTAL, 1, 10, 5);

        addGrid(p, 0, y, ret);
        return ret;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Create and add a JSlider to the given panel.
     *
     * @param p     JPanel to add to
     * @param x     Grid col
     * @param y     Grid line
     * @param c     Component to add
     */
    public static void addGrid(JPanel p, int x, int y, Component c) {
        addGrid(p, x, y, c, GridBagConstraints.HORIZONTAL);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Create and add a JSlider to the given panel.
     *
     * @param p     JPanel to add to
     * @param x     Grid col
     * @param y     Grid line
     * @param c     Component to add
     * @param fill  GridBagConstraints value like fill
     */
    public static void addGrid(JPanel p, int x, int y, Component c, int fill) {
        addGrid(p, x, y, 0, 0, c, fill);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Create and add a JSlider to the given panel.
     *
     * @param p         JPanel to add to
     * @param x         Grid col
     * @param y         Grid line
     * @param weightx   GridBagConstraints weightx
     * @param weighty   GridBagConstraints weighty
     * @param c         Component to add
     * @param fill      GridBagConstraints value like fill
     */
    public static void addGrid(JPanel p, int x, int y, double weightx, double weighty, Component c, int fill) {
        GridBagConstraints gb = new GridBagConstraints();
        gb.gridx = x;
        gb.gridy = y;
        if (x == 0) gb.fill = fill;
        gb.weightx = weightx;
        gb.weighty = weighty;
        gb.ipadx = 4;
        gb.ipady = 5;
        p.add(c, gb);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Put the panel in a JFrame and show it.<p>
     * The location and size is stored and restored by the preference framework.
     *
     * @param   panel Panel to show
     *
     * @return  The allocated JFrame with the panel
     */
    public static JFrame showBean(Component panel) {
        return showBean(panel, "BeanTestWindow");
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Put the panel in a JFrame and show it.
     * The location and size is stored and restored by the preference framework.
     *
     * @param panel     Panel to show
     * @param name      The window name to use
     *
     * @return  The allocated JFrame with the panel
     */
    public static JFrame showBean(Component panel, String name) {
        return showBean(panel, name, false);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Put the panel in a JFrame and show it.
     * The location and size is stored and restored by the preference framework.
     *
     * @param panel         Panel to show
     * @param name          The window name to use
     * @param exitOnClose   Exit the application if the frame is closed
     *
     * @return  The allocated JFrame with the panel
     */
    public static JFrame showBean(Component panel, String name, boolean exitOnClose) {

        panel.invalidate();

        final JFrame frame = new JFrame(name);
        frame.setName(name);

        // Default Size and position, if not restored later (FirstTime)
        frame.setLocation(30, 30);
        frame.setSize(900, 600);


        if (exitOnClose) frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
     * Generate a random color.
     *
     * @param wantedMin   minimum sum of all R/G/B Value wanted (0..3)
     *
     * @return            Generated color
     */
    public static Color getRndColor(float wantedMin) {
        while (true) {
            float r = Rnd.rnd(0f, 1f);
            float g = Rnd.rnd(0f, 1f);
            float b = Rnd.rnd(0f, 1f);
//			float a = Rnd.rnd(0f,1f);
//			a = 1;

            if (r + g + b < wantedMin) continue;
//			return new Color(r,g,b,a);
            return new Color(r, g, b);
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Going upwards the component hierarchy until it finds a JFrame for close and dispose it.
     *
     * @param c	Component which should be closed and disposed
     */
    public static void closeTop(Component c) {
        if (c == null) throw new IllegalArgumentException("No JFrame found for closing " + c);

        if (c instanceof JFrame) {
            JFrame jf = (JFrame) c;
            jf.setVisible(false);
            jf.dispose();
            return;
        }

        closeTop(c.getParent()); // recursive upwards
    }


}   // of class GuiUtils
