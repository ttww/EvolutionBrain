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
import java.util.LinkedList;
import java.util.List;

import tw.gui.annotiations.GuiBooleanAnnotation;
import tw.gui.annotiations.GuiEnumAnnotation;
import tw.gui.annotiations.GuiFloatAnnotation;
import tw.gui.annotiations.GuiInfoAnnotation;
import tw.master.brain.Brain;
import tw.master.brain.Neuron;
import tw.master.brain.NeuronCluster;
import tw.master.brain.Synapse;
import tw.master.brain.activation.ActivationFunctionFactory.ActivationFunction;
import tw.master.brain.utils.AvoidDoubleConnects;
import tw.master.engine.Engine;
import tw.master.mutation.MutationParameters;
import tw.master.utils.ObjectClone;
import tw.master.utils.Rnd;


@GuiInfoAnnotation(
        title = "WalkingBrainControls")
        public class WalkingBrainCrawler extends AbstractBrainCrawler implements MutationCrawlerInterface {

    private static final long         serialVersionUID   = 1L;

    private static float              minimumSynRndValue = -0.5f;

    private static float              maximumSynRndValue = 1.0f;

    private static boolean            createSimpleBrains = true;

    private static ActivationFunction activationFunction = ActivationFunction.Signum;

    // ---------------------------------------------------------------------------------------------

    @GuiEnumAnnotation(
            label = "Activation function",
            enumClass = ActivationFunction.class,
            tooltip = "Defines the activation function for the neuron"
    )
    public static void setActivationFunction(ActivationFunction which) {
        activationFunction = which;
    }

    public static ActivationFunction getActivationFunction() {
        return activationFunction;
    }

    /**
     * @param  t
     */
    @GuiBooleanAnnotation(
            label = "Create simple brains",
            text = "simple",
            tooltip = "If set, only primitive simple brains will be generated."
    )
    public static void setCreateSimpleBrains(boolean t) {
        createSimpleBrains = t;
    }

    /**
     * @return
     */
    public static boolean isCreateSimpleBrains() {
        return createSimpleBrains;
    }

    /**
     * @return   the minimumSynRndValue
     */
    @GuiFloatAnnotation(
            label = "Synapse factor RND minimum:",
            min = -2f,
            max = +2f,
            format = "%+4.2f",
            tooltip =
                "Determines the minimum factor for the randomly generated\n" +
                "synaptically connections"

    )
    public static float getMinimumSynRndValue() {
        return minimumSynRndValue;
    }

    /**
     * @param f   the minimumSynRndValue to set
     */
    public static void setMinimumSynRndValue(float f) {
        minimumSynRndValue = f;
    }

    /**
     * @return   the maximumSynRndValue
     */
    @GuiFloatAnnotation(
            label = "Synapse factor RND maximum",
            min = -2f,
            max = 2f,
            format = "%+4.2f",
            tooltip =
                "Determines the maximum factor for the randomly generated\n" +
                "synaptically connections"
    )
    public static float getMaximumSynRndValue() {
        return maximumSynRndValue;
    }

    /**
     * @param f   the maximumSynRndValue to set
     */
    public static void setMaximumSynRndValue(float f) {
        maximumSynRndValue = f;
    }



    public WalkingBrainCrawler(Engine engine) {
        super(engine);
        setSpeed(0.05f);
//		setAngle(Rnd.rnd(0f,360f));
        setAngle(130);
    }


    @Override
    public Brain createBrain() {

        if (createSimpleBrains) return createSimplestBrain();

        Brain brain = new Brain(getName());

        brain.setActivationFunction(activationFunction);

        // -----------------------------------------------------------------------------------------
        // Create see field:
        // -----------------------------------------------------------------------------------------
        int wm = mp.visionfieldWidth.getIntRndValue();
        int hm = mp.visionfieldDeep.getIntRndValue();

        float x, y, z;

        z = 0;
//		x = -wm / 2f;
//		y = -hm / 2f;
        x = -(wm - 1) / 2f;
        y = 0; // -hm / 2f;

        Neuron[][] visionField = new Neuron[hm][wm];
        for (int h = 0; h < hm; h++) {
            for (int w = 0; w < wm; w++) {
                visionField[h][w] = new Neuron(x + w, y + h, z);
                visionField[h][w].isInput = 1;
            }
        }

        NeuronCluster seeCluster = new NeuronCluster("seeCluster", visionField);
        seeCluster.clusterColor = Color.RED;

        brain.setVisionField(visionField);


        // -----------------------------------------------------------------------------------------
        // Create brain clusters:
        // -----------------------------------------------------------------------------------------
        LinkedList<NeuronCluster> clusters = new LinkedList<NeuronCluster>();
        int clusterCount = mp.brainClusterCount.getIntRndValue();
        int nSize = 0;
        for (int c = 0; c < clusterCount; c++) {
            NeuronCluster cluster = new NeuronCluster(mp);
            clusters.add(cluster);
            nSize += cluster.size();
        }

        // -----------------------------------------------------------------------------------------
        // Create cluster interconnects. Ensure that two neurons in different clusters are not
        // connected more than once to each other:
        // -----------------------------------------------------------------------------------------
        if (clusters.size() > 1) {
            AvoidDoubleConnects dcChecker = new AvoidDoubleConnects();

            int wantRuns = mp.clusterConnectCount.getIntRndValue();
            while (wantRuns > 0) {
                int nsCluster = Rnd.rnd(0, clusters.size() - 1);
                int ndCluster = Rnd.rnd(0, clusters.size() - 1);
                if (nsCluster == ndCluster) continue;
                wantRuns--;

                NeuronCluster nsBrainCluster = clusters.get(nsCluster);
                NeuronCluster ndBrainCluster = clusters.get(ndCluster);
                Neuron[] nsNeurons = clusters.get(nsCluster).getNeurons();
                Neuron[] ndNeurons = clusters.get(ndCluster).getNeurons();

                int wantSyns = mp.clusterInterConnectCount.getIntRndValue();

                int maxSynsLimit = (nsBrainCluster.size() - 1) * (ndBrainCluster.size() - 1);
                if (wantSyns > maxSynsLimit) wantSyns = maxSynsLimit;

                int wantSynsCount = wantSyns;

                int maxTrys = 50;
                while (wantSynsCount > 0 && maxTrys > 0) {
                    maxTrys--;

                    int ns = Rnd.rnd(0, nsBrainCluster.size() - 1);
                    int nd = Rnd.rnd(0, ndBrainCluster.size() - 1);
                    if (nd == ns) continue;

                    if (!dcChecker.add(nsCluster, ns, ndCluster, nd)) continue;

                    maxTrys = 50;
                    wantSynsCount--;

                    new Synapse(nsNeurons[ns], ndNeurons[nd], Rnd.rnd(minimumSynRndValue, maximumSynRndValue));
                } // while

                if (wantSynsCount > 0)
                    System.err.println("Cluster connect to hard, missing " + wantSynsCount + " out of " + wantSyns
                            + " synapses");

            } // while (wantRuns)....

            dcChecker = null;
        } // if (clusters.size())


        // -----------------------------------------------------------------------------------------
        // Connect motor neurons to motor cluster:
        // -----------------------------------------------------------------------------------------
        Neuron toLeftNeuron = new Neuron(-1, 0, 10, Color.RED);
        Neuron speedNeuron = new Neuron(0, 0, 10, Color.BLUE);
        Neuron toRightNeuron = new Neuron(1, 0, 10, Color.GREEN);

        Neuron[] motorNeurons = new Neuron[] { toLeftNeuron, /* speedNeuron,*/toRightNeuron };
        NeuronCluster motorCluster = new NeuronCluster("motorCluster", motorNeurons);

        brain.addNeuron("toLeftNeuron", toLeftNeuron);
        brain.addNeuron("toRightNeuron", toRightNeuron);
        brain.addNeuron(speedNeuron);
        //brain.addNeuron("speedNeuron",    speedNeuron);

        // -----------------------------------------------------------------------------------------
        // Choose motor cluster, should have at least 5 Neurons, if not, use the largest one
        // -----------------------------------------------------------------------------------------
        NeuronCluster largestBrainCluster = null;
        int largestSize = 0;
        NeuronCluster secondLargestBrainCluster = null;
        int secondLargestSize = 0;
        for (NeuronCluster cluster : clusters) {
//			System.err.println("------------------ ");
//			System.err.println("Check cluster             "+cluster);
//			System.err.println("largestSize               "+largestSize);
//			System.err.println("largestBrainCluster       "+largestBrainCluster);
//			System.err.println("secondLargestSize         "+secondLargestSize);
//			System.err.println("secondLargestBrainCluster "+secondLargestBrainCluster);

            if (cluster.size() > largestSize) {

                secondLargestSize = largestSize;
                secondLargestBrainCluster = largestBrainCluster;

                largestSize = cluster.size();
                largestBrainCluster = cluster;
            } else {
                if (cluster.size() > secondLargestSize) {
                    secondLargestSize = cluster.size();
                    secondLargestBrainCluster = cluster;
                }
            }
//			if (cluster.size() >= 5) {
//				motorBrainCluster = cluster;
//				break;
//			}
        }

        // Try to avoid the direct connection of the motor cluster with the visionfield cluster:
//		motorBrainCluster = largestBrainCluster;
//		if (motorBrainCluster.name.equals("seeCluster") && secondLargestBrainCluster != null)
//			motorBrainCluster = secondLargestBrainCluster;

        NeuronCluster motorCortexCluster = null;

        motorCortexCluster = secondLargestBrainCluster;
        if (motorCortexCluster == null)
            motorCortexCluster = largestBrainCluster;

        Neuron[] motorCortexNeurons = motorCortexCluster.getNeurons();

//		System.err.println("motorBrainCluster         "+motorBrainCluster);

        AvoidDoubleConnects dcChecker = new AvoidDoubleConnects();

        for (int m = 0; m < motorNeurons.length; m++) {
            int maxTrys = 50;

            int wsc = motorCortexNeurons.length;
            if (wsc > 10) wsc = 10;
            while (wsc > 0 && maxTrys > 0) {
                maxTrys--;

                int ns = Rnd.rnd(0, motorCortexNeurons.length - 1);

                if (!dcChecker.add(0, ns, 1, m)) continue;

                maxTrys = 50;
                wsc--;
                //System.err.println(motorBrainCluster.n[ns]+"("+ns+")"+"  --> "+motorNeurons[m]+"("+m+")");

                new Synapse(motorCortexNeurons[ns], motorNeurons[m], Rnd.rnd(minimumSynRndValue, maximumSynRndValue));
            } // while
        }

//        int wantSyns = motorNeurons.length;
//
//        int maxSynsLimit = (motorCortexNeurons.length - 1) * (motorNeurons.length - 1);
//        if (wantSyns > maxSynsLimit) wantSyns = maxSynsLimit;
//
//        //  2   5
//
//        int wantSynsCount = Math.min(motorNeurons.length, wantSyns);
//
//        for (int m = 0; m < motorNeurons.length; m++) {
////            System.err.println("motorNeurons.length   " + motorNeurons.length);
////            System.err.println("wantSynsCount         " + wantSynsCount);
//
//            int maxTrys = 50;
//
//            int wsc = wantSynsCount / motorNeurons.length;
//            while (wsc > 0 && maxTrys > 0) {
//                maxTrys--;
//
//                int ns = Rnd.rnd(0, motorCortexNeurons.length - 1);
//
//                if (!dcChecker.add(0, ns, 1, m)) continue;
//
//                maxTrys = 50;
//                wsc--;
//                wantSynsCount--;
//
//                //System.err.println(motorBrainCluster.n[ns]+"("+ns+")"+"  --> "+motorNeurons[m]+"("+m+")");
//
//                new Synapse(motorCortexNeurons[ns], motorNeurons[m], Rnd.rnd(minimumSynRndValue, maximumSynRndValue));
//                //Synapse syn = new Synapse(motorBrainCluster.n[ns],motorNeurons[m],Rnd.rnd(minimumSynRndValue,maximumSynRndValue));
//                //motorBrainCluster.n[ns].addSynapse(syn);
//                //break;
//            } // while
//        }
//        if (wantSynsCount > 0)
//            System.err.println("Motor cluster connect to hard, missing " + wantSynsCount + " out of " + wantSyns
//                    + " synapses");


        // -----------------------------------------------------------------------------------------
        // Connect vision field:
        // -----------------------------------------------------------------------------------------
        dcChecker = new AvoidDoubleConnects();

        int wantSyns = seeCluster.size() * 4; // MUTATION

        int maxSynsLimit = seeCluster.size() * (largestBrainCluster.size() - 1);
        if (wantSyns > maxSynsLimit) wantSyns = maxSynsLimit;

        int wantSynsCount = wantSyns;

        Neuron[] seeNeurons     = seeCluster.getNeurons();
        Neuron[] largestNeurons = largestBrainCluster.getNeurons();

        for (int m = 0; m < seeCluster.size(); m++) {
            for (int j = 0; j < 4; j++) {
                int maxTrys = 50;
                while (wantSynsCount > 0 && maxTrys > 0) {
                    maxTrys--;

                    int nd = Rnd.rnd(0, largestBrainCluster.size() - 1);

                    if (!dcChecker.add(0, nd, 1, m)) continue;

                    maxTrys = 50;
                    wantSynsCount--;


                    new Synapse(seeNeurons[m], largestNeurons[nd], Rnd.rnd(minimumSynRndValue,
                            maximumSynRndValue));
                    //Synapse syn = new Synapse(seeCluster.n[m],largestBrainCluster.n[nd],Rnd.rnd(minimumSynRndValue,maximumSynRndValue));
                    //seeCluster.n[m].addSynapse(syn);
                    break;
                } // while
            }
        }
        if (wantSynsCount > 0)
            System.err.println("Motor cluster connect to hard, missing " + wantSynsCount + " out of " + wantSyns
                    + " synapses");

        dcChecker = null;


        motorCluster.addToBrain(brain, false);
        seeCluster.addToBrain(brain, false);
        for (NeuronCluster cluster : clusters)
            cluster.addToBrain(brain, false);

        return brain;
    }

    private Brain createSimplestBrain() {
        Brain brain = new Brain(getName());

        brain.setActivationFunction(activationFunction);

        maxSteps = 50000;

        // -----------------------------------------------------------------------------------------
        // Create see field:
        // -----------------------------------------------------------------------------------------
        int wm = mp.visionfieldWidth.getIntRndValue();
        int hm = mp.visionfieldDeep.getIntRndValue();


        // wm = 2;
        float x, y, z;

        z = 0;
        x = -(wm - 1) / 2f;
        y = 0; // -hm / 2f;

        Neuron[][] visionField = new Neuron[hm][wm];
        for (int h = 0; h < hm; h++) {
            for (int w = 0; w < wm; w++) {
                visionField[h][w] = new Neuron(x + w, y + h, z);
            }
        }
        NeuronCluster seeCluster = new NeuronCluster("seeCluster", visionField);
        seeCluster.clusterColor = Color.GRAY;

        brain.setVisionField(visionField);

        // -----------------------------------------------------------------------------------------
        // Connect motor neurons to motor cluster:
        // -----------------------------------------------------------------------------------------
        Neuron toLeftNeuron  = new Neuron(-1, 0, 10, Color.RED);
        Neuron speedNeuron   = new Neuron( 0, 0, 10, Color.BLUE);
        Neuron toRightNeuron = new Neuron( 1, 0, 10, Color.GREEN);

        Neuron[] motorNeurons = new Neuron[] { toLeftNeuron, /* speedNeuron,*/toRightNeuron };
        NeuronCluster motorCluster = new NeuronCluster("motorCluster", motorNeurons);

        brain.addNeuron("toLeftNeuron", toLeftNeuron);
        brain.addNeuron("toRightNeuron", toRightNeuron);
        brain.addNeuron(speedNeuron);
        //brain.addNeuron("speedNeuron",    speedNeuron);

        //Synapse syn;

        for (int sn = 0; sn < wm; sn++) {
            for (int mn = 0; mn < motorNeurons.length; mn++) {
                for (int h = 0; h < hm; h++) {
                    new Synapse(visionField[h][sn], motorNeurons[mn], Rnd.rnd(-0.5f, 1f));
                }
            }
        }

        seeCluster.addToBrain(brain, false);
        motorCluster.addToBrain(brain, false);

        return brain;
    }


    @Override
    public Crawler getMutationCrawler() {

        // Mutations:
        //	1.	Disable of visionfield neurons
        //	2.	Adding Width to visionfield
        //	3.	Adding Deep  to visionfield
        //	4.	Removing Width to visionfield
        //	5.	Removing Deep  to visionfield
        //	6.	Adding Random Neurons to whole net
        //	7.	Removing Random Neurons to whole net
        //	8.	Adding Inter-cluster connects
        //	9.	Adding new cluster and cluster to cluster connects
        // 10.	Removing clusters (not usefull, because of degeneration and auto-cleanup ?)
        // 11.	Change of signal running time (Per brain)
        // 12.	Change of signal running time (Per neuron)

        int mutationIdx = Mutation.getRandomMutationIndex();
        Mutation mutation = Mutation.getMutationViaIndex(mutationIdx);

        long stepsSinceLastMutation = stepCount - lastMutation;
        if (stepsSinceLastMutation < 1000) return null;

        lastMutation = stepCount;

//		long start_ms = System.currentTimeMillis();
        WalkingBrainCrawler newCrawler = (WalkingBrainCrawler) ObjectClone.copy(this);
        newCrawler.setEngine(engine);

        // The new Crawler schould start with equals parameters:
        newCrawler.maxSteps = 50000;
        newCrawler.energy = 5000;
        newCrawler.stepCount = 0;
        newCrawler.lastMutation = 0;

        Brain b = newCrawler.getBrain();

        switch (mutation) {
            case DISABLE_VISIONFIELD_NEURONS: {
                // Try to kill a neuron. Don't care about the loop, if to much, this brain will died
                // anyway....:
                int maxTrys = b.visionFieldWidth * b.visionFieldHeight;

                while (maxTrys-- > 0) {
                    int w = Rnd.rnd(0, b.visionFieldWidth - 1);
                    int h = Rnd.rnd(0, b.visionFieldHeight - 1);
                    Neuron n = b.visionField[h][w];
                    if (n == null) continue;
                    n.isDead = true;
                    break;
                }
                trimVisionFieldRim(b);
                break;
            }

            case REMOVE_RANDOM_NEURON: {
                // Try to kill a neuron. Don't care about the loop, if to much, this brain will died
                // anyway....:

                List<Neuron> nl = b.neurons;

                int nc = nl.size() - 1;
                int maxTrys = nc;

                while (maxTrys-- > 0) {
                    Neuron n = nl.get(Rnd.rnd(0, nc));
                    if (n == null) continue;
                    n.isDead = true;
                    break;
                }
                trimVisionFieldRim(b);
                break;

            }

            case REDUCE_VISIONFIELD_DEEP: {
                if (Rnd.rnd(0, 1) == 0) {
                    // -----------------------------------------------------------------------------
                    // Test: Cut bottom
                    // -----------------------------------------------------------------------------
                    for (int w = 0; w < b.visionFieldWidth; w++) {
                        Neuron n = b.visionField[0][w];
                        if (n == null) continue;
                        n.isDead = true;
                    }
                } else {
                    // -----------------------------------------------------------------------------
                    // Test: Cut top
                    // -----------------------------------------------------------------------------
                    for (int w = 0; w < b.visionFieldWidth; w++) {
                        Neuron n = b.visionField[b.visionFieldHeight - 1][w];
                        if (n == null) continue;
                        n.isDead = true;
                    }
                }
                trimVisionFieldRim(b);
                break;
            }
            case REDUCE_VISIONFIELD_WIDTH: {
                if (Rnd.rnd(0, 1) == 0) {
                    // -----------------------------------------------------------------------------
                    // Test: Cut left
                    // -----------------------------------------------------------------------------
                    for (int h = 0; h < b.visionFieldHeight; h++) {
                        Neuron n = b.visionField[h][0];
                        if (n == null) continue;
                        n.isDead = true;
                    }
                } else {
                    // -----------------------------------------------------------------------------
                    // Test: Cut right
                    // -----------------------------------------------------------------------------
                    for (int h = 0; h < b.visionFieldHeight; h++) {
                        Neuron n = b.visionField[h][b.visionFieldWidth - 1];
                        if (n == null) continue;
                        n.isDead = true;
                    }
                }
                trimVisionFieldRim(b);
                break;
            }

//			case ADDING_RANDOM_NEURON: {
//				List<Neuron> nl = b.neurons;
//
//				int	numberToGen = 2;
//
//				BitSet bf = new BitSet();
//				for (int j=0; j<numberToGen; j++) {
//					int numberOfSynases = mp.
//				}
//
//				int nc = nl.size() - 1;
//				int maxTrys = nc;
//				int numb
//				while (maxTrys-- > 0) {
//					Neuron n = nl.get(Rnd.rnd(0,nc));
//					if (n == null) continue;
//					n.isDead = true;
//					break;
//				}
//
//				break;
//
//			}

            default: {
                System.err.println("Unhandled mutation " + mutation);
                throw new IllegalArgumentException("Unhandled mutation " + mutation);
            }
        } // switch

        newCrawler.generation++;
        int idx = name.indexOf(':');
        String newName;
        if (idx != -1) {
            newName = name.substring(0, idx) + "." + mutationIdx + ":" + engine.createCount;
        } else {
            newName = name + "." + mutationIdx + ":" + engine.createCount;
        }
        newCrawler.setName(newName);
        engine.createCount++;
        return newCrawler;

        //return null;
    }

    /**
     * Looks if there is a size with deadNeurons and remove it from the seeFiled.
     *
     * @param b
     *
     * @return true/false,	true if one rim line was removed.
     */
    private boolean trimVisionFieldRim(Brain b) {
        int w = b.visionFieldWidth;
        int h = b.visionFieldHeight;
        Neuron[][] sf = b.visionField;

        // -----------------------------------------------------------------------------------------
        // First we look which rims are to cut:
        // -----------------------------------------------------------------------------------------

        boolean canCutRimTop = true; // Optimistic: we want to cut :-)
        boolean canCutRimBottom = true;
        boolean canCutRimLeft = true;
        boolean canCutRimRight = true;

        // Bottom:
        for (int x = 0; x < w; x++) {
            Neuron n = sf[0][x];
            if (n != null) {
                if (n.isDead) continue;
            } else
                continue;

            canCutRimBottom = false;
            break;
        }

        // Top:
        for (int x = 0; x < w; x++) {
            Neuron n = sf[h - 1][x];
            if (n != null) {
                if (n.isDead) continue;
            } else
                continue;

            canCutRimTop = false;
            break;
        }

        // Left:
        for (int y = 0; y < h; y++) {
            Neuron n = sf[y][0];
            if (n != null) {
                if (n.isDead) continue;
            } else
                continue;

            canCutRimLeft = false;
            break;
        }

        // Right:
        for (int y = 0; y < h; y++) {
            Neuron n = sf[y][w - 1];
            if (n != null) {
                if (n.isDead) continue;
            } else
                continue;

            canCutRimRight = false;
            break;
        }

        // Nothing to do, start recursion:
        if (!canCutRimTop && !canCutRimBottom && !canCutRimLeft && !canCutRimRight) return false;

        int leftRimIdx = 0;
        int rightRimIdx = w;
        int bottomRimIdx = 0;
        int topRimIdx = h;

        if (canCutRimLeft) leftRimIdx++;
        if (canCutRimRight) rightRimIdx--;
        if (canCutRimBottom) bottomRimIdx++;
        if (canCutRimTop) topRimIdx--;

        int newW = rightRimIdx - leftRimIdx;
        int newH = topRimIdx - bottomRimIdx;


        //	System.err.println("CAN CUT !!! from "+w+","+h+"  to "+newW+","+newH);

        if (newW <= 0 || newH <= 0) return false; // Nothing left, brain will die anyway....

        Neuron[][] newSf = new Neuron[newH][newW];

        for (int y = 0; y < newH; y++) {
            Neuron[] nl = new Neuron[rightRimIdx - leftRimIdx];
            newSf[y] = nl;

            Neuron[] sfL = sf[bottomRimIdx + y];
            for (int x = 0; x < newW; x++) {
                nl[x] = sfL[leftRimIdx + x];
            }
        }

        // We don't use setVisionField(visionField), because the neurons are allready added
        b.visionFieldWidth = newW;
        b.visionFieldHeight = newH;
        b.visionField = newSf;

        // Next recursion:
        trimVisionFieldRim(b);
        return true;
    }

    @Override
    public void step() {
        super.step();

//		System.err.println("at "+Utils.getStacktrace());
        if (vfp.hasEverSeen) {
            if (vfp.w != 0) {
                changeEnergy(2);
            } else {
                changeEnergy(-1000);

                if (getEnergy() < 0) {
                    this.liveState = LiveState.Dead_LostLine;
                    return;
                }
            }

        } else {
            changeEnergy(1);
        }

        if (getEnergy() < 0) {
            this.liveState = LiveState.Dead_NoEnergy;
            //System.err.println("to less energy...");
        }
    }

    @Override
    public MutationParameters getMutationParameter() {
        return mp;
    }


}
