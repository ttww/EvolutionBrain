/*
 *	This file is part of the EvolutionBrain project.
 *
 *	Copyright (c) 2011 by Thomas Welsch (ttww@gmx.de). All rights reserved.
 *
 *	EvolutionBrain is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU Lesser General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	EvolutionBrain is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU Lesser General Public License for more details.
 *
 *	You should have received a copy of the GNU Lesser General Public License
 *	along with EvolutionBrain.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package tw.master.brain;

import java.awt.Color;

import org.junit.Test;


/**
 *
 *
 * @author Thomas Welsch
 *
 */
public class BrainSpeedTest {

    @Test
    public void testBrainSpeed() {
        oneBrainSpeed(true);
    }

    @Test
    public void testBrainSpeedNoDistance() {
        oneBrainSpeed(false);
    }

    private void oneBrainSpeed(boolean useDistance) {
        final int MAX_NC = 20;

        if (useDistance) {
            FloatingArray.MAX_SYNAPSE_LEN = (int) (MAX_NC * Math.sqrt(3)) + 1;
        } else {
            FloatingArray.MAX_SYNAPSE_LEN = 2;
        }

        boolean outputHuman = false;
        final int MEASURE_TIME_MS = 50;

        Brain brain = generate3Perzeptron(10, 10, 10, useDistance);
        NeuronCluster inputCluster = brain.getCluster("Input");
        Neuron[] input = inputCluster.getNeurons();

//        Brain3dDisplay b3d = new Brain3dDisplay(null);
//        b3d.setWatchedBrain(brain);
//        Utils.showBean(b3d,"Perzeptron");

        System.err.println("Warming up...");
        for (int t = 0; t < 10000; t++) {
            for (int i = 0; i < input.length; i++)
                input[i].a = 1;
            brain.step();
        }
        brain.freeNeuronsPotentials();

        boolean firstOutput = true;

        for (int nc = 1; nc <= MAX_NC; nc++) {
//            FloatingArray.MAX_SYNAPSE_LEN = (int) (nc * Math.sqrt(3)) + 1;
            brain = generate3Perzeptron(nc, nc, nc, useDistance);
            inputCluster = brain.getCluster("Input");
            input = inputCluster.getNeurons();

            if (outputHuman) {
                System.err.println("Benchmark network....");
                System.err.println("  Name................: " + brain.getName());
                System.err.println("  Measurment time.....: " + MEASURE_TIME_MS + " ms single thread");
                System.err.println("  Neuron Count........: " + brain.getNeuronCount());
                System.err.println("  Synapses Count......: " + brain.getSynapsesCount());
            }

            float nicpBest = 0;
            float sicpBest = 0;
            long ncBest = 0;
            long scBest = 0;
            long msBest = MEASURE_TIME_MS * 100;

            for (int r = 0; r < 5; r++) {
                Neuron.nc = 0;
                Neuron.sc = 0;

                long startMs = System.currentTimeMillis();
                long endMs = startMs + MEASURE_TIME_MS;
                long now;
                long steps = 0;
                while (true) {
                    for (int t = 0; t < 10; t++) {
                        for (int i = 0; i < input.length; i++)
                            input[i].a = 1;
                        brain.step();
                    }
                    steps += 10;
                    now = System.currentTimeMillis();
                    if (now > endMs) break;
                }

                float measureFac = 1000f / (now - startMs);
                float nicp = Neuron.nc * measureFac;
                float sicp = Neuron.sc * measureFac;

                if (nicp > nicpBest) nicpBest = nicp;
                if (sicp > sicpBest) sicpBest = sicp;

                if (Neuron.nc > ncBest) ncBest = Neuron.nc;
                if (Neuron.sc > scBest) scBest = Neuron.sc;

                if (now - startMs < msBest) msBest = now - startMs;
                if (outputHuman) {
                    System.err.println(
                            String.format(
                                    "Runtime was %d (%2.3f)  Neurons-PS = %5.2f m (%8d)  Synapse-IPC = %5.2f m (%8d)",
                                    new Integer((int) (now - startMs)), new Float(measureFac),
                                    new Float(nicp / 1000000), new Long(Neuron.nc),
                                    new Float(sicp / 1000000), new Long(Neuron.sc)
                            )
                    );
                }
            } // for (t...)

            if (!outputHuman) {
                if (firstOutput) {
                    firstOutput = false;
                    System.err.println("Name\tNeurons\tSynapses\tms\tNPS\tICPS\tncRaw\tscRaw");
                }
                System.err.println(
                        String.format("%s\t%3d\t%3d\t%4d\t%9.0f\t%9.0f\t%7d\t%7d",
                                brain.getName(),
                                new Integer(brain.getNeuronCount()),
                                new Long(brain.getSynapsesCount()),
                                new Long(msBest),
                                new Float(nicpBest),
                                new Float(sicpBest),
                                new Long(ncBest),
                                new Long(scBest)
                        )
                );
            }

            brain.freeNeuronsPotentials();

        } // for (nc...)
    }


    private Brain generate3Perzeptron(int inputCount, int hiddenCount, int outputCount, boolean useDistance) {
        Brain brain = new Brain("Perzeprton(input=" + inputCount + ",hidden=" + hiddenCount + ",output=" + outputCount
                + ")");

        // -----------------------------------------------------------------------------------------
        // Generate 3 layers:
        // -----------------------------------------------------------------------------------------
        Neuron[] input = new Neuron[inputCount];
        Neuron[] output = new Neuron[outputCount];
        Neuron[] hidden = new Neuron[hiddenCount];

        if (!useDistance) Neuron.distanceFactor = 0;
        else
            Neuron.distanceFactor = 1;

        for (int i = 0; i < inputCount; i++) {
            input[i] = new Neuron(i - inputCount / 2f, 0, 0, Color.YELLOW);
            input[i].immortally = true;
        }
        for (int i = 0; i < hiddenCount; i++) {
            hidden[i] = new Neuron(i - hiddenCount / 2f, 1, 0, Color.RED);
            hidden[i].immortally = true;
        }
        for (int i = 0; i < outputCount; i++) {
            output[i] = new Neuron(i - outputCount / 2f, 2, 0, Color.GREEN);
            output[i].immortally = true;
        }

        // -----------------------------------------------------------------------------------------
        // Connect layers:
        // -----------------------------------------------------------------------------------------
        for (int i = 0; i < inputCount; i++) {
            for (int h = 0; h < hiddenCount; h++) {
                new Synapse(input[i], hidden[h], 1);
            }
        }

        for (int h = 0; h < hiddenCount; h++) {
            for (int o = 0; o < inputCount; o++) {
                new Synapse(hidden[h], output[o], 1);
            }
        }

        new NeuronCluster("Input", Color.YELLOW, input).addToBrain(brain, false);
        new NeuronCluster("Hidden", Color.RED, hidden).addToBrain(brain, false);
        new NeuronCluster("Output", Color.GREEN, output).addToBrain(brain, false);

        return brain;
    }
}
