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

package tw.gui;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.junit.Test;

import tw.gui.SimpleImagePanel.ScaleMode;
import tw.master.utils.Utils;


/**
 *
 *
 * @author Thomas Welsch
 *
 */
public class SimpleImagePanelTest {

    /**
     * Test method for {@link tw.gui.SimpleImagePanel#SimpleImagePanel(java.awt.image.BufferedImage)}.
     *
     * @throws IOException  ImageIO read failure
     */
    @Test
    public final void testSimpleImagePanelBufferedImage() throws IOException {

        BufferedImage img = ImageIO.read(new File("images/Wirbelpalette.png"));
        SimpleImagePanel ip = new SimpleImagePanel(img);
        ip.setName("SimpleImagePanel");

        JPanel p = new JPanel(new BorderLayout());

        p.add(ip, BorderLayout.CENTER);

        JFrame frame = Utils.showBean(p, "ImagePanel");
        frame.setSize(800, 800);

        for (ScaleMode s : ScaleMode.values()) {
            ip.setScaleMode(s);
            Utils.sleep(1000);
        }

        frame.dispose();
    }

}
