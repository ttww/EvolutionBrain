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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.CRC32;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import tw.gui.GuiUtils;
import tw.gui.image.ImagePanel;
import tw.master.brain.Neuron;
import tw.master.crawler.Crawler;
import tw.master.crawler.CrawlerDrawer;
import tw.master.engine.Engine;
import tw.master.engine.EngineEventsInterface;
import tw.master.gui.StatusPanel;
import tw.master.remote.RemoteEngineClient;
import tw.master.utils.JmDnsHelper;
import tw.master.utils.MessageInterface;
import tw.master.utils.PreferencesUtils;
import tw.master.utils.Utils;


public class GlobalsServerGui implements EngineEventsInterface, MessageInterface {

    final int         PORT     = 18231;

    public Engine     engine;

    public ImagePanel ip;

    private JCheckBox runningCB;

    StatusPanel       statusPanel;

    ServerSocket      server;

    JmDnsHelper       api;

    /**
     * Flag: All initialization is done.
     */
    public boolean    initDone = false;

    private JLabel    msgLabel;


    @Override
    public void msg(String txt) {
        msgLabel.setText(txt);
        msgLabel.repaint();
    }

    public GlobalsServerGui(final Engine engine) {

        try {
            PreferencesUtils.setPreferencesRoot("EvolutionBrainServer");
        } catch (IllegalStateException e) { } // OK for testsing

        this.engine = engine;

        engine.addChangeListener(this);

        final int cpus = Runtime.getRuntime().availableProcessors();

        JPanel guiPanel = new JPanel(new GridBagLayout());


        // -----------------------------------------------------------------------------------------
        statusPanel = new StatusPanel(engine);
        GuiUtils.addGrid(guiPanel, 0, 0, statusPanel);
        //GuiUtils.showBean(statusPanel,"Status");

        this.ip = new ImagePanel("ServerImage", engine.img, engine.imgOverlay);

        ip.setBorder(new TitledBorder("Image"));
        ip.addImagerPanelDrawer(new CrawlerDrawer(engine));
        GuiUtils.addGrid(guiPanel, 0, 1, 1, 1, ip, GridBagConstraints.BOTH);
//		GuiUtils.showBean(ip,"Image");

        // Buttons ---------------------------------------------------------------------------------
        JPanel ctrlPanel = new JPanel(new GridBagLayout());
        ctrlPanel.setBorder(new TitledBorder("Control"));
        ctrlPanel.setPreferredSize(new Dimension(200, 200));
//		middlePanel.add(ctrlPanel,BorderLayout.CENTER);

        GuiUtils.addGrid(guiPanel, 0, 2, ctrlPanel);

//		JFrame cpf = GuiUtils.showBean(ctrlPanel, "Control");
//		GuiUtils.addGrid(middlePanel,0,2,ctrlPanel);


        JPanel msgPanel = new JPanel(new BorderLayout());
        msgPanel.setBorder(new TitledBorder("Messages"));
        msgLabel = new JLabel("...");
        msgLabel.setOpaque(true);
        msgLabel.setBackground(Color.ORANGE);
        msgPanel.add(msgLabel, BorderLayout.CENTER);
        GuiUtils.addGrid(guiPanel, 0, 3, msgPanel);

        //msgPanel.setPreferredSize(new Dimension(200,300));

        // Screen update thread --------------------------------------------------------------------


        int y = 0;
        runningCB = GuiUtils.addCheckBox(ctrlPanel, y++, "Running");
        runningCB.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setRunState(runningCB.isSelected());
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



        final JButton quitButton = new JButton("Quit");
        GuiUtils.addGrid(ctrlPanel, 0, y++, quitButton);

        quitButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                quitButton.setText("Quitting...");
                quitButton.setEnabled(false);

                msg("Quitting Server");

                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        if (engine.running) {
                            setRunState(false);
                        }
                        if (api != null) {
                            api.close();
                            api = null;
                        }
                        System.exit(0);
                    }
                });

            }
        });


        final Font sf = new Font("Courier", 0, 9);

        GuiUtils.addLabels(ctrlPanel, y++, "Calc. threads:");
        final JSlider cpuSlider = GuiUtils.addSlider(ctrlPanel, y++);
        cpuSlider.setMinimum(1);
        cpuSlider.setMaximum(cpus * 2);
        cpuSlider.setMajorTickSpacing(2);
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


        // -------------------------------------
        JFrame cpf = GuiUtils.showBean(guiPanel, "Evolution Server Engine");
        cpf.pack();


        initDone = true;

        serverEvent(ServerEvent.CrawlerLoaded, null);
        serverEvent(ServerEvent.WatchedChanged, null);

        new UpdateThread().start();

        if (startServer()) startBonjour();
    }

    public void refresh() {
        forceRefresh = true;
    }


    private JCheckBox disableDrawCB;

    private boolean   disableDraw  = false;

    private boolean   forceRefresh = false;

    private boolean   switchedOff  = false;

    private long      lastStep     = 0;

    // ---------------------------------------------------------------------------------------------

    class UpdateThread extends Thread {

        UpdateThread() {
            setName("ServerUpdateThread");
            setDaemon(true);
        }

        private boolean wasFrontmost = false;

        @Override
        public void run() {
            while (!quit) {

                if (engine.running != runningCB.isSelected()) runningCB.setSelected(engine.running);

                boolean isFrontmost = Utils.isFrontmostApplication();
                if (wasFrontmost) {
                    if (!disableDraw && !isFrontmost) {
                        disableDrawCB.setSelected(true);
                        disableDraw = true;
                        switchedOff = true;
                    }
                    if (disableDraw && isFrontmost && switchedOff) {
                        disableDrawCB.setSelected(false);
                        disableDraw = false;
                        switchedOff = false;
                    }

                } else {
                    if (isFrontmost) wasFrontmost = true;
                }

                engine.fps.add();

                engine.neuronsLoadCount.add(Neuron.nc);
                engine.synapsesLoadCount.add(Neuron.sc);

//			if (engine.running && lglobals.syncWithScreen) {
//				lglobals.engine.runQueueFillerThread.step();
//			}

                long actStep = engine.steps.getCounter();
                if (lastStep != actStep || forceRefresh) {
                    lastStep = actStep;
                    if (!disableDraw) {
                        ip.refresh();
                    }
                    //System.err.println("actStep = "+actStep);
                    statusPanel.updateLabels();
                    forceRefresh = false;
                }



//				ip.refresh();
//				ip.repaint();
                if (isFrontmost) Utils.sleep(100);
                else
                    Utils.sleep(1000);
            } // while
        }
    }

    // ---------------------------------------------------------------------------------------------

    private void setRunState(boolean running) {
        engine.setRunning(running);
        runningCB.setSelected(running);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void serverEvent(ServerEvent se, Crawler c) {
        switch (se) {
            case WatchedChanged:
                break;

            case CrawlerLoaded:
                statusPanel.updateLabels();
                break;
            default:
                break;
        } // switch

    }


//	private void handleConnection( Socket client ) throws IOException {
//	    InputStream  in  = client.getInputStream();
//	    OutputStream out = client.getOutputStream();
//
//	    byte[] b = "Byebye !\n".getBytes();
//	    out.write(b,0, b.length);
//	    out.flush();
//	    Utils.sleep(20);
//	    client.close();
//	}

    // Initialize Bonjour in background
    class BonjourUpdateThread extends Thread {

        public BonjourUpdateThread() {
            setName("Bonjour init thread");
            setDaemon(true);
        }

        private void updateCrc(CRC32 crc, String s) {
            byte[] ba = s.getBytes();
            crc.update(ba);
        }

        @Override
        public void run() {

            CRC32 crc = new CRC32();
            long lastNetworkHash = 0;

            while (true) {

                try {

                    crc.reset();

                    Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
                    for (NetworkInterface netint : Collections.list(nets)) {
                        updateCrc(crc, netint.getDisplayName());
                        updateCrc(crc, netint.getName());
                        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                            updateCrc(crc, inetAddress.toString());
                        }
                    }

                    if (crc.getValue() != lastNetworkHash) {
                        msg("Network changed, restarting Bonjour....");

                        if (api != null) {
                            api.close();
                            api = null;
                        }
                        lastNetworkHash = crc.getValue();
                        Utils.sleep(3000);
                    }
//					else msg("Same net...");


                    if (api == null) {
                        msg("Starting Bonjour....");
                        api = new JmDnsHelper("_javaobj._tcp.local.");
                        ConcurrentHashMap<String, String> settings = new ConcurrentHashMap<String, String>();
                        //					settings.put("param1","value11");
                        //					settings.put("param2","value22");
                        api.registerService("_javaobj._tcp.local.", "MutationBrainServer", PORT, settings);
                        msg("Bonjour is started, Server on port " + PORT + "....");
                    }

                } catch (SocketException e) {
                    msg("Wait for network interfaces...");
                }

                Utils.sleep(2000);
            } // while
//			api.startRefreshingServices();
        }


    }


    private void startBonjour() {

//		try {
//			System.err.println("Start bonjour, check firewall");
//			Socket checkFirewall = new Socket("10.0.1.9",PORT);
//
//			System.err.println("Start bonjour, got connection");
//
//			checkFirewall.close();
//
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

        System.err.println("Start bonjour");
        new BonjourUpdateThread().start();
//		new Thread() {
//
//			@Override
//			public void run() {
//				msg("Starting Bonjour....");
//				api = new JmDnsHelper("_javaobj._tcp.local.");
//				ConcurrentHashMap<String,String> settings = new ConcurrentHashMap<String,String>();
////				settings.put("param1","value11");
////				settings.put("param2","value22");
//				api.registerService("_javaobj._tcp.local.","MutationBrainServer",PORT,settings);
//				msg("Bonjour is started.");
//				msg("Bonjour is started, Server on port "+PORT+"....");
//			}
//		}.start();
    }

    private boolean quit = false;

    private boolean startServer() {

        msg("Start Server on port " + PORT + "....");
        try {
            System.err.println("Start server socket");
            server = new ServerSocket(PORT);
            System.err.println("Got   server socket");
        } catch (IOException e) {
            e.printStackTrace();
            msg("Server NOT started !");
            return false;
        }

        Thread t = new Thread() {

            @Override
            public void run() {

                msg("Server started, accepting connections....");
                while (!quit) {

                    try {
                        System.err.println("Start Accept");
                        Socket client = server.accept();

                        msg("Connection from " + client.getInetAddress());

                        RemoteEngineClient rec = new RemoteEngineClient(GlobalsServerGui.this.engine,
                                GlobalsServerGui.this);
                        rec.handleClientConnection(client);

                        msg("Connection close");

                        client.close();
                        client = null;
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }

                    Utils.sleep(100);
                }

            }
        };
        t.setDaemon(true);
        t.setName("ServerSockedThread");
        t.start();

        return true;
    }


}
