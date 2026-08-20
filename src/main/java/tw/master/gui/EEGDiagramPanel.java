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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.JPanel;

import tw.master.utils.Utils;



public class EEGDiagramPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private int               maxSteps         = 100;

    private boolean           globalValid      = false;

    private class OneLine {

        Object       o;

        @SuppressWarnings("unused")
        String       name;

        float        min;

        float        max;

        int          start;

        int          end;

        float[]      data;

        boolean      valid;

        Path2D.Float poly;

        boolean      lastSet;

        Color        col;
    }

    private ArrayList<OneLine>       lineArray = new ArrayList<OneLine>();

    private HashMap<Object, OneLine> lineMap   = new HashMap<Object, OneLine>();

    private final InnerDrawer        id;

    public EEGDiagramPanel() {
        setName("EEGDiagramPanel");
        id = new InnerDrawer();
    }

    public EEGDiagramPanel(int maxSteps) {
        this();
        this.maxSteps = maxSteps;
        setLayout(new BorderLayout());
        add(id, BorderLayout.CENTER);

        // Screen update thread --------------------------------------------------------------------
        // new StartStopStepThread("UpdateLineDiagramThread",70,true,true) {
        // @Override
        // public void doStep(Globals lglobals) {
        // checkRepaint();
        // }
        // };

    }

    public void clearLines() {
        synchronized (lineArray) {
            lineArray.clear();
            lineMap.clear();
            globalValid = false;
        }
    }

    public int getNumberOfLines() {
        return lineArray.size();
    }

    public void addLine(Object o, String name, float min, float max) {
        addLine(o, name, Color.GREEN, min, max);
    }

    public void addLine(Object o, String name, Color col, float min, float max) {

        //System.err.println("Add "+o+" name "+name+"  col "+col+"  min "+min+"  max "+max);

        OneLine ol = new OneLine();
        ol.o = o;
        ol.name = name;
        ol.min = min;
        ol.max = max;
        ol.col = col;
        ol.data = new float[maxSteps + 1];
        ol.poly = new Path2D.Float();
        synchronized (lineArray) {
            lineArray.add(ol);
        }
        lineMap.put(o, ol);
        globalValid = false;
    }

    public void shift() {
        LinkedList<Object> toDel = null;

        synchronized (lineArray) {
            for (OneLine ol : lineArray) {
                /*synchronized (ol)*/{

                    if (!ol.lastSet) {
                        ol.data[ol.end] = Float.NaN;
                        if (toDel == null) toDel = new LinkedList<Object>();
                        toDel.add(ol.o);
                    } else
                        ol.lastSet = false;

                    ol.end++;
                    if (ol.end == maxSteps + 1) ol.end = 0;
                    if (ol.end == ol.start) {
                        ol.start++;
                        if (ol.start == maxSteps + 1) ol.start = 0;
                    }
                    ol.valid = false;
                    ol.poly.reset();
                }
            }
            updateCount++;
            globalValid = false;
        }
        if (toDel != null) {
            for (Object o : toDel)
                removeLine(o);
        }
    }

    private long         updateCount;

//	private long lastDisplayed;

    private Line2D.Float line = new Line2D.Float();

    public void setLastValue(Object o, float f) {
        /*
         * Check becaus synchronize problem (fixed)
        if (((Neuron)o).a != f) {
        	System.err.println(Utils.getStacktrace());
        	throw new IllegalArgumentException("Changed f in slv !"+f);
        }
        if (f > 1) {
        	System.err.println(Utils.getStacktrace());
        	throw new IllegalArgumentException("Bad f in slv ! "+f);
        }
         */

        OneLine ol = lineMap.get(o);
        if (ol == null) return; // Object not in display

        //System.err.println("set "+o+"  value "+f);


        /*synchronized (ol)*/{
            ol.data[ol.end] = f;
            ol.lastSet = true;
            ol.valid = false;
        }
    }

    public boolean checkRepaint() {
        if (globalValid) return false;

        Utils.repaintCtrl(id, 20);
        return true;
    }

    public void removeLine(Object o) {
        synchronized (lineArray) {
            OneLine ol = lineMap.remove(o);
            //System.err.println("Remove MAP: "+ol);
            if (ol == null) return; // Object not in display
            lineArray.remove(ol);
            globalValid = false;
        }
    }

    class InnerDrawer extends JPanel {

        private static final long serialVersionUID = 1L;

        InnerDrawer() {
            setName("LDP-InnerDrawer");
        }

        @Override
        protected void paintComponent(Graphics g) {
//			super.paintComponent(g);

            if (Utils.skipPaint(this)) return;

            int w = this.getWidth();
            int h = this.getHeight();

            Graphics2D g2 = (Graphics2D) g;
            //Utils.setQuality(g2);

            g2.setColor(Color.DARK_GRAY.darker());
            g2.fillRect(0, 0, w, h);

//			g2.setColor(Color.RED);
//			g2.drawLine(0,0,w,h);
//			g2.drawLine(w,0,0,h);



            final float RIM = 10;
            final float SPACE_BETWEEN = 2;

            float hf = h - 2 * RIM;
            float wf = w - 2 * RIM;

            g2.draw(new Rectangle2D.Float(RIM, RIM, wf, hf));

            final float NUMBER_OF_LINES = lineArray.size();
            final float LINE_HEIGHT = hf / NUMBER_OF_LINES - SPACE_BETWEEN;
            final float YS = hf / NUMBER_OF_LINES;
            final float XS = wf / maxSteps;


            float y = RIM + LINE_HEIGHT;

            synchronized (lineArray) {

                line.y1 = 0;
                line.y2 = h;
                int n = 10;
                float xp = updateCount % n * XS;
                for (int x = 0; x < maxSteps / n + 2; x++) {
                    if (x % 100 == 0) g2.setColor(Color.GRAY);
                    else
                        g2.setColor(Color.DARK_GRAY);

                    line.x1 = x * XS * n - xp;
                    line.x2 = line.x1;
                    //System.err.println(line);
                    g2.draw(line);
                }

                Color minMaxCol = Color.GRAY;
                Color middleCol = Color.DARK_GRAY;

                long start_ms = System.currentTimeMillis();
                long p = 0;
                for (OneLine ol : lineArray) {

                    boolean debug = false;
                    if (ol.col.getRed() == 255 && ol.col.getGreen() == 255) debug = true;



                    if (!globalValid || !ol.valid) {

                        /*synchronized (ol)*/{
                            float xf = RIM;

                            boolean first = true;

                            float yv = ol.max - ol.min;

                            int x = ol.start;
                            while (x != ol.end) {

                                float v = ol.data[x] / yv * LINE_HEIGHT;

                                if (first) {
                                    ol.poly.moveTo(xf, y - v);
                                    first = false;
                                } else
                                    ol.poly.lineTo(xf, y - v);

                                p++;
                                xf += XS;
                                x++;
                                if (x == maxSteps + 1) x = 0;
                            }
                            ol.valid = true;

                            if (LINE_HEIGHT > 10) {
                                g2.setColor(minMaxCol);

                                line.x1 = 0;
                                line.x2 = w;
                                float v = ol.min / yv * LINE_HEIGHT;
                                line.y1 = y - v;
                                line.y2 = line.y1;
                                g2.draw(line);

                                v = ol.max / yv * LINE_HEIGHT;
                                line.y1 = y - v;
                                line.y2 = line.y1;
                                g2.draw(line);

                                g2.setColor(middleCol);
                                v = 0.5f * yv * LINE_HEIGHT;
                                line.y1 = y - v;
                                line.y2 = line.y1;
                                g2.draw(line);

                            }

                        }


                        y += YS;

                    } // globalValid
                }
                globalValid = true;

                long stop_ms = System.currentTimeMillis();

                long b_ms = stop_ms - start_ms;

                start_ms = System.currentTimeMillis();
                //System.err.println("+++++++++++++++++");
                for (OneLine ol : lineArray) {
                    g2.setColor(ol.col);
                    //g2.setColor(Color.RED);

                    //boolean debug = false;
                    //if (ol.col.getRed() == 255 && ol.col.getGreen() == 255) debug = true;
                    //if (debug) {
                    //System.err.println(debug + "   ol.col = "+ol.col+"  : "+ol.poly.getBounds());
                    //}

                    //if (ol.col.getAlpha() != 255) System.err.println("!!ALPHA: " + ol.col);
                    g2.draw(ol.poly);
                }

                //System.err.println("------------------");
                stop_ms = System.currentTimeMillis();
                long d_ms = stop_ms - start_ms;
                if (d_ms > 100)
                    System.err.println("Time to draw !!: P = "
                            + b_ms
                            + "   draw = "
                            + d_ms
                            + " for p "
                            + p
                            + "  in  "
                            + lineArray.size()
                            + " / "
                            + g2.getDeviceConfiguration().getBufferCapabilities().getBackBufferCapabilities()
                            .isTrueVolatile()
                            + " / "
                            + g2.getDeviceConfiguration().getBufferCapabilities().getBackBufferCapabilities()
                            .isAccelerated());

            } // sync

        }
    }

    /* (non-Javadoc)
     * @see java.awt.Component#resize(java.awt.Dimension)
     */
    @SuppressWarnings("deprecation")
    @Override
    public void resize(Dimension d) {
        globalValid = true;
        super.resize(d);
        Utils.repaintCtrl(this, 0);
    }

    /* (non-Javadoc)
     * @see java.awt.Component#resize(int, int)
     */
    @SuppressWarnings("deprecation")
    @Override
    public void resize(int width, int height) {
        globalValid = true;
        super.resize(width, height);
        Utils.repaintCtrl(this, 0);
    }

    /**
     * @param args
     */
    // public static void main(String[] args) {
    //
    // final EEGDiagramPanel lp = new EEGDiagramPanel(200);
    //
    // // final Object l1 = new Object();
    //
    // final Object[] all = new Object[50];
    //
    // for (int i=0; i<all.length; i++) {
    // all[i] = new Object();
    // while (true) {
    // float r = Rnd.rnd(0f,1f);
    // float g = Rnd.rnd(0f,1f);
    // float b = Rnd.rnd(0f,1f);
    // float a = Rnd.rnd(0f,1f);
    //
    // a = 1;
    //
    // if ( r + g + b < 0.4) continue;
    // lp.addLine(all[i],"Line "+i,new Color(r,g,b,a), 0f,1f);
    // break;
    // }
//		}
    //
    // // final Object l2 = new Object();
    // // lp.addLine(l1,"Line 1",Color.RED, 0f,1f);
    // // lp.addLine(l2,"Line 2",0f,1f);
    //
    // new Thread() {
    // @Override
    // public void run() {
    // int x = 0;
    // while (true) {
    // Utils.sleep(1);
    // x++;
    // for (int i=0; i<all.length; i++) {
    // if (i == 0) {
    // lp.setLastValue(all[i],x & 1);
    // continue;
    // }
    // if (i == 1) {
    // lp.setLastValue(all[i],1 - (x & 1));
    // continue;
    // }
    // if (i == all.length-1) {
    // lp.setLastValue(all[i],x & 1);
    // continue;
    // }
    //
    // lp.setLastValue(all[i],Rnd.rnd(0f,1f));
    // }
    // lp.shift();
    // lp.checkRepaint();
    // }
    // }
    // }.start();
    // // for (int i=0; i<50; i++) {
    // // lp.setLastValue(l1,Rnd.rnd(0f,1f));
    // // lp.setLastValue(l2,Rnd.rnd(0f,1f));
    // // lp.shift();
    // // }
    //
    // GuiUtils.showBean(lp, "Test Diagram");
    // }



}
