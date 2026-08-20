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

import java.util.LinkedList;
import java.util.List;

import tw.master.math.BestDoubleList;



public class CrawlerList {

    private LinkedList<CrawlerListUpdateInterface> listeners = new LinkedList<CrawlerListUpdateInterface>();

    private	BestDoubleList					crawlers;

    // ---------------------------------------------------------------------------------------------

    public CrawlerList(int size) {
        crawlers = new BestDoubleList(size);
    }

    public boolean add(Crawler c) {

        boolean needUpdate;
        int		oldSize,newSize;

        synchronized (crawlers) {
            oldSize = crawlers.size();

            double fitness = c.getFitness();

            needUpdate =  crawlers.add(fitness, c);

            newSize = crawlers.size();

        }
//		System.err.println("needUpdate = "+needUpdate);
//		System.err.println("oldSize    = "+oldSize);
//		System.err.println("newSize    = "+newSize);

        if (needUpdate) {
            updateCounter++;

            if (oldSize != newSize) {
                callSizeChanged();
            }
            else {
                callUpdateData();
            }

        }

        return needUpdate;
    }

    private void callUpdateData() {
        for (CrawlerListUpdateInterface cli : listeners) cli.updateData();
    }

    private void callSizeChanged() {
        for (CrawlerListUpdateInterface cli : listeners) cli.sizeChanged();
    }

    public void registerCrawlerListListener(CrawlerListUpdateInterface cli) {
        if (!listeners.contains(cli)) listeners.add(cli);
    }

    public void deregisterCrawlerListListener(CrawlerListUpdateInterface cli) {
        listeners.remove(cli);
    }

    public boolean removeAll(List<Crawler> cl) {
        boolean wasRemoved = false;

        synchronized (crawlers) {

            for (Crawler c : cl) {
                if (crawlers.remove(c)) {
                    wasRemoved = true;
                }
            }
        }

        if (wasRemoved) {
            updateCounter++;
            callSizeChanged();
        }

        return wasRemoved;
    }

    public boolean remove(Crawler c) {
        boolean wasRemoved = false;

        synchronized (crawlers) {
            wasRemoved = crawlers.remove(c);

            if (wasRemoved) {
                updateCounter++;

                callSizeChanged();
//				SwingUtilities.invokeLater(new Runnable() {
//					@Override
//					public void run() {
//						scrollPane.revalidate();
//					}
//				});

            }
        }

        return wasRemoved;
    }

    public volatile int	updateCounter;

    public BestDoubleList getBestList() {
        return crawlers;
    }

    public void setBestList(BestDoubleList newBestCrawlers) {
        crawlers = newBestCrawlers;
        updateCounter++;
        callSizeChanged();
    }

    public int size() {
        return crawlers.size();
    }

    public double getValue(int rowIndex) {
        return crawlers.getValue(rowIndex);
    }

    public Crawler getCrawler(int rowIndex) {
        synchronized (crawlers) {
            if (rowIndex >= crawlers.size()) return null;

            Crawler c = (Crawler) crawlers.getObject(rowIndex);
            if (c.rank != rowIndex) c.rank = rowIndex;
            return c;
        }
    }

    public void setSize(int v) {
        crawlers.setSize(v);
        updateCounter++;
        callSizeChanged();
    }

}
