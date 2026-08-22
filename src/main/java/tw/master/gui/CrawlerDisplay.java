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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import tw.gui.GuiUtils;
import tw.gui.image.ArrayPanel;
import tw.gui.image.ImagePanel;
import tw.master.GlobalsClientGui;
import tw.master.brain.Neuron;
import tw.master.crawler.AbstractBrainCrawler;
import tw.master.crawler.Crawler;
import tw.master.crawler.MutationCrawlerInterface;
import tw.master.gui.MutationParameterPanel.MutationPatameterType;
import tw.master.gui.way.WayVectorPanel;
import tw.master.math.MathUtils;
import tw.master.utils.UpdateListenerInterface;
import tw.master.utils.Utils;
import tw.master.visionfield.VisionFieldParams;


public class CrawlerDisplay extends JPanel implements UpdateListenerInterface {

    private static final long serialVersionUID = 1L;

    private GlobalsClientGui  globals;

    private int               KERNEL_RADIUS    = 2;

    public int                KERNEL_SIZE      = 2 * KERNEL_RADIUS + 1;

    public float[][]          kernel           = MathUtils.genGaussKernel(KERNEL_RADIUS, 1f, 0.2f);


    private JLabel            generationLabel;

    private JLabel            runLabel;

    private JLabel            energyLabel;

    private JLabel            liveStateLabel;

    private ArrayPanel        visionField;

    private JLabel            visionFieldSizeLabel;

    private EEGDiagramPanel   lineDiagram;

    private FitnessMeterPanel fmp;

    private WayVectorPanel    wayVectorPanel;

    private ImagePanel        tracePanel;

    public CrawlerDisplay(GlobalsClientGui globals) {
        this.globals = globals;

        setLayout(new GridBagLayout());

        visionField = new ArrayPanel(kernel);

        int y = 0;

        generationLabel = addLabel("Generation:", y++);
        runLabel = addLabel("Run:", y++);
        liveStateLabel = addLabel("LiveState:", y++);
        energyLabel = addLabel("Energy:", y++);

        addWithLabel("VisionField:", y, visionField);
        visionFieldSizeLabel = new JLabel("-");
        add(2, y, visionFieldSizeLabel);
        y++;

        addSeparator(y++);

        fmp = new FitnessMeterPanel(AbstractBrainCrawler.getInitialFitnessValues());
        addWithLabel("Fitness", y++, 2, 1, 2, 5, fmp);

        lineDiagram = new EEGDiagramPanel(200);
        lineDiagram.setPreferredSize(new Dimension(300, 220));

        addWithLabel("EEG", y++, 2, 2, 2, 10, lineDiagram);

        y++;
        addSeparator(y++);

        wayVectorPanel = new WayVectorPanel();
        addWithLabel("WayVector", y++, 2, 2, 2, 10, wayVectorPanel);


        y++;
        addSeparator(y++);

        Dimension tps = new Dimension(
                (int) (globals.engine.limits.width  - globals.engine.limits.x + 0.5f) + 1,
                (int) (globals.engine.limits.height - globals.engine.limits.y + 0.5f) + 1);
//        System.err.println("tps = "+tps);
//        tps = new Dimension(20,20);

        tracePanel = new ImagePanel("TracePanel", tps);
        addWithLabel("WayTrace", y++, 2, 2, 2, 10, tracePanel);

        y++;
        addSeparator(y++);


        JButton show3dButton = new JButton("Show 3D");

        show3dButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (watchedBrainCrawler != null)
                    CrawlerDisplay.this.globals.b3d.setWatchedBrain(watchedBrainCrawler.getBrain());
            }
        });

        add(0, y++, 3, 1, show3dButton);

        this.revalidate();
    }

    private JLabel addLabel(String txt, int y) {
        JLabel ret = new JLabel(txt);
        addWithLabel(txt, y, ret);
        return ret;
    }

    private void addSeparator(int y) {
        add(0, y, 3, 1, new JSeparator(SwingConstants.HORIZONTAL));
    }


    private void addWithLabel(String txt, int y, Component c) {
        JLabel l = new JLabel(txt);
        add(0, y, 1, 1, 1, 0.1, l);
        add(1, y, 1, 1, 1, 0.1, c);
    }

    private void addWithLabel(String txt, int y, int w, int h, int ww, int wh, Component c) {
        JLabel l = new JLabel(txt);

        add(0, y, 1, 1, l);
        add(1, y, w, h, ww, wh, c);
    }

    private void add(int x, int y, Component c) {
        add(x, y, 1, 1, c);
    }

    private void add(int x, int y, int w, int h, Component c) {
        double ww, wh;

        if (x != 0) {
            ww = 2;
            wh = 2;
        } else {
            ww = 1;
            wh = 1;
        }

        add(x, y, w, h, ww, wh, c);
    }

    private void add(int x, int y, int w, int h, double ww, double wh, Component c) {
        GridBagConstraints gb = new GridBagConstraints();
        gb.gridx = x;
        gb.gridy = y;
        gb.gridwidth = w;
        gb.gridheight = h;
        gb.fill = GridBagConstraints.BOTH;
        gb.insets = new Insets(4, 4, 4, 4);
        gb.weightx = ww;
        gb.weighty = wh;

        add(c, gb);
    }

    // ---------------------------------------------------------------------------------------------

    private Crawler                watchedCrawler;

    private AbstractBrainCrawler   watchedBrainCrawler;

    private MutationParameterPanel mpp;

    public void setWatchedCrawler(Crawler crawler) {


        if (watchedCrawler != null) {
            watchedCrawler.removeUpdateListener(this);
        }
        watchedCrawler = crawler;

//		if (mpp != null) {
//			GuiUtils.closeTop(mpp);
//			mpp = null;
//		}

        if (crawler == null) return;

        crawler.addUpdateListener(this);


        if (crawler instanceof AbstractBrainCrawler) {
            watchedBrainCrawler = (AbstractBrainCrawler) crawler;

            lineDiagram.clearLines();
            if (lineDiagram.getNumberOfLines() == 0) {
                for (Neuron n : watchedBrainCrawler.getBrain().neurons) {
                    lineDiagram.addLine(n, n.name, n.getColor(), 0, 1);
                }
            }

            fmp.setFitnessValues(watchedBrainCrawler.getFitnessValues());
            wayVectorPanel.setWay(watchedBrainCrawler.way);
        } else {
            watchedBrainCrawler = null;
        }

        if (crawler instanceof MutationCrawlerInterface) {
            final MutationCrawlerInterface mci = (MutationCrawlerInterface) crawler;

            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    if (mpp == null) {
                        mpp = new MutationParameterPanel(mci.getMutationParameter(), MutationPatameterType.PhenoType);
                        mppFrame = Utils.showBean(mpp, "Phenotype mutation parameter", false);

                        GuiUtils.setDrawingStoppedTitle(mppFrame, globals.disableDraw);
                    }
                    else if (!globals.disableDraw) {
                        // In-place value refresh (see MutationParameterPanel.updatePanel()) - no
                        // revalidate()/repaint() needed here, each slider repaints itself.
                        mpp.updatePanel(mci.getMutationParameter());
                    }
                }
            });


        }
        generationLabel.setText(crawler.getName());

        doUpdate();
    }

    private JFrame mppFrame;

    public JFrame getMppFrame() {
        return mppFrame;
    }

    private void updateLabels() {
        if (watchedCrawler == null) return;

        runLabel.setText(Long.toString(watchedCrawler.getStepCount()));
        liveStateLabel.setText(watchedCrawler.liveState.toString());
        energyLabel.setText(Long.toString((long) watchedCrawler.getEnergy()));

        AbstractBrainCrawler c = watchedBrainCrawler;
        if (c != null) {
            VisionFieldParams lfp = c.vfp;
            if (lfp != null) visionFieldSizeLabel.setText(lfp.width + "," + lfp.height);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void doUpdate() {
        if (SwingUtilities.isEventDispatchThread()) {
            doUpdateOnEDT();
        }
        else {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    doUpdateOnEDT();
                }
            });
        }
    }

    private void doUpdateOnEDT() {

        if (watchedCrawler == null) return;

        if (!globals.disableDraw) updateLabels();

        if (watchedBrainCrawler != null) {

            if (watchedBrainCrawler.vfp != null && watchedBrainCrawler.vfp.sf != null) {

                if (!globals.disableDraw && lastVisionFieldUpdate != watchedBrainCrawler.vfp.changeCounter) {
                    lastVisionFieldUpdate = watchedBrainCrawler.vfp.changeCounter;
                    visionField.setData(watchedBrainCrawler.vfp.sf);
                }

                ArrayList<Neuron> na = watchedBrainCrawler.getBrain().neurons;
                //System.err.println("NA = "+na.size());
                for (Neuron n : na) {
                    lineDiagram.setLastValue(n, n.a);
                }

                lineDiagram.shift();

                if (!globals.disableDraw) {
                    lineDiagram.checkRepaint();
                    wayVectorPanel.repaint();
                    fmp.repaint();
                    if (watchedBrainCrawler.visitUpdate != lastTraceWayUpdate) {
                        lastTraceWayUpdate = watchedBrainCrawler.visitUpdate;

                        BufferedImage img = Utils.toImage2(watchedBrainCrawler.visit, watchedBrainCrawler.visitWidth,
                                watchedBrainCrawler.visitHeight);

                        tracePanel.setImage(img);
                        tracePanel.repaint();
                    }
                }
            }
        }

    }

    private long lastVisionFieldUpdate = -1;

    private long lastTraceWayUpdate    = -1;

    /**
     * Free all resources (close JFrames opened from this class).
     */
    public void disposeAll() {
        if (mppFrame != null) {
            mppFrame.setVisible(false);
            mppFrame.dispose();
        }
    }

}
