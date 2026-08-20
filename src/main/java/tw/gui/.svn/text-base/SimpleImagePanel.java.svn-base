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
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import javax.swing.JPanel;

import tw.master.utils.Utils;


/**
 * Simple allround image panel.
 *
 * @author   Thomas Welsch
 */
@SuppressWarnings("serial")
public class SimpleImagePanel extends JPanel {

    private BufferedImage img;

    private ScaleMode     scalemode;

    /**
     * @author    Thomas Welsch
     */
    public enum ScaleMode {
        SCALE_NONE,
        SCALE_CENTER,
        SCALE_FIT,
        SCALE_FIT_CENTER,
        SCALE_WIDTH,
        SCALE_WIDTH_CENTER,
        SCALE_HIGHT,
        SCALE_HIGHT_CENTER,
    };

    // ---------------------------------------------------------------------------------------------

    /**
     * Create new image panel.
     *
     * @param img
     * 			Image to display
     *
     * @param scalemode
     * 			Mode how to handle scaling
     */
    public SimpleImagePanel(final BufferedImage img, final ScaleMode scalemode) {
        super();
        this.img = img;
        this.scalemode = scalemode;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Create new image panel with automatic centered scale to fit (SCALE_FIT_CENTER mode).
     *
     * @param img
     * 			Image to display
     */
    public SimpleImagePanel(final BufferedImage img) {
        this(img, ScaleMode.SCALE_FIT_CENTER);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Create new image panel without image but with scalemode. The image can later be set with
     * setImage().
     *
     * @param scalemode
     * 			Mode how to handle scaling
     */
    public SimpleImagePanel(final ScaleMode scalemode) {
        this(null, scalemode);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Setting the new image to display.
     *
     * @param newImg
     *          Image to display
     */
    public void setImage(final BufferedImage newImg) {
        this.img = newImg;
        repaint();
    }

    // ---------------------------------------------------------------------------------------------

    private RescaleOp rop = null;

    /**
     * Re-colorize the image with the given color factors.
     *
     * @param r		Red   factor (0..1)
     * @param g		Green factor (0..1)
     * @param b		Blue  factor (0..1)
     * @param a		Alpha factor (0..1)
     */
    public void setColorizer(float r, float g, float b, float a) {
        float[] scales = {
                r, g, b, a
        };

        float[] offsets = new float[4];
        rop = new RescaleOp(scales, offsets, null);
        Utils.repaintCtrl(this, 0);
    }


    /**
     * Set the overlay color.
     *
     * @param c New overlay color
     */
    public void setColorizer(Color c) {

        setColorizer(c.getRed() / 255f,
                c.getGreen() / 255f,
                c.getBlue() / 255f,
                c.getAlpha() / 255f);

    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Clear the colorize filter.
     */
    public void clearColorizer() {
        if (rop != null) {
            rop = null;
            repaint();
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void paintComponent(Graphics g) {

        if (Utils.skipPaint(this)) return;

        int w = this.getWidth();
        int h = this.getHeight();

        //g.setColor(Color.YELLOW);
        g.clearRect(0, 0, w, h);
        //super.paintComponents(g);

        Graphics2D g2 = (Graphics2D) g;

        if (img == null) return;

        int iw = img.getWidth();
        int ih = img.getHeight();

        int xp, yp;

        double fx, fy, f;

        fy = (double) h / ih;
        fx = (double) w / iw;

        Utils.setQuality(g2);

        //scalemode = ScaleMode.SCALE_FIT_CENTER;
        switch (scalemode) {
            case SCALE_NONE:
                break;
            case SCALE_FIT:
                g2.scale(fx, fy);
                break;
            case SCALE_FIT_CENTER:
                if (fy < fx) f = fy;
                else
                    f = fx;

                xp = w / 2 - (int) (iw * f) / 2;
                yp = h / 2 - (int) (ih * f) / 2;

                g2.translate(xp, yp);
                g2.scale(f, f);
                break;
            case SCALE_CENTER:
                xp = w / 2 - iw / 2;
                yp = h / 2 - ih / 2;
                g2.translate(xp, yp);
                break;
            case SCALE_HIGHT:
                fx = fy;
                g2.scale(fx, fy);
                break;
            case SCALE_HIGHT_CENTER:
                f = fy;

                xp = w / 2 - (int) (iw * f) / 2;
                yp = h / 2 - (int) (ih * f) / 2;

                g2.translate(xp, yp);
                g2.scale(f, f);
                break;
            case SCALE_WIDTH:
                fy = fx;
                g2.scale(fx, fy);
                break;
            case SCALE_WIDTH_CENTER:
                f = fx;

                xp = w / 2 - (int) (iw * f) / 2;
                yp = h / 2 - (int) (ih * f) / 2;

                g2.translate(xp, yp);
                g2.scale(f, f);
                break;
            default:
                throw new IllegalArgumentException();

        } // switch

        if (rop != null) {
            g2.drawImage(img, rop, 0, 0);
        } else {
            g2.drawImage(img, 0, 0, null);
        }

    } // paintComponent

    // ---------------------------------------------------------------------------------------------

    /**
     * Setting the scaling mode for the image.
     *
     * @param newScalemode     The new Scalemode
     */
    public void setScaleMode(ScaleMode newScalemode) {
        this.scalemode = newScalemode;
        repaint();
    }

} // of class SimpleImagePanel
