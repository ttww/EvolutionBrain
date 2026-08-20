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

package tw.master.math;

/**
 * Collection of Math routines.
 *
 * @author Thomas Welsch
 */
public class MathUtils {


    // ---------------------------------------------------------------------------------------------

    /**
     * Check if the difference of a and b < 0.00001.
     *
     * @param a
     *            first value
     * @param b
     *            second value
     * @return true if |b-a| < 0.00001;
     */
    public static final boolean equals_0_00001(double a, double b) {
        return Math.abs(b - a) < 0.00001;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the variance &Sigma; (x<sub>i</sub>-m)<sup>2</sup> of a float
     * array x[] with mean value m.
     * <p>
     * The calculation of the mean is not included due to performance reasons.
     *
     * @param x
     *            array
     * @param mean
     *            mean value
     * @return variance
     */
    private static float variance(float[] x, float mean) {
        float variance = 0;
        for (float f : x) {
            float d = f - mean;
            variance += d * d;
        }
        return variance;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the mean &Sigma;x<sub>i</sub>/n of an float array.
     *
     * @param x
     *            data array
     *
     * @return mean value
     */
    private static float mean(float[] x) {
        float sum = 0;
        for (float a : x)
            sum += a;
        return sum / x.length;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the standard deviation &radic;variance(x) of a float array.
     *
     * @param x
     *            data array
     *
     * @return standard deviation
     */
    public static final float standardDeviation(float[] x) {
        if (x.length < 2)
            throw new IllegalArgumentException("Cannot calculate standard deviation for arrays shorter 2.");

        return (float) Math.sqrt(variance(x, mean(x)));
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Generate 2D-array as neighborhood with gaussian weight values.
     *
     * @param radius	Field radius
     * @param a			Value in center of field
     * @param b			width of gaussian, 0.05 with 10 radius for filled array
     *
     * @return the calculated genGaussKernel
     */
    public static float[][] genGaussKernel(int radius, float a, float b) {
        int size = 2 * radius + 1;
        float[][] k = new float[size][size];

        for (int y = 0; y <= radius; y++) {
            for (int x = 0; x <= radius; x++) {
                float r = (float) Math.sqrt(x * x + y * y);
                float g = (float) (a * Math.exp(-b * r * r));

                k[radius + x][radius + y] = g;
                k[radius + x][radius - y] = g;
                k[radius - x][radius + y] = g;
                k[radius - x][radius - y] = g;
            }
        }
        return k;
    }


}
