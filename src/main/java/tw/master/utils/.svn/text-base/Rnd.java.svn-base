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

package tw.master.utils;

import java.awt.Color;
import java.util.Random;

/**
 * @author Thomas Welsch
 */
public class Rnd {

    private static Random random = null;

    // ---------------------------------------------------------------------------------------------

    /**
     * This method sets the seed of the random generator for reproducibleMode random values.
     *
     * @param seed
     */
    public static void setInitialRandomSeed(long seed) {
        if (random != null) {
            System.err.println("Warning: setting random seed already done!, resetting to value " + seed);
            //throw new IllegalArgumentException("setting random seed already done!, not setting to value " + seed);
        }
        random = new Random(seed);
    }

    private static void init() {
        random = new Random(System.currentTimeMillis());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Generate random value between to boundaries.
     * <p>
     * The result is from lo..hi (inclusive).
     * <p>
     * The random seed is initialized to the currentTimeMillis() by the first innovation.
     *
     * @param lo
     *            Low boundary
     * @param hi
     *            Hi boundary
     *
     */
    public static int rnd(int lo, int hi) {
        if (random == null) init();

        int ret = (int) (lo + Math.abs(random.nextLong()) % ((long) hi - lo + 1));

        //if (ret < lo || ret > hi) throw new IllegalArgumentException("Bad internal RND !!! lo="+lo+" hi="+hi+"  ret="+ret);

        return ret;
//		return lo + (Math.abs(random.nextInt())) % (hi - lo + 1);	// Falls ! Führt bei hohen werten zu fehlern !!! lo = 687255911, hi=lo+4 --> rnd = 687255908
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * @param lo
     * @param hi
     * @return the random value
     */
    public static double rnd(double lo, double hi) {
        if (random == null) init();
        return lo + random.nextDouble() * (hi - lo);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * @param lo
     * @param hi
     * @return the random value
     */
    public static float rnd(float lo, float hi) {
        if (random == null) init();
        return lo + random.nextFloat() * (hi - lo);
    }

    // ---------------------------------------------------------------------------------------------

    public static Color rndColor(int limit) {

        int sum = 0;
        int r, g, b;

        r = 0;
        g = 0;
        b = 0;

        while (sum < limit) {
            r = rnd(0, 255);
            g = rnd(0, 255);
            b = rnd(0, 255);

            sum = r + b + g;
        }

        return new Color(r, b, g);
    }

    // ---------------------------------------------------------------------------------------------

}
