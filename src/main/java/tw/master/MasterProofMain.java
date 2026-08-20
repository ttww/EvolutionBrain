/*
 *	This file is part of the EvolutionBrain project.
 *
 *	Copyright (c) 2011 by Thomas Welsch (ttww@gmx.de). All rights reserved.
 *
 *	EvolutionBrain is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU Lesser General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	EvolutionBrain is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU Lesser General Public License for more details.
 *
 *	You should have received a copy of the GNU Lesser General Public License
 *	along with EvolutionBrain.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package tw.master;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import tw.master.crawler.WalkingBrainCrawler;
import tw.master.engine.Engine;
import tw.master.utils.TerminateListener;
import tw.master.utils.Utils;


/**
 * This main class demonstrate a simple crawler/brain simulation.<p>
 * 
 * Run with the following line:
 *      -XX:+AggressiveOpts -XX:+UseFastAccessorMethods -Xdock:name="EvolutionBrain" -Xdock:icon=Brain.png -Xdock:name="EvolutionBrain" -mx1000m -Dsun.io.serialization.extendedDebugInfo=true
 *
 *
 * @author Thomas Welsch
 */
public class MasterProofMain implements EvolutionBrainStarterInterface {

    // ---------------------------------------------------------------------------------------------

    /**
     * Init. the crawler and brains.
     *
     * @see tw.master.EvolutionBrainStarterInterface#init()
     */
    @Override
    public void init() throws Exception {
        File orgFile = new File("images/Trails.png");
        File ovlFile = new File("images/TrailsEnergy.png");

        BufferedImage img = ImageIO.read(orgFile);
        BufferedImage imgOverlay = null;

        // -----------------------------------------------------------------------------------------
        // Create a blured image of the original trail as an energy image:
        // -----------------------------------------------------------------------------------------
        if (orgFile.lastModified() > ovlFile.lastModified()) {
            imgOverlay = Utils.makeBlureImage(img);
            ImageIO.write(imgOverlay, "PNG", ovlFile);
        }
        else {
            imgOverlay = ImageIO.read(ovlFile);
        }

        // -----------------------------------------------------------------------------------------
        // We are starting the engine with the reproducible mode, for getting the same
        // Results between runs !
        // -----------------------------------------------------------------------------------------
        Engine engine = new Engine(WalkingBrainCrawler.class, 100, img, imgOverlay, true, 0);

        engine.startPos = new Point.Float(img.getWidth() * 0.77f, img.getHeight() * 0.77f);

        new GlobalsClientGui(engine);
    }

    // ---------------------------------------------------------------------------------------------

    public static void main(final String[] args) {

        new TerminateListener(32085); // Terminate an already running MasterProofMain

        EvolutionBrainClientMain.startSimulation(new MasterProofMain());
    }

} // end of class TestBrainCrawlerMain

