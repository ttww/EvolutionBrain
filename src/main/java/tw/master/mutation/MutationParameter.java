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

import tw.master.utils.Rnd;


public class MutationParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    private GenotypeParameter gen;

    private float             value;

    public MutationParameter(/*String name,float value,*/ GenotypeParameter gen) {
//		this.name		= name;
        this.gen = gen;
//        this.value = value;
        this.value = gen.getLower();
    }

    /**
     * @return
     */
    public float getValue() {
        return value;
    }

    //	public void setValue(float value) {
    // Setting is not useful her, we use the RND functions for getting values;
    // Not used: this.value = value;
    //	}

    public float getRndValue() {
        value = Rnd.rnd(gen.getLower(), gen.getUpper());
        return value;
    }

//	public int getIntValue() {
//		return round(value);
//	}
    public int getIntRndValue() {
        int ivalue = Rnd.rnd(round(gen.getLower()), round(gen.getUpper()));
        value = ivalue;
        return ivalue;
    }

    private int round(float f) {
        if (f < 0) return (int) (f - 0.5f);
        return (int) (f + 0.5f);
    }

    public float getMin() {
        return gen.getLower();
    }

    public float getMax() {
        return gen.getUpper();
    }

}
