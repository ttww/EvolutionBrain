/*
 *	This file is part of the EvolutionBrain project.
 *
 *	Copyright (c) 2011 by Thomas Welsch (ttww@gmx.de). All rights reserved.
 *
 *	EvolutionBrain is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU Lesser General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	EvolutionBrain is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU Lesser General Public License for more details.
 *
 *	You should have received a copy of the GNU Lesser General Public License
 *	along with EvolutionBrain.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package tw.master.brain;

import java.io.Serializable;


/**
 *
 *
 * @author Thomas Welsch
 *
 */
class FloatingArray implements Serializable {

    // TODO: Automatic adjustment not working jet....
    public static int         MAX_SYNAPSE_LEN  = 100;

    private static final long serialVersionUID = 1L;

    // OPS?? Change to FloatingArray ? Mix between FloatingArray and PotRunArray, leftover from
    // separating the classes.... CHANGE and REFACTORING !!! Grrrr..... :-)
    PotRunArray[]             buf;

    long                      baseIndex;

    long                      maxIndex;

    public FloatingArray() {
        //System.err.println("Alloc new size "+MAX_SYNAPSE_LEN);
        buf = new PotRunArray[MAX_SYNAPSE_LEN];
    }

    public PotRunArray get(long i) {
        // System.err.println("GET : "+i+"   baseIndex="+baseIndex+"  maxIndex = "+maxIndex);
        // if (i >= maxIndex) return null;
        i -= baseIndex;
        if (i < 0 || i >= buf.length) return null;
        return buf[(int) i];
    }

    public boolean canReuse() {
        if (buf.length == MAX_SYNAPSE_LEN) return true;
        return false;
    }

    public void set(long i, PotRunArray o) {
        // System.err.println("SET : "+i+"   baseIndex="+baseIndex+"  maxIndex = "+maxIndex);
        // if (i>maxIndex) maxIndex = i;
        int ii = (int) (i - baseIndex);
        // System.err.println("SET : "+i+" used");
        try {
            buf[ii] = o;
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();

            System.err.println("BAD SET : " + i + " --> " + ii + "   baseIndex=" + baseIndex + "  maxIndex = "
                    + maxIndex + "  max int = " + Integer.MAX_VALUE);

            System.err.println("\nAdjust tw.master.brain.FloatingArray.MAX_SYNAPSE_LEN parameter !\n");
            throw e;
        }
    }

    public void slip() {
        int j = 0;
        int i = 1;

        while (i < buf.length)
            buf[j++] = buf[i++];
        buf[j] = null; // clear top

        if (baseIndex + 1 < baseIndex) throw new IllegalArgumentException("overrun");
        baseIndex++;
        // System.err.println("Slip : baseIndex ="+baseIndex);
    }


}
