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

import java.awt.Color;
import java.io.Serializable;

import tw.master.brain.utils.AvoidDoubleConnects;
import tw.master.brain.utils.BrainSize;
import tw.master.mutation.MutationParameters;
import tw.master.utils.Rnd;


public class NeuronCluster implements Serializable {

    private static final long serialVersionUID = 1L;

    public String             name;

    private  Neuron[]           n;

    public Color              clusterColor     = Rnd.rndColor(100);

    public BrainSize          brainSize        = new BrainSize();

    private int               nSize            = 0;

    /**
     * Is this cluster added to a brain ? This flag is used for simplify adding neurons/clusters
     */
    boolean                   inClusterBrainList;

    public void addToBrain(Brain brain, boolean freeArray) {
        if (n == null) return;
        nSize = n.length;
        brain.addNeuronCluster(this);
        if (freeArray) n = null;
    }

    public void addToBrain(Brain brain) {
        addToBrain(brain, true);
    }

    public NeuronCluster(MutationParameters mp) {
        n = new Neuron[mp.clusterNeuronCount.getIntRndValue()];
        nSize = n.length;

        Color c = null;
        int x, y, z;

        name = "NoName";

        x = Rnd.rnd(-10, 10);
        y = Rnd.rnd(-10, 10);
        z = 5;

        int r = mp.clusterSize.getIntRndValue();

        for (int i = 0; i < n.length; i++) {
            int xp = Rnd.rnd(-r, +r);
            int yp = Rnd.rnd(-r, +r);
            int zp = Rnd.rnd(-r, +r);

            Neuron sn = new Neuron(x + xp, y + yp, z + zp, c);
            brainSize.pos(sn.x, sn.y, sn.z);
            sn.cluster = this;
            n[i] = sn;
            //           brain.addNeuron(sn);
        }

        // CHECK_DOUBLE
        // -------------------------------------------------------------------------------------
        // Create interconnects. Ensure that two neurons are not connected more than
        // once to each other:
        // -------------------------------------------------------------------------------------
        AvoidDoubleConnects dcChecker = new AvoidDoubleConnects();

        int wantSyns = mp.clusterNeuronCount.getIntRndValue();
        int wantSynsCount = wantSyns;

        int maxSynsLimit = (n.length - 1) * (n.length - 1);
        if (wantSyns > maxSynsLimit) wantSyns = maxSynsLimit;

        int maxTrys = 50;
        while (wantSynsCount > 0 && maxTrys > 0) {
            maxTrys--;

            int ns = Rnd.rnd(0, n.length - 1);
            int nd = Rnd.rnd(0, n.length - 1);
            if (nd == ns) continue;

            if (!dcChecker.add(ns, nd)) continue;

            wantSynsCount--;
            maxTrys = 50;

            Synapse syn = new Synapse(n[ns], n[nd], Rnd.rnd(-0.5f, 1f));
            n[ns].addSynapse(syn);
        } // while

        if (wantSynsCount > 0)
            System.err.println("Intra cluster connect to hard, missing " + wantSynsCount + " out of " + wantSyns
                    + " synapses");
        dcChecker = null;

        //       brain.addNeuronCluster(this,false);
    }

    public NeuronCluster(String name, Neuron[][] na) {
        this.name = name;

        int w = na.length;
        int h = na[0].length;
        n = new Neuron[w * h];
        nSize = n.length;

        int i = 0;
        for (int wi = 0; wi < w; wi++) {
            for (int hi = 0; hi < h; hi++) {
                Neuron sn = na[wi][hi];

                brainSize.pos(sn.x, sn.y, sn.z);
                sn.cluster = this;
                n[i++] = sn;
            }
        }

        //       brain.addNeuronCluster(this,true);

    }

    public NeuronCluster(String name, Color clusterColor, Neuron[] n) {
        this(name, n);
        this.clusterColor = clusterColor;
    }

    public NeuronCluster(String name, Neuron[] n) {
        this.name = name;
        this.n = n;
        nSize = n.length;
        for (Neuron sn : n) {
            brainSize.pos(sn.x, sn.y, sn.z);
            sn.cluster = this;
        }

        //brain.addNeuronCluster(this,true);

    }

    public int size() {
        //System.err.println("NS = "+nSize+"    "+(n != null ? n.length : -12345));
        if (n == null) return nSize; // Neurons already gone
//        return nSize;
        return n.length;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Returns the neurons of this cluster.
     * 
     * @return  Neuron array
     */
    public Neuron[] getNeurons() {
        return n;
    }
}
