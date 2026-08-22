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
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import tw.gui.RangeSlider;
import tw.master.mutation.GenotypeParameter;

/**
 * Implements the GUI for a GuiMutationGenotypeAnnotation.<p>
 *
 * It is build with the GridBagLayout in the following form:<br>
 * <pre>
 *    0            1               2
 * [JLabel]  [RangeSlider] [Jlabel (format)]
 * </pre>
 *
 * @author Thomas Welsch
 */
public class GuiMutationGenotypeParameterHandler extends BasicHandler implements ChangeListener {

    private final RangeSlider   cs;

    private JLabel              formatLabel;

    private final float         min;

    private final float         max;

    private final String        format;

    private static final int SCALE = 1000;

    // ---------------------------------------------------------------------------------------------

    /**
     * Create the necessary GUI for float annotation.
     *
     * @param p             Panel to add to.
     * @param y             Row in GridbagLayout
     * @param oc            Class of object (if o is null)
     * @param o             Object for getter/setter
     * @param fieldName     Name of field
     * @param label         Label before JCheckbos
     * @param tooltip       Tooltip for components
     * @param min           The minimum value
     * @param max           The maximum value
     * @param format        Format for the label if needed. Eg. "%2.4f °C"
     *
     * @throws NoSuchMethodException     Mostly Reflection errors
     * @throws IllegalAccessException    Mostly Reflection errors
     * @throws InvocationTargetException Mostly Reflection errors
     */
    public GuiMutationGenotypeParameterHandler(
            JPanel              p,
            int                 y,
            Class<?>            oc,
            GenotypeParameter   o,
            String              fieldName,
            String              label,
            String              tooltip,
            float               min,
            float               max,
            String format) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        super(oc, o, "", float.class);

        //this.gen	= (GenotypeParameter) o;
        this.min = min;
        this.max = max;
        this.format = format;

        cs = new RangeSlider();
        cs.setMinimum(0);
        cs.setMaximum(SCALE);

        if (fieldName.length() == 0) {
            cs.setName(p.getName() + "." + label);
        }
        else {
            cs.setName(p.getName() + "." + fieldName);
        }
        //System.err.println("RangeSlider: Name = " + cs.getName());

        float lower = getLower();
        float upper = getUpper();
        cs.setValue(floatToSlider(lower));
        cs.setUpperValue(floatToSlider(upper));

        cs.addChangeListener(this);

        Hashtable<Integer, JComponent> tickLabels = new Hashtable<Integer, JComponent>();
//		tickLabels.put(new Integer(floatToSlider(min)), new JLabel(Float.toString(min)));
        tickLabels.put(Integer.valueOf(floatToSlider(min)), new JLabel(Integer.toString((int) (0.5f + min))));

        if (0 > min && 0 < max)
            tickLabels.put(Integer.valueOf(floatToSlider(0)), new JLabel("0"));

//		tickLabels.put(new Integer(floatToSlider(max)), new JLabel(Float.toString(max)));
        tickLabels.put(Integer.valueOf(floatToSlider(max)), new JLabel(Integer.toString((int) (0.5f + max))));

        cs.setLabelTable(tickLabels);
        cs.setMajorTickSpacing(SCALE / 10);
        cs.setMinorTickSpacing(SCALE / 20);
        cs.setPaintTicks(true);
        cs.setPaintLabels(true);


        // Looks better on the mac, but triggers an Null-Pointer-Exeption in
        // javax.swing.plaf.basic.BasicSliderUI.calculateFocusRect() about focusRect during
        // serialization.... (see GuiFloatHandler)
        //	cs.putClientProperty("JComponent.sizeVariant", "mini");
        //	cs.putClientProperty("Slider.paintThumbArrowShape", Boolean.TRUE);

        if (tooltip != null) {
            cs.setToolTipText(tooltip);
        }

        GridBagConstraints gb = new GridBagConstraints();
        gb.gridx = 1;
        gb.gridy = y;
        gb.weightx = 2;
        gb.fill = GridBagConstraints.HORIZONTAL;
        setDefaultInsetsForGridX(gb);

        p.add(cs, gb);

        if (format != null) {
            formatLabel = new JLabel();
            if (tooltip != null) {
                formatLabel.setToolTipText(tooltip);
            }

            gb = new GridBagConstraints();
            gb.gridx = 2;
            gb.gridy = y;
            gb.fill = GridBagConstraints.HORIZONTAL;
            setDefaultInsetsForGridX(gb);

            p.add(formatLabel, gb);
        }

        if (label != null) {
            JLabel jl = new JLabel(label);
            if (tooltip != null) {
                jl.setToolTipText(tooltip);
            }

            gb = new GridBagConstraints();
            gb.gridx = 0;
            gb.gridy = y;
            gb.fill = GridBagConstraints.HORIZONTAL;
            setDefaultInsetsForGridX(gb);

            p.add(jl, gb);
        }

        updateFormatLabel(lower, upper);

    }

    // ---------------------------------------------------------------------------------------------

    private int floatToSlider(float f) {
        float ff = f - min;
        ff = ff / (max - min);

        return (int) (0.5f + ff * SCALE);
    }

    // ---------------------------------------------------------------------------------------------

    private float sliderToFloat(int v) {
        float f = v * (max - min);
        f = f / SCALE;
        f = f + min;

        return f;
    }

    // ---------------------------------------------------------------------------------------------

    private void setUpper(float upper) throws IllegalAccessException,
    InvocationTargetException {
        setterUpper.invoke(o, new Float(upper));
    }

    // ---------------------------------------------------------------------------------------------

    private void setLower(float lower) throws IllegalAccessException,
    InvocationTargetException {
        setterLower.invoke(o, new Float(lower));
    }

    // ---------------------------------------------------------------------------------------------

    private float getLower() throws IllegalAccessException, InvocationTargetException {
        Float f = (Float) getterLower.invoke(o, (Object[]) null);
        return f.floatValue();
    }

    // ---------------------------------------------------------------------------------------------

    private float getUpper() throws IllegalAccessException, InvocationTargetException {
        Float f = (Float) getterUpper.invoke(o, (Object[]) null);
        return f.floatValue();
    }

    // ---------------------------------------------------------------------------------------------

    private void updateFormatLabel(float lower, float upper) {
        if (formatLabel != null) {
            formatLabel.setText(String.format(format, new Float(lower), new Float(upper)));
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void stateChanged(ChangeEvent e) {
        try {
            float lower = sliderToFloat(cs.getValue());
            float upper = sliderToFloat(cs.getUpperValue());
            updateFormatLabel(lower, upper);
            setLower(lower);
            setUpper(upper);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}   // of class GuiMutationGenotypeParameterHandler
