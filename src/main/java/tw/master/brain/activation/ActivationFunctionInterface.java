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

package tw.master.brain.activation;

import javax.swing.JPanel;

/**
 * This interface is usesd for implementing own activation functions.
 *
 * @author Thomas Welsch
 */
public interface ActivationFunctionInterface
{
    /**
     * Returns the activation for the neuron based on the effectiv input.
     *
     * @param actualActivity
     *              old actual activity of the neuron without the input. Range 0..+1
     * @param effInput
     *              Input in the range of -1..0..+1
     * @param numberOfInputs
     * 				Number of synaptic intput
     *
     * @return	Output between 0..1
     */
    public float getActivation(final float actualActivity, final float effInput, int numberOfInputs);

    // ---------------------------------------------------------------------------------------------
    // Internally used:
    // ---------------------------------------------------------------------------------------------
    public String getName();
    public int getSteps();
    public float getStepDivisor();
    public float getMaxStep();
    public float getMinValue();
    public float getMaxValue();
    public float getMinResult();
    public float getMaxResult();
    public JPanel getVisualPanel();
    public void freeVisualPanel(JPanel oldVisualPanel);

}
