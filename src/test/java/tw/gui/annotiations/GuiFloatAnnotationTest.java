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
import javax.swing.JPanel;

import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JSliderFixture;
import org.junit.Test;

import junit.framework.Assert;
import tw.master.math.MathUtils;
import tw.master.utils.Utils;


/**
 * Test class for demonstrate and test the GuiFloatAnnotation.
 *
 * @author Thomas Welsch
 *
 */
public class GuiFloatAnnotationTest {

    /**
     * Test method for {@link tw.gui.annotiations.GuiFloatAnnotation()}.
     *
     * @throws Exception    Mostly reflection errors
     */
    @Test
    public final void testBoolean() throws Exception {
        // -----------------------------------------------------------------------------------------
        // Generate test object which contains one float and the associated getter/setter:
        // -----------------------------------------------------------------------------------------
        TestFloatAnnotation ta = new TestFloatAnnotation();

        // -----------------------------------------------------------------------------------------
        // Generate panel from the object:
        // -----------------------------------------------------------------------------------------
        JPanel panel = AnnotationGuiGenerator.generateComponent(ta);

        // -----------------------------------------------------------------------------------------
        // Display the panel with Utils class (put it in a frame and show it)
        // -----------------------------------------------------------------------------------------
        JFrame frame = Utils.showBean(panel);

        // -----------------------------------------------------------------------------------------
        // Set the Title of the frame, get it from the @GuiInfoAnnotation:
        // -----------------------------------------------------------------------------------------
        frame.setTitle(AnnotationGuiGenerator.getClassTitle(ta));

        // -----------------------------------------------------------------------------------------
        // Starting the tests:
        // -----------------------------------------------------------------------------------------
        Assert.assertTrue(MathUtils.equals_0_00001(ta.getTestFloat(), 15));
        Assert.assertEquals("Float-Test", frame.getTitle());

        FrameFixture ff = new FrameFixture(frame);
        JSliderFixture sliderF = ff.slider("Float-Test.TestFloat");

        sliderF.slideToMinimum();
        Assert.assertTrue(MathUtils.equals_0_00001(ta.getTestFloat(), 10));
        Utils.sleep(500);

        sliderF.slideToMaximum();
        Assert.assertTrue(MathUtils.equals_0_00001(ta.getTestFloat(), 20));
        Utils.sleep(500);

        frame.dispose();
        ff.robot().cleanUpWithoutDisposingWindows();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Test-Class for annotations.
     */
    @GuiInfoAnnotation(title = "Float-Test")
    class TestFloatAnnotation {

        /**
         * Simple float property.
         */
        private float testFloat = 15;

        // -----------------------------------------------------------------------------------------

        /**
         * Return the testFloat property.
         *
         * @return  testFloat
         */
        @GuiFloatAnnotation(label = "Test-Label", tooltip = "Test-Tooltip", format = "%5.2f", min = 10, max = 20)
        public float getTestFloat() {
            return testFloat;
        }

        // -----------------------------------------------------------------------------------------

        /**
         * Set the testFloat property.
         *
         * @param testFloat     New value
         */
        public void setTestFloat(float testFloat) {
            this.testFloat = testFloat;
        }

    } // of inner class TestFloatAnnotation

} // of class GuiFloatAnnotationTest
