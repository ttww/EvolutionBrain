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

package tw.master;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import tw.master.crawler.NumberBrainCrawler;
import tw.master.crawler.TestBrainCrawler;
import tw.master.crawler.WalkingBrainCrawler;
import tw.master.engine.Engine;
import tw.master.utils.TerminateListener;
import tw.master.utils.Utils;


/**
 * Main client class.
 *
 * Run with: -XX:CompileThreshold=2 -XX:+AggressiveOpts -XX:+UseFastAccessorMethods -Xdock:name="EvolutionBrain" -Xdock:icon=Brain.png -Xdock:name="EvolutionBrain" -mx1000m -Dsun.io.serialization.extendedDebugInfo=true
 *
 * @author Thomas Welsch
 */
public class EvolutionBrainClientMain implements EvolutionBrainStarterInterface {

    private static final int        NUMBER_OF_BRAINS = 20 * Runtime.getRuntime().availableProcessors();


    public GlobalsClientGui globals;

    public Engine           engine;

    // --------------------------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see tw.master.EvolutionBrainStarterInterface#init()
     */
    @Override
    public void init() throws Exception {

        //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        //UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");

        //System.getProperties().list(System.out);
        if (true) {

            File orgFile = new File("images/Trails.png");
            File ovlFile = new File("images/TrailsEnergy.png");

            BufferedImage img = ImageIO.read(orgFile);
            BufferedImage imgOverlay = null;

            if (orgFile.lastModified() > ovlFile.lastModified()) {
                System.err.println("Create new energy trail");
                imgOverlay = Utils.makeBlureImage(img);
                ImageIO.write(imgOverlay, "PNG", ovlFile);
            } else {
                System.err.println("Use old energy trail");
                imgOverlay = ImageIO.read(ovlFile);
            }

            engine = new Engine(WalkingBrainCrawler.class, NUMBER_OF_BRAINS, img, imgOverlay);

            engine.startPos = new Point.Float(img.getWidth() * 0.77f, img.getHeight() * 0.77f);

            //globalsServer.loadState();
            globals = new GlobalsClientGui(engine);
        }
        //new ResourceWatcherPanel();

        if (false) {
            BufferedImage img = ImageIO.read(new File("images/Trails.png"));
            BufferedImage imgOverlay = null;
            /*,"images/LWS/20-000003.dcm-3.587ppmm--8bits-OL.png"*/

            engine = new Engine(TestBrainCrawler.class, NUMBER_OF_BRAINS, img, imgOverlay);
            globals = new GlobalsClientGui(engine);
        }

        if (false) {
            BufferedImage img = new BufferedImage(335, 160, BufferedImage.TYPE_INT_RGB);
            BufferedImage imgOverlay = new BufferedImage(335, 160, BufferedImage.TYPE_INT_ARGB);

            //			SimpleImagePanel simg  = new SimpleImagePanel(img,ScaleMode.SCALE_CENTER);
            //		SimpleImagePanel simgo = new SimpleImagePanel(imgOverlay,ScaleMode.SCALE_CENTER);

            Graphics2D gi = img.createGraphics();
            Graphics2D go = imgOverlay.createGraphics();

            gi.setFont(new Font("Courier", Font.PLAIN, 9));
            for (int i = 0; i < 10; i++) {
                int x = 30 + i * 30;
                int y = 85;
                go.setColor(new Color(100 + i * 10, 100, 100, 160));
                go.fillRect(x, y - 6, 4, 6);

                gi.setColor(Color.RED);
                gi.drawString(Integer.toString(i), x, y);
            }

            gi.dispose();
            go.dispose();
            //		go.dispose();
            //			Utils.showBean(simg,"IMG");
            //		Utils.showBean(simgo,"IMGO");

            ImageIO.write(imgOverlay, "PNG", new File("Number-Energy.png"));
            ImageIO.write(img, "PNG", new File("Number-View.png"));
            engine = new Engine(NumberBrainCrawler.class, NUMBER_OF_BRAINS, img, imgOverlay);
            globals = new GlobalsClientGui(engine);
            globals.step();
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Starting the simulation.
     *
     * @param starter   The starter object. It's called from a SwingUtilities.invokeLater() runnable.
     */
    public static void startSimulation(final EvolutionBrainStarterInterface starter) {
        tw.master.gl3d.OpenGL3dImplementation.initOpenGL();

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    starter.init();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Main starter for the application.
     *
     * @param args  Not used at the moment.
     */
    public static void main(final String[] args) {
        new TerminateListener(32085);
        startSimulation(new EvolutionBrainClientMain());
    }

} // of class EvolutionBrainClientMain
