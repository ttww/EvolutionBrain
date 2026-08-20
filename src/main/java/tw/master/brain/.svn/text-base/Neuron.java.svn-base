/*
 * This file is part of the EvolutionBrain project.
 *
 * Copyright (c) 2011 by Thomas Welsch (ttww@gmx.de). All rights reserved.
 *
 * EvolutionBrain is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EvolutionBrain is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with EvolutionBrain. If not, see <http://www.gnu.org/licenses/>.
 */

package tw.master.brain;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

import tw.master.brain.activation.ActivationFunctionInterface;
import tw.master.utils.Utils;



/**
 * This class implements a simulated neurons and keep track of the synaptic conections.<p>
 * 
 *
 * @author Thomas Welsch
 */
public class Neuron implements Serializable {

    private static final long   serialVersionUID      = 1L;

    /**
     *  Handled neurons counter.
     */
    public static volatile long nc;

    /**
     *  Handled synapses counter.
     */
    public static volatile long sc;

    /**
     * Set the factor how fast a signal is decrease to zero.
     */
    public static float         selfStimulationFactor = 0.5f;

    /**
     * Factor for distance. If 0, then we are using zero length ways between neurons and don't
     * respect the position in space.
     */
    public static float         distanceFactor        = 1;

    public static float         rechargePerRun        = 1.3f;

    public static float         maxChargeValue        = 20;

    /**
     * If true, this neuron is never pruned from the brain...
     */
    public boolean              immortally;

    public int                  isInput;

    public int                  autocycleInput;                                  // Automatically switched each n steps

    private long                run;

    /**
     * X coordinate in space.
     */
    float                       x;

    /**
     * Y coordinate in space.
     */
    float                       y;

    /**
     * Z coordinate in space.
     */
    float                       z;

    public float                a;


    private float               charge                = maxChargeValue;

    public Color                c;

    public String               name;

    public NeuronCluster        cluster;

    long                        wasActive;

    public boolean              wasTouched;

    public boolean              isDead                = false;

    ArrayList<Synapse>          synapses              = new ArrayList<Synapse>();

    /**
     * Is this neuron added to a brain ? This flag is used for simplify adding neurons/clusters
     */
    boolean                     inBrainList;


    // ---------------------------------------------------------------------------------------------

    /**
     * @param x
     * @param y
     * @param z
     */
    public Neuron(float x, float y, float z) {
        this(null, x, y, z, Color.YELLOW);
    }

    // ---------------------------------------------------------------------------------------------

    public Neuron(String name, float x, float y, float z, Color c) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.c = c;
    }

    // ---------------------------------------------------------------------------------------------

    public Neuron(float x, float y, float z, Color c) {
        this(null, x, y, z, c);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "N[a=" + a + ",dead=" + isDead + "]";
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Add the synapse to this neuron.
     * This method should only called from the Synapse() constructor.
     *
     * @param syn
     */
    void addSynapse(Synapse syn) {
        synapses.add(syn);
    }

    // ---------------------------------------------------------------------------------------------

    public long getRun() {
        return run;
    }

    // ---------------------------------------------------------------------------------------------

    float           lasta;

    private int     ic  = 0;

    private boolean icb = false;

    public final void fire() {

//		float energieUsed = 0;

        run++;
        nc++;

        if (autocycleInput > 0) {
            ic++;
            if (ic > autocycleInput) {
                ic = 0;
                icb = !icb;
            }

            if (icb) a = 1;
            else
                a = 0;
//			System.err.println("Autocycle = "+a+ "  ic = "+ic+"  aci = "+autocycleInput);
        }

        if (Math.abs(a - lasta) > 0.01) {
            wasActive = run;
            lasta = a;
        }


        int cleanupSynapses = 0;

        // -----------------------------------------------------------------------------------------
        // Shortcut: If we want to send 0 potentials, we only do a fake sending, but touching the
        // destination neuron. We do not increase the synapse connection count.
        // -----------------------------------------------------------------------------------------
        if (a < 0.001) {
            a = 0;

            for (Synapse syn : synapses) {
                Neuron dest = syn.destinationNeuron;

                if (dest.isDead) {
                    cleanupSynapses++;
                }
                else {
                    dest.wasTouched = true;
                }
            }

            // If some synapses points to dead neurons, these are cleaned....
            if (cleanupSynapses > 0) {
                cleanupSynapses(cleanupSynapses);
            }

            charge += 0.1;
            if (charge > maxChargeValue) charge = maxChargeValue;

            return;
        }


//		charge -= a;
//		if (charge < 0) {
//			a += charge;
//			charge = 0;
//		}

        // -----------------------------------------------------------------------------------------
        // We have to send out potential....
        // -----------------------------------------------------------------------------------------
        for (Synapse syn : synapses) {
            Neuron dest = syn.destinationNeuron;

            if (dest.isDead) {
                cleanupSynapses++;
            }
            else {
                float e = a * syn.w;
//				if (e > 0) energieUsed += e; else energieUsed -= e;

                //System.err.println("Send "+e);

                // ---------------------------------------------------------------------------------
                // Ignore or pay attention to the distance. We directly compare to 0 or 1, even if
                // this a float compare :-) and hope that nobode initalize this to 0.000001 for disabling.
                // So we gain some speed :-)
                // Minimum distance is 1, that means the signal reache the destination neuron with
                // the next simulation step.
                // ---------------------------------------------------------------------------------
                int dist = syn.distance;

                if (distanceFactor == 0) {
                    dist = 1;
                }
                else {
                    if (distanceFactor != 1) {
                        dist = (int) (0.5f + dist * distanceFactor);
                        if (dist < 1) dist = 1;
                    }
                }
                dest.addSignal(this, dist, e);
                dest.wasTouched = true;
            }
        }

        // Reduce the neuron potential about some factor.
        a = a * selfStimulationFactor;

        //System.err.println(a);
        // If some synapses points to dead neurons, these are cleaned....
        if (cleanupSynapses > 0) {
            cleanupSynapses(cleanupSynapses);
        }

        sc += synapses.size();

        charge += 0.1;
        if (charge > maxChargeValue) charge = maxChargeValue;

        //a = a * 0.5f;
        //if (a > 1) a = 1;

//		return energieUsed;
    }

    private void cleanupSynapses(int cleanupSynapses) {
        ArrayList<Synapse> deadSynapses = new ArrayList<Synapse>(cleanupSynapses);
        ArrayList<Synapse> lifeSynapses = new ArrayList<Synapse>(synapses.size() - cleanupSynapses);

        for (Synapse syn : synapses) {
            if (syn.destinationNeuron.isDead) {
                deadSynapses.add(syn);
            }
            else {
                lifeSynapses.add(syn);
            }
        }

        //System.err.println("Cleanup "+deadSynapses.size()+" dead synapses, keep "+lifeSynapses.size());

        synapses = lifeSynapses;

        if (synapses.size() == 0 && name == null && isInput == 0) {
            isDead = true;
            //System.err.println("Last synapse died...");
        }

    }

    public final long getActiveAge() {
        return run - wasActive;
    }

//	float aa;
//	int		ac;

//	static ArrayList<PotRunArray> freeOtRunArrays = new ArrayList<PotRunArray>();

    public void preparePotentialsForReuse() {
        if (potentials == null) return;
        for (int i = 0; i < potentials.buf.length; i++) {
            PotRunArray old = potentials.buf[i];
            if (old == null) continue;

            potentials.buf[i] = null;
            PotRunArray.freeInstance(old);
        }
        potentials = null;
    }

    final boolean               DEBUG      = false;

    FloatingArray               potentials = new FloatingArray();

    /**
     * Currently used activation function. We use this without get/setter for keeping the inner
     * function calls without parameters....
     */
    ActivationFunctionInterface activationFunction;

    private void addSignal(Neuron from, int distance, float af) {
//		System.err.println("Dist = "+distance+"  af = "+af);

        synchronized (potentials) {
            PotRunArray pra = null;

            pra = potentials.get(run + distance);

            if (pra == null) {
//				synchronized (freeOtRunArrays) {
//					if (freeOtRunArrays.size() != 0) {
//						if (DEBUG) System.err.println("Get PRA from buffer ("+freeOtRunArrays.size()+")");
//						pra = freeOtRunArrays.remove(freeOtRunArrays.size()-1);
//						pra.faFill = 0;
//					}
//				}
                if (pra == null) {
                    if (DEBUG) System.err.println(System.currentTimeMillis() + ": New PRA");

//					pra = new PotRunArray();
                    pra = PotRunArray.getInstance();
                }

                potentials.set(run + distance, pra);
            }

            if (pra.faFill >= pra.fa.length) {
                pra.makeSpace();
            }
            pra.fa[pra.faFill] = af;
            pra.fan[pra.faFill] = from;
            pra.faFill++;
            pra.forRun = run + distance;

        }
//		aa += af;
//		ac ++;
    }

    public final void collect() {

        if (run < potentials.baseIndex)
            throw new IllegalArgumentException("Bad Run " + run + " / " + potentials.baseIndex + " !");
        PotRunArray pra = potentials.get(run);
        if (pra != null) {
            if (DEBUG)
                System.err.println("Found PRA for run " + run + "  fill = " + pra.faFill + " act run = " + run
                        + " pra_run = " + pra.forRun);

            float aa = 0;
            for (int i = 0; i < pra.faFill; i++) {
                aa += pra.fa[i];
                //System.err.println(i+" pra = "+pra.fa[i]);
            }

            a = activationFunction.getActivation(a, aa, pra.faFill);

//			synchronized (freeOtRunArrays) {
//				freeOtRunArrays.add(pra);
//			}

        }
        potentials.slip();

    }

    public final void setActivity(float a) {
        if (a > 1) {
            System.err.println(Utils.getStacktrace());
            throw new IllegalArgumentException("Bad a in sa ! " + a);
        }
        this.a = a;
    }

    public final Color getColor() {
        if (isDead) { return Color.YELLOW; }
        if (c != null) return c;

        if (cluster != null && cluster.clusterColor != null) return cluster.clusterColor;

        return Color.ORANGE;
    }

//	public float getActivity() {
//		return a;
//	}
//
//	public Color getColor() {
//		return c;
//	}

//	public static void main(String[] args) {
//		Neuron n = new Neuron(0,0,0);
//
//		int r = 0;
//		for (int i=0; i<1000000000; i++) {
//			n.potentials.get(r);
//
//			for (int j=0; j<5; j++) {
//				int rnd = Rnd.rnd(r,r+4);
//				if (rnd < r) {
//					System.err.println("r = "+r+"   --> rnd = "+rnd);
//					throw new IllegalArgumentException("BAD RND !");
//				}
//				n.potentials.set(rnd, null);
//			}
//
//			n.potentials.slip();
//			r++;
//		}
//	}
}
