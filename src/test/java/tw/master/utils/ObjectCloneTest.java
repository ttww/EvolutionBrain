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

package tw.master.utils;

import java.io.Serializable;

import junit.framework.Assert;

import org.junit.Test;


/**
 * Tests the object copy/clone code.
 *
 * @author Thomas Welsch
 *
 */
public class ObjectCloneTest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Test method for {@link tw.master.utils.ObjectClone#copy(java.lang.Object)}.
     */
    @Test
    public final void testCopyObject() {
        DummyClass dc = new DummyClass();
        dc.sb.append("Hallo");
        for (int i = 0; i < dc.ia.length; i++) {
            dc.ia[i] = new Integer(i);
        }

        DummyClass cloneDc = (DummyClass) ObjectClone.copy(dc);
        Assert.assertEquals(dc.sb.toString(), cloneDc.sb.toString());
        Assert.assertEquals(dc.ia.length, cloneDc.ia.length);

        for (int i = 0; i < dc.ia.length; i++) {
            Assert.assertEquals(i, cloneDc.ia[i].intValue());
        }

    }

    /**
     * Dummy class for testing.
     *
     * @author Thomas Welsch
     */
    private class DummyClass implements Serializable {

        private static final long serialVersionUID = 1L;

        private StringBuffer      sb               = new StringBuffer();

        private Integer[]         ia               = new Integer[10000];
    }
}
