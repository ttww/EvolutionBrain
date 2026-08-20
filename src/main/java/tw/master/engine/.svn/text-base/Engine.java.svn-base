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

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import tw.master.crawler.Crawler;
import tw.master.crawler.CrawlerList;
import tw.master.crawler.CrawlerThread;
import tw.master.crawler.RunQueueFillerThread;
import tw.master.engine.EngineEventsInterface.ServerEvent;
import tw.master.gui.DisplayedCounter;
import tw.master.math.BestDoubleList;
import tw.master.remote.RemoteEnginesManager;
import tw.master.utils.Rnd;
import tw.master.utils.UpdateListenerInterface;
import tw.master.utils.Utils;
import tw.master.visionfield.VisionFieldHandler;


/**
 * @author     tw
 */
public class Engine implements UpdateListenerInterface {

    public Rectangle2D.Float            limits;

    public Point.Float                  startPos;

    public DisplayedCounter             steps;

    public DisplayedCounter             bornCount;

    public DisplayedCounter             diedCount;

    public long                         createCount;

    public DisplayedCounter             fps;

    public DisplayedCounter             neuronsLoadCount;

    public DisplayedCounter             synapsesLoadCount;

    public boolean                      fastDraw;


    public List<Crawler>                allCrawlers;

    public CountDownLatch               runQueueBarrier;

    public LinkedBlockingQueue<Crawler> runQueue;

    public volatile int                 handeldNeurons;

    public volatile long                handeldSynapses;

    public volatile int                 lastHandeldNeurons;

    public volatile long                lastHandeldSynapses;

    public boolean                      running;

    public int                          wantWorker;

    public volatile int                 gotWorker;

    public CrawlerList                  bestCrawlers;

    public int                          numberOfCrawlers;

    public Class<?>                     crawlerClass;

    public RunQueueFillerThread         runQueueFillerThread;

    public VisionFieldHandler           visionFieldHandler;

    public Crawler                      watchedCrawler;

    public Crawler                      controlledCrawler;

    public BufferedImage                img;

    public BufferedImage                imgOverlay;

    public RemoteEnginesManager         remoteEngines;


    public boolean                      reproducibleMode;



    // ---------------------------------------------------------------------------------------------
    public Engine(
            final Class<?> crawlerClass,
            final int numberOfCrawlers,
            final BufferedImage img,
            final BufferedImage imgOverlay) {

        this(crawlerClass, numberOfCrawlers, img, imgOverlay, false, 0);
    }


    public Engine(
            final Class<?> crawlerClass,
            final int numberOfCrawlers,
            final BufferedImage img,
            final BufferedImage imgOverlay,
            final boolean reproducibleMode,
            final long randomSeed) {


        this.reproducibleMode = reproducibleMode;

        if (reproducibleMode) {
            Rnd.setInitialRandomSeed(randomSeed);
        }

        final int cpus = Runtime.getRuntime().availableProcessors();

        this.img = img;
        this.imgOverlay = imgOverlay;


        this.numberOfCrawlers = numberOfCrawlers;

        this.crawlerClass = crawlerClass;

        if (img != null) {
            limits = new Rectangle2D.Float(1, 1, img.getWidth() - 2, img.getHeight() - 2);
            startPos = new Point.Float(img.getWidth() * 0.77f, img.getHeight() * 0.55f);
        } else {
            limits = new Rectangle2D.Float(0, 0, 100, 100);
            startPos = new Point.Float(50, 50);
        }



        wantWorker = cpus + 1;
        // wantWorker = 4;

        if (reproducibleMode) {
            wantWorker = 1;
        }

        steps = new DisplayedCounter();
        bornCount = new DisplayedCounter();
        diedCount = new DisplayedCounter();
        fps = new DisplayedCounter();
        neuronsLoadCount = new DisplayedCounter();
        synapsesLoadCount = new DisplayedCounter();

        neuronsLoadCount.setDivisor(1024 * 1024);
        synapsesLoadCount.setDivisor(1024 * 1024);

        runQueue = new LinkedBlockingQueue<Crawler>();

        allCrawlers = new ArrayList<Crawler>();
        bestCrawlers = new CrawlerList(numberOfCrawlers);

        visionFieldHandler = new VisionFieldHandler(this.img, this.imgOverlay);



        runQueueFillerThread = new RunQueueFillerThread(this);
        runQueueFillerThread.setSleepTime(0);
        runQueueFillerThread.setAutostep(false);
        runQueueFillerThread.registerUpdateListener(this);
        runQueueFillerThread.start();

        // Start one calculation thread. If more are needed, they will start
        // automatically.
        new CrawlerThread(this);

        if (reproducibleMode) {
            System.err.println("-------------------------------------------------------------------------------------");
            System.err.println("Warning: Engine is running in reproducible mode with random seed " + randomSeed + " !");
            System.err.println("         This reduces the use of threads (eg. only on calculation thread) but");
            System.err.println("         gives the same result between different runs with the same seed.");
            System.err.println("         Depending on your system you are loosing a lot of CPU power in this mode !");
            System.err.println("-------------------------------------------------------------------------------------");
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void enableRemoteEngines() {
        synchronized (this) {
            if (remoteEngines == null) {
                remoteEngines = new RemoteEnginesManager(this);
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    private LinkedList<EngineEventsInterface> changeListener = new LinkedList<EngineEventsInterface>();

    public void addChangeListener(final EngineEventsInterface gse) {
        synchronized (changeListener) {
            if (!changeListener.contains(gse)) changeListener.add(gse);
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void removeUpdateListener(final EngineEventsInterface gse) {
        synchronized (changeListener) {
            changeListener.remove(gse);
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * @param  c
     */
    public void setWatchedCrawler(final Crawler c) {
        //if (watchedCrawler != null) watchedCrawler.isWatched = false;
        watchedCrawler = c;
        //watchedCrawler.isWatched = true;

        sendChangeEvent(ServerEvent.WatchedChanged, c);
    }

    public void sendChangeEvent(final ServerEvent se, Crawler c) {
        for (EngineEventsInterface gse : changeListener)
            gse.serverEvent(se, c);
    }



    // ---------------------------------------------------------------------------------------------

    File defaultBrainFile = new File("brain.data");

    public void saveState() {
        saveState(defaultBrainFile);
    }

    @SuppressWarnings("boxing")
    public void saveState(final File brainFile) {
        // Write to disk with FileOutputStream
        try {

            long startMs = System.currentTimeMillis();

            FileOutputStream f_out = new FileOutputStream(brainFile);
            // Write object with ObjectOutputStream
            ObjectOutputStream obj_out = new ObjectOutputStream(f_out);

            System.err.println("Write metadata...");

            // Version
            obj_out.writeObject(1);

            obj_out.writeObject(this.handeldNeurons);
            obj_out.writeObject(this.handeldSynapses);
            obj_out.writeObject(this.lastHandeldNeurons);
            obj_out.writeObject(this.lastHandeldSynapses);

            obj_out.writeObject(this.fps.getCounter());
            obj_out.writeObject(this.steps.getCounter());
            obj_out.writeObject(this.bornCount.getCounter());
            obj_out.writeObject(this.diedCount.getCounter());
            obj_out.writeObject(this.neuronsLoadCount.getCounter());
            obj_out.writeObject(this.synapsesLoadCount.getCounter());

            obj_out.writeObject(this.numberOfCrawlers);
            obj_out.writeObject(this.createCount);

            System.err.println("Write brains...");

            // Write object out to disk
            obj_out.writeObject(this.allCrawlers);

            System.err.println("Write best brains...");

            // Write object out to disk
            obj_out.writeObject(this.bestCrawlers.getBestList());

            obj_out.writeObject(this.watchedCrawler);

            System.err.println("Write brains close...");

            obj_out.close();
            long stopMs = System.currentTimeMillis();

            System.err.println("Write brains done... Time " + (stopMs - startMs) + " ms  for " + brainFile.length()
                    + " bytes.");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendChangeEvent(ServerEvent.CrawlerSaved, null);
        System.err.println("Write finished...");

    }

    public void loadState() {
        loadState(defaultBrainFile);
    }

    @SuppressWarnings({ "unchecked", "boxing" })
    public void loadState(final File brainFile) {
        try {
            long startMs = System.currentTimeMillis();

            System.err.println("Open brains...");

            // Read from disk using FileInputStream
            FileInputStream f_in = new FileInputStream(brainFile);


            System.err.println("Read metadata...");

            // Read object using ObjectInputStream
            ObjectInputStream obj_in = new ObjectInputStream(f_in);

            // Version
            obj_in.readObject();

            this.handeldNeurons = (Integer) obj_in.readObject();
            this.handeldSynapses = (Long) obj_in.readObject();
            this.lastHandeldNeurons = (Integer) obj_in.readObject();
            this.lastHandeldSynapses = (Long) obj_in.readObject();

            this.fps.setCounter((Long) obj_in.readObject());
            this.steps.setCounter((Long) obj_in.readObject());
            this.bornCount.setCounter((Long) obj_in.readObject());
            this.diedCount.setCounter((Long) obj_in.readObject());
            this.neuronsLoadCount.setCounter((Long) obj_in.readObject());
            this.synapsesLoadCount.setCounter((Long) obj_in.readObject());

            this.numberOfCrawlers = (Integer) obj_in.readObject();
            this.createCount = (Long) obj_in.readObject();

            System.err.println("Read brains...");

            // Read an object
            this.allCrawlers = (List<Crawler>) obj_in.readObject();
            for (Crawler c : this.allCrawlers)
                c.setEngine(this);

            System.err.println("Read brains count = " + this.allCrawlers.size());


            System.err.println("Read best brains...");

            BestDoubleList newBestCrawlers = (BestDoubleList) obj_in.readObject();
            this.bestCrawlers.setBestList(newBestCrawlers);
            System.err.println("Read best brains count = " + newBestCrawlers.size());

            Crawler c = (Crawler) obj_in.readObject();
            if (c != null) {
                c.setEngine(this);
                this.setWatchedCrawler(c);
            }

            System.err.println("Read brains close...");
            obj_in.close();

            long stopMs = System.currentTimeMillis();

            System.err.println("Read brains done... Time " + (stopMs - startMs) + " ms  for " + brainFile.length()
                    + " bytes.");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        sendChangeEvent(ServerEvent.CrawlerLoaded, null);

    }

    /**
     * @param  running
     */
    public synchronized void setRunning(final boolean running) {

        if (this.running == running) return;

        this.running = running;

        runQueueFillerThread.setAutostep(running);

        if (!running) {
            long sleepTime = 1;
            while (!runQueueFillerThread.isQuit() && runQueueFillerThread.isInsideRun()) {
                System.err.println("Wait for finish run....!");
                Utils.sleep(sleepTime);
                sleepTime *= 2;
                if (sleepTime > 1000) sleepTime = 1000;
            }

        }
    }


    public void setMaxRunsPerSecond(final int rs) {
        if (rs == 0) runQueueFillerThread.setSleepTime(0);
        else
            runQueueFillerThread.setSleepTime(1000 / rs);
    }



    public ArrayList<Crawler> externalAddedCrawlerList = new ArrayList<Crawler>(50);

    public void addCrawlerToRunList(final Crawler c) {
        synchronized (externalAddedCrawlerList) {
            externalAddedCrawlerList.add(c);
        }
    }


    private LinkedBlockingDeque<Crawler> extractedCrawlerList = new LinkedBlockingDeque<Crawler>();


    private LinkedList<Crawler>          toRemoveList         = new LinkedList<Crawler>();

    public synchronized Crawler getForTransfer(Crawler c) {

        synchronized (toRemoveList) {
            toRemoveList.add(c);
            //	c.removeFromRunListForTransfer = true;
        }

        if (!running) doUpdate(); // No thread running, do it now !

        // Wait for the RunQueueFillerThread for putting the market Crawler to the
        // transfer list:
        try {
            c = extractedCrawlerList.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.err.println("Got best crawler " + c);
        return c;
    }

    @Override
    public void doUpdate() {
        if (runQueueFillerThread.isInsideRun()) throw new IllegalStateException("BAD UPDATE");

        synchronized (toRemoveList) {
            if (runQueueFillerThread.isInsideRun()) throw new IllegalStateException("BAD UPDATE");
            if (toRemoveList.size() > 0) {
                allCrawlers.removeAll(toRemoveList);
                bestCrawlers.removeAll(toRemoveList);

                extractedCrawlerList.addAll(toRemoveList);

                toRemoveList.clear();
            }
        }

        if (runQueueFillerThread.isInsideRun()) throw new IllegalStateException("BAD UPDATE");
        synchronized (externalAddedCrawlerList) {
            if (runQueueFillerThread.isInsideRun()) throw new IllegalStateException("BAD UPDATE");
            if (externalAddedCrawlerList.size() > 0) {
                if (runQueueFillerThread.isInsideRun()) throw new IllegalStateException("BAD UPDATE");
                synchronized (allCrawlers) {
                    if (runQueueFillerThread.isInsideRun()) throw new IllegalStateException("BAD UPDATE");
                    allCrawlers.addAll(externalAddedCrawlerList);
                }

                for (Crawler c : externalAddedCrawlerList) {
                    if (runQueueFillerThread.isInsideRun()) throw new IllegalStateException("BAD UPDATE");
//					System.err.println("re-add to best "+c.getStepCount());
                    bestCrawlers.add(c);

                }

                if (runQueueFillerThread.isInsideRun()) throw new IllegalStateException("BAD UPDATE");
                externalAddedCrawlerList.clear();
            }
            if (runQueueFillerThread.isInsideRun()) throw new IllegalStateException("BAD UPDATE");
        }

        if (runQueueFillerThread.isInsideRun()) throw new IllegalStateException("BAD UPDATE");


    }

    /**
     * @param  v
     */
    public void setNumberOfCrawlers(final int v) {
        numberOfCrawlers = v;
    }


    /**
     * 
     */
    public void disposeAll() {
        this.setRunning(false);
        runQueueFillerThread.terminate();
        sendChangeEvent(ServerEvent.EngineTerminated,null);
        remoteEngines.terminate();
    }


}
