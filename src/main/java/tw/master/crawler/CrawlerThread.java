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

package tw.master.crawler;

import tw.master.brain.Brain;
import tw.master.engine.Engine;
import tw.master.utils.ServerStartStopStepThread;

/**
 * @author Thomas Welsch
 */
public class CrawlerThread extends ServerStartStopStepThread  {

    //private volatile static int	ctn	= 0;
    private 		int n;

    public CrawlerThread(Engine engine) {
        super("", engine);
        n = engine.gotWorker++;

        setName("CrawlerThread #" + n);
        setDaemon(true);

        System.err.println("CT #" + n + " started");
        // ctn++;

        setAutostep(true);
        setSleepTime(0);
        start();
    }

    @Override
    public void doStep(Engine engine) {

        // -----------------------------------------------------------------------------------------
        // Adjust: Start more or stop the actual thread
        // -----------------------------------------------------------------------------------------
        if (n == 0) {
            int need = engine.wantWorker - engine.gotWorker;

            // System.err.println("wantWorker    " + globals.wantWorker);
            // System.err.println("Need to start " + need);

            while (need-- > 0) {
                new CrawlerThread(engine);
                // System.err.println("Start new");
            }
            // Utils.sleep(10); // Be sure, all is started :-)
        }
        else {
            if (n >= engine.wantWorker) {
                terminate();
                engine.gotWorker--;
                System.err.println("CT #" + n + " stopped");
                return;
            }
        }

        Crawler cc;
        try {
            cc = engine.runQueue.take();

            synchronized (cc) {		// Against mutation
                cc.doSimulationStep();
            }

            // Update brain statistics
            if (cc instanceof AbstractBrainCrawler) {
                AbstractBrainCrawler abc = (AbstractBrainCrawler) cc;
                Brain brain = abc.getBrain();

                engine.handeldNeurons  += brain.getNeuronCount();
                engine.handeldSynapses += brain.getSynapsesCount();
            }
            //System.err.println(n+" "+globals.runQueueBarrier.getCount());
            engine.runQueueBarrier.countDown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
