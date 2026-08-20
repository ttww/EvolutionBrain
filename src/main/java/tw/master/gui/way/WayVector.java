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

package tw.master.gui.way;

import java.io.Serializable;

import tw.master.math.MathUtils;


/**
 * @author Thomas Welsch
 *
 */
public class WayVector implements Serializable {

    private static final long	serialVersionUID	= 1L;

    static final float			MAX_VALUE			= 10;

    private final float			subPerAdd;

    float[]			          	directions;

    int				         	numberOfSectors;

    float			        	lastAngle;

    private float				stdd;
    private float				avgStdd				= -12345;
    private float				avgSum				= -12345;

    public WayVector(final int numberOfSectors) {
        this.numberOfSectors = numberOfSectors;

        directions = new float[numberOfSectors];

        subPerAdd = 1f / numberOfSectors * 0.3f;
        // System.err.println("SUB = " + SUB_PER_ADD);
    }

    public void addWay(float angle) {
        addWay(angle, 1);
    }

    public void addWay(float angle, float step) {
        float a = angle % 360f;

        lastAngle = a;

        int n = directions.length;
        for (int i = 0; i < n; i++) {
            if (directions[i] > 0) directions[i] -= subPerAdd;
            if (directions[i] < 0) directions[i] = 0;
        }

        int di = (int) (a / (360f / n));
        directions[di] += step;

        if (directions[di] > MAX_VALUE) directions[di] = MAX_VALUE;

        stdd = MathUtils.standardDeviation(directions);

        if (avgStdd == -12345)
            avgStdd = stdd;
        else
            avgStdd = (10000 * avgStdd + stdd) / 10001;

        float sum = 0;
        for (int i = 0; i < n; i++) sum += directions[i];

        if (avgSum == -12345)
            avgSum = sum;
        else
            avgSum = (10000 * avgSum + sum) / 10001;

    }

    public float getStandardDeviation() {
        return stdd;
    }

    public float getAverangeStandardDeviation() {
        return avgStdd;
    }

    public float getAverangeSum() {
        if (avgSum == -12345) return 0;
        return avgSum;
    }

}
