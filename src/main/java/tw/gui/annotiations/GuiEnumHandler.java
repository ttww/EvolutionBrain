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
import java.lang.reflect.Method;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import tw.master.utils.Utils;


/**
 * Implements the GUI for a GuiEnumAnnotation.<p>
 *
 * It is build with the GridBagLayout in the following form:<br>
 * <pre>
 *        0               1
 * y    [JLabel]      [JComboBox]
 * y+1  [ Panel from enum class ]
 * </pre>
 *
 * @author Thomas Welsch
 */
class GuiEnumHandler extends BasicHandler implements ActionListener {

    private final   JComboBox	cb;
    private final	JPanel		addPanel;

    private JPanel				infoPanel;
    private GridBagConstraints	infoPanelContraints;

    private Method				getPanelMethod;
    private Method				freePanelMethod;

//	private	Dimension	panelDimension;

    // ---------------------------------------------------------------------------------------------

    /**
     * Create the necessary GUI for boolean annotation.
     *
     * @param p             Panel to add to.
     * @param y             Row in GridbagLayout
     * @param oc            Class of object (if o is null)
     * @param o             Object for getter/setter
     * @param fieldName     Name of field
     * @param label         Label before JCheckbos
     * @param tooltip       Tooltip for components
     * @param enumClass     The enumeration class
     *
     * @throws NoSuchMethodException        Mostly reflection errors
     * @throws IllegalAccessException       Mostly reflection errors
     * @throws InvocationTargetException    Mostly reflection errors
     */
    public GuiEnumHandler(
            JPanel		p,
            int[]		y,
            Class<?>	oc,
            Object		o,
            String		fieldName,
            /*			Method	m,*/
            String		label,
            String		tooltip,
            Class<?>	enumClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        super(oc, o, fieldName, enumClass);


        addPanel = p;

        try {
            Class<?>[] paramsGet	= {enumClass};
            Class<?>[] paramsFree	= {enumClass, JPanel.class};
            getPanelMethod	= enumClass.getDeclaredMethod("getPanel", paramsGet);
            freePanelMethod = enumClass.getDeclaredMethod("freePanel", paramsFree);

            Object[] realParamsGet = { get() };

            infoPanel = (JPanel) getPanelMethod.invoke(o, realParamsGet);
        }
        catch (NoSuchMethodException e) {
            Utils.hookIgnoredException(e);
        }


        Object[] values = enumClass.getEnumConstants();

        cb = new JComboBox(values);
        cb.addActionListener(this);
        cb.setName(p.getName() + "." + fieldName);
        //System.err.println("JComboBox:   Name = "+cb.getName());

        cb.setSelectedItem(get());

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
            gb.gridx	= 0;
            gb.gridy	= y[0];
            gb.fill		= GridBagConstraints.HORIZONTAL;
            setDefaultInsetsForGridX(gb);

            addPanel.add(jl, gb);
        }

        gb = new GridBagConstraints();
        gb.gridx	= 1;
        gb.gridy	= y[0];
        gb.fill		= GridBagConstraints.HORIZONTAL;
        setDefaultInsetsForGridX(gb);

        addPanel.add(cb, gb);

        y[0]++;


        if (infoPanel != null) {

            infoPanelContraints = new GridBagConstraints();
            infoPanelContraints.gridx	= 0;
            infoPanelContraints.gridy	= y[0];
            infoPanelContraints.gridwidth = 3;
            infoPanelContraints.weightx  = 1.1;
            infoPanelContraints.weighty  = 1.1;

            infoPanelContraints.fill		= GridBagConstraints.BOTH;
            //setDefaultInsetsForGridX(infoPanelContraints);

            addPanel.add(infoPanel, infoPanelContraints);
            //p.add(borderPanel,gb);
            y[0]++;

        }
    }

    // ---------------------------------------------------------------------------------------------

    private void set(Object v) throws IllegalAccessException, InvocationTargetException {
        setter.invoke(o, v);
    }

    // ---------------------------------------------------------------------------------------------

    private Object get() throws IllegalAccessException, InvocationTargetException {
        return getter.invoke(o, (Object[]) null);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Calling the setter and update the panel if the user changes the JComboBox.
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        try {

            Object[] realParamsGet = { cb.getSelectedItem() };

            // -------------------------------------------------------------------------------------
            // If we have a JPanel for this enummeration, we need to remove the old one and add
            // the new to the parent JPanel:
            // -------------------------------------------------------------------------------------
            if (infoPanel != null) {
                Object[] realParamsFree = { get(), infoPanel };
                freePanelMethod.invoke(o, realParamsFree);

                addPanel.remove(infoPanel);
                infoPanel = (JPanel) getPanelMethod.invoke(o, realParamsGet);

                addPanel.add(infoPanel, infoPanelContraints);

                addPanel.revalidate();
            }

            set(realParamsGet[0]);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}   // of class GuiEnumHandler
