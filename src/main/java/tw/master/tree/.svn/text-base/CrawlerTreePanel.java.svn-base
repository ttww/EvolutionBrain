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

package tw.master.tree;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import tw.master.crawler.Crawler;
import tw.master.engine.Engine;
import tw.master.engine.EngineEventsInterface;


/**
 *
 *
 * @author Thomas Welsch
 *
 */
public class CrawlerTreePanel extends JPanel implements EngineEventsInterface {

    private static final long serialVersionUID = 1L;

    private Engine            engine;

    private TreeTable         data;

    private TreePanel         tp;

    /**
     * @param engine
     */
    public CrawlerTreePanel(Engine engine) {
        this.engine = engine;

        setLayout(new BorderLayout());
        engine.addChangeListener(this);

        data = new TreeTable();
        tp = new TreePanel(data);

        add(tp, BorderLayout.CENTER);
    }


    /* (non-Javadoc)
     * @see tw.master.engine.EngineEventsInterface#serverEvent(tw.master.engine.EngineEventsInterface.ServerEvent, tw.master.crawler.Crawler)
     */
    @Override
    public void serverEvent(ServerEvent se, Crawler c) {
        // System.err.println(se+" "+c.getName());

        switch (se) {
            case CrawlerBorn:
                data.addData(c);
                tp.repaint();
                break;
            case CrawlerDied:
                data.delData(c);
                tp.repaint();
                break;
            case CrawlerLoaded:
                data.clear();
                for (Crawler ec : engine.allCrawlers)
                    data.addData(ec);
                tp.repaint();
                break;
            case WatchedChanged:
                tp.repaint();
                break;

            default:
                System.err.println("CrawlerTreePanel: Event " + se + " " + (c != null ? c.getName() : ""));

        }
//      for (Crawler c : engine.allCrawlers) {
//      data.addData(c.getName());
//  }

    }


}
