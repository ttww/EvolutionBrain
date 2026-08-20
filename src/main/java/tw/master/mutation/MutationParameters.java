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

package tw.master.mutation;

import java.io.Serializable;

import tw.gui.annotiations.GuiMutationGenotypeParameterAnnotation;
import tw.gui.annotiations.GuiMutationPhenotypeParameterAnnotation;


public class MutationParameters implements Serializable {

    private static final long       serialVersionUID            = 1L;

    private static final String     GENO_FORMAT                 = "Range: %2.0f...%2.0f";

    private static final String     PHENO_FORMAT                = "= %2.0f";

    // ---------------------------------------------------------------------------------------------
    @GuiMutationGenotypeParameterAnnotation(
            label = "visionfieldWidth",
            tooltip = "Width of see field neurons",
            format = GENO_FORMAT,
            min = 3,
            max = 20)
            public static GenotypeParameter visionfieldWidthGen         = new GenotypeParameter(3, 10);

    @GuiMutationPhenotypeParameterAnnotation(
            label = "visionfieldWidth",
            tooltip = "Width of see field neurons",
            format = PHENO_FORMAT)
            public MutationParameter        visionfieldWidth            = new MutationParameter(visionfieldWidthGen);

    // ---------------------------------------------------------------------------------------------

    @GuiMutationGenotypeParameterAnnotation(
            label = "visionfieldDeep",
            tooltip = "Deep of see field neurons",
            format = GENO_FORMAT,
            min = 1,
            max = 20)
            public static GenotypeParameter visionfieldDeepGen          = new GenotypeParameter(1, 10);

    @GuiMutationPhenotypeParameterAnnotation(
            label = "visionfieldDeep",
            tooltip = "Deep of see field neurons",
            format = PHENO_FORMAT)
            public MutationParameter        visionfieldDeep             = new MutationParameter(visionfieldDeepGen);

    // ---------------------------------------------------------------------------------------------

    @GuiMutationGenotypeParameterAnnotation(
            label = "brainClusterCount",
            tooltip = "Number of brain clusters",
            format = GENO_FORMAT,
            min = 1,
            max = 20)
            public static GenotypeParameter brainClusterCountGen        = new GenotypeParameter(1, 7);

    @GuiMutationPhenotypeParameterAnnotation(
            label = "brainClusterCount",
            tooltip = "Number of brain clusters",
            format = PHENO_FORMAT)
            public MutationParameter        brainClusterCount           = new MutationParameter(brainClusterCountGen);

    // ---------------------------------------------------------------------------------------------

    @GuiMutationGenotypeParameterAnnotation(
            label = "clusterConnectCount",
            tooltip = "Number of interconnect runs between brain clusters",
            format = GENO_FORMAT,
            min = 1,
            max = 20)
            public static GenotypeParameter clusterConnectCountGen      = new GenotypeParameter(10, 20);

    @GuiMutationPhenotypeParameterAnnotation(
            label = "clusterConnectCount",
            tooltip = "Number of interconnect runs between brain clusters",
            format = PHENO_FORMAT)
            public MutationParameter        clusterConnectCount         = new MutationParameter(clusterConnectCountGen);

    // ---------------------------------------------------------------------------------------------

    @GuiMutationGenotypeParameterAnnotation(
            label = "clusterSize",
            tooltip = "Block-Size of cluster (not radius :-))",
            format = GENO_FORMAT,
            min = 1,
            max = 10)
            public static GenotypeParameter clusterSizeGen              = new GenotypeParameter(2, 5);

    @GuiMutationPhenotypeParameterAnnotation(
            label = "clusterSize",
            tooltip = "Block-Size of cluster (not radius :-))",
            format = PHENO_FORMAT)
            public MutationParameter        clusterSize                 = new MutationParameter(clusterSizeGen);

    // ---------------------------------------------------------------------------------------------

    @GuiMutationGenotypeParameterAnnotation(
            label = "clusterNeuronCount",
            tooltip = "Number of Neurons in Cluster",
            format = GENO_FORMAT,
            min = 2,
            max = 100)
            public static GenotypeParameter clusterNeuronCountGen       = new GenotypeParameter(10, 20);

    @GuiMutationPhenotypeParameterAnnotation(
            label = "clusterNeuronCount",
            tooltip = "Number of Neurons in Cluster",
            format = PHENO_FORMAT)
            public MutationParameter        clusterNeuronCount          = new MutationParameter(clusterNeuronCountGen);

    // ---------------------------------------------------------------------------------------------

    @GuiMutationGenotypeParameterAnnotation(
            label = "clusterInterConnectCount",
            tooltip = "Number of synapses per interconnect runs between brain clusters",
            format = GENO_FORMAT,
            min = 2,
            max = 50)
            public static GenotypeParameter clusterInterConnectCountGen = new GenotypeParameter(5, 25);

    /**
     * Number of synapses per interconnect runs between brain clusters.
     */
    @GuiMutationPhenotypeParameterAnnotation(
            label = "clusterInterConnectCount",
            tooltip = "Number of synapses per interconnect runs between brain clusters",
            format = PHENO_FORMAT)
            public MutationParameter        clusterInterConnectCount    = new MutationParameter(clusterInterConnectCountGen);

    // ---------------------------------------------------------------------------------------------

}
