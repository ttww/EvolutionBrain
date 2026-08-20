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

package tw.master.crawler;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import tw.gui.image.ImagePanelDrawerInterface;
import tw.master.engine.Engine;
import tw.master.visionfield.VisionFieldParams;


public class CrawlerDrawer implements ImagePanelDrawerInterface {

    /**
     * @uml.property  name="engine"
     */
    private Engine engine;

    public CrawlerDrawer(Engine engine) {
        this.engine = engine;
    }

//	private float grad2rad(float angle) {
//		return angle * (float) (Math.PI / 180);
//	}

    // ---------------------------------------------------------------------------------------------


    private ArrayList<Crawler> displayList = new ArrayList<Crawler>(200);

    @Override
    public void draw(int w, int h, Graphics2D g) {

        AffineTransform saveAt = g.getTransform();

        synchronized (engine.allCrawlers) {
            for (Crawler cc : engine.allCrawlers) {
                if (cc != engine.watchedCrawler) displayList.add(cc);
            }
        }
        if (engine.watchedCrawler != null) displayList.add(engine.watchedCrawler);

        for (Crawler c : displayList) {
            draw(c, g);
            g.setTransform(saveAt);
        } // for
        displayList.clear();
    }

    // ---------------------------------------------------------------------------------------------

    private static float grad2rad(float angle) {
        return angle * (float) (Math.PI / 180);
    }

    private Path2D.Float       shape;

    private static final Color NORMAL_COLOR    = Color.RED;

    private static final Color WATCHED_COLOR   = Color.YELLOW;

    private static final Color CONTROLED_COLOR = Color.MAGENTA;

    // ---------------------------------------------------------------------------------------------

    private void draw(Crawler c, Graphics2D g) {

        boolean watched = c == engine.watchedCrawler;

        if (watched) {
            if (c == engine.controlledCrawler) {
                if (System.currentTimeMillis() % 1000 > 500) g.setColor(WATCHED_COLOR);
                else
                    g.setColor(CONTROLED_COLOR);
            } else {
                g.setColor(WATCHED_COLOR);
            }
        } else {
            g.setColor(NORMAL_COLOR);
        }

        if (!watched && engine.fastDraw) {
            int xi, yi;
            xi = (int) (c.pos.x + 0.5f);
            yi = (int) (c.pos.y + 0.5f);
            g.drawLine(xi, yi, xi, yi);
        } else {

            if (shape == null) {
                shape = new Path2D.Float();
                shape.append(new Line2D.Float(0, 0, 0, -10), false);
                shape.append(new Line2D.Float(0, 0, -5, -5), false);
                shape.append(new Line2D.Float(0, 0, +5, -5), false);
            }

            AffineTransform saveAt = g.getTransform();

            float a;
            g.translate(c.pos.x, c.pos.y);
            a = c.direction;
//			}
            g.rotate(grad2rad(a));

            g.draw(shape);

            g.setTransform(saveAt);

            if (c == engine.watchedCrawler) drawField(c, g);
        }

    }

    private Color FIELD_COLOR = new Color(0, 255, 0, 50);

    private void drawField(Crawler c, Graphics2D g) {
        float a;
        float xf, yf;

        xf = c.pos.x;
        yf = c.pos.y;
        a = c.direction;
//		}

        int w = 10;
        int h = 10;

        if (engine.watchedCrawler instanceof AbstractBrainCrawler) {
            AbstractBrainCrawler bc = (AbstractBrainCrawler) engine.watchedCrawler;

            VisionFieldParams lfp = bc.vfp;
            if (lfp == null) return;
            w = lfp.width;
            h = lfp.height;
        }

//		Point2D.Float leftNear  = new Point2D.Float(0,-w/2f);
//		Point2D.Float rightNear = new Point2D.Float(0,-w/2f);
//		Point2D.Float leftFar   = new Point2D.Float(h,-w/2f);
//		Point2D.Float rightFar  = new Point2D.Float(-h,-w/2f);
//
//		rotatePoint(leftNear,a+90);
//		rotatePoint(rightNear,a-90);
//		rotatePoint(leftFar,a+90);
//		rotatePoint(rightFar,a-90);

        Line2D.Float line = new Line2D.Float();

//		line = new Line2D.Float(
//				pos.x + leftNear.x,
//				pos.y + leftNear.y,
//				pos.x + rightNear.x,
//				pos.y + rightNear.y
//		);
//
//		g.draw(line);
//
//		line = new Line2D.Float(
//				pos.x + leftFar.x,
//				pos.y + leftFar.y,
//				pos.x + rightFar.x,
//				pos.y + rightFar.y
//		);
//
//		g.draw(line);
//
//		line = new Line2D.Float(
//				pos.x + leftFar.x,
//				pos.y + leftFar.y,
//				pos.x + leftNear.x,
//				pos.y + leftNear.y
//		);
//
//		g.draw(line);

        Point2D.Float[][] cf = engine.visionFieldHandler.getVisionFieldCoordinates(a, w, h);

        g.setColor(FIELD_COLOR);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Point2D.Float cp = cf[y][x];

                float xi = xf + cp.x;
                float yi = yf + cp.y;
                line.x1 = xi;
                line.y1 = yi;
                line.x2 = xi;
                line.y2 = yi;
                g.draw(line);

            }
        }

    }


    // ---------------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------

//	private void rotatePoint(Point.Float p,float angle) {
//		float rad = grad2rad(angle);
//		float x = FastTrigonomic.cos(rad) * p.x - FastTrigonomic.sin(rad) * p.y;
//		float y = FastTrigonomic.sin(rad) * p.x + FastTrigonomic.cos(rad) * p.y;
////		x = (float) (Math.cos(rad) * p.x - Math.sin(rad) * p.y);
////		y = (float) (Math.sin(rad) * p.x + Math.cos(rad) * p.y);
//
//		p.x = x;
//		p.y = y;
//	}

}
