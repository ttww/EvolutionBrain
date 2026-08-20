/*
 *  This file is part of the EvolutionBrain project.
 *
 *  From http://nadeausoftware.com/articles/2008/01/java_tip_how_add_zebra_background_stripes_jtable
 *
 *  Copyright (c) 2008 by David Robert Nadeau. All rights reserved.
 *
 *  Slightly modified for the EvolutionBrain project by Thomas Welsch
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
import java.util.Vector;

import javax.swing.JTable;

/**
 * Creates a JTable with alternating background.
 *
 * @author David Robert Nadeau
 */
public class ZebraJTable extends JTable {

    private static final long serialVersionUID = 1L;

    private java.awt.Color[]  rowColors        = new java.awt.Color[2];

    private boolean           drawStripes      = false;

    /**
     * Constructs new JTable with alternating background.
     */
    public ZebraJTable() {
        super();
    }

    /**
     * Constructs new JTable with alternating background.
     *
     * @param numRows       Number of rows
     * @param numColumns    Number of columns
     */
    public ZebraJTable(final int numRows, final int numColumns) {
        super(numRows, numColumns);
    }

    /**
     * Constructs new JTable with alternating background.
     *
     * @param rowData       Array with data
     * @param columnNames   Array with column names
     */
    public ZebraJTable(final Object[][] rowData, final Object[] columnNames) {
        super(rowData, columnNames);
    }

    /**
     * Constructs new JTable with alternating background.
     *
     * @param dataModel     The data model to use for
     */
    public ZebraJTable(final javax.swing.table.TableModel dataModel) {
        super(dataModel);
    }

    /**
     * Constructs new JTable with alternating background.
     *
     * @param dataModel     The data model to use for
     * @param columnModel   The column mode to use
     */
    public ZebraJTable(
            final javax.swing.table.TableModel dataModel,
            final javax.swing.table.TableColumnModel columnModel) {
        super(dataModel, columnModel);
    }

    /**
     * Constructs new JTable with alternating background.
     *
     * @param dataModel         The data model to use for
     * @param columnModel       The column mode to use
     * @param selectionModel    The selection model to use
     */
    public ZebraJTable(
            final javax.swing.table.TableModel dataModel,
            final javax.swing.table.TableColumnModel columnModel,
            final javax.swing.ListSelectionModel selectionModel) {
        super(dataModel, columnModel, selectionModel);
    }

    /**
     * Constructs new JTable with alternating background.
     *
     * @param rowData       The row data
     * @param columnNames   The column names to use
     */
    public ZebraJTable(
            final Vector<? extends Vector> rowData,
            final Vector<?> columnNames) {
        super(rowData, columnNames);
    }

    /** Add stripes between cells and behind non-opaque cells. */
    /* (non-Javadoc)
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    public void paintComponent(java.awt.Graphics g) {
        drawStripes = isOpaque();
        if (!drawStripes) {
            super.paintComponent(g);
            return;
        }

        // Paint zebra background stripes
        updateZebraColors();
        final java.awt.Insets insets = getInsets();
        final int w = getWidth() - insets.left - insets.right;
        final int h = getHeight() - insets.top - insets.bottom;
        final int x = insets.left;
        int y = insets.top;
        int defRowHeight = 16; // A default for empty tables
        final int nItems = getRowCount();
        for (int i = 0; i < nItems; i++, y += defRowHeight) {
            defRowHeight = getRowHeight(i);
            g.setColor(rowColors[i & 1]);
            g.fillRect(x, y, w, defRowHeight);
        }
        // Use last row height for remainder of table area
        final int nRows = nItems + (insets.top + h - y) / defRowHeight;
        for (int i = nItems; i < nRows; i++, y += defRowHeight) {
            g.setColor(rowColors[i & 1]);
            g.fillRect(x, y, w, defRowHeight);
        }
        final int remainder = insets.top + h - y;
        if (remainder > 0) {
            g.setColor(rowColors[nRows & 1]);
            g.fillRect(x, y, w, remainder);
        }

        // Paint component
        setOpaque(false);
        super.paintComponent(g);
        setOpaque(true);
    }

    /** Add background stripes behind rendered cells. */
    /* (non-Javadoc)
     * @see javax.swing.JTable#prepareRenderer(javax.swing.table.TableCellRenderer, int, int)
     */
    @Override
    public java.awt.Component prepareRenderer(
            javax.swing.table.TableCellRenderer renderer, int row, int col) {
        final java.awt.Component c = super.prepareRenderer(renderer, row, col);
        if (drawStripes && !isCellSelected(row, col))
            c.setBackground(rowColors[row & 1]);
        return c;
    }

    /** Add background stripes behind edited cells. */
    /* (non-Javadoc)
     * @see javax.swing.JTable#prepareEditor(javax.swing.table.TableCellEditor, int, int)
     */
    @Override
    public java.awt.Component prepareEditor(
            javax.swing.table.TableCellEditor editor, int row, int col) {
        final java.awt.Component c = super.prepareEditor(editor, row, col);
        if (drawStripes && !isCellSelected(row, col))
            c.setBackground(rowColors[row & 1]);
        return c;
    }

    /** Force the table to fill the viewport's height. */
    /* (non-Javadoc)
     * @see javax.swing.JTable#getScrollableTracksViewportHeight()
     */
    @Override
    public boolean getScrollableTracksViewportHeight() {
        final java.awt.Component p = getParent();
        if (!(p instanceof javax.swing.JViewport))
            return false;
        return ((javax.swing.JViewport) p).getHeight() > getPreferredSize().height;
    }

    /** Compute zebra background stripe colors. */
    private void updateZebraColors() {
        rowColors[0] = getBackground();
        if (rowColors[0] == null) {
            rowColors[0] = Color.white;
            rowColors[1] = Color.white;
            return;
        }

        final Color sel = getSelectionBackground();
        if (sel == null) {
            rowColors[1] = rowColors[0];
            return;
        }
        final float[] bgHSB = Color.RGBtoHSB(
                rowColors[0].getRed(),
                rowColors[0].getGreen(),
                rowColors[0].getBlue(), null);
        final float[] selHSB = java.awt.Color.RGBtoHSB(
                sel.getRed(),
                sel.getGreen(),
                sel.getBlue(), null);

        rowColors[1] = Color.getHSBColor(
                selHSB[1] == 0.0 || selHSB[2] == 0.0 ? bgHSB[0] : selHSB[0],
                        0.1f * selHSB[1] + 0.9f * bgHSB[1], bgHSB[2]
                                                                  + (bgHSB[2] < 0.5f ? 0.05f : -0.05f));
    }
} // of class ZebraJTable
