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

import java.awt.BorderLayout;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.swing.JPanel;

import tw.gui.GridBagStripePanel;
import tw.master.mutation.GenotypeParameter;
import tw.master.mutation.MutationParameter;
import tw.master.utils.Utils;


/**
 * This class generate a JPanel based on java annotations.
 *
 * @author Thomas Welsch
 */
public final class AnnotationGuiGenerator {


    // ---------------------------------------------------------------------------------------------

    /**
     * Only static methods in this class.
     */
    private AnnotationGuiGenerator() {
        super();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the title which is set by a class GuiInfoAnnotation.
     *
     * @param c     The class
     * @return      Title or "unknown"
     */
    public static String getClassTitle(Class<?> c) {
        Annotation[] annos = c.getAnnotations();
        for (Annotation a : annos) {
            if (a instanceof GuiInfoAnnotation) {
                GuiInfoAnnotation ga = (GuiInfoAnnotation) a;
                return ga.title();
            }
        }
        return "unknown";
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the title which belongs to the class of the given object. From this class
     * the GuiInfoAnnotation is read.
     *
     * @param o     The object, class
     * @return      Title or "unknown"
     */
    public static String getClassTitle(Object o) {
        return getClassTitle(o.getClass());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Generate a JPanel which all generated GUI objects as defined by the GUI annotations.<p>
     * The panel contains only static getter/setter.
     *
     * @param   c       The class for accessing the static setter/getter methods
     *
     * @return  The generated JPanel
     *
     * @throws Exception    Mostly reflection errors
     */
    public static JPanel generateComponent(Class<?> c) throws Exception {
        return generateComponent(c, (Class<?>[]) null);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Generate a JPanel which all generated GUI objects as defined by the GUI annotations.<p>
     * The panel contains only static getter/setter.
     *
     * @param   c       The class for accessing the static setter/getter methods
     * @param   filter  Array of wanted annotations
     *
     * @return  The generated JPanel
     *
     * @throws Exception    Mostly reflection errors
     */
    public static JPanel generateComponent(Class<?> c, Class<?>... filter) throws Exception {
        return internGenerateComponent(c, null, filter);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Generate a JPanel which all generated GUI objects as defined by the GUI annotations.<p>
     * The panel contains the static getter/setter and then the object getter/setter.
     *
     * @param   o       A object for accessing the setter/getter methods
     * @param   filter  Array of wanted annotations
     *
     * @return  The generated JPanel
     *
     * @throws Exception    Mostly reflection errors
     */
    public static JPanel generateComponent(Object o, Class<?>... filter) throws Exception {

        JPanel staticPanel = internGenerateComponent(o.getClass(), null, filter);
        JPanel objectPanel = internGenerateComponent(o.getClass(), o, filter);

        if (staticPanel != null && objectPanel != null) {
            JPanel ret = new JPanel(new BorderLayout());
            ret.add(staticPanel, BorderLayout.NORTH);
            ret.add(objectPanel, BorderLayout.SOUTH);
            return ret;
        }
        if (staticPanel != null) return staticPanel;
        if (objectPanel != null) return objectPanel;

        return null;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Generate a JPanel which all generated GUI objects as defined by the GUI annotations.<p>
     * The panel contains the static getter/setter and then the object getter/setter.
     *
     * @param   o       A object for accessing the setter/getter methods
     *
     * @return  The generated JPanel
     *
     * @throws Exception    Mostly reflection errors
     */
    public static JPanel generateComponent(Object o) throws Exception {
        return generateComponent(o, (Class<?>[]) null);
    }

    // ---------------------------------------------------------------------------------------------

    private static boolean checkFilter(Annotation a, Class<?>... filter) {
        if (filter == null) return true;

        for (Class<?> c : filter) {
            if (a.annotationType() == c) return true;
        }
        return false;
    }

    // ---------------------------------------------------------------------------------------------

    private static JPanel internGenerateComponent(Class<?> c, Object o, Class<?>... filter) throws Exception {
        GridBagStripePanel ret = new GridBagStripePanel();

        // Setting the name to the @GuiInfoAnnotation name for GUI testing
        ret.setName(getClassTitle(c));
        ret.setGridDebug(false);

        int y = 0;

        try {

            Method[] methods = c.getMethods();

            for (Method m : methods) {
                Annotation[] annos = m.getAnnotations();
                if (annos.length == 0) continue;

                int idx = 3;
                if (m.getName().startsWith("is")) idx = 2;

                String fieldName = m.getName();
                fieldName = Character.toUpperCase(fieldName.charAt(idx)) + fieldName.substring(idx + 1);

                for (Annotation a : annos) {

                    if (!checkFilter(a, filter)) continue;

                    try {
                        // -------------------------------------------------------------------------
                        // Getter/Setter annotations:
                        // -------------------------------------------------------------------------
                        if (a instanceof GuiBooleanAnnotation) {
                            GuiBooleanAnnotation ba = (GuiBooleanAnnotation) a;
                            new GuiBooleanHandler(ret, y++, c, o, fieldName, ba.label(), ba.text(), ba.tooltip());
                            continue;
                        }

                        if (a instanceof GuiFloatAnnotation) {
                            GuiFloatAnnotation fa = (GuiFloatAnnotation) a;
                            new GuiFloatHandler(ret, y++, c, o, fieldName, fa.label(), fa.tooltip(), fa.min(),
                                    fa.max(), fa.format());
                            continue;
                        }

                        if (a instanceof GuiEnumAnnotation) {
                            GuiEnumAnnotation fa = (GuiEnumAnnotation) a;

                            int[] ya = { y };
                            // y in incremented depending of the added panels in GuiEnumHandler !
                            new GuiEnumHandler(ret, ya, c, o, fieldName, fa.label(), fa.tooltip(), fa.enumClass());
                            y = ya[0];
                            continue;
                        }

//						if (a instanceof GuiMutationGenotypeParameterAnnotation) {
//							GuiMutationGenotypeParameterAnnotation fa = (GuiMutationGenotypeParameterAnnotation) a;
//
//							new GuiMutationGenotypeParameterHandler(ret,y++,c,o,fieldName,fa.label(),fa.tooltip(),fa.min(),fa.max(),fa.format());
//							continue;
//						}

                    } catch (NoSuchMethodException e) {
                        // If we get this exception, this means that we have only a none static
                        // version of the method and we simple continues (Because none static annotiations
                        // will be handled afterwars.
                        Utils.hookIgnoredException(e);
                    }
                }
            } // Methods

            // -------------------------------------------------------------------------------------
            // Field / Object annotations:
            // -------------------------------------------------------------------------------------
            Field[] fields = c.getFields();
            for (Field f : fields) {
                Annotation[] annos = f.getAnnotations();
                if (annos.length == 0) continue;

                for (Annotation a : annos) {

                    if (!checkFilter(a, filter)) continue;

                    if (a instanceof GuiMutationPhenotypeParameterAnnotation) {
                        GuiMutationPhenotypeParameterAnnotation ma = (GuiMutationPhenotypeParameterAnnotation) a;

                        Object mp = null;
                        try {
                            mp = f.get(o);
                        } catch (NullPointerException e) {
                            // If we get this exception, this means that we have only the static
                            // version of the field and we simple continues (Because static annotiations
                            // where handled before.
                            continue;
                        }
                        new GuiMutationPhenotypeParameterHandler(ret, y++, c, (MutationParameter) mp, ma.label(),
                                ma.tooltip(), ma.format());
                        continue;
                    }

                    if (a instanceof GuiMutationGenotypeParameterAnnotation) {
                        GuiMutationGenotypeParameterAnnotation ma = (GuiMutationGenotypeParameterAnnotation) a;

                        Object gen = null;
                        try {
                            gen = f.get(o);
                        } catch (NullPointerException e) {
                            // If we get this exception, this means that we have only the static
                            // version of the field and we simple continues (Because static annotiations
                            // where handled before.
                            continue;
                        }


                        new GuiMutationGenotypeParameterHandler(ret, y++, GenotypeParameter.class,
                                (GenotypeParameter) gen, ma.fieldName(), ma.label(), ma.tooltip(), ma.min(), ma.max(),
                                ma.format());
                        continue;
                    }



                }
            } // Methods


        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        if (y == 0) return null; // Noting found

        return ret;
    }

    // ---------------------------------------------------------------------------------------------

} // of class AnnotationGuiGenerator
