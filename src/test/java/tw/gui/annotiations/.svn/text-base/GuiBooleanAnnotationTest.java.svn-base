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

package tw.gui.annotiations;

import javax.swing.JFrame;

import junit.framework.Assert;

import org.fest.swing.fixture.FrameFixture;
import org.junit.Test;

import tw.master.utils.Utils;


/**
 *
 *
 * @author Thomas Welsch
 *
 */
public class GuiBooleanAnnotationTest
{
    /**
     * Test method for {@link tw.gui.annotiations.GuiBooleanAnnotation()}.
     *
     * @throws Exception
     */
    @Test
    public final void testBoolean() throws Exception
    {
        TestBooleanAnnotation ta = new TestBooleanAnnotation();
        Assert.assertTrue(ta.isTestBool());

        JFrame frame = Utils.showBean(AnnotationGuiGenerator.generateComponent(ta));

        frame.setTitle(AnnotationGuiGenerator.getClassTitle(ta));

        Assert.assertEquals("Boolean-Test",frame.getTitle());

        FrameFixture ff = new FrameFixture(frame);

        Utils.sleep(500);
        ff.checkBox("Boolean-Test.TestBool").click();
        Assert.assertFalse(ta.isTestBool());
        Utils.sleep(500);
        ff.checkBox("Boolean-Test.TestBool").click();
        Assert.assertTrue(ta.isTestBool());
        Utils.sleep(500);
        ff.checkBox("Boolean-Test.TestBool").click();
        Assert.assertFalse(ta.isTestBool());
        Utils.sleep(500);

        frame.dispose();
        ff.robot.cleanUpWithoutDisposingWindows();
    }

    @GuiInfoAnnotation(title = "Boolean-Test")
    class TestBooleanAnnotation {
        private boolean testBool = true;

        @GuiBooleanAnnotation(label = "Test-Label", text = "Test-Text", tooltip = "Test-Tooltip")
        public boolean isTestBool()
        {
            return testBool;
        }

        public void setTestBool(boolean testBool)
        {
            this.testBool = testBool;
        }
    }
}
