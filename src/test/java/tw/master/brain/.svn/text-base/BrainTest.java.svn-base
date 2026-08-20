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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 *
 *
 * @author Thomas Welsch
 *
 */
public class BrainTest {

    private static Brain brain;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        brain = new Brain("Test-Brain");
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception { }

    /**
     * Test method for {@link tw.master.brain.Brain#Brain()}.
     */
    @Test
    public void testBrain() {
        new Brain();
    }

    /**
     * Test method for {@link tw.master.brain.Brain#Brain(java.lang.String)}.
     */
    @Test
    public void testBrainString() {
        Brain test = new Brain("xxx");
        Assert.assertTrue("Name", "xxx".equals(test.getName()));
        test = null;
    }

    /**
     * Test method for {@link tw.master.brain.Brain#setName(java.lang.String)}.
     * Test method for {@link tw.master.brain.Brain#getName()}.
     */
    @Test
    public void testGetSetName() {

        brain.setName("yyy");
        Assert.assertTrue("Name", "yyy".equals(brain.getName()));
        brain.setName("Test-Brain");
        Assert.assertTrue("Name", "Test-Brain".equals(brain.getName()));
    }

    /**
     * Test method for {@link tw.master.brain.Brain#freeNeuronsPotentials()}.
     */
    @Test
    public void testFreeNeurons() {
        brain.freeNeuronsPotentials();
    }

    /**
     * Test method for {@link tw.master.brain.Brain#addNeuronCluster(tw.master.brain.NeuronCluster)}.
     */
    @Test
    public void testAddNeuronCluster() {
        int nn = brain.neurons.size();

        Neuron[] na = { new Neuron("n1", 0, 0, 0, Color.YELLOW), new Neuron("n2", 0, 0, 1, Color.RED), };
        new NeuronCluster("Test-Cluster", na).addToBrain(brain);

        Assert.assertEquals("Cluster", nn + 2, brain.neurons.size());
    }

    /**
     * Test method for {@link tw.master.brain.Brain#addNeuron(java.lang.String, tw.master.brain.Neuron)}.
     * Test method for {@link tw.master.brain.Brain#getNeuron(java.lang.String)}.
     */
    @Test
    public void testAddNeuronStringNeuron() {
        Neuron n = new Neuron(0, 0, 10);
        brain.addNeuron("0,0,10", n);

        Neuron ng = brain.getNeuron("0,0,10");

        Assert.assertEquals(n, ng);
    }

    /**
     * Test method for {@link tw.master.brain.Brain#addNeuron(tw.master.brain.Neuron)}.
     */
    @Test
    public void testAddNeuronNeuron() {
        int nn = brain.neurons.size();
        brain.addNeuron(new Neuron(0, 10, 10));
        Assert.assertEquals("Cluster", nn + 1, brain.neurons.size());
    }

    /**
     * Test method for {@link tw.master.brain.Brain#setActivationFunction(tw.master.brain.activation.ActivationFunctionFactory.ActivationFunction)}.
     */
    @Test
    public void testSetActivationFunction() {
//        fail("Not yet implemented");
    }

    /**
     * Test method for {@link tw.master.brain.Brain#step()}.
     */
    @Test
    public void testStep() {
//        fail("Not yet implemented");
    }

    /**
     * Test method for {@link tw.master.brain.Brain#getBrainComplexity()}.
     */
    @Test
    public void testGetBrainComplexity() {
        float bc = brain.getBrainComplexity();
        brain.addNeuron(new Neuron(0, 10, 10));
        Assert.assertTrue("Complexity", brain.getBrainComplexity() > bc);
    }

    /**
     * Test method for {@link tw.master.brain.Brain#getNeuronCount()}.
     */
    @Test
    public void testGetNeuronCount() {
        int nn = brain.neurons.size();
        Assert.assertEquals("Size", brain.getNeuronCount(), nn);
    }

    /**
     * Test method for {@link tw.master.brain.Brain#getSynapsesCount()}.
     */
    @Test
    public void testGetSynapsesCount() {
        long sc = brain.getSynapsesCount();
        Neuron n1 = new Neuron(10, 0, 0);
        Neuron n2 = new Neuron(10, 10, 0);
        brain.addNeuron(n1);
        brain.addNeuron(n2);

        new Synapse(n1, n2, 0);

        Assert.assertEquals("Synapses", sc + 1, brain.getSynapsesCount());
    }

    /**
     * Test method for {@link tw.master.brain.Brain#setVisionField(tw.master.brain.Neuron[][])}.
     */
    @Test
    public void testSetVisionField() {
//        fail("Not yet implemented");
    }

    /**
     * Test method for {@link tw.master.brain.Brain#getVisionField()}.
     */
    @Test
    public void testGetVisionField() {
//        fail("Not yet implemented");
    }

}
