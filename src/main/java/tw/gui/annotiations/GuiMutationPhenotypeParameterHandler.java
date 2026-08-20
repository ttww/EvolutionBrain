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

package tw.gui.annotiations;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JPanel;

import tw.master.mutation.MutationParameter;

/**
 * Implements the GUI for a GuiMutationPhenotypeAnnotation.<p>
 *
 * It is build with the GridBagLayout in the following form:<br>
 * <pre>
 *    0            1               2
 * [JLabel]  [JSlider] [Jlabel (format)]
 * </pre>
 *
 * @author Thomas Welsch
 */
public class GuiMutationPhenotypeParameterHandler {

    private final MutationParameter	mp;

    // ---------------------------------------------------------------------------------------------

    /**
     * Create the necessary GUI for MutationParameter annotation.
     *
     * @param p             Panel to add to.
     * @param y             Row in GridbagLayout
     * @param oc            Class of object (if o is null)
     * @param mp            MutationParameter object for getter/setter
     * @param label         Label before JCheckbos
     * @param tooltip       Tooltip for components
     * @param format        Format for the label if needed. Eg. "%2.4f °C"
     *
     * @throws NoSuchMethodException     Mostly Reflection errors
     * @throws IllegalAccessException    Mostly Reflection errors
     * @throws InvocationTargetException Mostly Reflection errors
     */
    public GuiMutationPhenotypeParameterHandler(
            JPanel				p,
            int					y,
            Class<?>			oc,
            MutationParameter	mp,
            String				label,
            String				tooltip,
            String				format
    ) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        this.mp = mp;
        new GuiFloatHandler(p, y, this.getClass(), this, "Value", label, tooltip, mp.getMin(), mp.getMax(), format);
    }

    // ---------------------------------------------------------------------------------------------

//	public void setValue(float f) {
//		mp.setValue(f);
//	}

    // ---------------------------------------------------------------------------------------------

    /**
     * Get the value for the MutationParamter.
     *
     * @return  the value for the MutationParamter...
     */
    public float getValue() {
        return mp.getValue();
    }

}   // of class GuiMutationPhenotypeParameterHandler
