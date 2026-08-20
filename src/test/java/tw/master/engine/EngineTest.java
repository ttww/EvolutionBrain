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

package tw.master.engine;

import static org.junit.Assert.assertNotNull;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import tw.master.brain.Neuron;
import tw.master.crawler.Crawler;
import tw.master.crawler.TestBrainCrawler;
import tw.master.utils.Utils;


/**
 * Actually dummy tests for testing the build environment :-).
 *
 *
 * @author Thomas Welsch
 */
public class EngineTest {

    private static Engine        engine;

    private static BufferedImage img;

    /**
     * Setup engine.
     *
     * @throws Exception    Mostly IO errors :-)
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        File orgFile = new File("images/Trails.png");

        img = ImageIO.read(orgFile);
        engine = new Engine(TestBrainCrawler.class, 100, img, null);
    }

//    @AfterClass
//    public static void tearDownAfterClass() throws Exception {
//    }

    /**
     * Test method for {@link tw.master.engine.Engine#Engine(java.lang.Class, int, java.awt.image.BufferedImage, java.awt.image.BufferedImage)}.
     */
    @Test
    public void testEngine() {
        assertNotNull("Engine failed", engine);

        final LinkedList<String> eventsReceived = new LinkedList<String>();

        engine.setMaxRunsPerSecond(0);

        engine.addChangeListener(new EngineEventsInterface() {

            @Override
            public void serverEvent(ServerEvent se, Crawler c) {
                eventsReceived.add(se.toString());
            }
        });

        System.err.println("Warming up 5 sec...");
        engine.setRunning(true);
        Utils.sleep(5000);
        engine.setRunning(false);

        Neuron.nc = 0;
        Neuron.sc = 0;

        System.err.println("Running 10 sec...");
        long startMs = System.currentTimeMillis();
        engine.setRunning(true);
        Utils.sleep(10000);
        engine.setRunning(false); // Maybe we have to wait a few ms finish the current step

        long stopMs = System.currentTimeMillis();
        float usedMs = stopMs - startMs;

        //System.err.println("Engine.handeldNeurons = " + engine.handeldNeurons);
        System.err.println("Neurons  handeled     = " + Neuron.nc + "  = " + Neuron.nc / usedMs * 1000);
        System.err.println("Synapses handeled     = " + Neuron.sc + "  = " + Neuron.sc / usedMs * 1000);

        //Assert.assertTrue("Neurons: Was not running", engine.handeldNeurons > 0);
        Assert.assertTrue("Neurons: Was not running", Neuron.nc > 0);
        //Assert.assertTrue("Synapses: Was not running", engine.handeldSynapses > 0);
        Assert.assertTrue("Synapses: Was not running", Neuron.sc > 0);
        Assert.assertTrue("No events ", eventsReceived.size() > 0);


    }


    /**
     * Testing the save functionality.
     *
     * @throws IOException  Save failed
     */
    @Test
    public void testSaveLoadEngine() throws IOException {
        File tmpBrainFile = File.createTempFile("testBrain", ".brain");
        tmpBrainFile.deleteOnExit();

        Engine slEngine = new Engine(TestBrainCrawler.class, 100, img, null);
        System.err.println("AAA");
        slEngine.setRunning(true);
        Utils.sleep(10000);
        System.err.println("BBB");
        slEngine.setRunning(false);

        System.err.println("CCC");
        slEngine.saveState(tmpBrainFile);

        System.err.println("DDD");
        System.err.println("Size of " + tmpBrainFile.getAbsolutePath() + "  = " + tmpBrainFile.length());

        slEngine.setRunning(true);
        Utils.sleep(2000);
        slEngine.setRunning(false);

        slEngine = new Engine(TestBrainCrawler.class, 100, img, null);

        slEngine.loadState(tmpBrainFile);
        slEngine.setRunning(true);
        Utils.sleep(2000);
        slEngine.setRunning(false);

        tmpBrainFile.delete();
    }

}
