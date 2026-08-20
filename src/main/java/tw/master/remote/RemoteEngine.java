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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import javax.jmdns.ServiceInfo;

import tw.master.crawler.Crawler;
import tw.master.remote.Request.Command;
import tw.master.utils.UpdateListenerInterface;
import tw.master.utils.Utils;


public class RemoteEngine extends Thread {

    public ServiceInfo                   service;

    public String                        remoteName;

    public String                        message;

    private long                         msUsed;

    public ConnectionState               state    = ConnectionState.Init;

    public int                           remoteCPUs;

    public long                          remoteMaxMem;

    public long                          remoteFreeMem;

    public boolean                       remoteIsRunning;

    private boolean                      quit     = false;

    private LinkedBlockingQueue<Request> requests = new LinkedBlockingQueue<Request>(1);

    public RemoteEngine(ServiceInfo service) {
        this(service, null);
    }

    public RemoteEngine(ServiceInfo service, UpdateListenerInterface uli) {
        this.service = service;

        String s = service.getServer();
        if (s == null || s.length() == 0) s = service.getNiceTextString();
        int idx = s.indexOf('.');
        if (idx != -1) remoteName = s.substring(0, idx);
        else
            remoteName = s;

        // Register the caller for updates:
        registerUpdateListener(uli);

//		System.err.println("RemoteName = "+remoteName);
//		showService(service);

        setName("RemoteEngine communication " + remoteName);
        setDaemon(true);

        start();

        AliveCheckerThread act = new AliveCheckerThread();
        act.setName("Alive Checker for " + remoteName);
        act.setDaemon(true);
        act.start();
    }


    class AliveCheckerThread extends Thread {

        @Override
        public void run() {
            while (!quit) {
                try {

                    boolean ret;


                    msg("Check alive");
                    ret = isRemoteAlive();
                    msg("Alive = " + ret + " " + msUsed + "ms");

                    Utils.sleep(10);

                    if (remoteCPUs == 0) {
                        remoteCPUs = getRemoteCPUs();
                        Utils.sleep(10);
                        remoteMaxMem = getRemoteMaxMem() / 1024 / 1024;
                        Utils.sleep(10);
                    }
                    remoteFreeMem = getRemoteFreeMem() / 1024 / 1024;
                    Utils.sleep(10);
                    remoteIsRunning = isRemoteRunning();
                    Utils.sleep(1000);
                } catch (IOException e) {
                    System.err.println(remoteName + ": EXIT   " + e.getMessage());
                    e.printStackTrace();
                }
            } // while
        }
    }

    private LinkedList<UpdateListenerInterface> updateListener = new LinkedList<UpdateListenerInterface>();

    public void registerUpdateListener(UpdateListenerInterface uli) {
        synchronized (updateListener) {
            if (updateListener.contains(uli)) return;
            updateListener.add(uli);
        }
    }

    public void deregisterUpdateListener(UpdateListenerInterface uli) {
        synchronized (updateListener) {
            updateListener.remove(uli);
        }
    }

    public void showService(ServiceInfo si) {

        System.err.println("");
        System.err.println("SI:             " + si);
        System.err.println("   class:       " + si.getClass().getName());
        System.err.println("   appl:        " + si.getApplication());
        System.err.println("   domain:      " + si.getDomain());
        System.err.println("   hostadr      " + si.getHostAddress());
        System.err.println("   name         " + si.getName());
        System.err.println("   nice txt     " + si.getNiceTextString());
        System.err.println("   proto        " + si.getProtocol());
        System.err.println("   Q name       " + si.getQualifiedName());
        System.err.println("   server       " + si.getServer());
        System.err.println("   sub type     " + si.getSubtype());
//		System.err.println("   text         "+si.getTextString());
        System.err.println("   weight       " + si.getWeight());
        System.err.println("   URL          " + si.getURL());
        System.err.println("   InetAddr     " + si.getInetAddress());
        System.err.println("");
    }

    /**
     * @author    Thomas Welsch

     */
    enum ConnectionState {
        Init,
        Connect,
        Conneced,
        WaitCmd,
        Request,
        WaitResponse,
        Quit,
        Reconnect,
        Timeout
    };

    /**
     * @param  state
     */
    private void setState(ConnectionState state) {
        this.state = state;
        callUpdate();
    }

    private void msg(String msg) {
        this.message = msg;
        //System.out.println(msg);
        callUpdate();
    }


    private void callUpdate() {

        LinkedList<UpdateListenerInterface> updateListenerCopy = new LinkedList<UpdateListenerInterface>();

        synchronized (updateListener) {
            updateListenerCopy.addAll(updateListener);
        }
        for (UpdateListenerInterface uli : updateListenerCopy)
            uli.doUpdate();
    }

    // ---------------------------------------------------------------------------------------------

    public boolean isRemoteAlive() throws IOException {

        Request request = new Request(Command.ALIVE);
        requests.offer(request);

        return getBooleanResponse(request);
    }

    // ---------------------------------------------------------------------------------------------

    public boolean isRemoteRunning() throws IOException {

        Request request = new Request(Command.IS_RUNNING);
        requests.offer(request);

        return getBooleanResponse(request);
    }

    public void setRemoteRunning(boolean running) throws IOException {
        Request request = new Request(running ? Command.START : Command.STOP);
        requests.offer(request);

        remoteIsRunning = getBooleanResponse(request);
    }


    // ---------------------------------------------------------------------------------------------

    /**
     * @return
     * @throws IOException
     */
    public int getRemoteCPUs() throws IOException {

        Request request = new Request(Command.CPUS);
        requests.offer(request);

        return getIntResponse(request);
    }

    // ---------------------------------------------------------------------------------------------


    /**
     * @return
     * @throws IOException
     */
    public long getRemoteMaxMem() throws IOException {

        Request request = new Request(Command.MAX_MEMORY);
        requests.offer(request);

        return getLongResponse(request);
    }

    // ---------------------------------------------------------------------------------------------


    /**
     * @return
     * @throws IOException
     */
    public long getRemoteFreeMem() throws IOException {

        Request request = new Request(Command.FREE_MEMORY);
        requests.offer(request);

        return getLongResponse(request);
    }

    // ---------------------------------------------------------------------------------------------

    public Crawler getBestRemoteCrawler(float minimumFitness) throws IOException {
//		System.err.println("CLIENT: Request best");
        RequestWithData request = new RequestWithData(Command.BEST_CRAWLER, new Float(minimumFitness));

        requests.offer(request);

//		System.err.println("CLIENT: Wait for transfer to me");

        ResponseCrawler rc = (ResponseCrawler) getResponse(request);

//		System.err.println("CLIENT: Transfer done, got "+rc.c);

        return rc.c;
    }

    // ---------------------------------------------------------------------------------------------

    private Response getResponse(Request request) throws IOException {
        try {
            Response response = request.getResponse();

            if (response.error) {
                if (response instanceof ResponseException) throw new IOException(
                        ((ResponseException) response).exception);
                else
                    throw new IOException("Communication closed");
            }

            return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }

    // ---------------------------------------------------------------------------------------------

    private long getLongResponse(Request request) throws IOException {
        return getResponse(request).ret;
    }

    // ---------------------------------------------------------------------------------------------

    private int getIntResponse(Request request) throws IOException {
        return (int) getLongResponse(request);
    }

    // ---------------------------------------------------------------------------------------------

    private boolean getBooleanResponse(Request request) throws IOException {
        return getLongResponse(request) == 0 ? false : true;
    }

    // ---------------------------------------------------------------------------------------------

    private Socket             socket       = null;

    private ObjectOutputStream out          = null;

    private ObjectInputStream  in           = null;

    private int                resetCounter = 0;

    @Override
    public void run() {

        Request request = null;

        while (!quit) {
            Response response = null;

            try {

                setState(ConnectionState.WaitCmd);

                request = requests.take();

                checkSocket();

                resetCounter++;
                if (resetCounter > 100) {
                    out.reset();
                    //System.err.println("reset");
                    resetCounter = 0;
                }


                setState(ConnectionState.Request);
                long start_ms = System.currentTimeMillis();

                try {
                    out.writeObject(request);
                    out.flush();
                } catch (IOException e) {
                    System.err.println("Try reconnect write");
                    setState(ConnectionState.Reconnect);
                    closeAllStreams();
                    checkSocket();

                    out.writeObject(request);
                    out.flush();

                }
                setState(ConnectionState.WaitResponse);

                try {
                    response = (Response) in.readObject();
                } catch (IOException e) {
                    System.err.println("Try reconnect read");
                    setState(ConnectionState.Reconnect);
                    closeAllStreams();
                    checkSocket();

                    out.writeObject(request);
                    out.flush();


                    response = (Response) in.readObject();

                }

                long stop_ms = System.currentTimeMillis();

                msUsed = stop_ms - start_ms;

                request.offerResponses(response);
                request = null;

            } catch (InterruptedException e) { // The Thread was canceled...
                //e.printStackTrace();
                closeAllStreams();

                if (request != null) {
                    request.offerResponses(new ResponseException(e));
                    request = null;
                    System.err.println("Set QUIT ! InterruptedException !");
                    quit = true;
                }

            } catch (SocketTimeoutException e) { // Server sleep ?
                //e.printStackTrace();
                closeAllStreams();
                setState(ConnectionState.Timeout);
//				System.err.println("Set QUIT !");
//				quit = true;
            } catch (ConnectException e) { // Firewall ?
                //e.printStackTrace();
                closeAllStreams();
                setState(ConnectionState.Timeout);
//				System.err.println("Set QUIT !");
//				quit = true;
            } catch (IOException e) { // Socket close ?
                e.printStackTrace();
                closeAllStreams();
            } catch (Throwable e) {
                closeAllStreams();
                e.printStackTrace();

                // Check if someone are waiting for an answer....:
                if (request != null) {
                    request.offerResponses(new ResponseException(e));
                    request = null;
                }
            } finally {
                if (request != null) {
                    quit = true;
                    request.offerResponses(
                            new ResponseException(new IllegalStateException(
                            "Communication in finally...")));
                    request = null;
                    closeAllStreams();
                }
            }

        } // while (!quit)

        closeAllStreams();

        setState(ConnectionState.Quit);
        System.err.println("Communication to " + remoteName + " finished !");
    }

    private void checkSocket() throws IOException {
        if (socket == null) {

            setState(ConnectionState.Connect);
            socket = new Socket(service.getInetAddress(), service.getPort());
            socket.setSoLinger(true, 5);
            socket.setReuseAddress(true);
            socket.setSoTimeout(5000);
            setState(ConnectionState.Conneced);

            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            resetCounter = 0;
        }
    }

    private void closeAllStreams() {
        if (out != null) {
            try {
                out.flush();
                out.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            out = null;
        }
        if (in != null) {
            try {
                in.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            in = null;
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            socket = null;
        }
    }

    public void terminate() {
        if (quit) return;
        quit = true;
        System.err.println("Set QUIT in TERMINATE ! " + Utils.getStacktrace());

        if (!isInterrupted()) interrupt();
    }



}
