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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * Implements the GUI for a GuiBooleanAnnotation.<p>
 *
 * It is build with the GridBagLayout in the following form:<br>
 * <pre>
 *    0          1
 * [JLabel]  [JCheckBox]
 * </pre>
 *
 * @author Thomas Welsch
 */
class GuiBooleanHandler extends BasicHandler implements ActionListener {

    private final JCheckBox cb;

    // ---------------------------------------------------------------------------------------------

    /**
     * Create the necessary GUI for boolean annotiation.
     *
     * @param p             Panel to add to.
     * @param y             Row in GridbagLayout
     * @param oc            Class of object (if o is null)
     * @param o             Object for getter/setter
     * @param fieldName     Name of field
     * @param label         Label before JCheckbos
     * @param text          Text for JCheckBox
     * @param tooltip       Tooltip for components
     *
     * @throws NoSuchMethodException        Mostly reflection errors
     * @throws IllegalAccessException       Mostly reflection errors
     * @throws InvocationTargetException    Mostly reflection errors
     */
    public GuiBooleanHandler(
            JPanel      p,
            int         y,
            Class<?>    oc,
            Object      o,
            String      fieldName,
            /*			Method	m,*/
            String      label,
            String      text,
            String      tooltip) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        super(oc, o, fieldName, boolean.class);

        cb = new JCheckBox();
        cb.setText(text);
        cb.addActionListener(this);

        cb.setName(p.getName() + "." + fieldName);
        // System.err.println("JCheckBox:   Name = "+cb.getName());

        cb.setSelected(get());
        if (tooltip != null) {
            cb.setToolTipText(tooltip);
        }

        GridBagConstraints gb;

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

        gb = new GridBagConstraints();
        gb.gridx = 1;
        gb.gridy = y;
        gb.fill = GridBagConstraints.HORIZONTAL;
        setDefaultInsetsForGridX(gb);

        p.add(cb, gb);
    }

    // ---------------------------------------------------------------------------------------------

    private void set(boolean v) throws IllegalAccessException, InvocationTargetException {
        if (v) setter.invoke(o, Boolean.TRUE);
        else
            setter.invoke(o, Boolean.FALSE);
    }

    // ---------------------------------------------------------------------------------------------

    private boolean get() throws IllegalAccessException, InvocationTargetException {
        Boolean b = (Boolean) getter.invoke(o, (Object[]) null);
        return b.booleanValue();
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            set(cb.isSelected());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}   // of class GuiBooleanHandler
