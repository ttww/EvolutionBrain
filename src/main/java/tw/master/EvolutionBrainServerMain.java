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

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import tw.master.crawler.WalkingBrainCrawler;
import tw.master.engine.Engine;


/**
 * Main client class.
 *
 * Run with: -XX:CompileThreshold=2 -XX:+AggressiveOpts -XX:+UseFastAccessorMethods -Xdock:name="EvolutionBrainServer" -Xdock:icon=BrainServer.png -Xdock:name="EvolutionBrain" -mx1000m -Dsun.io.serialization.extendedDebugInfo=true
 *
 * @author Thomas Welsch
 */
public class EvolutionBrainServerMain implements EvolutionBrainStarterInterface {

    private static final int	NUMBER_OF_BRAINS	= 20 * Runtime.getRuntime().availableProcessors();

    Engine		engine;

    // ---------------------------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see tw.master.EvolutionBrainStarterInterface#init()
     */
    @Override
    public void init() throws Exception {
        System.err.println("Load images...");
        BufferedImage img           = ImageIO.read(new File("images/Trails.png"));
        BufferedImage imgOverlay    = null;

        System.err.println("Create server...");
        engine = new Engine(WalkingBrainCrawler.class, NUMBER_OF_BRAINS, img, imgOverlay);

        /*GlobalsServerGui serverGui =*/  new GlobalsServerGui(engine);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Starting the simulation.
     *
     * @param starter   The starter object. It's called from a SwingUtilities.invokeLater() runnable.
     */
    public static void startSimulation(final EvolutionBrainStarterInterface starter)
    {
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
        startSimulation(new EvolutionBrainServerMain());
    }

}   // of class EvolutionBrainServerMain
