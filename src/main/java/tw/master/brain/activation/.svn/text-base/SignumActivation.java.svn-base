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


@GuiInfoAnnotation(title = "SignumActivation")
public class SignumActivation extends AbstractActivationFunction {

    private static final long       serialVersionUID = 1L;

    private float                   slope            = 7.3f;

    private float                   shiftY           = 0.36f;


    private static SignumActivation sceleton;

    // ---------------------------------------------------------------------------------------------

    public static synchronized ActivationFunctionInterface getActivationFunction() {
        if (sceleton == null) sceleton = new SignumActivation();
        return sceleton;
    }

    // ---------------------------------------------------------------------------------------------

    private SignumActivation() {
        init(MIN_VALUE, MAX_VALUE, STEPS);
    }

    // ---------------------------------------------------------------------------------------------

    private void init(float minValue, float maxValue, int steps) {
        float[] fa = new float[steps];
        float step = (maxValue - minValue) / steps;

        int i = 0;
        for (float f = minValue; f < maxValue; f += step) {
            if (i == fa.length) break; // against rounding problems

            float v = 1 / (float) (1 + Math.exp(-slope * (f - shiftY)));
            //System.err.println(minValue+" / "+maxValue+" "+f+"  i="+i+"    --> "+String.format("%6.3f",new Float(v)));
            fa[i++] = v;
        }

        setMappingArray(minValue, maxValue, fa);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * @return the slope
     */
    @GuiFloatAnnotation(
            label = "Slope for signum",
            min = 0f,
            max = 100f,
            format = "%+4.2f",
            tooltip =
                "For adjusting the slope of the signum function"
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
        init(MIN_VALUE, MAX_VALUE, STEPS);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * @return the shift Y
     */
    @GuiFloatAnnotation(
            label = "Shift Y for signum",
            min = -1f,
            max = 1f,
            format = "%+4.2f",
            tooltip =
                "For shift Y the signum function around zero"
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
        init(MIN_VALUE, MAX_VALUE, STEPS);
    }

    // ---------------------------------------------------------------------------------------------



}
