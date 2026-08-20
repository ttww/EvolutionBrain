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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public class JmDnsHelper extends Thread {

    private JmDNS                        jmdns;

    private String                       mdns_type;

    private List<ServiceInfo>            servers;

    private boolean                      running;

    private long                         refreshInterval;

    private LinkedList<JmDnsListener>    listener      = new LinkedList<JmDnsListener>();

    private HashMap<String, ServiceInfo> knownServices = new HashMap<String, ServiceInfo>();

    public void registerServiceListener(JmDnsListener serviceListener) {
        if (listener.contains(serviceListener)) return;
        listener.add(serviceListener);
    }

    public void deregisterServiceListener(JmDnsListener serviceListener) {
        listener.remove(serviceListener);
    }


    @Override
    public void run() {
        while (running) {

            ServiceInfo[] infos = jmdns.list(mdns_type);

            boolean changed = false;

            Set<String> oldKnown = new HashSet<String>(knownServices.keySet());

            if (infos != null && infos.length > 0) {


                for (int i = 0; i < infos.length; i++) {
                    String name = infos[i].getQualifiedName() + "." + infos[i].getServer() + "." + infos[i].getURL();

                    if (knownServices.containsKey(name)) {
//						System.err.println(i+" FOUND KNOWN "+name);
                        // Already known
                        oldKnown.remove(name);
                    } else {
//						System.err.println(i+" FOUND NEW   "+name);
                        knownServices.put(name, infos[i]);
                        for (JmDnsListener serviceListener : listener)
                            serviceListener.serviceAppears(infos[i]);
                        changed = true;
                    }

                }


            }

            for (String disappeared : oldKnown) {
//				System.err.println("REMOVE OLD "+disappeared);
                ServiceInfo oldService = knownServices.remove(disappeared);
                for (JmDnsListener serviceListener : listener)
                    serviceListener.serviceDisappears(oldService);
                changed = true;
            }

            if (changed) {
                Collection<ServiceInfo> known = knownServices.values();
                ArrayList<ServiceInfo> temp = new ArrayList<ServiceInfo>(known.size());

                for (ServiceInfo si : known)
                    temp.add(si);
                servers = temp;

                for (JmDnsListener serviceListener : listener)
                    serviceListener.serviceListComplete(servers);

            }

            try {
                Thread.sleep(refreshInterval);
            } catch (InterruptedException e) {
                break;
            }

//			ArrayList<ServiceInfo> temp = new ArrayList<ServiceInfo>();
//			if (infos != null && infos.length > 0) {
//				for (int i = 0; i < infos.length; i++) {
//					temp.add(infos[i]);
//				}
//			}
//			servers = temp;
//
//			try {
//				Thread.sleep(refreshInterval);
//			} catch (InterruptedException e) {
//				break;
//			}

        }
    }

    public void startRefreshingServices() {
        if (!running) {
            running = true;
            this.start();
        }
    }

    public void stopRefreshingServices() {
        running = false;
    }

    public List<ServiceInfo> getServices() {
        return servers;
    }

    public JmDnsHelper(String mdns_type) {
        setName("JmDns Service Collector");
        setDaemon(true);

        refreshInterval = 1000;

//		long start = System.currentTimeMillis();
        try {
            jmdns = JmDNS.create();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.mdns_type = mdns_type;
        servers = new ArrayList<ServiceInfo>();
    }

    public void waitForReady() {
        boolean notReady = true;
        while (notReady) {
            if (servers.size() > 0) {
                notReady = false;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void close() {
        stopRefreshingServices();
        if (jmdns != null) {
            jmdns.unregisterAllServices();
            try {
                jmdns.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            jmdns = null;
        }
    }

    public void registerService(String type, String name, int port, Map<String, String> settings) {
        try {
            ServiceInfo info = ServiceInfo.create(type, name, port, 1, 1, settings);
            jmdns.registerService(info);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                System.err.println("Exit service...");
                close();
            }
        });
    }
}
