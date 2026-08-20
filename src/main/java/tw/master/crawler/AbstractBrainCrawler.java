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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import tw.master.brain.Brain;
import tw.master.brain.Neuron;
import tw.master.engine.Engine;
import tw.master.gui.way.WayVector;
import tw.master.mutation.MutationParameters;
import tw.master.visionfield.VisionFieldParams;


public abstract class AbstractBrainCrawler extends Crawler {

    private static final long serialVersionUID = 1L;

    private Brain             brain;

    private Neuron            toLeftNeuron;

    private Neuron            toRightNeuron;

    private Neuron            speedNeuron;

    private Neuron[][]        visionFieldNeurons;

    private int               visionFieldHeight;

    private int               visionFieldWidth;

    public VisionFieldParams  vfp;

    public WayVector          way;

    public MutationParameters mp;


    public AbstractBrainCrawler(Engine engine) {
        super(engine);
        fitnessParts = getInitialFitnessValues();
        mp = new MutationParameters();
        setBrain(createBrain());
    }

    /**
     * @param brain
     */
    public void setBrain(Brain brain) {
        this.brain = brain;

        way = new WayVector(32);

        toLeftNeuron = brain.getNeuron("toLeftNeuron");
        toRightNeuron = brain.getNeuron("toRightNeuron");
        speedNeuron = brain.getNeuron("speedNeuron");

        visionFieldNeurons = brain.getVisionField();

        if (visionFieldNeurons != null) {
            visionFieldHeight = visionFieldNeurons.length;
            visionFieldWidth = visionFieldNeurons[0].length;
        }

    }


    @Override
    public void setName(String name) {
        super.setName(name);
        brain.setName(name);
    }

    public abstract Brain createBrain();

    /**
     * @return
     */
    public Brain getBrain() {
        return brain;
    }

    private void updateVisionField() {

        if (vfp == null) {
            vfp = new VisionFieldParams(visionFieldWidth, visionFieldHeight);
            vfp.p = pos;
        }
        vfp.a = direction;

        engine.visionFieldHandler.updateVisionFieldData(vfp);

        // if (vfp.w != 0) System.out.println("w = "+vfp.w);

        for (int h = 0; h < visionFieldHeight; h++) {
            int x = visionFieldWidth - 1;
            for (int w = 0; w < visionFieldWidth; w++) {
                Neuron n = visionFieldNeurons[h][x];
                if (n != null && !n.isDead) n.setActivity(vfp.sf[h][w]);
                x--;
            }
        }

    }

    private int age = 0;

    @Override
    public synchronized void doSimulationStep() {

        try {


            if (toRightNeuron != null) toRightNeuron.wasTouched = false;
            if (toLeftNeuron != null) toLeftNeuron.wasTouched = false;
            if (speedNeuron != null) speedNeuron.wasTouched = false;

            updateVisionField();
            age++;
            brain.step();

            if (this == engine.controlledCrawler) return;

            final int LAST_ACTIVITY_LIMIT = 5000;

            if (toRightNeuron != null) {
                if (!toRightNeuron.wasTouched) {
                    // System.err.println("toRightNeuron disconnect");
                    // this.isDead = true;
                    this.liveState = LiveState.Dead_LostConnection;
                }
                if (toRightNeuron.getActiveAge() > LAST_ACTIVITY_LIMIT) {
                    // System.err.println("toRightNeuron dead");
                    // this.isDead = true;
                    this.liveState = LiveState.Dead_NoActivity;
                }

                if (!toLeftNeuron.wasTouched) {
                    // System.err.println("toLeftNeuron disconnect");
                    // this.isDead = true;
                    this.liveState = LiveState.Dead_LostConnection;
                }
                if (toLeftNeuron.getActiveAge() > LAST_ACTIVITY_LIMIT) {
                    // System.err.println("toLeftNeuron dead");
                    // this.isDead = true;
                    this.liveState = LiveState.Dead_NoActivity;
                }

                if (this.vfp.hasEverSeen
                        && age > 2000
                        &&
                        (way.getAverangeSum() > 150 || way.getAverangeStandardDeviation() < 3 /*10*//* || way.getAverangeStandardDeviation() > 22 */)) {

                    this.liveState = LiveState.Dead_Circling;
                }

                if (liveState != LiveState.Living) {
                    // System.err.println(System.currentTimeMillis()+"  Zombi...after "+age);
                    return;
                }

                float d = toRightNeuron.a - toLeftNeuron.a;
                // float s = speedNeuron.a - 0.3f; // Prefer movement...

                adjustAngle(d * 10);
                // setSpeed(s);

                way.addWay(getAngle());
                step();
            } else {
                if (liveState == LiveState.Living) {
                    step();
                }
            }
        } finally {
            callUpdateListeners();
        }
    }

    @Override
    public float getFitness() {

        // -----------------------------------------------------------------------------------------
        // Energy :  0...1,   1.0 is best
        // -----------------------------------------------------------------------------------------
        fitnessParts[ENERGY_FITNESS].fitness = super.getFitness();

        // -----------------------------------------------------------------------------------------
        // Map complexity 0...? to 0...5000 and then to 0...1
        // That means: The simplest brains will have the best value (==1.0)
        // -----------------------------------------------------------------------------------------
        float complexity = brain.getBrainComplexity();
        if (complexity > 5000) complexity = 5000;
        complexity = 5000 - complexity;
        complexity /= 5000;
        fitnessParts[COMPLEXITY_FITNESS].fitness = complexity;

        // -----------------------------------------------------------------------------------------
        // Age factor: We want older crawlers first, even if the young ones have an actually better
        // complexity and energyFactor.
        // -----------------------------------------------------------------------------------------
        float ageFactor = stepCount;
        if (ageFactor > 10000) ageFactor = 10000;
        ageFactor /= 10000;
        fitnessParts[AGE_FITNESS].fitness = ageFactor;

        // -----------------------------------------------------------------------------------------
        // Try to prefer best walks on energy (overlay) or image pixels (most exact)
        // -----------------------------------------------------------------------------------------
        float visitFitness = getVisitEnergy();
        if (visitFitness < 0) visitFitness = 0;
        fitnessParts[VISITENERGY_FITNESS].fitness = visitFitness;

        float stability = way.getAverangeSum();
        while (true) {
            if (stability > maxStability) maxStability = stability;
            if (stability < minStability) minStability = stability;

            if (stability > maxStability) continue; // Again... some thread was overwrite my value
            if (stability < minStability) continue;
            break;
        }

        float minS = minStability;
        float rangeS = maxStability - minS;

        stability -= minS;
        stability = 1 - stability / rangeS;

        fitnessParts[STABILITY_FITNESS].fitness = stability;


        float sum =
            fitnessParts[ENERGY_FITNESS].getValue() +
            fitnessParts[COMPLEXITY_FITNESS].getValue() +
            fitnessParts[AGE_FITNESS].getValue() +
            fitnessParts[VISITENERGY_FITNESS].getValue() +
            fitnessParts[STABILITY_FITNESS].getValue();

        fitnessParts[SUM_FITNESS].fitness = sum / fitnessImportance;

        return fitnessParts[SUM_FITNESS].fitness;
    }

    static volatile float    minStability        = 10000;

    static volatile float    maxStability        = 10;

    private static final int SUM_FITNESS         = 0;

    private static final int AGE_FITNESS         = 1;

    private static final int COMPLEXITY_FITNESS  = 2;

    private static final int ENERGY_FITNESS      = 3;

    private static final int VISITENERGY_FITNESS = 4;

    private static final int STABILITY_FITNESS   = 5;

    public FitnessValue[]    fitnessParts;

    private static float     fitnessImportance;

    public static FitnessValue[] getInitialFitnessValues() {
        FitnessValue[] ret = new FitnessValue[6];

        ret[SUM_FITNESS] = new FitnessValue("SUMMARY", "SUM", 1);
        ret[ENERGY_FITNESS] = new FitnessValue("EnergyFit:", "ef", 0.2f);
        ret[COMPLEXITY_FITNESS] = new FitnessValue("ComplexityFit:", "cf", 1f);
        ret[AGE_FITNESS] = new FitnessValue("AgeFit:", "af", 0.5f);
        ret[VISITENERGY_FITNESS] = new FitnessValue("VisitFit:", "vf", 2);
        ret[STABILITY_FITNESS] = new FitnessValue("Stabil.Fit:", "sf", 2);

        float fi = 0;

        for (int i = 1; i < ret.length; i++)
            fi += ret[i].importance;

        fitnessImportance = fi;

        return ret;
    }

    public FitnessValue[] getFitnessValues() {
        return fitnessParts;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        //System.err.println("Write BrainCrawler "+getName()+" "+liveState);
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        //System.err.println("Read BrainCrawler "+getName()+" done: "+liveState);
    }

}
