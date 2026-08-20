/*
 *	This file is part of the EvolutionBrain project.
 *
 *	Copyright (c) 2011 by Thomas Welsch (ttww@gmx.de). All rights reserved.
 *
 *  Parts of this code is based on code of jrfisher@csupomona.edu from
 *     http://www.csupomona.edu/~jrfisher/www/prolog_tutorial/logic_topics/visualize/visualize.html
 *  No copyright notice found at 17.04.2011, please let me know if this use is not ok.
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;


/**
 *
 *
 * @author Thomas Welsch
 *
 */
public class TreeNode implements Serializable {

    private static final long     serialVersionUID = 1L;

    private String                nodeName;

    @SuppressWarnings("unused")
    private TreeNode              root;

    private TreeNode              parent;

    private LinkedList<TreeNode>  childs;

    private TreeNodeInfoInterface nodeData;

    // other fields for drawing
    private static Font           font             = new Font("monospaced", Font.PLAIN, 11);

    private static Color          background       = Color.lightGray;

    private static Color          leafColor        = Color.green;

    private static Color          fontColor        = Color.black;

    private static Color          edgeColor        = Color.darkGray;

    Rectangle                     treeRect         = new Rectangle();

    Rectangle                     nodeRect         = new Rectangle();

    boolean                       isActiveFrame    = false;

    boolean                       showChilds       = true;


    // ---------------------------------------------------------------------------------------------

    /**
     * @param nodeName
     */
    public TreeNode(TreeNode root, TreeNode parent, String nodeName) {
        this.root = root;
        this.parent = parent;
        this.nodeName = nodeName;

        if (root == null) this.root = this;

    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Clears all nodes below this node.
     */
    public void clear() {
        if (childs != null) childs.clear();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Lookup the childName inside this node.
     *
     * @param childName
     * @return The found node or null
     */
    public TreeNode get(String childName) {
        if (childs == null) return null;

        for (TreeNode n : childs) {
            if (n.getNodeName().equals(childName)) return n;
        }
        return null;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * @return
     */
    public TreeNode getParent() {
        return parent;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * @return
     */
    public String getNodeName() {
        return nodeName;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Adds a new child to the current tree node.
     *
     * @param childNode
     */
    public void addChild(TreeNode childNode) {
        if (childs == null) childs = new LinkedList<TreeNode>();
        childs.add(childNode);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Removes the child from the current tree node.
     *
     * @param childNode
     */
    public void removeChild(TreeNode childNode) {
        if (childs == null) return;
        childs.remove(childNode);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * @return
     */
    public Iterator<TreeNode> getChilds() {
        if (childs == null) return null;
        return childs.listIterator();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Is this a leaf ? (no subelements in tree)
     *
     * @return
     */
    public boolean isLeaf() {
        if (childs == null || childs.size() == 0) return true;
        return false;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * @param nodeData
     */
    public void setNodeData(TreeNodeInfoInterface nodeData) {
        this.nodeData = nodeData;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * @return
     */
    public TreeNodeInfoInterface getNodeData() {
        return nodeData;
    }

    // ---------------------------------------------------------------------------------------------

    private final int LAYOUT_X_SPACE_X = 20;

    private final int LAYOUT_X_SPACE_Y = 10;

    @Override
    public String toString() {
        if (nodeData != null) return nodeData.getText();
        return nodeName;
    }

    /**
     * Get the width of this node when drawn.
     *
     * @param   g2 Graphic context
     * @return
     */
    public int getNodeWidth(Graphics2D g2) {
        // g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        int w = fm.stringWidth(toString());
        int margin = fm.stringWidth(" ");
        return 2 * margin + w;
    }

    /**
     * Get the height of this node when drawn.
     *
     * @param   g2 Graphic context
     * @return
     */
    public int getNodeHeight(Graphics2D g2) {
        // g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        int h = fm.getHeight();
        return 2 * h;
    }

    public int getTreeWidth(Graphics2D g2) {
        int w = getNodeWidth(g2);
        if (!showChilds || childs == null) return w;

        if (layoutX) {
            int csMax = 0;
            for (TreeNode node : childs) {
                int cs = node.getTreeWidth(g2) + LAYOUT_X_SPACE_X;
                if (cs > csMax) csMax = cs;
            }
            return w + csMax;
        } else {
            int cs = 0;
            for (TreeNode node : childs)
                cs += node.getTreeWidth(g2);

            return Math.max(w, cs);
        }

    }

    public int getTreeHeight(Graphics2D g2) {
        int h = getNodeHeight(g2);
        if (!showChilds || childs == null) return h;

        if (layoutX) {
            int cs = 0;
            for (TreeNode node : childs)
                cs += node.getTreeHeight(g2) + LAYOUT_X_SPACE_Y;
            //cs += LAYOUT_X_SPACE_Y * childs.size();
            return Math.max(h, cs);
        } else {
            int csMax = 0;
            for (TreeNode node : childs) {
                int cs = node.getTreeHeight(g2);
                if (cs > csMax) csMax = cs;
            }
            return h + csMax;
        }
    }

    public void drawNode(Graphics2D g2, int graphicsWidth, int graphicsHeight, float x, float y) {
        //g.setFont(this.font);
        FontMetrics fm = g2.getFontMetrics(TreeNode.font);

        int width = fm.stringWidth(toString());

        int margin = fm.stringWidth(" ");
        int height = fm.getHeight();

        int xi = round(x);
        int yi = round(y);

        nodeRect.x = xi;
        nodeRect.y = yi;
        nodeRect.width = 2 * margin + width;
        nodeRect.height = 2 * height;

        int tw = getTreeWidth(g2);
        int th = getTreeHeight(g2);
        if (layoutX) {
            treeRect.x = xi;
            treeRect.y = round(yi - th / 2f + nodeRect.height / 2f);
            treeRect.width = tw;
//            treeRect.width  = 2*tw - nodeRect.width;
            treeRect.height = th;
        } else {
            treeRect.x = round(x - tw / 2 + nodeRect.width / 2);
            treeRect.y = yi;
            treeRect.width = tw;
            treeRect.height = 2 * th - nodeRect.height;
        }

        Color wantColor = null;
        if (nodeData != null) wantColor = nodeData.getColor();

        if (wantColor == null) {
            if (isLeaf()) wantColor = leafColor;
            else {
                if (nodeData != null) {
                    wantColor = leafColor;
                } else {
                    wantColor = TreeNode.background;
                }
            }
        }

        if (!showChilds && !isLeaf()) {
            g2.setColor(Color.YELLOW);
            g2.fillRoundRect(nodeRect.x, nodeRect.y, nodeRect.width, nodeRect.height, 20, 20);
        } else {
            g2.setColor(wantColor);
            g2.fill3DRect(nodeRect.x, nodeRect.y, nodeRect.width, nodeRect.height, true);
        }
//        if (nodeData == null) {
//            g2.setColor(Color.RED);
//            drawLine(g2,nodeRect.x,nodeRect.y + nodeRect.height/2f,nodeRect.x + nodeRect.width,nodeRect.y + nodeRect.height/2f);
//        }
        g2.setColor(TreeNode.fontColor);
        g2.drawString(toString(), round(x + margin), round(y + 1.3f * height));


        if (isActiveFrame) {
            g2.setColor(Color.RED);
            Stroke oldStroke = g2.getStroke();
            g2.setStroke(new BasicStroke(2));
            if (!showChilds && !isLeaf()) {
                g2.drawRoundRect(nodeRect.x, nodeRect.y, nodeRect.width, nodeRect.height, 20, 20);
            } else {
                g2.draw(nodeRect);
            }
            g2.setStroke(oldStroke);
            // g2.setColor(Color.YELLOW);
            // g2.draw(treeRect);
        }

        if (nodeData != null) {
            float rank = nodeData.getBarValue();
            if (rank >= 0) {
                float scale = (float) g2.getTransform().getScaleX();
                barRect.x = round(graphicsWidth * 0.9f / scale);
                barRect.y = nodeRect.y;
                barRect.width = round(graphicsWidth * 0.09f / scale);
                barRect.height = nodeRect.height;
                if (barRect.height > 10) barRect.height = 10;
                g2.setColor(Color.GRAY);
                g2.fill(barRect);

                int w = barRect.width;
                barRect.width = round(barRect.width * rank * 0.01f);
                g2.setColor(Color.GREEN);
                g2.fill(barRect);

                barRect.width = w;

                g2.setColor(Color.BLACK);
                g2.draw(barRect);

            }
        }

    }

    Rectangle barRect = new Rectangle();

    boolean   layoutX = true;

    private static int round(float f) {
        if (f > 0) return (int) (f + 0.5f);
        return (int) (f - 0.5f);
    }

    /**
     * @param g2
     * @param x
     * @param y
     */
    public void drawRecursiveTree(TreeNode node, Graphics2D g2, int graphicsWidth, int graphicsHeight, float x, float y) {
        g2.setFont(font);
        if (layoutX) {
            int th = node.getTreeHeight(g2);

            int nh = node.getNodeHeight(g2);
            int w = node.getNodeWidth(g2);

            node.drawNode(g2, graphicsWidth, graphicsHeight, x, y + (th - nh) / 2f);

            if (!node.showChilds) { return; }

            Iterator<TreeNode> nodeChilds = node.getChilds();
            if (nodeChilds == null) return;

            float dy = y; // Build from top to down
            while (nodeChilds.hasNext()) {
                TreeNode child = nodeChilds.next();

                int ch = child.getTreeHeight(g2) + LAYOUT_X_SPACE_Y;

                g2.setColor(TreeNode.edgeColor);

                float xn = x + w + LAYOUT_X_SPACE_X;
                float yn = dy + LAYOUT_X_SPACE_Y / 2;

                drawLine(g2, x + w, y + th / 2f, xn, yn + ch / 2f - LAYOUT_X_SPACE_Y / 2);

                drawRecursiveTree(child, g2, graphicsWidth, graphicsHeight, xn, yn);
                // move over to down on drawing pane
                dy += ch;
            }

        } else {
            int tw = node.getTreeWidth(g2);

            int nw = node.getNodeWidth(g2);
            int h = node.getNodeHeight(g2);

            node.drawNode(g2, graphicsWidth, graphicsHeight, x + (tw - nw) / 2, y);

            if (!node.showChilds) return;

            Iterator<TreeNode> nodeChilds = node.getChilds();
            if (nodeChilds == null) return;

            float dx = x; // Build from left to right
            while (nodeChilds.hasNext()) {
                TreeNode child = nodeChilds.next();

                int cw = child.getTreeWidth(g2);

                g2.setColor(TreeNode.edgeColor);
                drawLine(g2, x + tw / 2f, y + h, dx + cw / 2f, y + 2 * h);

                drawRecursiveTree(child, g2, graphicsWidth, graphicsHeight, dx, y + 2 * h);
                // move over to right on drawing pane
                dx += cw;
            }

        }

    }

    /**
     * @param point
     * @return
     */
    public TreeNode findComponent(Point point) {
        return findComponent(this, point);
    }

    private TreeNode findComponent(TreeNode node, Point point) {
        if (node.nodeRect.contains(point)) return node;

        if (!node.showChilds) return null;

        Iterator<TreeNode> nodeChilds = node.getChilds();
        if (nodeChilds == null) return null;

        while (nodeChilds.hasNext()) {
            TreeNode child = nodeChilds.next();

            child = child.findComponent(point);
            if (child != null) return child;
        }

        return null;
    }

    private Line2D.Float line = new Line2D.Float();

    private void drawLine(Graphics2D g2, float x1, float y1, float x2, float y2) {
        line.x1 = x1;
        line.y1 = y1;
        line.x2 = x2;
        line.y2 = y2;
        g2.draw(line);
    }



} // of class TreeNode
