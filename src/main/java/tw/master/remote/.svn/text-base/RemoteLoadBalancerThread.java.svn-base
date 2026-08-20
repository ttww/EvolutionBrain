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

package tw.master.remote;

import java.io.IOException;
import java.util.List;

import tw.master.crawler.Crawler;
import tw.master.engine.Engine;
import tw.master.utils.Rnd;
import tw.master.utils.Utils;


public class RemoteLoadBalancerThread extends Thread {

    private Engine engine;

    private boolean quit = false;

    public RemoteLoadBalancerThread(Engine engine) {
        this.engine = engine;
        setName("RemoteLoadBalancer");
        setDaemon(true);
    }

    public void terminate() {
        quit = true;
        interrupt();
    }

    @Override
    public void run() {
        while (engine.remoteEngines == null) Utils.sleep(100);	// wait for assignment

        while (!quit) {
            Utils.sleep(10000);
            if (quit) break;
            if (!engine.running) continue;

//			Utils.sleep(Rnd.rnd(0,150));

            if (false) {
                if (engine.bestCrawlers.size() > 0) {

                    Crawler c = engine.bestCrawlers.getCrawler(0);

                    System.err.println("getForTransfer best crawler = "+c);
                    c = engine.getForTransfer(c);

                    System.err.println("Got best crawler "+ c);

                    Utils.sleep(Rnd.rnd(0,50));
                    engine.addCrawlerToRunList(c);
                    c = null;
                }

            }

            if (true) {
                List<RemoteEngine> remoteEngines = engine.remoteEngines.getRemoteEngines();

                for (RemoteEngine remoteEngine : remoteEngines) {
                    if (quit) break;

                    Crawler c = null;
                    try {
                        Crawler localTop = null;
                        int		top = 5;
                        while (top >= 0 && localTop == null) localTop = engine.bestCrawlers.getCrawler(top--);
                        if (localTop != null) {
                            System.err.println("Top 5 = "+localTop);

                            c = remoteEngine.getBestRemoteCrawler(localTop.getFitness());

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (quit) break;

                    if (c != null) {
                        System.err.println("Got Best Crawler from "+remoteEngine.remoteName+": "+c);
                        engine.addCrawlerToRunList(c);
                        c = null;
                    }

//				Utils.sleep(1000);
                }
            }
        }	// while
    }	// run

}
