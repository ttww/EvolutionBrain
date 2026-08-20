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

package tw.master.gui.way;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

import tw.master.math.FastTrigonomic;
import tw.master.utils.Utils;


public class WayVectorPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private WayVector         way;

    private Point.Float[]     pa;

    private Point.Float       p                = new Point.Float();

    public WayVectorPanel() {
        super();
    }

    public WayVectorPanel(WayVector way) {
        super();

        this.way = way;

        init();
    }

    /**
     * @param  way
     */
    public void setWay(WayVector way) {
        this.way = way;
        init();
    }

    private void init() {
        int n = way.numberOfSectors;
        pa = new Point.Float[n];

        for (int i = 0; i < n; i++) {
            pa[i] = new Point.Float(0, 1);

            rotatePoint(pa[i], 360 / n * i);
        }
    }

    private static float grad2rad(float angle) {
        return angle * (float) (Math.PI / 180);
    }

    // ---------------------------------------------------------------------------------------------

    private static void rotatePoint(Point.Float p, float angle) {
        float rad = grad2rad(angle);
        float x = FastTrigonomic.cos(rad) * p.x - FastTrigonomic.sin(rad) * p.y;
        float y = FastTrigonomic.sin(rad) * p.x + FastTrigonomic.cos(rad) * p.y;

        p.x = x;
        p.y = y;
    }

    private Line2D.Float line = new Line2D.Float();

    @Override
    protected void paintComponent(Graphics g) {
        // super.printComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        Utils.setQuality(g2);

        float wm = this.getWidth() / 2;
        float hm = this.getHeight() / 2;

        if (way == null) {
            g2.setColor(Color.YELLOW);
            g2.drawString("NO WAY", wm - 20, hm);
            return;
        }

        Point.Float[] lpa = pa;
        if (lpa == null) return;

        float r = wm;
        if (hm < r) r = hm;

        line.x1 = wm;
        line.y1 = hm;


        Stroke lw = g2.getStroke();

        g2.setStroke(new BasicStroke(0.5f));
        g.setColor(new Color(0, 200, 0));

        int n = lpa.length;
        for (int i = 0; i < n; i++) {
            float f = r * 0.7f;

            line.x2 = lpa[i].x * f + wm;
            line.y2 = lpa[i].y * f + hm;

            g2.draw(line);
        }

        g2.setStroke(lw);

        g.setColor(Color.RED);

        boolean first = true;
        float firstX = 0;
        float firstY = 0;

        for (int i = 0; i < n; i++) {
            float f = r * 0.7f * way.directions[i] / WayVector.MAX_VALUE;
            // float f = (r * 0.7f) * (directions[i]);

            if (first) {
                firstX = lpa[i].x * f + wm;
                firstY = lpa[i].y * f + hm;
                line.x1 = firstX;
                line.y1 = firstY;

                first = false;

                continue;
            }

            line.x2 = lpa[i].x * f + wm;
            line.y2 = lpa[i].y * f + hm;

            g2.draw(line);

            line.x1 = line.x2;
            line.y1 = line.y2;
        }
        line.x2 = firstX;
        line.y2 = firstY;
        g2.draw(line);

        g.setColor(Color.YELLOW);
        float f = r * 0.7f;
        line.x1 = wm;
        line.y1 = hm;
        p.x = 0;
        p.y = 1;

        rotatePoint(p, way.lastAngle);

        line.x2 = p.x * f + wm;
        line.y2 = p.y * f + hm;
        g2.draw(line);

        float stdd = way.getStandardDeviation();
        float avgStdd = way.getAverangeStandardDeviation();
        float avgSum = way.getAverangeSum();

        g2.setColor(Color.YELLOW);
        g2.drawString(String.format("%3.2f   %3.1f  %3.1f", new Float(stdd), new Float(avgStdd), new Float(avgSum)), 2,
                20);
    }

//	public static void main(String[] args) {
//		final int S = 16;
//
//		WayVector wv = new WayVector(S);
//
//		final WayVectorPanel wp = new WayVectorPanel(wv);
//		SwingUtilities.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//				Utils.showBean(wp, "WayVectorTest");
//			}
//		});
//
//		// for (int i = 0; i < 360; i++) {
//		// ww.addWay(i);
//		// }
//		for (int i = 0; i < 10000; i++) {
//			float a;
//
//			while (true) {
//				a = Rnd.rnd(0, 359);
//				if (a > 20 && a < 200) continue;
//				break;
//			}
//
//			Utils.sleep(25);
//
//			wv.addWay(a);
//			wp.repaint();
//		}
//	}

}
