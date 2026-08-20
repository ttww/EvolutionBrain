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

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import tw.master.crawler.TestBrainCrawler;
import tw.master.engine.Engine;


/**
 * This main class demonstrate a simple crawler/brain simulation.<p>
 * 
 * Run with the following line:
 *      -XX:+AggressiveOpts -XX:+UseFastAccessorMethods -Xdock:name="EvolutionBrain" -Xdock:icon=Brain.png -Xdock:name="EvolutionBrain" -mx1000m -Dsun.io.serialization.extendedDebugInfo=true
 *
 *
 * @author Thomas Welsch
 */
public class TestBrainCrawlerMain implements EvolutionBrainStarterInterface
{

    // ---------------------------------------------------------------------------------------------

    /**
     * Init. the crawler and brains.
     *
     * @see tw.master.EvolutionBrainStarterInterface#init()
     */
    @Override
    public void init() throws Exception
    {
        BufferedImage img           = ImageIO.read(new File("images/Trails.png"));
        BufferedImage imgOverlay    = null;

        Engine              engine  = new Engine(TestBrainCrawler.class,100,img,imgOverlay);

        new GlobalsClientGui(engine);   // Start the engine with GUI

        // or:
        // engine.setRunning(true);     // Start the engine without GUI
    }

    // ---------------------------------------------------------------------------------------------

    public static void main(final String[] args) {
        EvolutionBrainClientMain.startSimulation(new TestBrainCrawlerMain());
    }

}   // end of class TestBrainCrawlerMain

