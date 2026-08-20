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
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Shape;


/**
 * This Class extends a JPanel with the GridBagLayout-Manager. After the components are drawn, all
 * GridBagLayout lines (odd/even) are over paint with a (best transparent :-) ) color. The odd/even
 * colors can be set.
 *
 * @author Thomas Welsch
 */
public class GridBagStripePanel extends GridBagPanel {

    private static final long serialVersionUID = 1L;

    private boolean           showStripes      = true;

    private int               stripsStart      = 0;

    private Color             evenColor;

    private Color             oddColor;

    // ---------------------------------------------------------------------------------------------

    /**
     * Create a new JPanel with a GridBag layout.
     */
    public GridBagStripePanel() {
        super();
        this.oddColor = GuiDefaults.STRIPE_COLOR;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Not for public use, we use GridBag layout.
     *
     * @param layout
     *      LayoutManager to use
     */
    @SuppressWarnings("unused")
    private GridBagStripePanel(final LayoutManager layout) {
        super();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Setting the color for even cols.
     *
     * @param newEvenCol Color for even cols
     */
    public void setEvenColor(Color newEvenCol) {
        this.evenColor = newEvenCol;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Setting the color for odd cols.
     *
     * @param newOddCol Color for odd cols
     */
    public void setOddColor(Color newOddCol) {
        this.oddColor = newOddCol;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Getting the color for even cols.
     *
     * @return  actual color for even cols
     */
    public Color getEvenColor() {
        return this.evenColor;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Getting the color for odd cols.
     *
     * @return  actual color for odd cols
     */
    public Color getOddColor() {
        return this.oddColor;
    }

    // ---------------------------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {

        if (showStripes) {
            Shape c = g.getClip();

            super.paintComponent(g);

            g.setClip(c);

            if (evenColor == null && oddColor == null) return;

            GridBagLayout gbl = (GridBagLayout) getLayout();

            int[][] b = gbl.getLayoutDimensions();

            int ym = b[1].length;

            Point zp = gbl.getLayoutOrigin();
            int yp = zp.y;
            for (int y = stripsStart; y < ym; y++) {
                if ((y - stripsStart) % 2 == 0) {
                    if (evenColor != null) {
                        g.setColor(evenColor);
                        g.fillRect(0, yp, getWidth(), b[1][y]);
                    }
                } else {
                    if (oddColor != null) {
                        g.setColor(oddColor);
                        g.fillRect(0, yp, getWidth(), b[1][y]);
                    }
                }
                yp += b[1][y];
            }
        } else {
            super.paintComponent(g);
        }

        if (gridDebug) debugGridBagLayout(this, g);

    }

    // ---------------------------------------------------------------------------------------------

    /**
     * @return the showStripes
     */
    public boolean isShowStripes() {
        return showStripes;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * @param showStripes the showStripes to set
     */
    public void setShowStripes(boolean showStripes) {
        this.showStripes = showStripes;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * @return the stripsStart
     */
    public int getStripsStart() {
        return stripsStart;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * @param stripsStart the stripsStart to set
     */
    public void setStripsStart(int stripsStart) {
        this.stripsStart = stripsStart;
    }

}   // of class GridBagStripePanel
