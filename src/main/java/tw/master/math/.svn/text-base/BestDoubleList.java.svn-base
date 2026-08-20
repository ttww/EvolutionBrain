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

package tw.master.math;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

/**
 * This class keeps a TOP-n list of best values and assigned objects.<p> You can specify the number of sorted entries. The TOP-n elements are sorted from hight to low.
 *
 * @author Thomas Welsch
 */
public class BestDoubleList implements Serializable {

    // ---------------------------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    /**
     * Internal class for sorting.
     *
     * @author Thomas Welsch
     */
    class Best implements Comparable<Object>, Serializable {

        private static final long serialVersionUID = 1L;

        double                    f;

        Object                    o;

        @Override
        public int compareTo(Object o1) {
            if (o1 == null) return -1;

            Best ob = (Best) o1;

            //			System.out.println("Compare "+ob.f+"  <>  "+f);

            if (sortOrder == SortOrder.HIGHEST_FIRST) {
                if (ob.f < this.f) return -1;
                if (ob.f > this.f) return +1;
            } else {
                if (ob.f < this.f) return +1;
                if (ob.f > this.f) return -1;
            }

            return 0;
        }

        @Override
        public String toString() {
            return "[" + f + " / '" + o + "']";
        }
    }

    /**
     * @author    Thomas Welsch

     */
    public enum SortOrder {
        HIGHEST_FIRST,
        LOWEST_FIRST
    };

    // ---------------------------------------------------------------------------------------------

    private Best[]                best;

    private HashMap<Object, Best> bestMap    = new HashMap<Object, Best>();

    private int                   bestSize;

    private int                   lastFilled = -1;

    private final SortOrder       sortOrder;

    // ---------------------------------------------------------------------------------------------

    /**
     * Constructs a new BestDoubleList object with given capacity.
     *
     * @param size	Maximum elements in BestDoubleList
     */
    public BestDoubleList(int size) {
        sortOrder = SortOrder.HIGHEST_FIRST;
        bestSize = size;
        best = new Best[size];
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Constructs a new BestDoubleList object with given capacity and sort order.
     *
     * @param size	Maximum elements in BestDoubleList
     * @param order	SortOrder
     */
    public BestDoubleList(int size, SortOrder order) {
        sortOrder = order;
        bestSize = size;
        best = new Best[size];
    }

    // ---------------------------------------------------------------------------------------------


    /**
     * Checks if the given value lies inside the TOP-n list.
     *
     * @param f		Value
     *
     * @return	true/false
     */
    public boolean isBest(double f) {
        if (lastFilled < 0) {
            //System.out.println("isBest first   "+f);
            return true;
        }
        if (lastFilled + 1 < bestSize) {
            //System.out.println("isBest notfull "+f);
            return true;
        }

        if (sortOrder == SortOrder.HIGHEST_FIRST) {
            if (f > best[lastFilled].f) {
                //System.out.println("isBest full "+f+"  last min was "+ best[lastFilled].f);
                return true;
            }
        } else {
            if (f < best[lastFilled].f) {
                //System.out.println("isBest full "+f+"  last max was "+ best[lastFilled].f);
                return true;
            }
        }

        //System.out.println("isBest NO "+f+"  for "+ sortOrder);
        return false;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Adds an element with given value and object.
     *
     * @param f		Value
     * @param o		Object assigned to value
     * 
     * @return		true: Was added (isBest() was true,  false: not added
     */
    public boolean add(double f, Object o) {
        Best oldBest = bestMap.get(o);
        if (oldBest != null) {
            oldBest.f = f;

//			System.err.println("Update old best");
//			System.err.println("U resort "+(lastFilled+1)+ " / "+bestMap.size());

            Arrays.sort(best, 0, lastFilled + 1);
            return true;
        } else {
            if (!isBest(f)) return false;
        }

        if (lastFilled + 1 < bestSize) lastFilled++;

        if (best[lastFilled] == null) {
            best[lastFilled] = new Best();
//			System.err.println("Alloc new best");
        } else {
//			System.err.println("Reuse best");
            bestMap.remove(best[lastFilled].o);
        }

        bestMap.put(o, best[lastFilled]);


        best[lastFilled].f = f;
        best[lastFilled].o = o;

        Arrays.sort(best, 0, lastFilled + 1);
//		System.err.println("R resort "+(lastFilled+1)+ " / "+bestMap.size());
        return true;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean remove(Object o) {
        Best oldBest = bestMap.get(o);
        if (oldBest == null) return false;

        bestMap.remove(o);

        int src = 0;
        while (best[src] != oldBest)
            src++;

        int dest = src;
        src++;
        while (src <= lastFilled)
            best[dest++] = best[src++];
        best[dest] = null; // Delete last element because GC
        lastFilled--;
        return true;
    }

    // ---------------------------------------------------------------------------------------------

    private void writeObject(ObjectOutputStream out) throws IOException {
//		System.err.println("Write BestDoubleList "+bestMap.size());
        out.defaultWriteObject();
//		System.err.println("Write BestDoubleList done");
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
//		System.err.println("Read BestDoubleList");
        in.defaultReadObject();
//		System.err.println("Read done: "+bestMap.size());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the elements in this BestDoubleList. The size is limited through the constructor.
     *
     * @return	Elements in BestDoubleList
     */
    public int size() {
        return lastFilled + 1;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Gets the sorted value with the given index.
     *
     * @param	idx		Sorted element index
     *
     * @return	Value for index
     */
    public double getValue(int idx) {
        Best b = best[idx];
        if (b == null) return Double.NaN;
        return b.f;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Gets the sorted element with the given index.
     *
     * @param	idx		Sorted element index
     *
     * @return	assigned object
     */
    public Object getObject(int idx) {
        return best[idx].o;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Rest the BestDoubleList to original state. Clearing all saved elements.
     */
    public void clear() {
        lastFilled = -1;
        Arrays.fill(best, null);
    }

    public void setSize(int newSize) {
        if (newSize == bestSize) return;

//		System.err.println("Old size   = "+bestSize);
//		System.err.println("New size   = "+newSize);
//		System.err.println("lastFilled = "+lastFilled);

        Best[] old = best;
        best = null; // Trigger null-Pointer if access th to list....

        Best[] newBest = new Best[newSize];

        if (newSize > bestSize) {

            System.arraycopy(old, 0, newBest, 0, lastFilled + 1);

//			System.err.println("New is bigger");
            bestSize = newSize;
            best = newBest;
        } else {
//			System.err.println("New is smaller");
            int i = 0;
            while (i < newSize) {
//				System.err.println("   copy "+i);
                newBest[i] = old[i];
                i++;
            }
            if (lastFilled > newSize - 1) lastFilled = newSize - 1;

            while (i < bestSize) {
                if (old[i] != null && old[i].o != null) bestMap.remove(old[i].o);
//				System.err.println("   free "+i);
                i++;
            }
            bestSize = newSize;
            best = newBest;
//			System.err.println("lastFilled = "+lastFilled+" NEW");
        }

    }

}
