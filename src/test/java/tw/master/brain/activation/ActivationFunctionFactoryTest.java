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

import javax.swing.JFrame;

import org.fest.swing.fixture.FrameFixture;
import org.junit.Test;

import tw.master.utils.Utils;


/**
 *
 *
 * @author Thomas Welsch
 *
 */
public class ActivationFunctionFactoryTest {

    /**
     * Test method for {@link tw.master.brain.activation.ActivationFunctionFactory#getActivationFunction(tw.master.brain.activation.ActivationFunctionFactory.ActivationFunction)}.
     */
    @Test
    public void testGetActivationFunction() {
        ActivationFunctionInterface afi1 = LinearActivation.getActivationFunction();
        JFrame f1 = Utils.showBean(afi1.getVisualPanel(), afi1.getName());

        ActivationFunctionInterface afi2 = SignumActivation.getActivationFunction();
        JFrame f2 = Utils.showBean(afi2.getVisualPanel(), afi2.getName());

        f1.setSize(800, 400);
        f2.setSize(800, 400);
        f1.setLocation(30, 30);
        f2.setLocation(30, 450);

        checkAF(f2, "SignumActivation");
        checkAF(f1, "LinearActivation");

    }

    private static void checkAF(JFrame f, String name) {
        FrameFixture ff = new FrameFixture(f);

        String n = name + ".ScaleInputs";
        ff.checkBox(n).click();
        Utils.sleep(100);
        ff.checkBox(n).click();

        Utils.sleep(100);

        n = name + ".Slope";
        int org = ff.slider(n).target.getValue();

        ff.slider(n).slideToMinimum();
        ff.slider(n).slideToMaximum();
        ff.slider(n).slideTo(org);

        for (int i = 0; i <= 1000; i = i + 100) {
            ff.slider(n).slideTo(i);
            //   Utils.sleep(2);
        }
        ff.slider(n).slideTo(org);

        n = name + ".ShiftY";
        org = ff.slider(n).target.getValue();

        ff.slider(n).slideToMinimum();
        ff.slider(n).slideToMaximum();
        ff.slider(n).slideTo(org);

        for (int i = 0; i <= 1000; i = i + 100) {
            ff.slider(n).slideTo(i);
            //   Utils.sleep(2);
        }
        ff.slider(n).slideTo(org);

        f.dispose();
        ff.robot.cleanUpWithoutDisposingWindows();
        //ff.close();
    }
}
