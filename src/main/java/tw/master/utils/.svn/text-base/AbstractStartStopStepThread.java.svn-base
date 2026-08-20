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

package tw.master.utils;

import java.util.LinkedList;



public abstract class AbstractStartStopStepThread extends Thread {

    private boolean          autostep;

    private long             sleepTime;

    private volatile long    stopAfterCount = -1;   // no limits

    private volatile boolean insideRun;

    private boolean        quit           = false;

    /**
     * Create but not start the Thread.
     *
     * @param name
     * @param globals
     */
    public AbstractStartStopStepThread(String name) {
        this(name, 0, false, false);
    }

    public AbstractStartStopStepThread(String name, int sleepTime, boolean autostep) {
        this(name, sleepTime, autostep, false);
    }

    public AbstractStartStopStepThread(String name, int sleepTime, boolean autostep, boolean autostart) {
        setName(name);
        setDaemon(true);
        this.sleepTime = sleepTime;
        this.autostep = autostep;
        if (autostart) start();
    }


    /**
     * @param  autostep
     */
    public final synchronized void setAutostep(boolean autostep) {
        if (this.autostep == autostep) return;

        if (autostep) {
            this.autostep = autostep;
            notify();
        } else {
            this.autostep = autostep;
        }

    }

    /**
     * @return
     */
    public final synchronized boolean getAutostep() {
        return autostep;
    }


    public synchronized void step() {
        notify();
    }

    public abstract void doStep();

    public void terminate() {
        this.quit = true;
        interrupt();
    }

    /**
     * @return the quit
     */
    public boolean isQuit() {
        return quit;
    }

    @Override
    public void run() {

        while (!isQuit()) {

            if (stopAfterCount != -1) {
                stopAfterCount--;
                if (stopAfterCount == 0) {
                    autostep = false;
                    stopAfterCount = -1;
                }
            }

            if (!autostep) {
                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                }
            } // autostep

            if (isQuit()) break;

            long startMs = System.currentTimeMillis();

            insideRun = true;
            doStep();
            insideRun = false;

            // Call registered after stepper:
            if (updateListener.size() != 0) {
                for (UpdateListenerInterface as : updateListener)
                    as.doUpdate();
            }

            long stopMs = System.currentTimeMillis();

            if (stopMs - startMs > 400) {
                System.err.println("Warn: time for step = " + (stopMs - startMs) + "ms for " + this);
            }

            if (isQuit()) break;

            if (sleepTime != 0) Utils.sleep(sleepTime);
        }
        // System.err.println("SS " + getName() + " finished");

    }

    /**
     * @param  sleepTime
     */
    public final void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    public final void stopAfter(long newStopAfterCount) {
        this.stopAfterCount = newStopAfterCount;
    }

    /**
     * @return
     */
    public final long getStopAfterCount() {
        return this.stopAfterCount;
    }

    /**
     * @return
     */
    public final boolean isInsideRun() {
        return insideRun;
    }

    /*
    if (updateListener.size() != 0) {
    	for (UpdateListenerInterface as : updateListener) as.afterStep();
    }
     */
    private final transient LinkedList<UpdateListenerInterface> updateListener = new LinkedList<UpdateListenerInterface>();

    public final void registerUpdateListener(UpdateListenerInterface as) {
        synchronized (updateListener) {
            if (!updateListener.contains(as)) updateListener.add(as);
        }
    }

    public final void deregisterUpdateListener(UpdateListenerInterface as) {
        synchronized (updateListener) {
            updateListener.remove(as);
        }
    }

}
