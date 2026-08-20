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

package tw.master.gui;

import javax.swing.JFrame;

import org.junit.Test;

import tw.master.brain.Brain;
import tw.master.gl3d.Panel3dFactory;
import tw.master.utils.Utils;


/**
 * @author Thomas Welsch
 *
 */
public class Brain3dDisplayTest {

    /**
     * Test method for {@link tw.master.gui.Brain3dDisplay#Brain3dDisplay(tw.master.GlobalsClientGui)}.
     */
    @Test
    public void testBrainOpenGl3dDisplay() {
        Brain3dDisplay b3d = new Brain3dDisplay(null);
        b3d.setAutoRot(true);

        Brain brain = new Brain("TestBrain");
        b3d.setWatchedBrain(brain);

        JFrame f = Utils.showBean(b3d, "TEST-3D OpenGL");
        Utils.sleep(5000);

        f.dispose();
    }

    /**
     * Test method for {@link tw.master.gui.Brain3dDisplay#Brain3dDisplay(tw.master.GlobalsClientGui, tw.master.gl3d.Panel3dFactory)}.
     */
    @Test
    public void testBrainJava3dDisplay() {
        Brain3dDisplay b3d = new Brain3dDisplay(null, Panel3dFactory.TYPE_3D.JAVA);
        b3d.setAutoRot(true);

        Brain brain = new Brain("TestBrain");
        b3d.setWatchedBrain(brain);

        JFrame f = Utils.showBean(b3d, "TEST-3D Java");
        Utils.sleep(5000);

        f.dispose();
    }

}
