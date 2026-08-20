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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import tw.master.utils.Utils;


/**
 *	Just a normal JPanel with default GridBagLayout() and a paintComponent() which allows debugging
 *  via Utils.debugGridBagLayout().
 *
 * @author Thomas Welsch
 */
public class GridBagPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    /**
     * Show the grid for this panel.
     */
    public boolean            gridDebug        = false;

    // ---------------------------------------------------------------------------------------------

    /**
     * Create a JPanel with GridBagLayout.
     */
    public GridBagPanel() {
        super();
        setLayout(new GridBagLayout());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * @param isDoubleBuffered
     *              State of double buffering
     */
    public GridBagPanel(final boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        setLayout(new GridBagLayout());
    }

    // ---------------------------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gridDebug) {
            debugGridBagLayout(this, g);
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Paint GridBagLayout grid raster on Component. Thes method can be called at the end of a
     * paintComponent() method with the actual panel and graphics context.<p>
     * Depending on on the isGridDebug() return the grid is paint over the component. This is
     * switchable with CTRL-F in develop mode from StEventQueue.
     *
     * @param p		actual panel
     * @param g		actual graphics. No state changes are made
     *
     */
    public static void debugGridBagLayout(JPanel p, Graphics g) {

        LayoutManager layout = p.getLayout();
        if (!(layout instanceof GridBagLayout)) {
            System.err.println(p + ": Need GridBagLayout !");
            //throw new IllegalArgumentException(p+": Need GridBagLayout !");
            return;
        }

        Graphics2D g2 = (Graphics2D) g;

        Font oldFont = g2.getFont();
        Color oldColor = g2.getColor();
        RenderingHints oldHints = g2.getRenderingHints();

        GridBagLayout gbl = (GridBagLayout) layout;

        int[][] b = gbl.getLayoutDimensions();

        int ym = b[1].length;
        int xm = b[0].length;

        g2.setColor(Color.ORANGE);

        //System.err.println("xm "+xm);
        //System.err.println("ym "+ym);

        Point zp = gbl.getLayoutOrigin();

        Utils.setQuality(g2);
        g2.setFont(new Font("Dialog", Font.PLAIN, 9));

        int xp = zp.x;
        for (int x = 0; x < xm; x++) {
            int yp = zp.y;
            for (int y = 0; y < ym; y++) {
                g2.setColor(Color.ORANGE);
                g2.drawLine(xp, yp, xp + b[0][x], yp);
                g2.drawLine(xp, yp, xp, yp + b[1][y]);

                String s = x + "/" + y;

                g2.setColor(Color.BLACK);
                for (int xi = -2; xi <= 2; xi++) {
                    for (int yi = -1; yi <= 1; yi++) {
                        g2.drawString(s, xi + xp + 3, yi + yp + 10);
                    }
                }

                g2.setColor(Color.ORANGE);
                g2.drawString(s, xp + 3, yp + 10);
                yp += b[1][y];
            }
            xp += b[0][x];
        }

        // Restore modified values
        g2.setRenderingHints(oldHints);
        g2.setFont(oldFont);
        g2.setColor(oldColor);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * @return the gridDebug
     */
    public boolean isGridDebug() {
        return gridDebug;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * @param gridDebug the gridDebug to set
     */
    public void setGridDebug(boolean gridDebug) {
        this.gridDebug = gridDebug;
    }

}   // of class GridBagPanel
