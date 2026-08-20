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

package tw.gui.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import tw.master.utils.Utils;



/**
 *  Display a 2 dimensional float array in a panel, mapping the values from 0..1 to green levels.
 *
 * @author Thomas Welsch
 */
@SuppressWarnings("serial")
public class ArrayPanel extends JPanel {

    /** The data. */
    private float[][] data;

//	private float[][] normData;
    private int       xm;

    private int       ym;

    // ---------------------------------------------------------------------------------------------

    /**
     * Create a new ArrayPanel with empty data set. Use setData() to set the data set later.
     */
    public ArrayPanel() {
        super();
        setName("ArrayPanel");

    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Create a new ArrayPanel with the given data set.
     *
     * @param data  2 dimensional float array with values between 0..1
     */
    public ArrayPanel(float[][] data) {
        this();
        setData(data);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Setting a new data set and trigger redisplay.
     *
     * @param data  2 dimensional float array with values between 0..1
     */
    public final void setData(float[][] data) {

        // We don't copy, because we replace very often...:
        this.data = data;

//		int oym = ym;
//		int oxm = xm;
        ym = data.length;
        if (ym != 0) {
            xm = data[0].length;
        } else {
            if (xm != 0) {
                xm = 0;
                Utils.repaintCtrl(this, 1);
                // repaint(1);
            }

            return;
        }

        Utils.repaintCtrl(this, 50);
    }

    // ---------------------------------------------------------------------------------------------

//	private void normalize() {
//		float max = Float.MIN_VALUE;
//
//		for (int y=0; y<ym; y++) {
//			for (int x=0; x<xm; x++) {
//				if (data[y][x] > max) max = data[y][x];
//			}
//		}
//		for (int y=0; y<ym; y++) {
//			for (int x=0; x<xm; x++) {
//				normData[y][x] = data[y][x] / max;
//			}
//		}
//	}

    // ---------------------------------------------------------------------------------------------

    /**
     * Draw the data set as a 2 dimensional field.
     *
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {

        if (Utils.skipPaint(this)) return;

        super.paintComponent(g);

//		normalize();

        Graphics2D g2 = (Graphics2D) g;

        int w = getWidth() - 1;
        int h = getHeight() - 1;


        float xs = w / (float) xm;
        float ys = h / (float) ym;

        if (xs < ys)
            ys = xs;
        else
            xs = ys;

        float rx = (w - xs * xm) / 2;
        float ry = (h - ys * ym) / 2;

//		System.err.println("rx = "+rx+"  ry = "+ry);

//		g2.setFont(g2.getFont().deriveFont(2));
        try {
            for (int y = 0; y < ym; y++) {
                for (int x = 0; x < xm; x++) {
                    Rectangle2D.Float r = new Rectangle2D.Float(rx + x * xs, ry + y * ys, xs, ys);

                    //				float d = normData[ym - y - 1][xm - x - 1];
                    float d = data[ym - y - 1][xm - x - 1];
                    if (d > 1f) d = 1f;
                    if (d < 0f) d = 0f;
                    g2.setColor(new Color(0f, d, 0));
                    g2.fill(r);

                    //				g2.setColor(Color.RED);
                    //				g2.drawString(String.format("%3.1f",d), r.x, r.y+r.height);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // It's ok, data array was changed..., just queue a repaint and don't care....
            repaint();
        }
//		Ellipse2D.Double circle = new Ellipse2D.Double(xCenter,yCenter,radius,radius);
//		Line2D.Double center = new Line2D.Double(xCenter+(radius/2),yCenter+(radius/2),xCenter+(radius/2),yCenter+(radius/2));
//		g2.draw(circle);
//		g2.draw(center);

    }


}   // of class ArrayPanel
