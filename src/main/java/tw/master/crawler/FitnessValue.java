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

import java.io.Serializable;


/**
 * @author Thomas Welsch
 *
 */
public final class FitnessValue implements Serializable {

    private static final long	serialVersionUID	= 1L;

    public			float	fitness;
    public final	float	importance;
    public final	String	what;
    public final	String	shortWhat;

    public FitnessValue(String what, String shortWhat, float importance) {
        this.what		= what;
        this.shortWhat	= shortWhat;
        this.importance = importance;
    }

    public final float getValue() {

        return fitness * importance;
    }
}

