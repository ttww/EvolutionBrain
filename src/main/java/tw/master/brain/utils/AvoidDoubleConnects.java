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

package tw.master.brain.utils;

import java.util.HashSet;

/**
 * @author Thomas Welsch
 *
 */
public class AvoidDoubleConnects {

    HashSet<String> gotCons = new HashSet<String>();
    StringBuffer	sb		= new StringBuffer();

    public boolean add(int ns, int nd) {

        if (ns == nd) return false;

        // Build string to check double connections via check in a set:
        sb.setLength(0);
        if (ns < nd) {
            sb.append(Integer.toHexString(ns));
            sb.append('.');
            sb.append(Integer.toHexString(nd));
        }
        else {
            sb.append(Integer.toHexString(nd));
            sb.append('.');
            sb.append(Integer.toHexString(ns));
        }
        String s = sb.toString();
        if (gotCons.contains(s)) return false;
        gotCons.add(s);

        return true;
    }

    public boolean add(int nsCluster, int ns, int ndCluster, int nd) {

        if (nsCluster == ndCluster) return false;

        // Build string to check double connections via check in a set:
        sb.setLength(0);
        if (nsCluster < ndCluster) {
            sb.append(Integer.toHexString(nsCluster));
            sb.append(':');
            sb.append(Integer.toHexString(ns));
            sb.append('.');
            sb.append(Integer.toHexString(ndCluster));
            sb.append(':');
            sb.append(Integer.toHexString(nd));
        }
        else {
            sb.append(Integer.toHexString(ndCluster));
            sb.append(':');
            sb.append(Integer.toHexString(nd));
            sb.append('.');
            sb.append(Integer.toHexString(nsCluster));
            sb.append(':');
            sb.append(Integer.toHexString(ns));
        }
        String s = sb.toString();
        if (gotCons.contains(s)) return false;
        gotCons.add(s);

        return true;
    }

}
