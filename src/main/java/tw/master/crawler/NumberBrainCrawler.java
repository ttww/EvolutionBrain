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

package tw.master.crawler;

import java.awt.Color;

import tw.master.brain.Brain;
import tw.master.brain.Neuron;
import tw.master.brain.Synapse;
import tw.master.engine.Engine;
import tw.master.utils.Rnd;



public class NumberBrainCrawler extends AbstractBrainCrawler {

    private static final long serialVersionUID = 1L;


    public NumberBrainCrawler(Engine engine) {
        super(engine);

        pos.x = 20;
        pos.y = 81;
        direction = 270;
        speed = 0.1f;
    }

    @Override
    public Brain createBrain() {
        Brain brain = new Brain(getName());

        int wm = 8;
        int hm = 8;

//		wm = 5;
//		hm = 1;

        float x, y, z;

        z = 0;

        x = -wm / 2f;
        y = -hm / 2f;

        int wantedNeurons = 50;
        int outputNeurons = 10;
        int wantedSynapses = 100;
        int visionFieldToBrainSynapses = 3;
        int brainFieldToOutputSynapses = 3;

        outNeurons = new Neuron[outputNeurons];
        for (int o = 0; o < outputNeurons; o++) {
            outNeurons[o] = new Neuron(-(outputNeurons / 2) + o, 0, 12, Color.GREEN);
            brain.addNeuron(outNeurons[o]);

        }

//		Neuron toLeftNeuron  = new Neuron(-1,0,10,Color.RED);
//		Neuron speedNeuron	 = new Neuron( 0,0,10,Color.BLUE);
//		Neuron toRightNeuron = new Neuron( 1,0,10,Color.GREEN);
//
//		brain.addNeuron("toLeftNeuron",		toLeftNeuron);
//		brain.addNeuron("toRightNeuron",	toRightNeuron);
//		brain.addNeuron("speedNeuron", 		speedNeuron);

        Neuron[][] visionField = new Neuron[hm][wm];
        for (int h = 0; h < hm; h++) {
            for (int w = 0; w < wm; w++) {
                visionField[h][w] = new Neuron(x + w, y + h, z);
                visionField[h][w].isInput = 1;
                visionField[h][w].c = Color.RED;
            }
        }
        brain.setVisionField(visionField);



        float brainRadius = 8;
        Neuron[] na = new Neuron[wantedNeurons];
        for (int i = 0; i < na.length; i++) {
            na[i] = new Neuron(
                    Rnd.rnd(-brainRadius, +brainRadius),
                    Rnd.rnd(-brainRadius, +brainRadius),
                    Rnd.rnd(2f, 8f), Color.ORANGE);
            brain.addNeuron(na[i]);
        }

        // -----------------------------------------------------------------------------------------
        // Connect the see field with the brain field
        // -----------------------------------------------------------------------------------------
        char[][] connected = new char[wm * hm][wantedNeurons];
        int ns = 0, nd;
        for (int h = 0; h < hm; h++) {
            for (int w = 0; w < wm; w++) {
                for (int i = 0; i < visionFieldToBrainSynapses; i++) {
                    while (true) {
                        nd = Rnd.rnd(0, wantedNeurons - 1);
                        if (connected[ns][nd] == 0) break;
                    }
                    connected[ns][nd] = 1;
                    new Synapse(visionField[h][w], na[nd], Rnd.rnd(0f, 1f));
                    //Synapse syn = new Synapse(visionField[h][w],na[nd],Rnd.rnd(0f,1f));
                    //visionField[h][w].addSynapse(syn);
                }
                ns++;
            }
        }

        // -----------------------------------------------------------------------------------------
        // Connect the BrainField with interconnections:
        // -----------------------------------------------------------------------------------------
        if (wantedSynapses > wantedNeurons * wantedNeurons - wantedNeurons)
            throw new IllegalArgumentException("to much synapses for brain");

        connected = new char[wantedNeurons][wantedNeurons];
        while (wantedSynapses > 0) {

            int ntry = 0;
            ns = 0;
            nd = 0;
            while (ntry < 10) {
                ns = Rnd.rnd(0, wantedNeurons - 1);
                nd = Rnd.rnd(0, wantedNeurons - 1);
                if (nd != ns && connected[nd][ns] == 0) break;
            }
            if (ntry == 10) {
                System.err.println("Warning, need " + ntry + " tries for finding free neuron");
                continue;
            }

            new Synapse(na[ns], na[nd], Rnd.rnd(-0.5f, 1f));
            //Synapse syn = new Synapse(na[ns],na[nd],Rnd.rnd(-0.5f,1f));
            //na[ns].addSynapse(syn);
            connected[nd][ns] = 1;
            wantedSynapses--;
        }

        // -----------------------------------------------------------------------------------------
        // Connect the BrainField with the output connections:
        // -----------------------------------------------------------------------------------------
        connected = new char[outputNeurons][wantedNeurons];
        ns = 0;
        for (int o = 0; o < outputNeurons; o++) {
            for (int i = 0; i < brainFieldToOutputSynapses; i++) {
                while (true) {
                    ns = Rnd.rnd(0, wantedNeurons - 1);
                    if (connected[o][ns] == 0) break;
                }
                connected[o][ns] = 1;
                new Synapse(na[ns], outNeurons[o], Rnd.rnd(0f, 1f));
                //Synapse syn = new Synapse(na[ns],outNeurons[o],Rnd.rnd(0f,1f));
                //na[ns].addSynapse(syn);
            }
        }

        return brain;
    }

    int      nextExpected = 0;

    Neuron[] outNeurons;

    //float	energy = 100;


//	private check
    /* (non-Javadoc)
     * @see tw.master.crawler.Crawler#step()
     */
    @Override
    public void step() {
        super.step();

//		System.err.println("at "+Utils.getStacktrace());
        // Check if there is a wanted output neuron:
        if (!Float.isNaN(vfp.overlayValue)) {

            // get the value we want to have:
            int want = (int) (vfp.overlayValue * 10);

            // analyze values from overlay neurons: (0..9)
            for (int i = 0; i < outNeurons.length; i++) {
                float a = outNeurons[i].a;
                if (i == want) {
                    // This output neuron we want to have !
                    if (a > 0.2) energy++;
                    else
                        energy -= 1;
                } else {
                    // This output neuron we don't want to have !
                    if (a < 0.2) energy++;
                    else
                        energy -= 1;
                }
            }
        } else {
            // No value area, all should be down
            for (int i = 0; i < outNeurons.length; i++) {
                float a = outNeurons[i].a;
                if (a < 0.1) energy++;
                else
                    energy--;
            }
        }

        if (energy < 0) {
//			this.isDead = true;
            this.liveState = LiveState.Dead_NoEnergy;
            //System.err.println("to less energy...");
        }

    }


}
