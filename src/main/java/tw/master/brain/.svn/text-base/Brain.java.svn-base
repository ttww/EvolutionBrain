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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import tw.master.brain.activation.ActivationFunctionFactory;
import tw.master.brain.activation.ActivationFunctionFactory.ActivationFunction;
import tw.master.brain.activation.ActivationFunctionInterface;
import tw.master.gl3d.World3dDrawInterface;
import tw.master.gl3d.World3dInterface;
import tw.master.utils.SoundUtils;


public class Brain implements World3dDrawInterface, Serializable {

    private static final long       serialVersionUID   = 1L;

    public ArrayList<Neuron>        neurons            = new ArrayList<Neuron>(50);

    public Neuron[][]               visionField;

    public int                      visionFieldWidth;

    public int                      visionFieldHeight;

    private String                  name;

    private final HashMap<String, Neuron> mappedNeurons      = new HashMap<String, Neuron>();

    private final List<NeuronCluster>     neuronClusterList     = new LinkedList<NeuronCluster>();

    ActivationFunctionInterface     activationFunction = ActivationFunctionFactory.getActivationFunction(ActivationFunction.Signum);

    // --------------------------------------------------------------------------------------------

    public Brain() { }

    public Brain(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * For avoiding GC if the brain is dead, you can put the used potentials back in a global pool.
     */
    public void freeNeuronsPotentials() {
        for (Neuron n : neurons) {
            n.preparePotentialsForReuse();
        }
    }

    public void addNeuronCluster(NeuronCluster neuronCluster) {
        if (neuronCluster.inClusterBrainList) return;
        neuronCluster.inClusterBrainList = true;

        neuronClusterList.add(neuronCluster);

        Neuron[] na = neuronCluster.getNeurons();

        for (Neuron n : na) {
            addNeuron(n);
        }
    }

    /**
     * @param string
     * @return
     */
    public NeuronCluster getCluster(String wantedClusterName) {
        for (NeuronCluster nc : neuronClusterList) {
            if (wantedClusterName.equals(nc.name)) return nc;
        }
        return null;
    }



    public void addNeuron(String neuronName, Neuron n) {
        if (n.inBrainList) return;
        n.inBrainList = true;

        n.name = neuronName;

        if (n.name != null && n.name.length() != 0) {
            mappedNeurons.put(neuronName, n);
            //System.err.println("Name = "+n.name);
        }
        neurons.add(n);

        n.activationFunction = activationFunction;

//		size = null;
    }

    public void addNeuron(Neuron n) {
        addNeuron(n.name, n);
    }

    public Neuron getNeuron(String neuronName) {
        return mappedNeurons.get(neuronName);
    }

//	private long step = 0;

    private final long MAX_INACTIVE_AGE = 2000;

    private long       actBrainSynCount = 0;

    public void setActivationFunction(ActivationFunction which) {
        this.activationFunction = ActivationFunctionFactory.getActivationFunction(which);

        for (Neuron n : neurons)
            n.activationFunction = activationFunction;

    }

    public void step() {

        boolean needCleanup = false;

        long newActBrainSynCount = 0;

        for (Neuron n : neurons) {
            n.fire();
            newActBrainSynCount += n.synapses.size();

            n.collect();
            if (!needCleanup && checkDead(n)) needCleanup = true;
        }

        actBrainSynCount = newActBrainSynCount;

        if (needCleanup) cleanupBrain();
    }


    public float getBrainComplexity() {
        return neurons.size() * (actBrainSynCount + 1);
    }

    private boolean checkDead(Neuron n) {
        if (n.immortally) return false;
        if (n.isDead) return true;
        if (n.isInput == 0 && n.name == null && (n.getActiveAge() > MAX_INACTIVE_AGE || n.synapses.size() == 0))
            return true;
        return false;

    }

    private void cleanupBrain() {
        ArrayList<Neuron> toDeleteList = new ArrayList<Neuron>();
        ArrayList<Neuron> toKeepList = new ArrayList<Neuron>();

        for (Neuron n : neurons) {
            if (checkDead(n)) {
                n.isDead = true; // Mark it as dead, all synapses point to it will degenerate
                toDeleteList.add(n);
                //System.err.println("Prune neuron "+n);
            } else {
                toKeepList.add(n);
            }
        }

        //System.err.println("Cleanup "+toDeleteList.size()+" dead neurons, keep "+toKeepList.size());

        neurons = toKeepList;

        // -----------------------------------------------------------------------------------------
        // Remove all references to the dead neurons....
        // -----------------------------------------------------------------------------------------
        for (Neuron n : toDeleteList) {

            for (int w = 0; w < visionFieldWidth; w++) {
                for (int h = 0; h < visionFieldHeight; h++) {
                    if (visionField[h][w] == n) {
                        visionField[h][w] = null;
                        w = visionFieldWidth; // bad boy....
                        break;
                    }
                }
            }

        }


    }

    public int getNeuronCount() {
        return neurons.size();
    }

    public long getSynapsesCount() {

        if (actBrainSynCount == 0) { // Request before first run....
            long newActBrainSynCount = 0;
            for (Neuron n : neurons)
                newActBrainSynCount += n.synapses.size();
            actBrainSynCount = newActBrainSynCount;
        }
        return actBrainSynCount;
    }


    private static Color synColor         = new Color(130, 130, 130, 180);

    private static Color synColorPos      = new Color(0, 200, 0, 180);

    private static Color synColorNeg      = new Color(200, 0, 10, 180);

    private static Color signalColPos     = new Color(0, 255, 0, 255);

    private static Color signalColNeg     = new Color(255, 0, 0, 255);

    private static float synapseLineWidth = 1;

    /**
     * @return the synapseLineWidth
     */
    public static float getSynapseLineWidth() {
        return synapseLineWidth;
    }

    /**
     * @param synapseLineWidth the synapseLineWidth to set
     */
    public static void setSynapseLineWidth(float synapseLineWidth) {
        Brain.synapseLineWidth = synapseLineWidth;
        if (Brain.synapseLineWidth < 0.1f) {
            Brain.synapseLineWidth = 0.1f;
            SoundUtils.playTink();
        }
        if (Brain.synapseLineWidth > 10f) {
            Brain.synapseLineWidth = 10f;
            SoundUtils.playTink();
        }
        // System.err.println("set to "+synapseLineWidth);
    }


    @Override
    public void draw(World3dInterface g3d) {

        float oldWidth = g3d.setLineWidth(1);

        float w = visionFieldHeight + 1;
        float wl = w - 0.75f;
        float as = 0.2f;


        g3d.setColor(Color.YELLOW);
        g3d.drawLine(0, 0, 0, 0, w, 0);
        g3d.drawLine(0, w, 0, -as, wl, 0);
        g3d.drawLine(0, w, 0, +as, wl, 0);
        g3d.drawLine(0, w, 0, 0, wl, -as);
        g3d.drawLine(0, w, 0, 0, wl, +as);


        w = visionFieldWidth / 2f + 0.5f;
        g3d.drawText(+w, 0, 0, "R");
        g3d.drawText(-w, 0, 0, "L");

        if (false) {
            w = 10;
            g3d.setColor(Color.RED);
            g3d.drawLine(0, 0, 0, w, 0, 0);
            g3d.drawText(w, 0, 0, "X");

            g3d.setColor(Color.GREEN);
            g3d.drawLine(0, 0, 0, 0, w, 0);
            g3d.drawText(0, w, 0, "Y");

            g3d.setColor(Color.BLUE);
            g3d.drawLine(0, 0, 0, 0, 0, w);
            g3d.drawText(0, 0, w, "Z");
        }

        g3d.setLineWidth(oldWidth);


//		System.err.println("ns = "+neurons.size());
        for (Neuron n : neurons) {
            float x = n.x;
            float y = n.y;
            float z = n.z;

            if (drawSynapses) {
                if (!drawColoredSynpses) g3d.setColor(synColor);
                g3d.setLineWidth(synapseLineWidth);
                for (Synapse syns : n.synapses) {
                    //if (Rnd.rnd(0,40) == 0)
                    if (drawColoredSynpses) {
                        if (syns.w >= 0) g3d.setColor(synColorPos);
                        else
                            g3d.setColor(synColorNeg);
                    }
                    g3d.drawLine(x, y, z, syns.destinationNeuron.x, syns.destinationNeuron.y, syns.destinationNeuron.z);
                } // for

                g3d.setLineWidth(synapseLineWidth);
            }

            g3d.setLineWidth(1);

            if (drawSignals) {

                for (int runDist = 0; runDist < 30; runDist++) {
                    FloatingArray pots = n.potentials;
                    if (pots == null) return;
                    PotRunArray pra = pots.get(n.getRun() + runDist);
                    if (pra == null) continue;
                    if (pra.fan == null) continue; // Old class


                    for (int source = 0; source < pra.faFill; source++) {
                        float pot = pra.fa[source];

                        if (pot > 0) {
                            g3d.setColor(signalColPos);
                        } else {
                            g3d.setColor(signalColNeg);
                        }

                        Neuron sourceNeuron = pra.fan[source];

                        float xs = sourceNeuron.x;
                        float ys = sourceNeuron.y;
                        float zs = sourceNeuron.z;

                        float dx = xs - x;
                        float dy = ys - y;
                        float dz = zs - z;

                        float distance = (float) Math.sqrt((dx * dx + dy * dy + dz * dz));

                        float xp, yp, zp;

                        if (runDist != 0) {
                            xp = x + dx / (distance / runDist);
                            yp = y + dy / (distance / runDist);
                            zp = z + dz / (distance / runDist);
                        } else {
                            xp = x;
                            yp = y;
                            zp = z;
                        }

//					System.err.println("Dist   = "+runDist);
//					System.err.println("Pra n  = "+source);
//					System.err.println("Act    = "+x+","+y+","+z);
//					System.err.println("Source = "+xs+","+ys+","+zs);
//					System.err.println("Dist   = "+dx+","+dy+","+dz);

                        //g3d.drawSphere(xp, yp, zp, 0.03f);
                        g3d.drawSphere(xp, yp, zp, pot * 0.1f);

                    }

                }
            }

            float a = 9 * n.a;
            a = n.a;
//			if (a<0) a = 0;
//			if (a<1) a = 1;
//			if (a != 1) System.err.println(a);
            Color c = n.getColor();
            if (n.isDead) {
                a = 3;
                g3d.setColor(Color.RED);
            } else {
                g3d.setColor(c);
            }
            //System.err.println("x = "+x+"  y = "+y);
            g3d.drawSphere(x, y, z, a);

            if (n.name != null) {

                g3d.drawText(x - name.length() * 0.5f, y - 1, z, n.name);
            }

        }

        if (drawCluster) {
            for (NeuronCluster nc : neuronClusterList) {
                Color c = nc.clusterColor;
                g3d.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 60));

                //			System.err.println(nc.name+"  S = "+nc.size);
                float b = 0.1f;
                g3d.drawBox(
                        nc.brainSize.minX - b, nc.brainSize.maxX + b,
                        nc.brainSize.minY - b, nc.brainSize.maxY + b,
                        nc.brainSize.minZ - b, nc.brainSize.maxZ + b
                );

            }
        }


        //g3d.drawBox(-5,5,-5,5,-5,5);
    }

    public static boolean drawSynapses       = true;

    public static boolean drawColoredSynpses = true;

    public static boolean drawSignals        = false;

    public static boolean drawCluster        = true;

    @Override
    public void drawBackgound(World3dInterface g3d, float w, float h) { }


    /**
     * Setting the vision field (==eye) for this brain.
     * 
     * @param visionField   2-dimensional field with neurons. These will be added to the brain
     *                      and the isInput flag is set.
     */
    public void setVisionField(Neuron[][] visionField) {
        this.visionField  = visionField;

        visionFieldHeight = visionField.length;
        visionFieldWidth  = visionField[0].length;

        for (int y = 0; y < visionFieldHeight; y++) {
            for (int x = 0; x < visionFieldWidth; x++) {
                Neuron n = visionField[y][x];

                if (n == null) continue;
                if (n.inBrainList) continue;

                neurons.add(n);
                n.activationFunction = activationFunction;
                n.inBrainList        = true;
                n.isInput            = 1;
            }
        }
    }

    /**
     * Returns the currently set visionFiled.
     * 
     * @return  2-dimensional array if neurons, or null
     */
    public Neuron[][] getVisionField() {
        return visionField;
    }



//	private void writeObject(ObjectOutputStream out) throws IOException {
//		System.err.println("Write Brain "+name);
//		out.defaultWriteObject();
//		System.err.println("Write done");
//	}
//	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
//		System.err.println("Read Brain");
//		in.defaultReadObject();
//		System.err.println("Read "+name+" done");
//	}

}
