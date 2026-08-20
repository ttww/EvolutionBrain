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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import tw.master.utils.PreferencesUtils;
import tw.master.utils.Utils;



/**
 * This class displays a image inside a JScroolPane and provieds zooming, image overlay and active
 * drawing overlay.
 *
 * @author Thomas Welsch
 */
public class ImagePanel extends JScrollPane implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

    private static final long               serialVersionUID = 1L;

    private static final boolean           DIRECT_DRAW      = false;       // Dont't use to much time

    private String            name;

    private BufferedImage     img;

    private BufferedImage     imgOverlay;

    private BufferedImage     imgDrawPanelOverlay;


    // in paintComponent !

    private DrawPanel         dp;

    private final Object      sync             = new Object();

    private final Dimension   size = new Dimension(1, 1);

    private       Dimension scaledSize;

    // ---------------------------------------------------------------------------------------------

    /**
     * Instantiates the new image panel with the given image.
     *
     * @param name  Name of the image. This is the also the component name of the inner JPanel.
     * @param img   Buffered Image to display.
     */
    public ImagePanel(String name, BufferedImage img) {
        this(name, img, null);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Instantiates the new image panel with the given size.
     *
     * @param name  Name of the image. This is the also the component name of the inner JPanel.
     * @param size  Size for the place holder until the real image is set.
     */
    public ImagePanel(String name, Dimension size) {
        super();

        this.name = name;

        dp = new DrawPanel(name);

        setViewportView(dp);

//		setLayout(new BorderLayout());
//		this.add(dp,BorderLayout.CENTER);

//		this.size.width  = (int) (size.width  * scale + 0.5);
//		this.size.height = (int) (size.height * scale + 0.5);

        this.size.width = size.width;
        this.size.height = size.height;

        dp.addMouseListener(this);
        dp.addMouseMotionListener(this);
        dp.addMouseWheelListener(this);

        dp.setFocusable(true);
        dp.requestFocus();
        dp.addKeyListener(this);

        scale = PreferencesUtils.getFloat("EvolutionBrain." + name, "zoom", scale);
        internSetSize();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Instantiates the new image panel with the given size.
     *
     * @param name          Name of the image. This is the also the component name of the inner JPanel.
     * @param img           BufferedImage to display.
     * @param imgOverlay    BufferedImage as overlay.

     */
    public ImagePanel(String name, BufferedImage img, BufferedImage imgOverlay) {
        this(name, new Dimension(img.getWidth(), img.getHeight()));
        this.img = img;
        this.imgOverlay = imgOverlay;
        setImage(img);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Sets the new image. The size is set to image size and the panel is refreshed.
     *
     * @param newImg   The new image
     */
    public final void setImage(BufferedImage newImg) {
        synchronized (sync) {
            this.img = newImg;
//			size.width  = (int) (img.getWidth()  * scale + 0.5);
//			size.height = (int) (img.getHeight() * scale + 0.5);
            size.width  = newImg.getWidth();
            size.height = newImg.getHeight();

            if (debug) System.err.println("SET INVLAID TO true");
            mergedImgInvalid = true;
            mergedImg = null;
        }
        //refresh();

        internSetSize();
    }

    // ---------------------------------------------------------------------------------------------

    private void internSetSize() {
        scaledSize = new Dimension((int) (size.getWidth() * scale + 0.5), (int) (size.getHeight() * scale + 0.5));
        //this.setPreferredSize(scaledSize);
        //this.setMinimumSize(new Dimension(scaledSize.width, scaledSize.height));
        //dp.setPreferredSize(scaledSize);
        //dp.setMinimumSize(scaledSize);
        mergedImgInvalid = true;
        mergedImg = null;
        dp.revalidate();
    }

    // ---------------------------------------------------------------------------------------------

    private final List<ImagePanelDrawerInterface> drawer = new LinkedList<ImagePanelDrawerInterface>();

    private final List<EventInterface>            event  = new LinkedList<EventInterface>();

    // ---------------------------------------------------------------------------------------------

    /**
     * Adds a ImagePanelDrawer drawer to this ImagePanel. All ImagePanelDrawer are called after the
     * image is shown witch the current Graphics.
     *
     * @param ipd    ImagePanelDrawer to add
     */
    public void addImagerPanelDrawer(ImagePanelDrawerInterface ipd) {
        synchronized (drawer) {
            drawer.add(ipd);
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Try to remove a ImagePanelDrawer drawer.
     *
     * @param ipd    ImagePanelDrawer to remove
     *
     * @return true, if successful
     */
    public boolean removeImagerPanelDrawer(ImagePanelDrawerInterface ipd) {
        synchronized (drawer) {
            return drawer.remove(ipd);
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Removes all ImagePanelDrawer drawer.
     */
    public void removeAllImagerPanelDrawer() {
        synchronized (drawer) {
            drawer.clear();
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Adds an event receiver.
     *
     * @param evi   Event receiver
     */
    public void addEventReceiver(EventInterface evi) {
        if (!event.contains(evi)) event.add(evi);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Removes en event receiver.
     *
     * @param evi   Event receiver
     *
     * @return true, if successful
     */
    public boolean removeEventReceiver(EventInterface evi) {
        return event.remove(evi);
    }

    // ---------------------------------------------------------------------------------------------

    // rendering to the image
    private void renderOffscreen() {
        synchronized (sync) {
            do {
                int w, h;

                w = scaledSize.width;
                h = scaledSize.height;

                if (mergedImg.validate(getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE) {
                    // old vImg doesn't work with new GraphicsConfig; re-create it
                    mergedImg = createVolatileImage(w, h);
                }

                //System.err.println("New mergedImg...");
                Graphics2D g = mergedImg.createGraphics();

                Utils.setQuality(g);

                //g.setColor(new Color(0,0,0));
                g.setColor(new Color(60, 60, 60));
                g.fillRect(0, 0, w, h);

                g.scale(scale, scale);

                if (img != null) {
                    g.drawImage(img, 0, 0, null);
//				g.setColor(Color.RED);
//				g.drawRect(0,0,img.getWidth()-1,img.getHeight()-1);
                }
                if (imgOverlay != null) {
                    g.drawImage(imgOverlay, 0, 0, null);
//				g.setColor(Color.ORANGE);
//				g.drawRect(0,0,imgOverlay.getWidth()-1,imgOverlay.getHeight()-1);
                }

                g.dispose();
            } while (mergedImg.contentsLost());

            if (!DIRECT_DRAW) {
                if (imgDrawPanelOverlay != null &&
                        (imgDrawPanelOverlay.getWidth() != mergedImg.getWidth() ||
                                imgDrawPanelOverlay.getHeight() != mergedImg.getHeight())) imgDrawPanelOverlay = null;

                if (imgDrawPanelOverlay == null) {
                    imgDrawPanelOverlay = new BufferedImage(mergedImg.getWidth(), mergedImg.getHeight(),
                            BufferedImage.TYPE_INT_ARGB);
                    updateCacheImage();
                }
            }

            if (debug) System.err.println("SET INVLAID TO false");
            mergedImgInvalid = false;

        } // GOOD ???
    }

    // ---------------------------------------------------------------------------------------------


    private int           xTranslate;

    private int           yTranslate;

    // image creation
    private VolatileImage mergedImg;

    private boolean       mergedImgInvalid = true;

    private boolean       debug;

    private float         scale            = 1;

    public  boolean       writeImage        = false;

    public  int           writeImageNum     = 0;
    /**
     * The Class DrawPanel.
     */
    class DrawPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        /**
         * Instantiates a new draw panel.
         *
         * @param name the name
         */
        DrawPanel(String name) {
            super();
            setName(name);
        }

        /* (non-Javadoc)
         * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
         */
        @Override
        protected void paintComponent(Graphics g) {
            internPaintComponent(g);
            if (writeImage) {
                wic++;
                if (wic == 10) {
                    wic = 0;
                    System.err.println("Write image "+name+" "+writeImageNum);

                    if (writeImageNum == 0) {
                        File f = new File("./film/");
                        f.mkdir();
                    }

                    BufferedImage bi = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_ARGB);
                    internPaintComponent(bi.createGraphics());

                    bi = cutRim(bi,Color.WHITE);

                    int w = bi.getWidth();
                    int h = bi.getHeight();
                    for (int y=0; y<h; y++) {
                        for (int x=0; x<w; x++) {
                            int c = bi.getRGB(x, y);
                            // -16777216
                            if (c == 0xff000000) bi.setRGB(x,y,0);
                            //System.err.println("c="+Integer.toHexString(c));
                            //System.exit(0);
                        }

                    }
                    Utils.writeImage(bi, String.format("./film/%05d_%s.png", new Integer(writeImageNum),name));
                    writeImageNum++;
                }
            }
        }
        private int wic = 0;

        private void internPaintComponent(Graphics g) {
            if (Utils.skipPaint(this)) return;

            Graphics2D g2 = (Graphics2D) g;

            Utils.setQuality((Graphics2D) g);

            int w = getWidth();
            int h = getHeight();
            g.setColor(Color.WHITE);
//			g.setColor(new Color(20,20,20));
            g.fillRect(0, 0, w, h);


            xTranslate = (int) ((w - scaledSize.width) / 2.0 + 0.5);
            yTranslate = (int) ((h - scaledSize.height) / 2.0 + 0.5);

            g.setColor(Color.GRAY);
            g.drawRect(xTranslate - 1, yTranslate - 1, scaledSize.width + 1, scaledSize.height + 1);

            // System.err.println("scale = "+scale+"  size = "+size);


            if (debug) Utils.writeImage(img, "2_img");

            if (debug)
                System.err.println("A mergedImg ? " + (mergedImg != null ? "got" : "no") + "  invalid = "
                        + mergedImgInvalid);

            // copying from the image (here, gScreen is the Graphics
            // object for the onscreen window)
            synchronized (sync) {

                if (mergedImg == null) {
                    mergedImg = createVolatileImage(scaledSize.width, scaledSize.height);
                }

                do {
                    if (debug)
                        System.err.println("B mergedImg ? " + (mergedImg != null ? "got" : "no") + "  invalid = "
                                + mergedImgInvalid);


                    int returnCode = mergedImg.validate(getGraphicsConfiguration());
                    if (returnCode == VolatileImage.IMAGE_RESTORED) {
                        // Contents need to be restored
                        renderOffscreen(); // restore contents
                        if (debug)
                            System.err.println("C mergedImg ? " + (mergedImg != null ? "got" : "no") + "  invalid = "
                                    + mergedImgInvalid);
                    } else
                        if (returnCode == VolatileImage.IMAGE_INCOMPATIBLE) {
                            // old vImg doesn't work with new GraphicsConfig; re-create it
                            mergedImg = createVolatileImage(scaledSize.width, scaledSize.height);
                            if (debug)
                                System.err.println("D mergedImg ? " + (mergedImg != null ? "got" : "no")
                                        + "  invalid = " + mergedImgInvalid);
                            renderOffscreen();
                        } else {
                            if (mergedImgInvalid) renderOffscreen();
                            if (debug)
                                System.err.println("E mergedImg ? " + (mergedImg != null ? "got" : "no")
                                        + "  invalid = " + mergedImgInvalid);
                        }

                    if (debug)
                        System.err.println("F mergedImg ? " + (mergedImg != null ? "got" : "no") + "  invalid = "
                                + mergedImgInvalid);


                    g.drawImage(mergedImg, xTranslate, yTranslate, this);
//					g.drawImage(mergedImg,(int) (xTranslate / scale + 0.5),(int)( yTranslate / scale), this);
                } while (mergedImg.contentsLost() || mergedImgInvalid);
            } // sync

            if (debug)
                System.err.println("G mergedImg ? " + (mergedImg != null ? "got" : "no") + "  invalid = "
                        + mergedImgInvalid);
            if (debug) Utils.writeImage(img, "3_img");
            if (debug) Utils.writeImage(mergedImg.getSnapshot(), "4_mimg");

            long startMs = System.currentTimeMillis();

            g2.translate(xTranslate, yTranslate);
//			g2.scale(1.0/scale,1.0/scale);
//			g2.scale(scale,scale);

            if (DIRECT_DRAW) {

                drawAll(g2);
            } else {
                BufferedImage oi = imgDrawPanelOverlay;
                if (oi != null) {
                    g.drawImage(oi, 0, 0, this);
                }
            }

            long stopMs = System.currentTimeMillis();
            if (stopMs - startMs > 100) {
                System.err.println("Warn: time for draw = " + (stopMs - startMs) + "ms");
            }
        }

    }

    // ---------------------------------------------------------------------------------------------

    private static BufferedImage cutRim(BufferedImage img, Color rimColor) {
        Rectangle r = findRim(img,rimColor);
        // Same size --> no rim...:
        if (r.width == img.getWidth() && r.height == img.getHeight())
            return img;

        BufferedImage ret = new BufferedImage(r.width, r.height, img.getType());

        Graphics2D g = ret.createGraphics();
        g.drawImage(img, -r.x, -r.y, null);
        g.dispose();

        return ret;
    }

    // ---------------------------------------------------------------------------------------------

    private static Rectangle findRim(BufferedImage img,Color rimColor) {

        int w = img.getWidth();
        int h = img.getHeight();

        int rim = rimColor.getRGB();

        Rectangle r = new Rectangle(0, 0, w, h);

        // -----------------------------------------------------------------------------------------
        // First Y
        // -----------------------------------------------------------------------------------------
        int firstY = -1;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int c = img.getRGB(x, y);

                if (c != rim) {
                    firstY = y + 1;
                    break;
                }
            }
            if (firstY != -1)
                break;
        }

        // -----------------------------------------------------------------------------------------
        // Last Y
        // -----------------------------------------------------------------------------------------
        int lastY = -1;

        for (int y = h - 1; y >= 0; y--) {
            for (int x = 0; x < w; x++) {
                int c = img.getRGB(x, y);

                if (c != rim) {
                    lastY = y;
                    break;
                }
            }
            if (lastY != -1)
                break;
        }

        if (lastY == -1)
            return r;

        // -----------------------------------------------------------------------------------------
        // First X
        // -----------------------------------------------------------------------------------------
        int firstX = -1;

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int c = img.getRGB(x, y);

                if (c != rim) {
                    firstX = x + 1;
                    break;
                }
            }
            if (firstX != -1)
                break;
        }

        // -----------------------------------------------------------------------------------------
        // Last X
        // -----------------------------------------------------------------------------------------
        int lastX = -1;

        for (int x = w - 1; x >= 0; x--) {
            for (int y = 0; y < h; y++) {
                int c = img.getRGB(x, y);

                if (c != rim) {
                    lastX = x;
                    break;
                }
            }
            if (lastX != -1)
                break;
        }

        r.x = firstX;
        r.y = firstY;
        r.width = lastX - firstX;
        r.height = lastY - firstY;
        return r;
    }

    // ---------------------------------------------------------------------------------------------

    private void drawAll(Graphics2D g2) {
        Utils.setQuality(g2);

        AffineTransform saveAt = g2.getTransform();

//		System.err.println("Draw ALL !!!!");
        synchronized (drawer) {
            for (ImagePanelDrawerInterface ipd : drawer) {
                ipd.draw(size.width, size.height, g2);
                g2.setTransform(saveAt);
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    private void updateCacheImage() {
        if (!DIRECT_DRAW) {
            BufferedImage oi = imgDrawPanelOverlay;
            if (oi != null) {
//				System.err.println("Draw REFRESH 111 !!!!");
                Graphics2D g2 = oi.createGraphics();

                long startMs = System.currentTimeMillis();
                Composite oc = g2.getComposite();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
                g2.fillRect(0, 0, oi.getWidth(), oi.getHeight());
                g2.setComposite(oc);

                Utils.setQuality(g2);
                g2.scale(scale, scale);

                drawAll(g2);

                long stopMs = System.currentTimeMillis();
                if (stopMs - startMs > 100) {
                    System.err.println("Warn: time for overlay draw = " + (stopMs - startMs) + "ms");
                }

                g2.dispose();
            }
        }

    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Refresh.
     */
    public void refresh() {

//		System.err.println("Draw REFRESH !!!!");

        updateCacheImage();
        Utils.repaintCtrl(dp, 20);
    }

    // ---------------------------------------------------------------------------------------------

    private MouseEvent getTranslatedMouseEvent(MouseEvent e) {
        return new MouseEvent(
                e.getComponent(),
                e.getID(),
                e.getWhen(),
                e.getModifiers(),
                (int) ((e.getX() - xTranslate) / scale + 0.5),
                (int) ((e.getY() - yTranslate) / scale + 0.5),
                e.getClickCount(),
                e.isPopupTrigger(),
                e.getButton());
    }

    // ---------------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent e) {
//		e.translatePoint(-xTranslate,-yTranslate);

        MouseEvent n = getTranslatedMouseEvent(e);
        // System.err.println("Clicked at "+n.getPoint()+"   from "+e.getPoint());

        for (EventInterface evi : event) {
            evi.mouseEvent(n);
        }

        refresh();
    }

    // ---------------------------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseEntered(MouseEvent e) {
//		e.translatePoint(-xTranslate,-yTranslate);

        MouseEvent n = getTranslatedMouseEvent(e);

        dp.requestFocus();

        for (EventInterface evi : event) {
            evi.mouseEvent(n);
        }
    }

    // ---------------------------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(MouseEvent e) {
//		e.translatePoint(-xTranslate,-yTranslate);
        MouseEvent n = getTranslatedMouseEvent(e);
        for (EventInterface evi : event) {
            evi.mouseEvent(n);
        }
    }

    // ---------------------------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
//		e.translatePoint(-xTranslate,-yTranslate);
        MouseEvent n = getTranslatedMouseEvent(e);
        for (EventInterface evi : event) {
            evi.mouseEvent(n);
        }
    }

    // ---------------------------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {
//		e.translatePoint(-xTranslate,-yTranslate);
        MouseEvent n = getTranslatedMouseEvent(e);
        for (EventInterface evi : event) {
            evi.mouseEvent(n);
        }
    }

    // ---------------------------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseDragged(MouseEvent e) {
//		e.translatePoint(-xTranslate,-yTranslate);
        MouseEvent n = getTranslatedMouseEvent(e);
        for (EventInterface evi : event) {
            evi.mouseEvent(n);
        }
    }

    // ---------------------------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseMoved(MouseEvent e) {
//		e.translatePoint(-xTranslate,-yTranslate);
        MouseEvent n = getTranslatedMouseEvent(e);
        for (EventInterface evi : event) {
            evi.mouseEvent(n);
        }
    }

    // ---------------------------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
//		e.translatePoint(-xTranslate,-yTranslate);
//		for (EventInterface evi : event) evi.mouseEvent(e);

        scale -= e.getWheelRotation() / 10.0;

        if (scale < 0.1) {
            scale = 0.1f;
            Toolkit.getDefaultToolkit().beep();
        }
        if (scale > 10) {
            scale = 10;
            Toolkit.getDefaultToolkit().beep();
        }

        PreferencesUtils.putFloat("EvolutionBrain." + name, "zoom", scale);
        PreferencesUtils.flushPreferences();
//		size.width  = (int) (img.getWidth()  * scale + 0.5);
//		size.height = (int) (img.getHeight() * scale + 0.5);

        //System.err.println("Scale = "+scale);
        internSetSize();
        repaint();
//		this.repaint();
        //dp.repaint();
    }

    // ---------------------------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    @Override
    public void keyPressed(KeyEvent e) {
        for (EventInterface evi : event) {
            evi.keyEvent(e);
        }
    }

    // ---------------------------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    @Override
    public void keyReleased(KeyEvent e) {
        for (EventInterface evi : event) {
            evi.keyEvent(e);
        }
    }

    // ---------------------------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    @Override
    public void keyTyped(KeyEvent e) {
        for (EventInterface evi : event) {
            evi.keyEvent(e);
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Sets the debug.
     *
     * @param debug the new debug
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

}   // of the to complex class ImagePanel :-)
