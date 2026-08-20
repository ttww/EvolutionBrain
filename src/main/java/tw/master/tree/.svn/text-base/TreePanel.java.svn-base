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

package tw.master.tree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import tw.master.utils.SoundUtils;
import tw.master.utils.Utils;


/**
 *
 *
 * @author Thomas Welsch
 *
 */
public class TreePanel extends JPanel implements DragMoveEventPassThroughInterface {

    private static final long serialVersionUID = 1L;

    TreeTable                 data;

    JScrollPane               scrollpane;


    /**
     * @param data
     */
    public TreePanel(TreeTable data) {
        this.data = data;

        setLayout(new BorderLayout());

        InnerDrawerPanel idp = new InnerDrawerPanel();
        // The events are rooted through the DragMoverListener(), because we can't override the
        // listeners.
        // idp.addMouseListener(this);
        // idp.addMouseMotionListener(this);
        // idp.addMouseWheelListener(this);

        idp.setPreferredSize(new Dimension(300, 300));
        scrollpane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollpane.setViewportView(idp);

        this.add(scrollpane, BorderLayout.CENTER);

        new DragMoverListener(scrollpane.getViewport(), idp, this);

        idp.setPreferredSize(new Dimension(300, 300));
    }


    class InnerDrawerPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        /* (non-Javadoc)
         * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
         */
        @Override
        protected void paintComponent(Graphics g) {
            //super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            Utils.setQuality(g2);

            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.scale(zoom, zoom);

            TreeNode root = data.getRoot();

            synchronized (root) {
                root.drawRecursiveTree(root, g2, getWidth(), getHeight(), 20, 20);
            }

            setPreferredSize(new Dimension((int) (zoom * root.treeRect.width) + 40,
                    (int) (zoom * root.treeRect.height) + 40));
            this.invalidate();
            scrollpane.revalidate();
        }
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
//        System.err.println("Wheel move "+e);
        zoom += e.getWheelRotation() * -0.02f;

        zoom = Math.max(0.02f, zoom);
        zoom = Math.min(10f, zoom);
        repaint();
    }

    float zoom = 1;

    public void mouseDragged(MouseEvent e) {
        System.err.println("Drag " + e);
    }

    TreeNode lastFoundNode;

    private void mouseMoved(MouseEvent e, Point displacement) {
        TreeNode root = data.getRoot();
        TreeNode foundNode;
        synchronized (root) {
            foundNode = root.findComponent(adjustZoom(e.getPoint()));
        }

        if (foundNode == null) {
            if (lastFoundNode != null) {
                lastFoundNode.isActiveFrame = false;
                repaintRect(lastFoundNode.nodeRect, displacement);
            }
        } else {
            if (lastFoundNode != null) {
                lastFoundNode.isActiveFrame = false;
                repaintRect(lastFoundNode.nodeRect, displacement);
            }
            if (!foundNode.isActiveFrame) {
                foundNode.isActiveFrame = true;
                lastFoundNode = foundNode;
                repaintRect(lastFoundNode.nodeRect, displacement);
            }
        }
    }

    /**
     * @param nodeRect
     */
    private void repaintRect(Rectangle rr, Point displacement) {
        //repaint();
        // Update with frame, for getting some rims :-)
        Rectangle r = new Rectangle(
                (int) ((rr.x - 2) * zoom + 0.5f) - displacement.x,
                (int) ((rr.y - 2) * zoom + 0.5f) - displacement.y,
                (int) ((rr.width + 6) * zoom + 0.5f),
                (int) ((rr.height + 6) * zoom + 0.5f));
        repaint(r);
    }

    private void mouseClicked(MouseEvent e) {
        TreeNode root = data.getRoot();
        TreeNode foundNode;
        synchronized (root) {
            foundNode = root.findComponent(adjustZoom(e.getPoint()));
        }

        if (foundNode != null) {
            TreeNodeInfoInterface nodeData = foundNode.getNodeData();
            if (nodeData != null) {
                SoundUtils.playTink();
                nodeData.treeNodeClicked();
            } else {
                foundNode.showChilds = !foundNode.showChilds;
            }
            repaint();
        }
    }

    /**
     * @param point
     * @return
     */
    private Point adjustZoom(Point point) {
        point.x = (int) (point.x / zoom + 0.5f);
        point.y = (int) (point.y / zoom + 0.5f);
        return point;
    }


    /* (non-Javadoc)
     * @see tw.master.tree.DragMoveEventPassThroughInterface#mouseEvene(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseEvent(MouseEvent event, Point displacement) {
        switch (event.getID()) {
            case MouseEvent.MOUSE_CLICKED:
                mouseClicked(event);
                break;
            case MouseEvent.MOUSE_MOVED:
                mouseMoved(event, displacement);
                break;
            case MouseEvent.MOUSE_WHEEL:
                mouseWheelMoved((MouseWheelEvent) event);
                break;
            default:
                //System.err.println(event.getID()+": ??? event = "+event);
        }
    }



}
