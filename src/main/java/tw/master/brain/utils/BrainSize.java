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

package tw.master.brain.utils;

import java.io.Serializable;

/**
 * @author Thomas Welsch
 * 
 */
public class BrainSize implements Serializable {
    private static final long serialVersionUID = 1L;

    public float minX = +1000000;
    public float minY = +1000000;
    public float minZ = +1000000;

    public float maxX = -1000000;
    public float maxY = -1000000;
    public float maxZ = -1000000;

    public void pos(float x, float y, float z) {
        if (x < minX)	minX = x;
        if (y < minY)	minY = y;
        if (z < minZ)	minZ = z;

        if (x > maxX)	maxX = x;
        if (y > maxY)	maxY = y;
        if (z > maxZ)	maxZ = z;
    }

    @Override
    @SuppressWarnings("boxing")
    public String toString() {
        return String.format("x(%2.1f..%2.1f) y(%2.1f..%2.1f) z(%2.1f..%2.1f)",
                minX, maxX, minY, maxY, minZ, maxZ);
    }

}
