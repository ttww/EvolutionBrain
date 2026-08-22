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

import javax.swing.SwingUtilities;

import tw.master.brain.Neuron;
import tw.master.utils.GuiStartStopStepThread;
import tw.master.utils.Utils;



public class UpdateThread extends GuiStartStopStepThread {

    private long	lastStep = 0;
//	private long	lastMs;



    public UpdateThread(GlobalsClientGui globals) {
        super("UpdateThread", globals);
    }

    private boolean wasFrontmost = false;
    private boolean switchedOff = false;

    @Override
    public void doStep(GlobalsClientGui lglobals) {
        //System.err.println("RUN = "+lglobals.engine.running+"  sync = "+lglobals.syncWithScreen);

        // -----------------------------------------------------------------------------------------
        // Disable / Enable screen updates if the application is in front ore not.
        // -----------------------------------------------------------------------------------------
        if (lglobals.initDone) {
            boolean isFrontmost = Utils.isFrontmostApplication();
            if (wasFrontmost) {
                if (!lglobals.disableDraw && !isFrontmost) {
                    switchedOff = true;
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            lglobals.disableDrawCB.setSelected(true);
                            lglobals.disableDraw = true;
                            lglobals.updateDrawStoppedTitles();
                        }
                    });
                }
                if (lglobals.disableDraw && isFrontmost && switchedOff) {
                    switchedOff = false;
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            lglobals.disableDrawCB.setSelected(false);
                            lglobals.disableDraw = false;
                            lglobals.updateDrawStoppedTitles();
                        }
                    });
                }

            }
            else {
                if (isFrontmost) wasFrontmost = true;
            }
        }

        lglobals.engine.fps.add();

        lglobals.engine.neuronsLoadCount.add(Neuron.nc);
        lglobals.engine.synapsesLoadCount.add(Neuron.sc);

//		if (lglobals.engine.running && lglobals.syncWithScreen) {
//			lglobals.engine.runQueueFillerThread.step();
//		}

        if (lglobals.engine.controlledCrawler != null && lglobals.engine.controlledCrawler.isDead()) {
            lglobals.engine.controlledCrawler = null;
            lglobals.engine.watchedCrawler    = null;
        }

        if (lglobals.engine.controlledCrawler != null) {
            if (lglobals.controlledLeft) lglobals.engine.controlledCrawler.adjustAngle(2);
            else
                if (lglobals.controlledRight) lglobals.engine.controlledCrawler.adjustAngle(-2);

            if (lglobals.controlledUp) {
                lglobals.engine.controlledCrawler.step();
                lglobals.engine.controlledCrawler.step();
            }
            else
                if (lglobals.controlledDown) {
                    lglobals.engine.controlledCrawler.adjustAngle(-180);
                    lglobals.engine.controlledCrawler.step();
                    lglobals.engine.controlledCrawler.step();
                    lglobals.engine.controlledCrawler.adjustAngle(180);
                }
            if (lglobals.controlledLeft || lglobals.controlledRight || lglobals.controlledUp || lglobals.controlledDown) lglobals.forceRefresh = true;
        }

        long actStep = lglobals.engine.steps.getCounter();
        if (lastStep != actStep || lglobals.forceRefresh) {
            lastStep = actStep;
            lglobals.forceRefresh = false;

            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    if (!lglobals.disableDraw) {
                        lglobals.ip.refresh();
                    }
                    //System.err.println("actStep = "+actStep);
                    lglobals.statusPanel.updateLabels();
                }
            });
        }


    }

}
