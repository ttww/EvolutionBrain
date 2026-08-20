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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.jmdns.ServiceInfo;

import tw.master.engine.Engine;
import tw.master.utils.JmDnsHelper;
import tw.master.utils.JmDnsListener;
import tw.master.utils.UpdateListenerInterface;

public class RemoteEnginesManager implements JmDnsListener, UpdateListenerInterface {

    private Engine                   engine;

    private JmDnsHelper              api;

    private LinkedList<RemoteEngine> knownRemoteEngines = new LinkedList<RemoteEngine>();

    private RemoteLoadBalancerThread rlbt;

    private boolean                  quit = false;

    public RemoteEnginesManager(Engine engine) {
        this.engine = engine;

        new BonjourInitThread().start();

        rlbt = new RemoteLoadBalancerThread(this.engine);
        rlbt.start();

    }

    // Initialize Bonjour in background
    class BonjourInitThread extends Thread {

        public BonjourInitThread() {
            setName("Bonjour init thread");
            setDaemon(true);
        }

        @Override
        public void run() {
            if (quit) return;

            if (api == null) {
                api = new JmDnsHelper("_javaobj._tcp.local.");
                api.registerServiceListener(RemoteEnginesManager.this);
                api.startRefreshingServices();
            }
        }
    }

    private LinkedList<UpdateListenerInterface> updateListener = new LinkedList<UpdateListenerInterface>();

    public void registerUpdateListener(UpdateListenerInterface uli) {
        if (updateListener.contains(uli)) return;
        updateListener.add(uli);
    }

    public void deregisterUpdateListener(UpdateListenerInterface uli) {
        updateListener.remove(uli);
    }

    /**
     * Return the actually known remote engines. The returned list is a copy of the actual list and
     * will not change !
     * 
     * @return
     */
    public List<RemoteEngine> getRemoteEngines() {
        synchronized (knownRemoteEngines) {
            List<RemoteEngine> ret = new ArrayList<RemoteEngine>(knownRemoteEngines.size());
            ret.addAll(knownRemoteEngines);
            return ret;
        }
    }

    @Override
    public void serviceAppears(ServiceInfo service) {
//		System.err.println("Appears    "+service);

        RemoteEngine re = null;
//		try {
//			System.err.println("New    "+service);
        re = new RemoteEngine(service, this);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

        System.err.println("Try add    " + service);
        synchronized (knownRemoteEngines) {
//			System.err.println("Add    "+service);
            knownRemoteEngines.add(re);
        }
//		System.err.println("Finish    "+service);
    }

    @Override
    public void serviceDisappears(ServiceInfo service) {
        LinkedList<RemoteEngine> toDel = new LinkedList<RemoteEngine>();

        synchronized (knownRemoteEngines) {
            for (RemoteEngine re : knownRemoteEngines) {
                if (re.service.getURL().equals(service.getURL())) {
                    System.err.println(" Want delete " + re);
                    toDel.add(re);
                }
            }
        }

        for (RemoteEngine re : toDel) {
            re.deregisterUpdateListener(this);
            re.terminate();
        }

        synchronized (knownRemoteEngines) {
            knownRemoteEngines.removeAll(toDel);
        }

        System.err.println("Disappears " + service);
    }

    @Override
    public void serviceListComplete(List<ServiceInfo> servers) {
        System.err.println("Complete ");
        synchronized (knownRemoteEngines) {
            for (UpdateListenerInterface uli : updateListener)
                uli.doUpdate();
        }
    }

    /**
     * Got updates from one of our connections, queue it to our listeners....
     */
    @Override
    public void doUpdate() {
        for (UpdateListenerInterface uli : updateListener)
            uli.doUpdate();
    }

    /**
     * Terminate all internal threads and free resources.
     */
    public void terminate() {
        quit = true;
        rlbt.terminate();
    }



}
