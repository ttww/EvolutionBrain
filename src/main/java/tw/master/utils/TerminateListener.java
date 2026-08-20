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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * This thread try to reach another application who is running on the given port and try
 * to send and "Quit please" command.<p>
 * After waiting 2 seconds, a new server socket for the given port is created and listen
 * for future "Quit please" commands.<p>
 * This is useful during development (you don't neet to cleanup everything by your own....)<p>
 * The server port is only listen on localhost and is not reachable from outside the machine !
 * 
 * @author Thomas Welsch
 */
public class TerminateListener extends Thread {

    // Port to listen:
    private final int port;

    /**
     * Try to terminate application listen to given port and start an server for the port.
     * 
     * @param port
     * 			Port to reach and listen
     */
    public TerminateListener(int port) {

        this.port = port;

        setDaemon(true);
        setName("TerminateThread on port "+port+", waiting for connections...");

        // -----------------------------------------------------------------------------------------
        // Start starting:
        // -----------------------------------------------------------------------------------------
        start();

        // -----------------------------------------------------------------------------------------
        // Try connect to localhost at given port and send quit command:
        // -----------------------------------------------------------------------------------------
        try {
            Socket socket = new Socket((String) null, port);	// null --> localhost

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            out.writeBytes("Quit please\n");

            out.flush();
            out.close();
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
        }

    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void run() {

        // -----------------------------------------------------------------------------------------
        // Give the other application 2 seconds for close the server socket...:
        // -----------------------------------------------------------------------------------------
        Utils.sleep(2000);

        try {
            ServerSocket listen = new ServerSocket(port);

            for (;;) {
                Socket client = listen.accept();
                System.err.println("TerminateThread: Connection from "+client);

                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                String line = in.readLine();

                // ---------------------------------------------------------------------------------
                // Only one command (yet)
                // ---------------------------------------------------------------------------------
                if ("Quit please".equals(line)) {
                    System.err.println("TerminateThread: Receive exit via socket...");

                    in.close();
                    client.close();
                    listen.close();

                    System.exit(0);
                }

                System.err.println("TerminateThread: Receive unknown command via socket: |"+line+"|");

                break;		// only one chance....

            }	// for

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
