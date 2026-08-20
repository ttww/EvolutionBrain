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

package tw.master;

import org.junit.Test;

import tw.master.utils.Utils;


/**
 *
 *
 * @author Thomas Welsch
 *
 */
public class EvolutionBrainClientMainTest {

    /**
     * Test method for {@link tw.master.EvolutionBrainClientMain#main(java.lang.String[])}.
     */
    @Test
    public final void testMain() {

        EvolutionBrainClientMain ebc = new EvolutionBrainClientMain();

        EvolutionBrainClientMain.startSimulation(ebc);

        Utils.sleep(9000);
        ebc.engine.setRunning(true);
        Utils.sleep(5000);
        ebc.engine.setRunning(false);
    }

}
