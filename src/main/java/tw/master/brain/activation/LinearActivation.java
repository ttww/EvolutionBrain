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

package tw.master.brain.activation;

import tw.gui.annotiations.GuiFloatAnnotation;
import tw.gui.annotiations.GuiInfoAnnotation;

@GuiInfoAnnotation(title = "LinearActivation")
public class LinearActivation extends AbstractActivationFunction {

    private static final long	serialVersionUID	= 1L;
    private float slope = 2f;
    private float shiftY = -1.0f;


    private static LinearActivation	sceleton;

    public static synchronized ActivationFunctionInterface getActivationFunction() {
        if (sceleton == null) sceleton = new LinearActivation();
        return sceleton;
    }

    private LinearActivation() {
        init(MIN_VALUE,MAX_VALUE,STEPS);
    }

    private void init(float minValue, float maxValue, int steps) {
        float[] fa = new float[steps];
        float step = (maxValue - minValue) / steps;


        float rs = 1f / steps;
        int i=0;
        for (float f = minValue; f < maxValue; f += step) {
            if (i==fa.length) break;	// against rounding problems

            fa[i++] = shiftY + rs * i * slope;
        }
        setMappingArray(minValue,maxValue,fa);
    }


    /**
     * @return the slope
     */
    @GuiFloatAnnotation (
            label = "Slope for linear",
            min = 0f,
            max = 10f,
            format = "%+4.2f",
            tooltip =
                "For adjusting the slope of the linear function"
    )
    public float getSlope() {
        return slope;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * @param slope the slope to set
     */
    public void setSlope(float slope) {
        this.slope = slope;
        init(MIN_VALUE,MAX_VALUE,STEPS);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * @return the shift Y
     */
    @GuiFloatAnnotation (
            label = "Shift Y for linear",
            min = -10f,
            max = 10f,
            format = "%+4.2f",
            tooltip =
                "For shift Y the linear function around zero"
    )
    public float getShiftY() {
        return shiftY;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * @param shiftY the shift Y to set
     */
    public void setShiftY(float shiftY) {
        this.shiftY = shiftY;
        init(MIN_VALUE,MAX_VALUE,STEPS);
    }

    // ---------------------------------------------------------------------------------------------

}
