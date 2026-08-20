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

package tw.master.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import tw.gui.annotiations.AnnotationGuiGenerator;
import tw.gui.annotiations.GuiMutationGenotypeParameterAnnotation;
import tw.gui.annotiations.GuiMutationPhenotypeParameterAnnotation;
import tw.master.mutation.MutationParameters;


public class MutationParameterPanel extends JPanel {

    public enum MutationPatameterType { GenoType, PhenoType };

    private static final long	serialVersionUID	= 1L;

    private	JScrollPane 			scrollPane;
    private	JPanel					lastPanel;

    private	MutationPatameterType	which;


    public MutationParameterPanel(MutationParameters mutationParameters,MutationPatameterType which) {
        super(new BorderLayout());

        this.which = which;

        lastPanel = getPanel(mutationParameters);

        scrollPane = new JScrollPane(lastPanel);

        this.add(scrollPane,BorderLayout.CENTER);

    }

    private JPanel getPanel(MutationParameters mutationParameters) {
        JPanel ret = null;

        try {
            switch (which) {
                case GenoType:
                    ret = AnnotationGuiGenerator.generateComponent(mutationParameters.getClass(),GuiMutationGenotypeParameterAnnotation.class);
                    break;
                case PhenoType:
                    ret = AnnotationGuiGenerator.generateComponent(mutationParameters,GuiMutationPhenotypeParameterAnnotation.class);
                    break;
                default:
                    break;
            } // switch
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public void updatePanel(MutationParameters mutationParameters) {

        try {
            JPanel p = getPanel(mutationParameters);

            scrollPane.remove(lastPanel);
            lastPanel = p;
            scrollPane.add(lastPanel);
            scrollPane.setViewportView(lastPanel);
            scrollPane.revalidate();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
