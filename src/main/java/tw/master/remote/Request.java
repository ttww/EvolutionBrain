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

import java.io.Serializable;
import java.util.concurrent.LinkedBlockingQueue;


public class Request implements Serializable {


    public Command what;

    private transient LinkedBlockingQueue<Response>	oneResponse = new LinkedBlockingQueue<Response>(1);

    private static final long serialVersionUID = 1L;


    /**
     * @author    Thomas Welsch

     */
    public enum Command {
        CLOSE,
        ALIVE,
        CPUS,
        FREE_MEMORY,
        MAX_MEMORY,
        IS_RUNNING,
        START,
        STOP,
        BEST_CRAWLER,
    };



    public Request(Command what) {
        this.what = what;
    }

    public Response getResponse() throws InterruptedException {
        return oneResponse.take();
    }

    public void offerResponses(Response response) {
        oneResponse.offer(response);
    }

}
