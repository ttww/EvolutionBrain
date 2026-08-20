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

package tw.gui.image;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.assertj.swing.fixture.FrameFixture;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import tw.master.utils.Utils;



/**
 *
 *
 * @author Thomas Welsch
 *
 */
public class ImagePanelTest {

    private static ImagePanel   ip;

    private static JFrame       frame;

    private static FrameFixture ff;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        BufferedImage img = ImageIO.read(new File("images/Wirbelpalette.png"));
        ip = new ImagePanel("TestPanel", img);
        ip.setName("ImagePanel");

        JPanel p = new JPanel(new BorderLayout());

        p.add(ip, BorderLayout.CENTER);

        frame = Utils.showBean(p, "ImagePanel");
        ff = new FrameFixture(frame);

    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        frame.dispose();
        ff.robot().cleanUpWithoutDisposingWindows();
    }

    /**
     * Test method for {@link tw.gui.image.ImagePanel#ImagePanel(java.awt.image.BufferedImage, java.awt.image.BufferedImage)}.
     */
    @Test
    public final void testImagePanelBufferedImageBufferedImage() {
        ff.show(new Dimension(400, 400));
        Utils.sleep(500);
        ff.show(new Dimension(50, 50));
        Utils.sleep(500);
        ff.show(new Dimension(600, 600));
    }

    @Test
    public final void testImagePanelTriggers() {
        final StringBuffer events = new StringBuffer();

        ff.show(new Dimension(600, 600));

        ff.scrollPane().click();

        ip.addEventReceiver(new EventInterface() {

            @Override
            public void mouseEvent(MouseEvent e)
            {
                if (events.length() != 0) events.append(',');
                events.append(e.getButton() + "|" + e.getPoint().x + "/" + e.getPoint().y);
            }

            @Override
            public void keyEvent(KeyEvent e)
            {
                if (events.length() != 0) events.append(',');
                events.append(e);
            }
        });

        ff.scrollPane().click();
        // ff.pressAndReleaseKeys('a','b'); // not working ?
        Utils.sleep(1000);
        System.err.println("events = |" + events + "|");
        Assert.assertTrue(events.length() > 0);

//        ff.robot.rotateMouseWheel(-10);
//        Utils.sleep(500);
//        ff.robot.rotateMouseWheel(10);
//        Utils.sleep(500);
//        ff.robot.rotateMouseWheel(5);
//        Utils.sleep(500);

    }


}
