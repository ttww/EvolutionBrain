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

package tw.master.brain;

import java.io.Serializable;
import java.util.LinkedList;

final class PotRunArray extends FloatingArray implements Serializable {

    private PotRunArray() {
        super();
    };

    private static final long	serialVersionUID	= 1L;

    float[]		fa	= new float[20];
    Neuron[]	fan	= new Neuron[20];

    int		faFill;
    long	forRun;

    final void makeSpace() {
        float[]		nfa  = new float [fa.length  + 20];
        Neuron[]	nfan = new Neuron[fan.length + 20];
        //			System.err.println("Realloc PRA fa from "+pra.fa.length+" to "+nfa.length);
        System.arraycopy(fa,  0, nfa,  0, faFill);
        System.arraycopy(fan, 0, nfan, 0, faFill);
        fa  = nfa;
        fan = nfan;
    }


    private static transient Object sync = new Object();

    static transient LinkedList<PotRunArray> freePotRuns = new LinkedList<PotRunArray>();

    static final PotRunArray getInstance() {

        if (freePotRuns.size() == 0) return new PotRunArray();

        PotRunArray ret = null;
        while (ret == null) {
            synchronized (sync) {
                if (freePotRuns.size() == 0) return new PotRunArray();
                ret = freePotRuns.removeFirst();
                if (ret.canReuse()) break;
                //System.err.println("Discard old PotRunArray in get");
                ret = null;
            }
        }

        if (ret == null) return new PotRunArray();

        //System.err.println("Reuse old pot, new size = "+freePotRuns.size());

        ret.faFill = 0;
        ret.forRun = 0;

        //System.err.println("getInstance: MAX_SYNAPSE_LEN = "+MAX_SYNAPSE_LEN+"  got "+ret.buf.length);
        return ret;
    }

    static final void freeInstance(PotRunArray old) {
        synchronized (sync) {
            if (old.canReuse()) {
                freePotRuns.addFirst(old);
            }
            else {
                // old is for the GC....
                //System.err.println("Discard old PotRunArray in free");
            }
        }

    }

}

