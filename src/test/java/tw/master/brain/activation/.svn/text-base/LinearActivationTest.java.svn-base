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

package tw.master.brain.activation;

import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Thomas Welsch
 *
 */
public class LinearActivationTest {

    /**
     * Test method for {@link tw.master.brain.activation.AbstractActivationFunction#getActivation(float, float, int)}.
     */
    @Test
    public void testGetActivation() {
        ActivationFunctionInterface af = LinearActivation.getActivationFunction();

//        System.out.println(af.getActivation(1,1,1));
//        System.out.println(af.getActivation(1,1,2));
//        System.out.println(af.getActivation(1,2,1));
//
//        System.out.println(af.getActivation(-1,1,1));
//        System.out.println(af.getActivation(-1,1,2));
//        System.out.println(af.getActivation(-1,2,1));
//
//        System.out.println(af.getActivation(0,1,1));
//        System.out.println(af.getActivation(0,1,2));
//        System.out.println(af.getActivation(0,2,1));

        Assert.assertTrue(Math.abs(af.getActivation(1, 1, 1) - 1) < 0.001);
        Assert.assertTrue(Math.abs(af.getActivation(1, 1, 2) - 1) < 0.001);
        Assert.assertTrue(Math.abs(af.getActivation(1, 2, 1) - 1) < 0.001);

        Assert.assertTrue(Math.abs(af.getActivation(-1, 1, 1) - 0.001) < 0.001);
        Assert.assertTrue(Math.abs(af.getActivation(-1, 1, 2) - 0.001) < 0.001);
        Assert.assertTrue(Math.abs(af.getActivation(-1, 2, 1) - 1) < 0.001);

        Assert.assertTrue(Math.abs(af.getActivation(0, 1, 1) - 1) < 0.001);
        Assert.assertTrue(Math.abs(af.getActivation(0, 1, 2) - 0.501) < 0.001);
        Assert.assertTrue(Math.abs(af.getActivation(0, 2, 1) - 1) < 0.001);

    }

}
