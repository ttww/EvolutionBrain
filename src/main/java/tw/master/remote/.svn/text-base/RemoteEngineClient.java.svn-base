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
import java.net.Socket;

import tw.master.crawler.Crawler;
import tw.master.engine.Engine;
import tw.master.utils.MessageInterface;


public class RemoteEngineClient {

    MessageInterface mi;

    Engine           engine;

    public RemoteEngineClient(Engine engine, MessageInterface mi) {
        this.engine = engine;
        this.mi = mi;
    }

    private ObjectInputStream  in;

    private ObjectOutputStream out;

    private void msg(String txt) {
        if (mi != null) mi.msg(txt);
        else
            System.err.println(txt);
    }

    public void handleClientConnection(Socket client) throws IOException {
        out = new ObjectOutputStream(client.getOutputStream());
        in = new ObjectInputStream(client.getInputStream());

        try {

            int resetCounter = 0;

            long cmdNum = 0;

            boolean quit = false;
            while (!quit) {

                resetCounter++;
                if (resetCounter > 100) {
                    out.reset();
                    resetCounter = 0;
                }

                cmdNum++;
                msg("Wait for request " + cmdNum);
                Request request = (Request) in.readObject();

                msg("CLIENT: Got request #" + cmdNum + ": " + request.what);
                //System.err.println("CLIENT: Got request #"+cmdNum+": "+request.what);

                switch (request.what) {
                    case ALIVE:
                        out.writeObject(new Response(1));
                        break;
                    case MAX_MEMORY:
                        out.writeObject(new Response(Runtime.getRuntime().maxMemory()));
                        break;
                    case FREE_MEMORY:
                        out.writeObject(new Response(Runtime.getRuntime().freeMemory()));
                        break;
                    case CPUS:
                        out.writeObject(new Response(Runtime.getRuntime().availableProcessors()));
                        break;
                    case IS_RUNNING:
                        out.writeObject(new Response(engine.running ? 1 : 0));
                        break;
                    case START:
                        engine.setRunning(true);
                        out.writeObject(new Response(1));
                        break;
                    case STOP:
                        engine.setRunning(false);
                        out.writeObject(new Response(0));
                        break;
                    case CLOSE:
                        out.writeObject(new Response(1));
                        quit = true;
                        break;
                    case BEST_CRAWLER:
                        if (engine.bestCrawlers.size() > 0) {
                            RequestWithData rd = (RequestWithData) request;

                            Crawler c = engine.bestCrawlers.getCrawler(0);
                            if (c != null) {
                                if (c.getFitness() < rd.getFloatData()) {
//			    			if (c.getLastEnergyChanges() < 1.98f || c.getStepCount() > 6000) {
                                    c = null; // to bad
                                } else {
//				    			System.err.println("Best crawler marked, old marker = "+c.removeFromRunListForTransfer);
                                    c = engine.getForTransfer(c);
                                }
                            }
                            out.writeObject(new ResponseCrawler(0, c));
                            out.flush();
//		    			if (c != null) System.err.println("Flush done");
                        } else {
                            out.writeObject(new ResponseCrawler(-1, null));
                        }
                        break;
                    default:
                        break;
                } // switch

                msg("flush data");
                out.flush();

                msg("Request done");

//				if (resetCounter == 50) break;
            } // while

        } catch (IOException e) {
            msg("Connection close: " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
            closeAllStreams();
            throw new IOException(t);
        }
        closeAllStreams();
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
    }


}
