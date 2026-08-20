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

package tw.master.crawler;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import tw.master.GlobalsClientGui;
import tw.master.engine.Engine;
import tw.master.utils.ResourceWatcherPanel;
import tw.master.utils.Utils;


/**
 *
 *
 * @author Thomas Welsch
 */
public class WalkingBrainCrawlerTest {

    private static ResourceWatcherPanel rwp;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        rwp = new ResourceWatcherPanel();
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        rwp.disposeAll();
    }

    /**
     * Test method for {@link tw.master.crawler.WalkingBrainCrawler#createBrain()}.
     * @throws Exception
     */
    @Test
    public final void testCreateBrain() throws Exception {
        File orgFile = new File("images/Trails.png");
        File ovlFile = new File("images/TrailsEnergy.png");

        BufferedImage img           = ImageIO.read(orgFile);
        BufferedImage imgOverlay    = null;

        // -----------------------------------------------------------------------------------------
        // Create a blured image of the original trail as an energy image:
        // -----------------------------------------------------------------------------------------
        if (orgFile.lastModified() > ovlFile.lastModified()) {
            imgOverlay = Utils.makeBlureImage(img);
            ImageIO.write(imgOverlay, "PNG", ovlFile);
        }
        else {
            imgOverlay  = ImageIO.read(ovlFile);
        }

        // -----------------------------------------------------------------------------------------
        // We are starting the engine with the reproducible mode, for getting the same
        // Results between runs !
        // -----------------------------------------------------------------------------------------
        WalkingBrainCrawler.setCreateSimpleBrains(false);
        Engine engine = new Engine(WalkingBrainCrawler.class,100,img,imgOverlay,true,0);

        engine.startPos = new Point.Float(img.getWidth() * 0.77f, img.getHeight() * 0.77f);

        GlobalsClientGui globals = new GlobalsClientGui(engine);

        engine.setRunning(true);

        Utils.sleep(10000);

        engine.setRunning(false);

        engine.disposeAll();
        System.err.println("Done");

        globals.disposeAll();
        Utils.sleep(15000);

    }

}
