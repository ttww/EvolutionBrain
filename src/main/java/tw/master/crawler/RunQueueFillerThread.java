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

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import tw.master.crawler.Crawler.LiveState;
import tw.master.engine.Engine;
import tw.master.engine.EngineEventsInterface.ServerEvent;
import tw.master.utils.Rnd;
import tw.master.utils.ServerStartStopStepThread;


public class RunQueueFillerThread extends ServerStartStopStepThread {

    private ArrayList<Crawler> addList     = new ArrayList<Crawler>(50);

    private ArrayList<Crawler> deadList    = new ArrayList<Crawler>(50);

//	private	LinkedList<Crawler> prepareExtractedCrawlerList		= new LinkedList<Crawler>();

    boolean                    firstCreate = true;

    long                       cycle_stop_ms;

    long                       cycle_start_ms;


    public RunQueueFillerThread(Engine engine) {
        super("RunQueueFillerThread", engine);
    }

    @Override
    public void doStep(Engine engine) {

        //System.err.println("STEP");
        engine.steps.add();

        cycle_stop_ms = System.currentTimeMillis();

        long cycle_used_ms = cycle_stop_ms - cycle_start_ms;
        if (cycle_used_ms > 300 && engine.running) System.err.println("Cycle time : " + cycle_used_ms);

        //				System.err.println("Add to RunQueue");

        // -----------------------------------------------------------------------------------------
        // Wait for the calculation threads to finish:
        // This is only done at this place, if we are not running in the reproducibleMode mode !
        // (see below)
        // -----------------------------------------------------------------------------------------
        if (engine.runQueueBarrier != null) try {
            long start_ms = System.currentTimeMillis();

            engine.runQueueBarrier.await();

            long stop_ms = System.currentTimeMillis();
            long used_ms = stop_ms - start_ms;
            if (used_ms > 500) System.err.println("Warning, need more than 500 for barrier wait: " + used_ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        engine.runQueueBarrier = null;

        cycle_start_ms = System.currentTimeMillis();

        // -----------------------------------------------------------------------------------------
        // Sort out dead brains:
        // -----------------------------------------------------------------------------------------
        int brainsForNextRound = 0;
        for (Crawler c : engine.allCrawlers) {

            if (c.isDead()) {
                deadList.add(c);
                continue;
            }

            brainsForNextRound++;
            engine.bestCrawlers.add(c); // Remember the bests

        } // for

        // -----------------------------------------------------------------------------------------
        // Kill the worst fitest  10% of crawlers, if they are older than 7000 steps:
        // -----------------------------------------------------------------------------------------
        int forTheDogIdx = (int) (engine.bestCrawlers.size() * 0.90f);
        for (int i = forTheDogIdx; i < engine.bestCrawlers.size(); i++) {
            Crawler forTheDog = engine.bestCrawlers.getCrawler(i);
            if (forTheDog != null && forTheDog.stepCount > 7000) {
                forTheDog.liveState = LiveState.Dead_ByDog;
            }
        }
//		System.err.println("111. ALL SIZE = "+engine.allCrawlers.size()+"  dead = "+deadCount+"  marked = "+markedCount);


        synchronized (engine.allCrawlers) {
            engine.allCrawlers.removeAll(deadList);
        }

        engine.bestCrawlers.removeAll(deadList);
        for (Crawler c : deadList) {
            engine.sendChangeEvent(ServerEvent.CrawlerDied, c);
        }


        engine.lastHandeldNeurons = engine.handeldNeurons;
        engine.lastHandeldSynapses = engine.handeldSynapses;

        engine.handeldNeurons = 0;
        engine.handeldSynapses = 0;


        // -----------------------------------------------------------------------------------------
        // Fill up until we'r reached the limit that we want to run parallel:
        // -----------------------------------------------------------------------------------------
        engine.runQueueBarrier = new CountDownLatch(brainsForNextRound);
        this.setPriority(MAX_PRIORITY);
        for (Crawler c : engine.allCrawlers) {
            engine.runQueue.add(c);
            brainsForNextRound--;
        } // for
        this.setPriority(NORM_PRIORITY);

        // -----------------------------------------------------------------------------------------
        // If we running in the reproducibleMode mode, we are not working in parallel :-(, but this
        // ensures, that we get the same results between runs.
        // We are waiting here for the (only!) calculation thread to finish:
        // -----------------------------------------------------------------------------------------
        if (engine.reproducibleMode) {
            try {
                engine.runQueueBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            engine.runQueueBarrier = null;
        }

//		System.err.println("222. ALL SIZE = "+engine.allCrawlers.size()+"  dead = "+deadCount+"  marked = "+markedCount);

//		System.err.println("222. ALL SIZE = "+engine.allCrawlers.size());

        if (brainsForNextRound != 0) throw new IllegalStateException("COUNT !!!???");

        engine.diedCount.add(deadList.size());

//		lglobals.bestCrawler.removeAll(deadList);

        deadList.clear();

        engine.bestCrawlers.setSize(engine.numberOfCrawlers);

        // -----------------------------------------------------------------------------------------
        // Starting of mutation:
        // In 80% we are creating mutants from the existing crawlers and creating 20%
        // new new crawler.
        // The parents for the 80% mutants are chosen randomly.
        // -----------------------------------------------------------------------------------------
        int needCrawler = engine.numberOfCrawlers - engine.allCrawlers.size() - addList.size();

        if (needCrawler > 0 && Rnd.rnd(0, 100) >= 80) {
            int mutationCount = needCrawler;
            // mutationCount = 0;
            for (int i = 0; i < mutationCount; i++) {
                Crawler c = engine.bestCrawlers.getCrawler(Rnd.rnd(0, engine.numberOfCrawlers - 1));
                if (c instanceof MutationCrawlerInterface) {
                    MutationCrawlerInterface mc = (MutationCrawlerInterface) c;

                    synchronized (mc) { // Against running
                        Crawler mutant = mc.getMutationCrawler();
                        if (mutant != null) {
                            addList.add(mutant);
                            engine.sendChangeEvent(ServerEvent.CrawlerBorn, mutant);
                        }
                    }
                }
            } // for
        }


//		System.err.println("Need start "+needCrawler);

        // -----------------------------------------------------------------------------------------
        // Fill the crawler list
        // -----------------------------------------------------------------------------------------
        needCrawler = engine.numberOfCrawlers - engine.allCrawlers.size() - addList.size();

        while (needCrawler-- > 0) {
            Crawler c = CrawlerFactory.getNewCrawler(engine.crawlerClass, engine);
            c.setName(Long.toString(engine.createCount));
            engine.sendChangeEvent(ServerEvent.CrawlerBorn, c);

            if (!firstCreate) {
                if (engine.watchedCrawler != null && engine.watchedCrawler.isDead()) {
                    firstCreate = true;
                    engine.controlledCrawler = null;
                }
            }
            if (firstCreate) {
                engine.setWatchedCrawler(c);
                firstCreate = false;
            }

            engine.createCount++;
            engine.sendChangeEvent(ServerEvent.CrawlerBorn, c);

            addList.add(c);
        }
        engine.bornCount.add(addList.size());


//		System.err.println("Add new "+addList.size());
        synchronized (engine.allCrawlers) {
            engine.allCrawlers.addAll(addList);
        }
        addList.clear();
//		System.err.println("Size new "+lglobals.allCrawlers.size());	}

    }


}
