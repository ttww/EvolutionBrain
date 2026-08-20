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

import java.awt.Color;
import java.io.Serializable;
import java.util.Iterator;


/**
 *
 *
 * @author Thomas Welsch
 *
 */
public class TreeTable implements Serializable
{
    private static final long serialVersionUID = 1L;

    TreeNode    root = new TreeNode(null,null,"root");

    // ---------------------------------------------------------------------------------------------

    class TreeNodeInfoInterfaceWrapper implements TreeNodeInfoInterface {

        private String info;

        /**
         * @param sNodeData
         */
        public TreeNodeInfoInterfaceWrapper(String info)
        {
            this.info = info;
        }

        @Override
        public String getText()
        {
            return info;
        }

        @Override
        public String getHierarchy()
        {
            return info;
        }

        @Override
        public String getTooltipText()
        {
            return info;
        }

        @Override
        public Color getColor()
        {
            return null;
        }

        @Override
        public float getBarValue()
        {
            return -1;
        }

        @Override
        public void treeNodeClicked()
        {
        }
    }

    public TreeTable(String[] sdata)
    {
        for (String s : sdata) addData(s);
    }

    // ---------------------------------------------------------------------------------------------

    public TreeTable(TreeNodeInfoInterface[] data)
    {
        for (TreeNodeInfoInterface nd : data) addData(nd);
    }

    // ---------------------------------------------------------------------------------------------

    public TreeTable()
    {
    }

    // ---------------------------------------------------------------------------------------------

    public void clear()
    {
        synchronized (root) {
            root.clear();
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void addData(String sNodeData) {
        addData(new TreeNodeInfoInterfaceWrapper(sNodeData));
    }

    // ---------------------------------------------------------------------------------------------

    public void delData(String sNodeData) {
        delData(new TreeNodeInfoInterfaceWrapper(sNodeData));
    }

    // ---------------------------------------------------------------------------------------------

    public void addData(TreeNodeInfoInterface nodeData)
    {
        String d = nodeData.getHierarchy();

        String[] da = d.split("\\.");

        synchronized (root) {
            //System.err.println("add d = "+d);

            TreeNode node = root;
            for (String part : da) {
                TreeNode partNode = node.get(part);
                if (partNode == null) {
                    partNode = new TreeNode(root,node,part);
                    node.addChild(partNode);
                }
                node = partNode;
            }
            node.setNodeData(nodeData);
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void delData(TreeNodeInfoInterface nodeData)
    {
        String d = nodeData.getHierarchy();
        String[] da = d.split("\\.");

        synchronized (root) {
            //System.err.println("\ndel d = "+d);
            TreeNode node   = root;
            TreeNode parent = null;
            for (String part : da) {
                TreeNode partNode = node.get(part);
                if (partNode == null) {
                    System.err.println("NOT FOUND !?  "+d);
                }
                parent = node;
                node = partNode;
            }

            //System.err.println(node.hashCode()+" Node   = "+node.getNodeName()+"  "+node.getDataName()+ "  leaf = "+node.isLeaf());
            //System.err.println(parent.hashCode()+" Parent = "+parent.getNodeName()+"  "+parent.getDataName()+ "  leaf = "+parent.isLeaf());

            node.setNodeData(null);

            if (node.isLeaf() && node != root) {
                while (node != root && node.isLeaf() && node.getNodeData() == null) {
                    //System.err.println(node.hashCode()+ "-- Node   = "+node.getNodeName()+"  "+node.getDataName()+ "  leaf = "+node.isLeaf());
                    //System.err.println(parent.hashCode()+ "-- Parent = "+parent.getNodeName()+"  "+parent.getDataName()+ "  leaf = "+parent.isLeaf());
                    parent.removeChild(node);
                    node = parent;
                    parent = node.getParent();
                    //System.err.println("  isLeaf, remove");
                }
            }
            else {
                //System.err.println("  !isLeaf");
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * @return
     */
    public TreeNode getRoot()
    {
        return root;
    }

    // ---------------------------------------------------------------------------------------------

    public class TreeWalkData {
        int     currentDeep;
        int     maxDeep;
    }

    // ---------------------------------------------------------------------------------------------

    public void showAsciiTree()
    {
        TreeWalkData twd = new TreeWalkData();
        showAsciiTree(root,twd);
    }

    // ---------------------------------------------------------------------------------------------

    private StringBuffer    blankPool   = new StringBuffer();
    private String          lastBlanks  = "";

    private String getBlanks(int n) {
        while (blankPool.length() < n) blankPool.append("          ");

        if (lastBlanks.length() != n) lastBlanks = blankPool.substring(0,n);
        return lastBlanks;
    }

    // ---------------------------------------------------------------------------------------------

    private void showAsciiTree(TreeNode node,TreeWalkData twd)
    {
        System.err.println(getBlanks(twd.currentDeep)+" Node = "+node.getNodeName() + "    "+node.getNodeData().getText());

        twd.currentDeep++;

        Iterator<TreeNode> itr = node.getChilds();
        if (itr != null) {
            while (itr.hasNext()) {
                showAsciiTree(itr.next(),twd);
            }
        }
        twd.currentDeep--;

    }


}
