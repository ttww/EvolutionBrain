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

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import tw.master.utils.Utils;


/**
 * Support class to be extended by GUI annotation with some basic methods and fields.
 *
 * @author Thomas Welsch
 */
public class BasicHandler {

    protected Method getter;

    protected Method setter;

    protected Method getterUpper;

    protected Method setterUpper;

    protected Method getterLower;

    protected Method setterLower;

    protected Object o;

    // ---------------------------------------------------------------------------------------------

    protected BasicHandler(
            Class<?>    oc,
            Object      o,
            String      fieldName,
            Class<?>    typeClass) throws NoSuchMethodException {

        this.o = o;

//		System.err.println("OC    = "+oc);
//		System.err.println("C     = "+typeClass);

        // -----------------------------------------------------------------------------------------
        // Try to get normal getter/setter based on the field name:
        // -----------------------------------------------------------------------------------------
        try {
            setter = oc.getMethod("set" + fieldName, typeClass);
        } catch (NoSuchMethodException e) {
            Utils.hookIgnoredException(e);
        }

        try {
            getter = oc.getMethod("get" + fieldName, (Class[]) null);
        } catch (NoSuchMethodException e) {
            try {
                getter = oc.getMethod("is" + fieldName, (Class[]) null);
            } catch (NoSuchMethodException ee) {
                Utils.hookIgnoredException(ee);

            }
        }

        // -----------------------------------------------------------------------------------------
        // Try to get normal upper/lower getter/setter for range objects:
        // -----------------------------------------------------------------------------------------
        try {
            getterLower = oc.getMethod("getLower" + fieldName, (Class[]) null);
        } catch (NoSuchMethodException e) {
            Utils.hookIgnoredException(e);
        }

        try {
            setterLower = oc.getMethod("setLower" + fieldName, typeClass);
        } catch (NoSuchMethodException e) {
            Utils.hookIgnoredException(e);
        }

        try {
            setterUpper = oc.getMethod("setUpper" + fieldName, typeClass);
        } catch (NoSuchMethodException e) {
            Utils.hookIgnoredException(e);
        }

        try {
            getterUpper = oc.getMethod("getUpper" + fieldName, (Class[]) null);
        } catch (NoSuchMethodException e) {
            Utils.hookIgnoredException(e);
        }

        // -----------------------------------------------------------------------------------------
        // Give some hints if the normal getter/setter are wrong:
        // -----------------------------------------------------------------------------------------
        if (setter != null && getter != null) {
            boolean setterIsStatic = Modifier.isStatic(setter.getModifiers());
            boolean getterIsStatic = Modifier.isStatic(getter.getModifiers());

            if (o == null) {
                if (!setterIsStatic) throw new NoSuchMethodException(setter.getName() + " should be static !");
                if (!getterIsStatic) throw new NoSuchMethodException(getter.getName() + " should be static !");
            } else {
                if (setterIsStatic) throw new NoSuchMethodException(setter.getName() + " should NOT be static !");
                if (getterIsStatic) throw new NoSuchMethodException(getter.getName() + " should NOT be static !");

            }
        }
//		System.err.println("Getter  = "+getter);
//		System.err.println("Setter  = "+setter);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Sets the default insets according to the given gridx.
     *
     * @param gb	GridBagConstraints which inset field is set.
     */
    protected void setDefaultInsetsForGridX(GridBagConstraints gb) {
        int t = 2;
        int b = 2;
        int l = 1;
        int r = 1;

        switch (gb.gridx) {
            case 0:
                l = 6;
                break;
            case 1:
                r = 3;
                l = 3;
                break;
            case 2:
                r = 6;
                break;
            default:
                break;
        }

        gb.insets = new Insets(t, l, b, r);

    }

}   // of class BasicHandler
