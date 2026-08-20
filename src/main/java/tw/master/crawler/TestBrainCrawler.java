/*
 *	This file is part of the EvolutionBrain project.
 *
 *	Copyright 2011 by Thomas Welsch (ttww@gmx.de)
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
 */

package tw.master.crawler;

import java.awt.Color;

import tw.gui.annotiations.GuiEnumAnnotation;
import tw.gui.annotiations.GuiInfoAnnotation;
import tw.master.brain.Brain;
import tw.master.brain.Neuron;
import tw.master.brain.Synapse;
import tw.master.brain.activation.ActivationFunctionFactory.ActivationFunction;
import tw.master.engine.Engine;

/**
 * This class demonstrate the basic usage for the AbstractBrainCrawler class.<p>
 * It implements a simple crawler with a very simple neuronal network
 * 
 * @author Thomas Welsch
 */
@GuiInfoAnnotation(
        title = "Test-BrainControls"
)
public class TestBrainCrawler extends AbstractBrainCrawler
{

    private static final long serialVersionUID = 1L;

    // ---------------------------------------------------------------------------------------------

    /**
     * Construct a new crawler.
     *
     * @param engine	Engine to use
     */
    public TestBrainCrawler(Engine engine)
    {
        super(engine);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Current activation function for all instances of this crawler.
     */
    private static ActivationFunction activationFunction = ActivationFunction.Signum;

    // ---------------------------------------------------------------------------------------------

    /**
     * Set the activation function for all instances of this crawler.
     *
     * @param which
     * 			The new activation function
     */
    @GuiEnumAnnotation(
            label       = "Activation function",
            enumClass   = ActivationFunction.class,
            tooltip     = "Defines the activation function for the neuron"
    )
    public static void setActivationFunction(ActivationFunction which)
    {
        activationFunction = which;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Get the current activation function.
     *
     * @return
     * 			current activation function
     */
    public static ActivationFunction getActivationFunction()
    {
        return activationFunction;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Create a neuronal network for controlling this crawler.
     *
     * @return
     * 			A new created brain for this crawler
     *
     * @see tw.master.crawler.AbstractBrainCrawler#createBrain()
     */
    @Override
    public Brain createBrain()
    {
        // -----------------------------------------------------------------------------------------
        // 1. Create the neuronal network and setting of general parameters:
        // -----------------------------------------------------------------------------------------
        Brain brain = new Brain();
        brain.setActivationFunction(activationFunction);

        Neuron.distanceFactor = 0; // We want ignore the distances in this test net

        // -----------------------------------------------------------------------------------------
        // 2. Define eyes (vision field):
        // -----------------------------------------------------------------------------------------
        Neuron leftEye  = new Neuron(-5, 0, 0, Color.YELLOW);
        Neuron rightEye = new Neuron(+5, 0, 0, Color.YELLOW);

        Neuron[][] visionField = {
                { null,     null,   null,   null },
                { null,     null,   null,   null },
                { leftEye,  null,   null,   rightEye },
        };
        brain.setVisionField(visionField); // Adding vision field neurons to brain

        // -----------------------------------------------------------------------------------------
        // 3. Define motor neurons:
        // -----------------------------------------------------------------------------------------
        Neuron toLeftNeuron  = new Neuron(-5, 0, 10, Color.RED);
        Neuron toRightNeuron = new Neuron( 5, 0, 10, Color.GREEN);

        brain.addNeuron("toLeftNeuron",  toLeftNeuron);
        brain.addNeuron("toRightNeuron", toRightNeuron);

        // -----------------------------------------------------------------------------------------
        // 4. Define simple synapses:
        // -----------------------------------------------------------------------------------------
        new Synapse(leftEye,  toLeftNeuron,  1f);
        new Synapse(rightEye, toRightNeuron, 1f);

        return brain;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Doing one simulation step.
     *
     * @see tw.master.crawler.Crawler#step()
     */
    @Override
    public void step()
    {
        // -----------------------------------------------------------------------------------------
        // 1. Move the crawler:
        // -----------------------------------------------------------------------------------------
        super.step();

        // -----------------------------------------------------------------------------------------
        // 2. Adjust energy:
        // -----------------------------------------------------------------------------------------
        if (vfp.hasEverSeen) {  // Did we ever see a signal with our visionField ?
            if (vfp.w != 0) {   // Did we now  see a signal with our visionField ?
                changeEnergy(2);
            }
            else {
                changeEnergy(-100); // We lost the line,
                if (getEnergy() < 0) {
                    this.liveState = LiveState.Dead_LostLine;
                }
            }
        }
        else {
            changeEnergy(-1);
        }

        if (getEnergy() < 0) {
            this.liveState = LiveState.Dead_LostLine;
        }
    }
} // end of class



/*
public Brain xxcreateBrain() {
	Brain brain = new Brain(getName());

	brain.setActivationFunction(activationFunction);

	final int XM = 1;
	final int YM = 5;

	final int SPACE_X = 3;
	final int SPACE_Y = 3;

	Color[] colors = new Color[YM];
	for (int y=0; y<YM; y++) colors[y] = Rnd.rndColor(200);

	int ic=10;

	for (int x=0; x<XM; x++) {
		float xf = x * SPACE_X - (XM * SPACE_X - SPACE_X) / 2f;

		Neuron firstNeuron = new Neuron(xf,0,0,Color.RED);

		firstNeuron.autocycleInput = (ic++)*5;
		firstNeuron.immortally = true;
		brain.addNeuron(firstNeuron);

		for (int y=1; y<YM; y++) {
			float yf = y * SPACE_Y;

			Neuron n = new Neuron(xf,yf,0,colors[y]);
			n.immortally = true;
			brain.addNeuron(n);

			new Synapse(firstNeuron,n,1.0f);
			//firstNeuron.addSynapse(syn);

			firstNeuron = n;
		}
	}


//	int ic=10;
//	for (int x=-0; x<=5; x+=5) {
//		Neuron ni = new Neuron(x,5,0,Color.RED);
//		ni.autocycleInput = (ic++)*5;
//
//		Neuron no = new Neuron(x,10,0,Color.YELLOW);
//		Neuron noo= new Neuron(x,20,0,Color.GREEN);
//
//		new Synapse(ni,no,0.8f);
//		new Synapse(no,noo,0.8f);
//
//		ni.immortally = true;
//		no.immortally = true;
//		noo.immortally = true;
//
//		brain.addNeuron(ni);
//		brain.addNeuron(no);
//		brain.addNeuron(noo);
//	}

	//System.err.println("Brain with "+brain.getNeuronCount());
	return brain;
}
 */
