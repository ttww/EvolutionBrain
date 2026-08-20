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

package tw.master.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import tw.master.crawler.AbstractBrainCrawler;
import tw.master.crawler.FitnessValue;
import tw.master.utils.Rnd;
import tw.master.utils.Utils;



public class FitnessMeterPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static Font       font;

    private FitnessValue[]    fitnessValues;

    public FitnessMeterPanel(FitnessValue[] fitnessValues) {
        if (font == null) {
            font = new Font("Dialog", 0, 10);
        }

        this.addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {

                String s = null;

                for (int i = 0; i < rect.length; i++) {
                    RoundRectangle2D.Float r = rect[i];
                    Point mp = getMousePosition();
                    if (r != null && mp != null && r.contains(mp)) {
                        s = getText(10000, i);
                        break;
                    }
                }

                final String tooltip = s;

                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        setToolTipText(tooltip);
                    }
                });
            }

        });
        setFitnessValues(fitnessValues);
    }

    public void setFitnessValues(FitnessValue[] fitnessValues, boolean repaint) {
        this.fitnessValues = fitnessValues;
        if (rect == null || rect.length != fitnessValues.length) {
            rect = new RoundRectangle2D.Float[fitnessValues.length];
        }

        if (repaint) repaint();
    }

    /**
     * @param  fitnessValues
     */
    public void setFitnessValues(FitnessValue[] fitnessValues) {
        setFitnessValues(fitnessValues, true);
    }

    private Line2D.Float             line = new Line2D.Float();

    private RoundRectangle2D.Float[] rect;

    @Override
    protected void paintComponent(Graphics g) {
        //super.paintComponent(g);

        int w = this.getWidth();
        int h = this.getHeight();

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.GRAY);
        g2.fillRect(0, 0, w, h);

        Utils.setQuality(g2);

        final int RIM = 5;
        final int SPACE = 2;
        final int TEXT_SPACE = 20;


        int uh = h - TEXT_SPACE - RIM;

        g2.setColor(Color.WHITE.darker());
        float h10 = uh / 10f;

        for (int i = 0; i <= 10; i++) {
            float y = h - TEXT_SPACE - h10 * i;
            drawLine(g2, RIM, y, w - 2 * RIM, y);
        }


        float lw = (w - 2 * RIM) / (float) fitnessValues.length;

        g2.setColor(Color.GREEN.brighter());

        for (int i = 0; i < fitnessValues.length; i++) {
            float f = fitnessValues[i].fitness;
            float lh = uh * f;

            float x = RIM + i * lw;
            float y = h - TEXT_SPACE - lh;

            if (rect[i] == null) rect[i] = new RoundRectangle2D.Float();

            drawRect(g2, rect[i], x, y, lw - SPACE - 1, lh, Color.GREEN, 8, TEXT_SPACE, uh);
            rect[i].height += TEXT_SPACE;

            g2.setColor(Color.WHITE);

            String s = getText((int) lw, i);

            g.setFont(font);
            g.drawString(s, round(x), round(h - 5));
        }
    }

    private String getText(int haveSize, int i) {
        float f = fitnessValues[i].fitness;
        String s = "";
        if (haveSize > 137) s = String.format("%s=%4.2f", fitnessValues[i].what, new Float(f));
        else
            if (haveSize > 55) s = String.format("%s=%4.2f", fitnessValues[i].shortWhat, new Float(f));
            else
                s = String.format("%4.2f", new Float(f));

        return s;
    }

    private final int round(float f) {
        if (f >= 0) return (int) (f + 0.5f);
        return (int) (f - 0.5f);
    }

    private void drawLine(Graphics2D g, float x1, float y1, float x2, float y2) {
        line.x1 = x1;
        line.x2 = x2;
        line.y1 = y1;
        line.y2 = y2;
        g.draw(line);
    }

    private void drawRect(Graphics2D g, RoundRectangle2D.Float r, float x, float y, float width, float height, Color c,
            int rad, int startY, int maxHeight) {

        Paint old = g.getPaint();

        r.x = x + 5;
        r.y = y - 5;
        r.width = width;
        r.height = height;
        r.arcwidth = rad;
        r.archeight = rad;

        g.setColor(Color.GRAY.darker());
        g.fill(r);

        r.x = x;
        r.y = y;

        GradientPaint gradient = new GradientPaint(
                0, startY, c,
                0, startY - maxHeight, Color.RED, true);

        g.setPaint(gradient);


//		g.setColor(c);
        g.fill(r);

        g.setPaint(old);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        final FitnessMeterPanel emp = new FitnessMeterPanel(AbstractBrainCrawler.getInitialFitnessValues());

        Utils.showBean(emp, "FitnessMeterPanel");

        new Thread() {

            @Override
            public void run() {
                FitnessValue[] fv = AbstractBrainCrawler.getInitialFitnessValues();
                while (true) {
                    Utils.sleep(1000);

                    for (int i = 0; i < fv.length; i++) {
                        fv[i].fitness = Rnd.rnd(0f, 1f);
                    }
                    emp.setFitnessValues(fv);
                }
            }
        }.start();
    }

}
