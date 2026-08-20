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

package tw.master.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ResourceWatcherPanel extends JPanel {

    private static final int  MAX_VALUES          = 1000;

    private static final int  WATCHER_INTERVAL_MS = 100;

    private static final long serialVersionUID    = 1L;


    private Watcher           memWatcher;

    private Watcher           threadWatcher;

    private Watcher           runWatcher;

    private JFrame            frame;

    private boolean           quit = false;

    public ResourceWatcherPanel() {
        memWatcher = new Watcher("Memory Mb", Color.GREEN);
        memWatcher.scaleMax = Runtime.getRuntime().maxMemory();
        memWatcher.divider = 1024 * 1024;
        memWatcher.format = "%d Mb";

        threadWatcher = new Watcher("Threads", Color.YELLOW);
        threadWatcher.format = "#%d";

        runWatcher = new Watcher("Running", Color.RED);
        runWatcher.smooth = true;
        runWatcher.format = "#%2.1f";

        new WatcherCollectorThread().start();

        this.addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent e) { }

            @Override
            public void mousePressed(MouseEvent e) { }

            @Override
            public void mouseExited(MouseEvent e) { }

            @Override
            public void mouseEntered(MouseEvent e) { }

            @Override
            public void mouseClicked(MouseEvent e) {
                System.err.println("Doing 3 * GC...");
                System.gc();
                System.gc();
                System.gc();
            }
        });
        frame = Utils.showBean(this, "Used Resources", false);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new ResourceWatcherPanel();
            }
        });
    }

    // -----------------------------------------------------------------------------------------

    class Watcher {

        public boolean   smooth;

        String           format;

        String           name;

        Color            color;

        Color            maxColor;

        LinkedList<Long> values   = new LinkedList<Long>();

        long             divider  = 1;

        long             min      = Long.MAX_VALUE;

        long             max      = Long.MIN_VALUE;

        long             scaleMax = 0;

        long             first;

        public Watcher(String name, Color color) {
            this.name = name;
            this.color = color;
            this.maxColor = color.darker().darker();
        }

        synchronized void addValue(long v) {
            if (v > max) max = v;
            if (v < min) min = v;
            if (values.size() == 0) first = v;

            values.addLast(new Long(v));

            if (values.size() >= MAX_VALUES) {
                long old = values.removeFirst().longValue();

                if (old == max || old == min) {
//						min	= Long.MAX_VALUE;
//						max	= Long.MIN_VALUE;
                }
            }
        }

        private void updateMinMax() {
            if (min != Long.MAX_VALUE) return;

            for (Long l : values) {
                long ll = l.longValue();
                if (ll > max) max = ll;
                if (ll < min) min = ll;
            }
        }

        synchronized long getMinValue() {
            updateMinMax();
            return min;
        }

        synchronized long getMaxValue() {
            updateMinMax();
            return max;
        }

        String getFormatedValue(float v) {
            if (format != null) {
                if (smooth) {
                    // System.err.println("format = "+format+"  v = "+v+"  --> "+String.format(format,new Float(v/divider)));
                    return String.format(format, new Float(v / divider));
                } else
                    return String.format(format, new Long((long) (v / divider + 0.5f)));
            } else {
                if (smooth) return Float.toString(v / divider);
                else
                    return Long.toString((long) (v / divider + 0.5f));
            }

        }
    }

    // -----------------------------------------------------------------------------------------

    class WatcherCollectorThread extends Thread {

        WatcherCollectorThread() {
            setName("WatcherCollectorThread");
            setDaemon(true);
        }

        Thread[] allThreads = new Thread[100];

        @Override
        public void run() {
            Runtime rt = Runtime.getRuntime();
            while (!quit) {

                long roundStart = System.currentTimeMillis();

                memWatcher.addValue(rt.totalMemory() - rt.freeMemory());
//				System.err.println("MAX   = "+rt.maxMemory());
//				System.err.println("FREE  = "+rt.freeMemory());
//				System.err.println("TOTAL = "+rt.totalMemory());
//				System.err.println("");

                ThreadGroup tg = Thread.currentThread().getThreadGroup();
                while (tg.getParent() != null)
                    tg = tg.getParent();

                int active = tg.enumerate(allThreads);
                int running = 0;
                for (int i = 0; i < active; i++) {
                    Thread t = allThreads[i];

                    String n = t.getName();
                    if (n.equals("Signal Dispatcher")) continue;
                    if (n.startsWith("AWT-")) continue;
                    if (n.equals("DestroyJavaVM")) continue;
                    if (n.startsWith("SocketListener")) continue;
                    if (n.equals("WatcherCollectorThread")) continue;

                    // Prefer running processes
                    boolean isRunning = false;
                    for (int r = 0; r < 1; r++) {
                        if (t.getState() == State.RUNNABLE) {
                            isRunning = true;
                            break;
                        }
                        Utils.sleep(3);
                        if (quit) break;
                    }
                    if (isRunning) running++;

                }

                if (quit) break;
                runWatcher.addValue(running);
                threadWatcher.addValue(active);
                //threadWatcher.addValue(tg.activeCount());

                //Thread[] t = new Thread[tg.activeCount()];
                //tg.enumerate(t);

                repaint();
//				Utils.sleep(1000);

                long now = System.currentTimeMillis();
                long needToSleep = WATCHER_INTERVAL_MS - (now - roundStart);
                //			System.err.println("Time needed = "+(now - roundStart)+"  need to sleep = "+needToSleep);
                if (needToSleep < 0) needToSleep = 10;
                Utils.sleep(needToSleep);
            }
        }
    }

    // -----------------------------------------------------------------------------------------

    private Path2D.Float poly = new Path2D.Float();

    private Line2D.Float line = new Line2D.Float();

    private void drawWatcher(int n, Graphics2D g, Watcher watcher) {
        int w = this.getWidth();
        int h = this.getHeight();

        int rimTop = 10;
        int rimBottom = 20;
        int rimLeft = 10;
        int rimRight = 10;
        w -= rimLeft + rimRight;
        h -= rimBottom + rimTop;

        poly.reset();

        long scaleMax = 0, max = 0, ll = 0;

        float xx = 0, yy = 0, xf, yf;

        boolean smooth = watcher.smooth;

        float lastSmooth = 0;

        final int USE_AVG = 10;

        synchronized (watcher) {
            max = watcher.max;
            scaleMax = watcher.scaleMax;

            xf = w / (float) MAX_VALUES;
            if (scaleMax != 0) yf = h / (float) scaleMax;
            else
                yf = h / (float) max;

            int x = 0;
            for (Long l : watcher.values) {
                xx = rimLeft + x * xf;

                ll = l.longValue();

                if (smooth) {
                    if (x == 0) {
                        lastSmooth = ll;
                    } else {
                        lastSmooth = (lastSmooth * (USE_AVG - 1) + ll) / USE_AVG;
                    }
                    yy = rimTop + h - lastSmooth * yf;
                } else {
                    yy = rimTop + h - ll * yf;
                }

                //	System.err.println("w = "+w+"  h = "+h+ "  "+ "x = "+x+"  --> "+xx +"  y = "+ll+"  --> "+yy);
                if (smooth) {
                    if (x < 2 * USE_AVG) poly.moveTo(xx, yy);
                    else
                        poly.lineTo(xx, yy);

                } else {
                    if (x == 0) poly.moveTo(xx, yy);
                    else
                        poly.lineTo(xx, yy);
                }

                x++;
            }
        } // sync

        //Utils.setQuality(g);

        g.setColor(watcher.maxColor);
        line.x1 = rimLeft;
        line.x2 = line.x1 + w;
        line.y1 = rimTop + h - max * yf + n;
        line.y2 = line.y1;
        g.draw(line);

        g.setColor(watcher.color);
        g.drawString(watcher.name + " (max = " + watcher.getFormatedValue(max) + ")", rimLeft + 20 + n * 200, h
                + rimTop + 15);
        g.draw(poly);


        int yv = -10;
        xx -= 30;

        if (yy + yv < 20) yv = 20;
        yy = yy + yv;


        String f;
        if (smooth) {
            f = watcher.getFormatedValue(lastSmooth);
        } else {
            f = watcher.getFormatedValue(ll);
        }

        Rectangle2D r = g.getFont().getStringBounds(f, g.getFontRenderContext());
        g.setColor(Color.DARK_GRAY.darker());
        g.translate(xx, yy);
        g.fill(r);

        g.setColor(watcher.color);
        g.drawString(f, 0, 0);
        g.translate(-xx, -yy);


        //		g.drawString(f,xx,yy+yv);

    }

    /* (non-Javadoc)
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g1) {
//		super.paintComponent(g);

        Graphics2D g = (Graphics2D) g1;

        int w = this.getWidth();
        int h = this.getHeight();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, w, h);

        drawWatcher(0, g, memWatcher);
        drawWatcher(1, g, threadWatcher);
        drawWatcher(2, g, runWatcher);
    }

    /**
     * Free all resources and terminate the thread.
     */
    public void disposeAll() {
        quit = true;
        frame.setVisible(false);
        frame.dispose();

        frame         = null;
        memWatcher    = null;
        threadWatcher = null;
        runWatcher    = null;
    }

}
