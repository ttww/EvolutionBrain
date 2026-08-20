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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import tw.master.utils.SoundUtils;


/**
 * Implements the GUI for a GuiFloatAnnotation..<p>
 *
 * It is build with the GridBagLayout in the following form:<br>
 * <pre>
 *    0          1
 * [JLabel]  [JSlider]
 * </pre>
 *
 * @author Thomas Welsch
 */
class GuiFloatHandler extends BasicHandler implements ChangeListener, MouseListener {

    private final JSlider          cs;

    private final float            min;

    private final float            max;

    private final String           format;

    private JLabel           formatLabel;

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
    public GuiFloatHandler(
            JPanel p,
            int y,
            Class<?> oc,
            Object o,
            String fieldName,
            String label,
            String tooltip,
            float min,
            float max,
            String format) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        super(oc, o, fieldName, float.class);

        this.min = min;
        this.max = max;
        this.format = format;

        cs = new JSlider();
        cs.setName(p.getName() + "." + fieldName);
        //System.err.println("JSlider:     Name = "+cs.getName());

        //cs			= new RangeSlider();
        cs.setMinimum(0);
        cs.setMaximum(SCALE);

        float f = get();
        cs.setValue(floatToSlider(f));

        // Setting the Background
        cs.setOpaque(false);
        cs.addChangeListener(this);

        // Not needed if opaque is accepted....:
        // cs.setBackground(GuiDefaults.STRIPE_COLOR);

        Hashtable<Integer, JComponent> tickLabels = new Hashtable<Integer, JComponent>();
        tickLabels.put(Integer.valueOf(floatToSlider(min)), new JLabel(Integer.toString((int) (0.5f + min))));

        if (0 > min && 0 < max)
            tickLabels.put(Integer.valueOf(floatToSlider(0)), new JLabel("0"));

        tickLabels.put(Integer.valueOf(floatToSlider(max)), new JLabel(Integer.toString((int) (0.5f + max))));

        cs.setLabelTable(tickLabels);
        cs.setMajorTickSpacing(SCALE / 10);
        cs.setMinorTickSpacing(SCALE / 20);
        cs.setPaintTicks(true);
        cs.setPaintLabels(true);

        // I want enabled it all the time and give a feedback if the setter is not working...
        // This is not working in the Mac L&F if the user clicks in the slider instead dragging it....
        // The slieder jumps to the clicked position, but did't change it's value....
        // But it's ok for now...
        cs.addMouseListener(this);
        // if (setter == null) cs.setEnabled(false);

        // Looks better on the mac, but triggers an Null-Pointer-Exeption in
        // javax.swing.plaf.basic.BasicSliderUI.calculateFocusRect() about focusRect during
        // serialization....
        //	cs.putClientProperty( "JComponent.sizeVariant", "mini" );
        //	cs.putClientProperty( "Slider.paintThumbArrowShape", Boolean.TRUE );

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

        updateFormatLabel(f);

    }

    // ---------------------------------------------------------------------------------------------

    private int floatToSlider(float f) {
        float fn = f - min;
        fn = fn / (max - min);

        return (int) (0.5f + fn * SCALE);
    }

    // ---------------------------------------------------------------------------------------------

    private float sliderToFloat(int v) {
        float f = v * (max - min);
        f = f / SCALE;
        f = f + min;

        return f;
    }

    // ---------------------------------------------------------------------------------------------

    private void set(float v) throws IllegalAccessException, InvocationTargetException {
        if (setter != null) setter.invoke(o, new Float(v));
    }

    // ---------------------------------------------------------------------------------------------

    private float get() throws IllegalAccessException, InvocationTargetException {
        Float f = (Float) getter.invoke(o, (Object[]) null);
        return f.floatValue();
    }

    // ---------------------------------------------------------------------------------------------

    private void updateFormatLabel(float f) {
        if (formatLabel != null) {
            formatLabel.setText(String.format(format, new Float(f)));
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Hack routine for try to workaround Mac L&F slider bug....
     */
    private void validateSliderPos() {
        try {
            int sliderValue = cs.getValue();
            // System.err.println("sliderValue = "+sliderValue);

            float f = sliderToFloat(sliderValue);
            //System.err.println("f           = "+f);
            set(f);

            f = get(); // Check it the setter accepted the value...
            // System.err.println("real f      = "+f);
            final int checkSliderValue = floatToSlider(f);
            // System.err.println("real sv     = "+checkSliderValue);

            if (!cs.getValueIsAdjusting()) {
                if (checkSliderValue != sliderValue) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            cs.setValue(checkSliderValue);
                            SoundUtils.playTink();
                        }
                    });
                } else {
                    updateFormatLabel(f);
                }

            } else {
                if (checkSliderValue != sliderValue) {
                    SoundUtils.playTink();
                }
                updateFormatLabel(f);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ---------------------------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        validateSliderPos();
    }

    // ---------------------------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        validateSliderPos();
    }

    // ---------------------------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) { }

    // ---------------------------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        validateSliderPos();
    }

    // ---------------------------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseEntered(MouseEvent e) { }

    // ---------------------------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(MouseEvent e) {
        validateSliderPos();
    }

} // of class GuiFloatHandler
