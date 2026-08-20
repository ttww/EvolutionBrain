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

import javax.swing.JPanel;


/**
 * This class is the factory class for getting one instance of the activation function based on
 * the enumeration ActivationFunction.
 *
 * @author Thomas Welsch
 */
public class ActivationFunctionFactory {

    // ---------------------------------------------------------------------------------------------

    /**
     * Enumeration for holding all known activation function.
     *
     * @author Thomas Welsch
     *
     */
    public enum ActivationFunction {
        Signum, Linear;

        // -----------------------------------------------------------------------------------------

        /**
         * Create a GUI-Panel for the GuiEnumAnnotation.
         *
         * @param which
         *          Function to use
         *
         * @return  generated jPanel for integration into to GUI
         */
        public static JPanel getPanel(ActivationFunction which) {
            return getActivationFunction(which).getVisualPanel();
        }

        // -----------------------------------------------------------------------------------------

        /**
         * Free a a generated panel if the user choose another enum in the GUI.
         *
         * @param   oldWhich
         *              Enum for which the panel was generated
         * @param   oldVisualPanel
         *              Old panel
         */
        public static void freePanel(ActivationFunction oldWhich, JPanel oldVisualPanel) {
            getActivationFunction(oldWhich).freeVisualPanel(oldVisualPanel);
        }
    }

    // ---------------------------------------------------------------------------------------------

    public static ActivationFunctionInterface getActivationFunction(ActivationFunction which) {

        switch (which) {
            case Signum:
                return SignumActivation.getActivationFunction();
            case Linear:
                return LinearActivation.getActivationFunction();
            default:
                break;
        }

        return null;
    }

    //	public static void main(String[] args) {
    //		ActivationFunction af = ActivationFunction.Linear;
    //	}

} // of class ActivationFunctionFactory

