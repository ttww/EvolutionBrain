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

import java.io.Serializable;


public class Synapse extends
Brain implements Serializable {

    private static final long serialVersionUID = 1L;

    public float              w;

    public Neuron             destinationNeuron;

    public int                distance;


    /**
     * @param destinationNeuron
     * @param w
     */
    public Synapse(Neuron srcNeuron, Neuron destinationNeuron, float w) {
        this.destinationNeuron = destinationNeuron;
        this.w = w;

        float dx = srcNeuron.x - destinationNeuron.x;
        float dy = srcNeuron.y - destinationNeuron.y;
        float dz = srcNeuron.z - destinationNeuron.z;

        // Calculate distance to destination neuron for putring into the right distance list
        distance = (int) (Math.sqrt(dx * dx + dy * dy + dz * dz) + 0.5);

        if (Neuron.distanceFactor != 0 && distance > FloatingArray.MAX_SYNAPSE_LEN) {
            System.err.println("Adjust potential run array from " + FloatingArray.MAX_SYNAPSE_LEN + " to " + distance);
            FloatingArray.MAX_SYNAPSE_LEN = distance;
        }
        srcNeuron.addSynapse(this);
    }
}
