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
 *
 */

package tw.master;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ConcurrentModificationException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import tw.gui.GuiUtils;
import tw.gui.annotiations.AnnotationGuiGenerator;
import tw.gui.image.EventInterface;
import tw.gui.image.ImagePanel;
import tw.master.GlobalsClientGui.RunCountButton;
import tw.master.brain.Brain;
import tw.master.crawler.AbstractBrainCrawler;
import tw.master.crawler.Crawler;
import tw.master.crawler.CrawlerDrawer;
import tw.master.engine.Engine;
import tw.master.engine.EngineEventsInterface;
import tw.master.engine.EngineEventsInterface.ServerEvent;
import tw.master.gui.BestCrawlerPanel;
import tw.master.gui.Brain3dDisplay;
import tw.master.gui.CrawlerDisplay;
import tw.master.gui.StatusPanel;
import tw.master.remote.AvailableServersPanel;
import tw.master.tree.CrawlerTreePanel;
import tw.master.tree.TreePanel;
import tw.master.tree.TreeTable;
import tw.master.utils.GuiStartStopStepThread;
import tw.master.utils.PreferencesUtils;
import tw.master.utils.Utils;


/**
 * This class holds all global states and panels for the EvolutionBrain client GUI.
 *
 * @author Thomas Welsch
 */
public class GlobalsClientGui implements EventInterface, EngineEventsInterface {

    public Engine            engine;


    public boolean           syncWithScreen;

    public boolean           disableDraw;

    public boolean           forceRefresh;

    public ImagePanel        ip;

    public boolean           controlledLeft;
    public boolean           controlledRight;
    public boolean           controlledUp;
    public boolean           controlledDown;

    public final StatusPanel       statusPanel;


    private final BestCrawlerPanel  bestCrawlerPanel;

    private final CrawlerDisplay    crawlerDisplay;

    private Thread                  stepCountThread;

    private Thread                  saveThread;

    private Thread                  loadThread;

    private final JCheckBox         runningCB;

    public JCheckBox                disableDrawCB;

    private final JSlider           crawlerSlider;


    /**
     * Flag: All initialization is done.
     */
    public boolean           initDone = false;

    // --------------------------------------------------------------------------------------------

    /**
     * Create the EvolutionBrain client GUI.
     *
     * @param engine        Engine to be used.
     *
     * @throws Exception    ...
     */
    public GlobalsClientGui(final Engine engine) throws Exception {

        try {
            PreferencesUtils.setPreferencesRoot("EvolutionBrain");
        } catch (IllegalStateException e) { } // OK for testing


        this.engine = engine;

        engine.addChangeListener(this);

        final int cpus = Runtime.getRuntime().availableProcessors();

        this.b3d = new Brain3dDisplay(this);
        addToOpenFrames(GuiUtils.showBean(this.b3d, "Brain"));


        // -----------------------------------------------------------------------------------------
        statusPanel = new StatusPanel(engine);
        addToOpenFrames(GuiUtils.showBean(statusPanel, "Status"));

        JPanel imagePanelAndTools = new JPanel(new BorderLayout());

        this.ip = new ImagePanel("ClientImage", engine.img, engine.imgOverlay);
        this.ip.writeImage = true;

        ip.setBorder(new TitledBorder("Image"));
        ip.addEventReceiver(this);
        //GuiUtils.showBean(ip,"Image");
        imagePanelAndTools.add(ip, BorderLayout.CENTER);
        //imagePanelAndTools.add(new JButton("HI!"), BorderLayout.SOUTH);
        addToOpenFrames(GuiUtils.showBean(imagePanelAndTools, "Image"));


        bestCrawlerPanel = new BestCrawlerPanel(engine.bestCrawlers, "BestCrawlerPanel", this);
        addToOpenFrames(GuiUtils.showBean(bestCrawlerPanel, "BestCrawler"));


        // Buttons ---------------------------------------------------------------------------------
        JPanel ctrlPanel = new JPanel(new GridBagLayout());
        ctrlPanel.setBorder(new TitledBorder("Control"));
        ctrlPanel.setPreferredSize(new Dimension(200, 200));
//		middlePanel.add(ctrlPanel,BorderLayout.CENTER);
        JFrame cpf = GuiUtils.showBean(ctrlPanel, "Control");
        addToOpenFrames(cpf);
//		GuiUtils.addGrid(middlePanel,0,2,ctrlPanel);


        // Screen update thread --------------------------------------------------------------------
        GuiStartStopStepThread sst = new UpdateThread(this);
        sst.setSleepTime(150);
        sst.setAutostep(true);
        sst.start();


        ip.addImagerPanelDrawer(new CrawlerDrawer(engine));

        int y = 0;
        JButton stepButton = new JButton("Step");
        GuiUtils.addGrid(ctrlPanel, 0, y++, stepButton);

        final JButton run2MioButton = new RunCountButton("Run 2 Mio steps", 2000000);
        GuiUtils.addGrid(ctrlPanel, 0, y++, run2MioButton);

        final JButton run1MioButton = new RunCountButton("Run 1 Mio steps", 1000000);
        GuiUtils.addGrid(ctrlPanel, 0, y++, run1MioButton);

        final JButton run100kButton = new RunCountButton("Run 100000 steps", 100000);
        GuiUtils.addGrid(ctrlPanel, 0, y++, run100kButton);

        final JButton run10kButton = new RunCountButton("Run 10000 steps", 10000);
        GuiUtils.addGrid(ctrlPanel, 0, y++, run10kButton);

        final JButton run2kButton = new RunCountButton("Run 2000 steps", 2000);
        GuiUtils.addGrid(ctrlPanel, 0, y++, run2kButton);

        runningCB = GuiUtils.addCheckBox(ctrlPanel, y++, "Running");
        runningCB.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setRunState(runningCB.isSelected());
            }
        });

        stepButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (engine.running) {
                    setRunState(false);
                }
                engine.runQueueFillerThread.step();
                GlobalsClientGui.this.ip.refresh();
            }
        });


        final JCheckBox syncCB = GuiUtils.addCheckBox(ctrlPanel, y++, "Sync with screen");
        syncCB.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                syncWithScreen = syncCB.isSelected();
                setRunState(engine.running);
            }
        });

        final JCheckBox fastDrawCB = GuiUtils.addCheckBox(ctrlPanel, y++, "Fast drawing");
        fastDrawCB.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                engine.fastDraw = fastDrawCB.isSelected();
                refresh();
            }
        });

        disableDrawCB = GuiUtils.addCheckBox(ctrlPanel, y++, "Disable drawing");
        disableDrawCB.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                disableDraw = disableDrawCB.isSelected();
                refresh();
            }
        });

        final JCheckBox showSynabsesCB = GuiUtils.addCheckBox(ctrlPanel, y++, "Show synapses");
        showSynabsesCB.setSelected(Brain.drawSynapses);
        showSynabsesCB.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Brain.drawSynapses = showSynabsesCB.isSelected();
//				refresh();
            }
        });

        final JCheckBox showSynabsesColoredCB = GuiUtils.addCheckBox(ctrlPanel, y++, "Synapses Colors");
        showSynabsesColoredCB.setSelected(Brain.drawColoredSynpses);
        showSynabsesColoredCB.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Brain.drawColoredSynpses = showSynabsesColoredCB.isSelected();
//				refresh();
            }
        });

        final JCheckBox showSignalsCB = GuiUtils.addCheckBox(ctrlPanel, y++, "Show signals");
        showSignalsCB.setSelected(Brain.drawSignals);
        showSignalsCB.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Brain.drawSignals = showSignalsCB.isSelected();
            }
        });

        final JCheckBox showClusterCB = GuiUtils.addCheckBox(ctrlPanel, y++, "Show cluster");
        showClusterCB.setSelected(Brain.drawCluster);
        showClusterCB.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Brain.drawCluster = showClusterCB.isSelected();
            }
        });

        final Font sf = new Font("Courier", 0, 9);

        GuiUtils.addLabels(ctrlPanel, y++, "Crawler:");
        crawlerSlider = GuiUtils.addSlider(ctrlPanel, y++);
        crawlerSlider.setMinimum(1);
        crawlerSlider.setMaximum(1001);
        crawlerSlider.setMajorTickSpacing(100);
        crawlerSlider.setMinorTickSpacing(50);
        crawlerSlider.setPaintTicks(true);
        crawlerSlider.setPaintLabels(true);
        crawlerSlider.setValue(engine.numberOfCrawlers);

        // Create the labels
        final Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put(Integer.valueOf(1), new JLabel("1"));
        for (int i = 100; i <= 1000; i += 100) {
            labelTable.put(new Integer(i), new JLabel(Integer.toString(i)));
        }

        crawlerSlider.setFont(sf);
        crawlerSlider.setLabelTable(labelTable);
        // Setup fonts for slider. If set to early, the LaF UI is
        // reseted...:-(((
        // See http://nadeausoftware.com/node/93....
        // Simple way on mac:
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                for (JLabel l : labelTable.values()) {
                    l.setFont(sf);
                    // l.setForeground(Color.RED);
                    l.setSize(l.getPreferredSize());
                }
            }
        });
        crawlerSlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                int v = crawlerSlider.getValue();
                if (v == crawlerSlider.getMaximum()) v = crawlerSlider.getMaximum() - 1;
                engine.setNumberOfCrawlers(v);
                setTopTitle(bestCrawlerPanel, "Best " + v + " crawlers");
            }

        });

        GuiUtils.addLabels(ctrlPanel, y++, "Calc. threads:");
        final JSlider cpuSlider = GuiUtils.addSlider(ctrlPanel, y++);
        cpuSlider.setMinimum(1);
        cpuSlider.setMaximum(cpus * 2);
        cpuSlider.setMajorTickSpacing(1);
        cpuSlider.setMinorTickSpacing(1);
        cpuSlider.setPaintTicks(true);
        cpuSlider.setPaintLabels(true);
        cpuSlider.setFont(sf);

        cpuSlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                engine.wantWorker = cpuSlider.getValue();
                statusPanel.updateCpuLabel();
            }
        });
        cpuSlider.setValue(engine.wantWorker);



        final JButton saveButton = new JButton("Save");
        GuiUtils.addGrid(ctrlPanel, 0, y++, saveButton);
        saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (saveThread == null) {

                    saveThread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            saveButton.setEnabled(false);
                            saveButton.repaint();
                            String txt = saveButton.getText();
                            boolean oldRunning = engine.running;

                            saveButton.setText("Wait for finsh run...");
                            setRunState(false);

                            while (engine.runQueueFillerThread.isInsideRun()) {
                                //System.err.println("Inside run...");
                                Utils.sleep(1);
                            }

                            saveButton.setText("Saving...");

                            engine.saveState();

                            saveButton.setText("Done !");

                            Utils.sleep(1000);

                            saveButton.setText(txt);

                            setRunState(oldRunning);

                            saveButton.setEnabled(true);
                            saveButton.repaint();

                            saveThread = null;
                        }

                    }, "saveThread");
                    saveThread.start();
                }

            }
        });

        // ---------------------------------

        final JButton loadButton = new JButton("Load");
        GuiUtils.addGrid(ctrlPanel, 0, y++, loadButton);
        loadButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (loadThread == null) {

                    loadThread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            loadButton.setEnabled(false);
                            loadButton.repaint();
                            String txt = loadButton.getText();
                            boolean oldRunning = engine.running;

                            loadButton.setText("Wait for finish run...");

                            setRunState(false);

                            while (engine.runQueueFillerThread.isInsideRun()) {
                                Utils.sleep(1);
                            }

                            loadButton.setText("Loading...");

                            engine.loadState();

                            loadButton.setText("Done !");

                            Utils.sleep(1000);

                            loadButton.setText(txt);

                            setRunState(oldRunning);

                            loadButton.setEnabled(true);
                            loadButton.repaint();

                            loadThread = null;
                        }

                    }, "loadThread");
                    loadThread.start();
                }

            }
        });

        final JButton mutationButton = new JButton("Show mutation tree");
        GuiUtils.addGrid(ctrlPanel, 0, y++, mutationButton);
        mutationButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                mutationButton.setEnabled(false);
                mutationButton.repaint();
                String txt = mutationButton.getText();
                boolean oldRunning = engine.running;

                mutationButton.setText("Wait for finish run...");

                setRunState(false);

                while (engine.runQueueFillerThread.isInsideRun()) {
                    Utils.sleep(1);
                }

                TreeTable data = new TreeTable();
                for (Crawler c : engine.allCrawlers) {
                    data.addData(c);
                    //System.err.println("    \""+c.getName()+"\",");
                }

                TreePanel tp = new TreePanel(data);

                Utils.showBean(tp, "Mutation tree");


                mutationButton.setText(txt);

                setRunState(oldRunning);

                mutationButton.setEnabled(true);
                mutationButton.repaint();

            }
        });

        // -------------------------------------
        cpf.pack();


        this.crawlerDisplay = new CrawlerDisplay(this);
        addToOpenFrames(GuiUtils.showBean(crawlerDisplay, "SelectedCrawler"));

        addToOpenFrames(GuiUtils.showBean(new CrawlerTreePanel(engine), "Mutation tree"));

        engine.enableRemoteEngines();
        AvailableServersPanel availableServersPanel = new AvailableServersPanel(engine);
        availableServersPanel.setBorder(new TitledBorder("Found servers"));
        addToOpenFrames(GuiUtils.showBean(availableServersPanel, "Available Servers"));

//		com.st.utils.EventDispatchThreadHangMonitor.initMonitoring();


        JPanel bp = AnnotationGuiGenerator.generateComponent(engine.crawlerClass);
        JScrollPane scrollPane = new JScrollPane(bp);

        addToOpenFrames(GuiUtils.showBean(scrollPane, AnnotationGuiGenerator.getClassTitle(engine.crawlerClass)));

        initDone = true;

        serverEvent(ServerEvent.CrawlerLoaded, null);
        serverEvent(ServerEvent.WatchedChanged, null);
    }

    /**
     * Helper class for count down buttons.
     *
     * @author Thomas Welsch
     */
    class RunCountButton extends JButton {

        /**
         * Create a new JButton with the given title and number of runs.
         *
         * @param title Text for button.
         * @param runs  Number of runs for this button.
         */
        public RunCountButton(String title, final long runs) {
            super(title);
            Dimension d = getSize();
            d = new Dimension(300, 20);

            setPreferredSize(d);
            setSize(d);

            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!engine.running) {
                        setRunState(true);
                    }
                    if (engine.runQueueFillerThread.getStopAfterCount() != -1) {
                        Toolkit.getDefaultToolkit().beep();
                        return;
                    }
                    engine.runQueueFillerThread.stopAfter(runs);

                    if (stepCountThread == null) {
                        stepCountThread = new Thread(new Runnable() {

                            @Override
                            public void run() {
                                RunCountButton.this.setEnabled(false);
                                RunCountButton.this.repaint();
                                String txt = RunCountButton.this.getText();
                                while (engine.running && engine.runQueueFillerThread.getStopAfterCount() != -1) {
                                    RunCountButton.this.setText(txt + " "
                                            + engine.runQueueFillerThread.getStopAfterCount() + " left");
                                    Utils.sleep(500);
                                }
                                engine.runQueueFillerThread.stopAfter(-1); // in case running was switched of...
                                RunCountButton.this.setText("Done !");
                                Utils.sleep(1000);
                                RunCountButton.this.setText(txt);
                                stepCountThread = null;
                                setRunState(false);

                                RunCountButton.this.setEnabled(true);
                                RunCountButton.this.repaint();
                            }

                        }, "CountRunner");
                        stepCountThread.start();
                    }
                }
            });

        }

        private static final long serialVersionUID = 1L;


    }

    // ---------------------------------------------------------------------------------------------

    private void setTopTitle(Component cc, String txt) {
        Component c = cc;
        while (c != null) {
            if (c instanceof JFrame) {
                ((JFrame) c).setTitle(txt);
                return;
            }
            c = c.getParent();
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Doing one simulation step.
     */
    public void step() {
        engine.runQueueFillerThread.step();
    }

    // ---------------------------------------------------------------------------------------------

    private void setRunState(boolean running) {
        if (syncWithScreen) {
            // The step-Calls are done in the update loop:
            //engine.setRunning(false);
            engine.setMaxRunsPerSecond(30);
            engine.setRunning(running);
            runningCB.setSelected(running);
        } else {
            engine.setMaxRunsPerSecond(0);
            engine.setRunning(running);
            runningCB.setSelected(running);
        }
    }

    // ---------------------------------------------------------------------------------------------

//	private JLabel add3(JPanel p, int y, String txt, JLabel l1, String txt2) {
//		JLabel l0 = new JLabel(txt);
//		JLabel l2 = new JLabel(txt2);
//
//		l1.setHorizontalAlignment(SwingConstants.LEFT);
//		l2.setHorizontalAlignment(SwingConstants.LEFT);
//
//		p.add(l0);
//		p.add(l1);
//		p.add(l2);
//		return l1;
//	}

//	private String maxMem = Long.toString(Runtime.getRuntime().maxMemory() / 1024 / 1024);

//	public void updateLabels() {
//
//		globalsServer.fps.updateDisplay();
//		globalsServer.steps.updateDisplay();
//		globalsServer.bornCount.updateDisplay();
//		globalsServer.diedCount.updateDisplay();
//		globalsServer.neuronsLoadCount.updateDisplay();
//		globalsServer.synapsesLoadCount.updateDisplay();
//
//		crawlerLabel.setText(Integer.toString(globalsServer.allCrawlers.size()));
//
//		String memUsed = Long.toString(Runtime.getRuntime().freeMemory() / 1024 / 1024);
//		memoryLabel.setText(memUsed + " MB  /  "+maxMem + " MB");
//
//		neuronsLabel.setText(Integer.toString(globalsServer.lastHandeldNeurons));
//		synapsesLabel.setText(Long.toString(globalsServer.lastHandeldSynapses));
//	}

    public void refresh() {
        forceRefresh = true;
    }

    public Brain3dDisplay b3d;

    @Override
    public void mouseEvent(MouseEvent e) {
        switch (e.getID()) {
            case MouseEvent.MOUSE_CLICKED:
//				System.err.println(e);

                double nearestDistance = Double.MAX_VALUE;
                Crawler nearestCrawler = null;

                try {
                    for (Crawler cc : engine.allCrawlers) {
                        cc.getPos(mouseP);
                        double dist = mouseP.distance(e.getPoint());
                        if (dist < nearestDistance) {
                            nearestDistance = dist;
                            nearestCrawler = cc;
                        }
                    }
                } catch (ConcurrentModificationException ee) {
                    System.err.println("Ops..."); //-)
                }

                engine.setWatchedCrawler(nearestCrawler);

                break;
            default:
                break;
        } // switch
    }


//	public void setWatchedCrawler(Crawler crawler) {
//		.watchedCrawler = crawler;
//
//		if (crawler instanceof AbstractBrainCrawler) {
//			AbstractBrainCrawler c = (AbstractBrainCrawler) crawler;
//
//			b3d.setWatchedBrain(c.getBrain());
//
//		}
//
//		refresh();
//
//		crawlerDisplay.setWatchedCrawler(crawler);
//	}

    @Override
    public final void serverEvent(ServerEvent se, Crawler c) {
        switch (se) {
            case WatchedChanged:
                if (engine.watchedCrawler instanceof AbstractBrainCrawler) {
                    AbstractBrainCrawler ac = (AbstractBrainCrawler) engine.watchedCrawler;

                    b3d.setWatchedBrain(ac.getBrain());
                }
                crawlerDisplay.setWatchedCrawler(engine.watchedCrawler);

                break;

            case CrawlerLoaded:

                System.err.println("LOADED !");
                ip.setImage(engine.img);
//					ip.refresh();
                crawlerSlider.setValue(engine.numberOfCrawlers);

                statusPanel.updateLabels();

                break;
            default:
                break;


        } // switch

    }

    // ---------------------------------------------------------------------------------------------

    private final Point.Float mouseP = new Point.Float();

    @Override
    public void keyEvent(KeyEvent e) {
//		System.err.println("e = "+e.getKeyCode());
        switch (e.getID()) {
            case KeyEvent.KEY_PRESSED:
                switch (e.getKeyCode()) {
                    case 32:
                        if (engine.controlledCrawler != null) {
                            engine.controlledCrawler = null;
                        } else {
                            engine.controlledCrawler = engine.watchedCrawler;
                        }
                        e.consume();
                        refresh();
//						System.err.println("controledCrawler = "+controledCrawler);
                        break;
                    case KeyEvent.VK_LEFT:
                        if (engine.controlledCrawler != null) e.consume();
                        controlledLeft = true;
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (engine.controlledCrawler != null) e.consume();
                        controlledRight = true;
                        break;
                    case KeyEvent.VK_UP:
                        if (engine.controlledCrawler != null) e.consume();
                        controlledUp = true;
                        break;
                    case KeyEvent.VK_DOWN:
                        if (engine.controlledCrawler != null) e.consume();
                        controlledDown = true;
                        break;
                    default:
                        System.err.println("e = " + e.getKeyCode());
                }
                break;
            case KeyEvent.KEY_RELEASED:
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        if (engine.controlledCrawler != null) e.consume();
                        controlledLeft = false;
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (engine.controlledCrawler != null) e.consume();
                        controlledRight = false;
                        break;
                    case KeyEvent.VK_UP:
                        if (engine.controlledCrawler != null) e.consume();
                        controlledUp = false;
                        break;
                    case KeyEvent.VK_DOWN:
                        if (engine.controlledCrawler != null) e.consume();
                        controlledDown = false;
                        break;
                    case KeyEvent.VK_T:
                        showThreads();
                        break;
                    default:
                        break;

                }
                break;
            default:
                break;
        } // switch

//		System.err.println("fl = "+controlledLeft+" / "+controlledRight+" / "+controlledUp+" / "+controlledDown);

    }

    private void showThreads() {
        // Find the root thread group
        ThreadGroup root = Thread.currentThread().getThreadGroup().getParent();
        while (root.getParent() != null) {
            root = root.getParent();
        }
        // Visit each thread group
        visit(root, 0);
    }

    // This method recursively visits all thread groups under `group'.
    @SuppressWarnings("boxing")
    private static void visit(ThreadGroup group, int level) {
        // Get threads in `group'
        int numThreads = group.activeCount();
        Thread[] threads = new Thread[numThreads * 2];
        numThreads = group.enumerate(threads, false);

        // Enumerate each thread in `group'
        for (int i = 0; i < numThreads; i++) {
            // Get thread
            Thread thread = threads[i];

            System.err.println(String.format("TD: %-20s Pri %2d   : %s", thread.getName(), thread.getPriority(),
                    thread.getState()));
        }

        // Get thread subgroups of `group'
        int numGroups = group.activeGroupCount();
        ThreadGroup[] groups = new ThreadGroup[numGroups * 2];
        numGroups = group.enumerate(groups, false);

        // Recursively visit each subgroup
        for (int i = 0; i < numGroups; i++) {
            visit(groups[i], level + 1);
        }
    }

    private List<JFrame> openedFrames = new LinkedList<JFrame>();
    private void addToOpenFrames(JFrame f) {
        synchronized (openedFrames) {
            if (!openedFrames.contains(f)) openedFrames.add(f);
        }
    }

    public void openCrawlerWindow(final Crawler c) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                CrawlerDisplay cd = new CrawlerDisplay(GlobalsClientGui.this);
                addToOpenFrames(GuiUtils.showBean(cd, "SelectedCrawler " + c.getName()));
                cd.setWatchedCrawler(c);
            }
        });


    }


    /**
     * 
     */
    public void disposeAll() {

        crawlerDisplay.disposeAll();

        synchronized (openedFrames) {
            for (JFrame f : openedFrames) {
                f.setVisible(false);
                f.dispose();
            }
            openedFrames.clear();
        }
    }

}
