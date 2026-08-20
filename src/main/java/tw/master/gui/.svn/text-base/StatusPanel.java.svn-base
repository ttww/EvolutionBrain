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

package tw.master.gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import tw.gui.SpringUtilities;
import tw.master.engine.Engine;

public class StatusPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private JLabel            memoryLabel;

    private JLabel            crawlerLabel;

    private JLabel            neuronsLabel;

    private JLabel            synapsesLabel;

    private JLabel            cpuLabel;

    private Engine            engine;

    public StatusPanel(Engine engine) {
        super(new SpringLayout());

        this.engine = engine;

        setBorder(new TitledBorder("Status"));

        int y = 1;

        JLabel h1 = new JLabel(" What");
        JLabel h2 = new JLabel(" Count");
        JLabel h3 = new JLabel(" per Sec");
        h1.setOpaque(true);
        h2.setOpaque(true);
        h3.setOpaque(true);
        h1.setBackground(Color.LIGHT_GRAY);
        h2.setBackground(Color.LIGHT_GRAY);
        h3.setBackground(Color.LIGHT_GRAY);
        h1.setMinimumSize(new Dimension(50, 10));
        h2.setMinimumSize(new Dimension(150, 10));
        h3.setMinimumSize(new Dimension(150, 10));
        h1.setPreferredSize(new Dimension(50, 10));
        h2.setPreferredSize(new Dimension(150, 10));
        h3.setPreferredSize(new Dimension(150, 10));

        add(h1);
        add(h2);
        add(h3);

        memoryLabel = add3(this, y++, "Memory", new JLabel(), null);
        crawlerLabel = add3(this, y++, "Crawler", new JLabel(), null);
        neuronsLabel = add3(this, y++, "Neurons", new JLabel(), null);
        synapsesLabel = add3(this, y++, "Synapses", new JLabel(), null);
        cpuLabel = add3(this, y++, "Threads/CPU", new JLabel(), null);


        engine.steps.setupGui(this, "Steps", "%10d", "%6d perSec");
        y++;
        engine.fps.setupGui(this, "FPS", "%10d", "%6d perSec");
        y++;
        engine.bornCount.setupGui(this, "  Born", "%10d", "%6d perSec");
        y++;
        engine.diedCount.setupGui(this, "  Died", "%10d", "%6d perSec");
        y++;
        engine.neuronsLoadCount.setupGui(this, "Neuron load", "%10d Mio", "%6d M/perSec");
        y++;
        engine.synapsesLoadCount.setupGui(this, "Synapses load", "%10d Mio", "%6d M/perSec");
        y++;



        SpringUtilities.makeCompactGrid(this, y, 3, // rows, cols
                6, 3, // initX, initY
                4, 4); // xPad, yPad

        updateLabels();

    }

    private final String maxMem = Long.toString(Runtime.getRuntime().maxMemory() / 1024 / 1024);

    private final int    cpus   = Runtime.getRuntime().availableProcessors();

    public void updateLabels() {

        //System.err.println(System.currentTimeMillis());
        engine.fps.updateDisplay();
        engine.steps.updateDisplay();
        engine.bornCount.updateDisplay();
        engine.diedCount.updateDisplay();
        engine.neuronsLoadCount.updateDisplay();
        engine.synapsesLoadCount.updateDisplay();

        crawlerLabel.setText(Integer.toString(engine.allCrawlers.size()) + " ("
                + Integer.toString(engine.numberOfCrawlers) + ")");

        String memUsed = Long.toString(Runtime.getRuntime().freeMemory() / 1024 / 1024);
        memoryLabel.setText(memUsed + " MB  /  " + maxMem + " MB");

        neuronsLabel.setText(Integer.toString(engine.lastHandeldNeurons));
        synapsesLabel.setText(Long.toString(engine.lastHandeldSynapses));
    }

    public void updateCpuLabel() {
        cpuLabel.setText(engine.wantWorker + " thread" + (engine.wantWorker > 1 ? "s" : "") + " on " + cpus + " CPU"
                + (cpus > 1 ? "s" : ""));
    }

    private JLabel add3(JPanel p, int y, String txt, JLabel l1, String txt2) {
        JLabel l0 = new JLabel(txt);
        JLabel l2 = new JLabel(txt2);

        l1.setHorizontalAlignment(SwingConstants.LEFT);
        l2.setHorizontalAlignment(SwingConstants.LEFT);

        p.add(l0);
        p.add(l1);
        p.add(l2);
        return l1;
    }

}
