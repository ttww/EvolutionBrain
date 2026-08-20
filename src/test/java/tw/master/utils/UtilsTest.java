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

package tw.master.utils;

import org.junit.Test;


/**
 * @author Thomas Welsch
 *
 */
public class UtilsTest {

//	/**
//	 * Test method for {@link tw.master.utils.Utils#isEmpty(java.lang.String)}.
//	 */
//	@Test
//	public void testIsEmpty() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link tw.master.utils.Utils#sleep(long)}.
//	 */
//	@Test
//	public void testSleep() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link tw.master.utils.Utils#stringReplace(java.lang.String, java.lang.String, java.lang.String)}.
//	 */
//	@Test
//	public void testStringReplace() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link tw.master.utils.Utils#showBean(javax.swing.JPanel)}.
//	 */
//	@Test
//	public void testShowBeanJPanel() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link tw.master.utils.Utils#showBean(javax.swing.JPanel, java.lang.String)}.
//	 */
//	@Test
//	public void testShowBeanJPanelString() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link tw.master.utils.Utils#showBean(javax.swing.JPanel, java.lang.String, boolean)}.
//	 */
//	@Test
//	public void testShowBeanJPanelStringBoolean() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link tw.master.utils.Utils#readAttr(java.lang.String, java.lang.String)}.
//	 */
//	@Test
//	public void testReadAttr() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link tw.master.utils.Utils#readFileInList(java.io.InputStream, java.lang.String)}.
//	 */
//	@Test
//	public void testReadFileInList() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link tw.master.utils.Utils#replaceUniEncoding(java.lang.String)}.
//	 */
//	@Test
//	public void testReplaceUniEncoding() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link tw.master.utils.Utils#toStacktrace(java.lang.Throwable)}.
//	 */
//	@Test
//	public void testToStacktrace() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link tw.master.utils.Utils#getStacktrace()}.
//	 */
//	@Test
//	public void testGetStacktrace() {
//		fail("Not yet implemented");
//	}

    /**
     * Test method for {@link tw.master.utils.Utils#getActualSourceAndLine()}.
     */
    @Test
    public void testGetActualSourceAndLine() {
        String ret = Utils.getActualSourceAndLine();
        org.junit.Assert.assertEquals("UtilsTest.java:124", ret);	// Adjust this if line is moved :-)
    }

//	/**
//	 * Test method for {@link tw.master.utils.Utils#getInputText(java.lang.String, java.lang.String, java.lang.String)}.
//	 */
//	@Test
//	public void testGetInputText() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link tw.master.utils.Utils#formatDouble(double, int, int)}.
//	 */
//	@Test
//	public void testFormatDouble() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link tw.master.utils.Utils#setQuality(java.awt.Graphics2D)}.
//	 */
//	@Test
//	public void testSetQuality() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link tw.master.utils.Utils#showTimeUsed(long, java.lang.String)}.
//	 */
//	@Test
//	public void testShowTimeUsed() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link tw.master.utils.Utils#colorToName(java.awt.Color)}.
//	 */
//	@Test
//	public void testColorToName() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link tw.master.utils.Utils#stripChars(java.lang.String, java.lang.String)}.
//	 */
//	@Test
//	public void testStripChars() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link tw.master.utils.Utils#charCount(java.lang.String, char)}.
//	 */
//	@Test
//	public void testCharCount() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link tw.master.utils.Utils#describeImage(java.awt.image.BufferedImage)}.
//	 */
//	@Test
//	public void testDescribeImage() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link tw.master.utils.Utils#typeName(int)}.
//	 */
//	@Test
//	public void testTypeNameInt() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link tw.master.utils.Utils#typeName(int, java.awt.image.BufferedImage)}.
//	 */
//	@Test
//	public void testTypeNameIntBufferedImage() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link tw.master.utils.Utils#transferTypeName(int)}.
//	 */
//	@Test
//	public void testTransferTypeName() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link tw.master.utils.Utils#typeName(java.awt.color.ColorSpace)}.
//	 */
//	@Test
//	public void testTypeNameColorSpace() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link tw.master.utils.Utils#toImage(short[], int, int)}.
//	 */
//	@Test
//	public void testToImageShortArrayIntInt() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link tw.master.utils.Utils#toImage(byte[], int, int)}.
//	 */
//	@Test
//	public void testToImageByteArrayIntInt() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link tw.master.utils.Utils#toImage2(byte[], int, int)}.
//	 */
//	@Test
//	public void testToImage2() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link tw.master.utils.Utils#toImage(int[], int, int)}.
//	 */
//	@Test
//	public void testToImageIntArrayIntInt() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link tw.master.utils.Utils#skipPaint(java.awt.Component)}.
//	 */
//	@Test
//	public void testSkipPaint() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link tw.master.utils.Utils#repaintCtrl(java.awt.Component, long)}.
//	 */
//	@Test
//	public void testRepaintCtrl() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link tw.master.utils.Utils#writeImage(java.awt.image.BufferedImage, java.lang.String)}.
//	 */
//	@Test
//	public void testWriteImage() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link tw.master.utils.Utils#isFrontmostApplication()}.
//	 */
//	@Test
//	public void testIsFrontmostApplication() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link tw.master.utils.Utils#makeBlureImage(java.awt.image.BufferedImage)}.
//	 */
//	@Test
//	public void testMakeBlureImage() {
//		fail("Not yet implemented");
//	}

}
