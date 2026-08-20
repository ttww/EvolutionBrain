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

package tw.master.crawler;

import tw.master.mutation.MutationParameters;
import tw.master.utils.Rnd;



public interface MutationCrawlerInterface {

    // Mutations:
    /**
     * @author    Thomas Welsch

     */
    public enum Mutation {
        //	Disable of visionfield neurons
        DISABLE_VISIONFIELD_NEURONS,

        //	Adding Width to visionfield
//		ADD_VISIONFIELD_WIDTH,

        //	Adding Deep  to visionfield
//		ADD_VISIONFIELD_DEEP,

        //	Removing Width to visionfield
        REDUCE_VISIONFIELD_WIDTH,

        //	Removing Deep  to visionfield
        REDUCE_VISIONFIELD_DEEP,

        //	Adding Random Neurons to whole net
//		ADDING_RANDOM_NEURON,

        //	Removing Random Neurons to whole net
        REMOVE_RANDOM_NEURON,

        //	Adding Inter-cluster connects
//		ADDING_CLUSTER_INTERCONNECTS,

        //	Adding new cluster and cluster to cluster connects
//		ADDING_CLUSTER,

        // Removing clusters (not usefull, because of degeneration and auto-cleanup ?)
//		REMOVE_CLUSTER,

        // Change of signal running time (Per brain)
//		CHANGE_BRAIN_SPEED,

        // Change of signal running time (Per neuron)
//		CHANGE_NEURON_SPEED,
        ;

        static Mutation[] all = values();

        public static Mutation getRandomMutation() {
//			if (true) return DISABLE_VISIONFIELD_NEURONS;

            return all[Rnd.rnd(0,all.length-1)];
        }

        public static Mutation getMutationViaIndex(int mutationIdx) {
            return all[mutationIdx];
        }

        public static int getRandomMutationIndex() {
//			if (true) return 0;

            return Rnd.rnd(0,all.length-1);
        }

    }

    public Crawler getMutationCrawler();

    public MutationParameters getMutationParameter();

}
