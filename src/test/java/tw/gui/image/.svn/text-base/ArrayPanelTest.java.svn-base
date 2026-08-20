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

package tw.gui.image;

import javax.swing.JFrame;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import tw.master.utils.Rnd;
import tw.master.utils.Utils;


/**
 *
 *
 * @author Thomas Welsch
 *
 */
public class ArrayPanelTest {

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception { }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception { }

    private static float[][] generateData(boolean rnd) {

        int xm = 10;
        int ym = 10;
        float f = 0;
        float s = 1f / (xm * ym);

        float[][] ret = new float[ym][];
        for (int y = 0; y < ym; y++) {
            ret[y] = new float[xm];
            for (int x = 0; x < xm; x++) {
                if (rnd) {
                    ret[y][x] = Rnd.rnd(0f, 1f);
                } else {
                    ret[y][x] = f;
                    f += s;
                }
            }
        }
        return ret;
    }

    /**
     * Test method for {@link tw.gui.image.ArrayPanel#ArrayPanel(float[][])}.
     */
    @Test
    public final void testArrayPanelFloatArrayArray() {
        ArrayPanel p = new ArrayPanel(generateData(false));

        JFrame frame = Utils.showBean(p, p.getName());

        for (int i = 0; i < 100; i++) {
            p.setData(generateData(true));
            Utils.sleep(40);
        }

        frame.dispose();
    }

}
