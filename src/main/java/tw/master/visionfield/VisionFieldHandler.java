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

package tw.master.visionfield;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import tw.master.math.FastTrigonomic;
import tw.master.utils.Utils;



public class VisionFieldHandler {

//	public	int				lookWide   = 10;
//	public	int				lookHeight = 20;

    public BufferedImage img;

    public BufferedImage imgOverlay;

//	private  int KERNEL_RADIUS = 2;
//	private	 int KERNEL_SIZE = 2*KERNEL_RADIUS + 1;
//
//	private float[][] kernel = MathUtils.genGaussKernel(KERNEL_RADIUS,1f,0.2f);

    public VisionFieldHandler(BufferedImage img) {
        this(img, null);
    }

    public VisionFieldHandler(BufferedImage img, BufferedImage imgOverlay) {
        this.img = img;
        this.imgOverlay = imgOverlay;
        setupFimg();
    }

    private float[][] fImg;

    private float[][] fOverlayImg;

    private int       wm;

    private int       hm;

    public final int getNumberOfSetPixels() {
        return notZeroPixels;
    }

    private int notZeroPixels;

    private void setupFimg() {
        if (img == null) return;

        long start_ms = System.currentTimeMillis();

        wm = img.getWidth();
        hm = img.getHeight();

        fImg = new float[hm][wm];
        for (int h = 0; h < hm; h++) {
            for (int w = 0; w < wm; w++) {
                float f = getImageValue(w, h);
                if (f != 0) {
                    notZeroPixels++;
                    fImg[h][w] = f;
                }
            }
        }

        if (imgOverlay != null) {
            fOverlayImg = new float[hm][wm];

            for (int h = 0; h < hm; h++) {
                for (int w = 0; w < wm; w++) {
                    float f = getOverlayValue(w, h);
                    fOverlayImg[h][w] = f;
//					if (!Float.isNaN(f)) {
//						System.err.println("f = "+f);
//					}
                }
            }
        }


        long stop_ms = System.currentTimeMillis();

        System.err.println("Used " + (stop_ms - start_ms) + " ms for img cache build of " + Utils.describeImage(img));


    }

    public void setImage(BufferedImage img) {
        this.img = img;
        setupFimg();
    }

    public final float getCachedImageValue(int xi, int yi) {
        if (xi < 0) xi += wm;
        else
            if (xi >= wm) xi -= wm;
        if (yi < 0) yi += hm;
        else
            if (yi >= hm) yi -= hm;

        return fImg[yi][xi];
    }



    private float getImageValue(int xi, int yi) {
        //	if (true) return 0;
        if (xi < 0 || yi < 0) return 0;
        if (xi >= img.getWidth() || yi >= img.getHeight()) return 0;

        int v = img.getRGB(xi, yi);

        int a = v & 0xff000000;

        int c;

        if (a == 0) c = 0;
        else
            c = v & 0x00ffffff;
        if (c != 0) {

            short vm = 0;
            short cc;

            cc = (short) ((c & 0x00ff0000) >> 16);
            if (cc > vm) vm = cc;
            cc = (short) ((c & 0x0000ff00) >> 8);
            if (cc > vm) vm = cc;
            cc = (short) (c & 0x000000ff);
            if (cc > vm) vm = cc;

            //System.err.println(vm);
            return vm / 255f;
        }

        return 0;
    }

//	private float getImageValue(Point2D.Float p) {
//		int xi = (int) (p.x + 0.5f);
//		int yi = (int) (p.y + 0.5f);
//
//		return getImageValue(xi,yi);
//	}

    private float getImageValue(float x, float y) {
        int xi = (int) (x + 0.5f);
        int yi = (int) (y + 0.5f);

        return getCachedImageValue(xi, yi);
    }

    public final float getCachedOverlayValue(int xi, int yi) {
        if (xi < 0) xi += wm;
        else
            if (xi >= wm) xi -= wm;
        if (yi < 0) yi += hm;
        else
            if (yi >= hm) yi -= hm;

        return fOverlayImg[yi][xi];
    }

    private float getCachedOverlayValue(float x, float y) {
        int xi = (int) (x + 0.5f);
        int yi = (int) (y + 0.5f);

        return getCachedOverlayValue(xi, yi);
    }

    private float getOverlayValue(float x, float y) {
        int xi = (int) (x + 0.5f);
        int yi = (int) (y + 0.5f);

        int v = imgOverlay.getRGB(xi, yi);
        int a = (v & 0x00ff0000) >> 16;

        if (a >= 100) {
            a -= 100;
//			if (a > 100) return Float.NaN;
            if (a > 100) return 1;
        } else {
            return Float.NaN;
        }
        return a / 100f;
    }


    public void updateVisionFieldData(VisionFieldParams lfp) {

        int w = lfp.width;
        int h = lfp.height;

        if (lfp.sf == null) lfp.sf = new float[h][w];

        if (img == null) {
            System.err.println("No img");
            return;
        }

        Point2D.Float[][] cf = getCachedVisionFieldCoordinates(lfp.a, w, h);

        int xp = (int) (lfp.p.x + 0.5);
        int yp = (int) (lfp.p.y + 0.5);

        // Check if something changed....:
        if (lfp.lastXp != xp || lfp.lastYp != yp || lfp.lastA != lfp.a) {
            lfp.lastXp = xp;
            lfp.lastYp = yp;
            lfp.lastA = lfp.a;

            // System.err.println("New");
        } else {
            // System.err.println("Old");

            return;
        }

        boolean haveOverlay = false;
        if (imgOverlay == null) haveOverlay = true; // Suppress checks :-)
        lfp.overlayValue = Float.NaN;

        if (lfp.update) {
            System.err.println(Utils.getStacktrace());
            throw new IllegalArgumentException("Bad update LFP");
        }

        lfp.update = true;

        boolean updateChangeCounter = false;

        lfp.w = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Point2D.Float cp = cf[y][x];

                float xf = xp + cp.x;
                float yf = yp + cp.y;

                float f = getImageValue(xf, yf);

                if (lfp.sf[y][x] != f) {
                    lfp.sf[y][x] = f;
                    updateChangeCounter = true;
                }

                if (!haveOverlay && f > 0.0001) {
                    lfp.overlayValue = getCachedOverlayValue(xf, yf);
                    haveOverlay = true;
                }
                if (f > lfp.w) lfp.w = f;
            }
        }

        if (lfp.w > 0) lfp.hasEverSeen = true;

        if (updateChangeCounter) lfp.changeCounter++;
        // System.err.println(updateChangeCounter);
        lfp.update = false;
    }

    public float[][] getVisionField(Point2D.Float p, float a, int w, int h) {

        float[][] sf = new float[h][w];

        Point2D.Float[][] cf = getCachedVisionFieldCoordinates(a, w, h);

        int xp = (int) (p.x + 0.5);
        int yp = (int) (p.y + 0.5);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Point2D.Float cp = cf[y][x];
                sf[y][x] = getImageValue(xp + cp.x, yp + cp.y);
            }
        }
        return sf;
    }

//	public static void main(String[] args) {
//		VisionFieldHandler g = new VisionFieldHandler();
//
//		int w=5;
//		int h=3;
//		Point2D.Float[][] cf = g.getVisionFieldCoordinates(0,w,h);
//
//		System.err.println();
//
//		for (int y=0; y<h;y++) {
//			System.err.print("y = "+y+":  ");
//			for (int x=0; x<w;x++) {
//				System.err.print(pc(cf[y][x])+"    ");
//			}
//			System.err.println();
//		}
//
//}

//	@SuppressWarnings("boxing")
//	private void outp(String txt, Point2D.Float p) {
//		System.err.println(String.format("%-14s:  %5.2f , %5.2f",txt,p.x,p.y));
//	}
//	@SuppressWarnings("boxing")
//	private static String pc(Point2D.Float p) {
//		return String.format("%5.2f,%5.2f",p.x,p.y);
//	}

    private HashMap<String, Point2D.Float[][]> lfCache = new HashMap<String, Point2D.Float[][]>();

    private Point2D.Float[][] getCachedVisionFieldCoordinates(float a, int w, int h) {
        String cacheName = Integer.toString((int) a * 10) + '.' + w + '.' + h;

        synchronized (lfCache) {

            Point2D.Float[][] ret = lfCache.get(cacheName);

            if (ret == null) {
                ret = getVisionFieldCoordinates(a, w, h);
                lfCache.put(cacheName, ret);
//			System.err.println(System.currentTimeMillis()+": Create "+cacheName);
            }

            return ret;
        }
    }

    public Point2D.Float[][] getVisionFieldCoordinates(float a, int w, int h) {
        Point2D.Float[][] f = new Point2D.Float[h][w];
        float wh = w / 2f;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Point2D.Float p = f[y][x];
                if (p == null) {
                    p = new Point2D.Float();
                    f[y][x] = p;
                }
                p.x = x - wh;
                p.y = y;
                rotatePoint(p, a);
            }
        }

        return f;
    }


    private float grad2rad(float angle) {
        return angle * (float) (Math.PI / 180);
    }

    private void rotatePoint(Point.Float p, float angle) {
        float rad = grad2rad(angle);
        float x = FastTrigonomic.cos(rad) * p.x - FastTrigonomic.sin(rad) * p.y;
        float y = FastTrigonomic.sin(rad) * p.x + FastTrigonomic.cos(rad) * p.y;
//		x = (float) (Math.cos(rad) * p.x - Math.sin(rad) * p.y);
//		y = (float) (Math.sin(rad) * p.x + Math.cos(rad) * p.y);

        p.x = x;
        p.y = y;
    }


}
