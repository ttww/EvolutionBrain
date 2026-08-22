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
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JPanel;

import tw.gui.GridBagStripePanel;
import tw.master.mutation.GenotypeParameter;
import tw.master.mutation.MutationParameter;
import tw.master.mutation.MutationParameters;
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

    /**
     * Collects the current values of all static @GuiFloatAnnotation/@GuiBooleanAnnotation/
     * @GuiEnumAnnotation annotated getter/setter pairs of the given class, keyed by field name.
     * Used to persist "global" GUI controls (e.g. WalkingBrainControls) across
     * {@code Engine.saveState()}/{@code loadState()}.
     *
     * @param c   Class to scan (typically engine.crawlerClass)
     *
     * @return    Map of field name to current value (Float/Boolean/Enum), in a serializable form
     */
    public static LinkedHashMap<String, Object> saveStaticAnnotatedValues(Class<?> c) {
        LinkedHashMap<String, Object> ret = new LinkedHashMap<String, Object>();

        for (Method m : c.getMethods()) {
            if (!Modifier.isStatic(m.getModifiers())) continue;

            boolean relevant = false;
            for (Annotation a : m.getAnnotations()) {
                if (a instanceof GuiFloatAnnotation || a instanceof GuiBooleanAnnotation
                        || a instanceof GuiEnumAnnotation) {
                    relevant = true;
                    break;
                }
            }
            if (!relevant) continue;

            String name = m.getName();
            int idx;
            if (name.startsWith("is")) idx = 2;
            else if (name.startsWith("get") || name.startsWith("set")) idx = 3;
            else continue;

            String fieldName = Character.toUpperCase(name.charAt(idx)) + name.substring(idx + 1);

            try {
                Method getter;
                try {
                    getter = c.getMethod("get" + fieldName, (Class[]) null);
                } catch (NoSuchMethodException nsme) {
                    getter = c.getMethod("is" + fieldName, (Class[]) null);
                }
                ret.put(fieldName, getter.invoke(null));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ret;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Restores values previously collected by {@link #saveStaticAnnotatedValues(Class)} by calling
     * the matching static setter for each entry.
     *
     * @param c        Class to apply the values to (typically engine.crawlerClass)
     * @param values   Field name to value map, as produced by {@link #saveStaticAnnotatedValues(Class)}
     */
    public static void restoreStaticAnnotatedValues(Class<?> c, Map<String, Object> values) {
        if (values == null) return;

        for (Map.Entry<String, Object> e : values.entrySet()) {
            Object value = e.getValue();
            if (value == null) continue;

            String setterName = "set" + e.getKey();

            for (Method m : c.getMethods()) {
                if (!m.getName().equals(setterName)) continue;
                if (!Modifier.isStatic(m.getModifiers())) continue;
                if (m.getParameterTypes().length != 1) continue;

                try {
                    m.invoke(null, value);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Result of {@link #generatePhenotypeComponent(MutationParameters)}: the generated panel plus
     * the created handlers, keyed by the MutationParameter field they were bound to, so the panel
     * can later be rebound to a different MutationParameters object via
     * {@link #rebindPhenotypeComponent(Map, MutationParameters)} without destroying/recreating the
     * JSlider components (fragile under Aqua LookAndFeel when done frequently).
     */
    public static final class PhenotypeComponent {

        public final JPanel                                             panel;

        public final Map<Field, GuiMutationPhenotypeParameterHandler>   handlers;

        private PhenotypeComponent(JPanel panel, Map<Field, GuiMutationPhenotypeParameterHandler> handlers) {
            this.panel = panel;
            this.handlers = handlers;
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Builds the Phenotype mutation-parameter panel for the given MutationParameters object,
     * returning both the panel and the created handlers so they can be rebound to a different
     * MutationParameters object later (see {@link #rebindPhenotypeComponent}) instead of rebuilding
     * the whole panel from scratch.
     *
     * @param mutationParameters   The object to read the current MutationParameter field values from
     *
     * @return   The generated panel plus its field-to-handler map
     *
     * @throws Exception   Mostly reflection errors
     */
    public static PhenotypeComponent generatePhenotypeComponent(MutationParameters mutationParameters)
            throws Exception {
        GridBagStripePanel ret = new GridBagStripePanel();
        ret.setName(getClassTitle(mutationParameters));
        ret.setGridDebug(false);

        LinkedHashMap<Field, GuiMutationPhenotypeParameterHandler> handlers =
                new LinkedHashMap<Field, GuiMutationPhenotypeParameterHandler>();

        int y = 0;
        for (Field f : mutationParameters.getClass().getFields()) {
            GuiMutationPhenotypeParameterAnnotation ma = f.getAnnotation(GuiMutationPhenotypeParameterAnnotation.class);
            if (ma == null) continue;

            try {
                Object mp = f.get(mutationParameters);
                if (mp == null) continue;

                GuiMutationPhenotypeParameterHandler h = new GuiMutationPhenotypeParameterHandler(ret, y++,
                        mutationParameters.getClass(), (MutationParameter) mp, ma.label(), ma.tooltip(), ma.format());
                handlers.put(f, h);
            } catch (NullPointerException e) {
                // Only the static version of the field exists - already handled elsewhere.
            } catch (Exception e) {
                // Don't let one bad field abort the whole panel - the caller falls back to a full
                // rebuild whenever a field is missing from the handler map, which would otherwise
                // destroy/recreate every JSlider on every refresh instead of just the failing one.
                e.printStackTrace();
            }
        }

        return new PhenotypeComponent(ret, handlers);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Rebinds a previously generated Phenotype panel (see {@link #generatePhenotypeComponent}) to a
     * new MutationParameters object, updating each slider's value in place instead of destroying and
     * recreating the JSlider components.
     *
     * @param handlers             Field-to-handler map, as produced by {@link #generatePhenotypeComponent}
     * @param mutationParameters   The new object to read the current MutationParameter field values from
     */
    public static void rebindPhenotypeComponent(Map<Field, GuiMutationPhenotypeParameterHandler> handlers,
            MutationParameters mutationParameters) {
        for (Map.Entry<Field, GuiMutationPhenotypeParameterHandler> e : handlers.entrySet()) {
            try {
                Object mp = e.getKey().get(mutationParameters);
                if (mp == null) continue;

                e.getValue().setMutationParameter((MutationParameter) mp);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
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
