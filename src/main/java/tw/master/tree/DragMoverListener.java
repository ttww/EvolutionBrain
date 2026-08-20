/*
 *	This file is part of the EvolutionBrain project.
 *
 *	Copyright (c) 2011 by Thomas Welsch (ttww@gmx.de). All rights reserved.
 *
 * 
 *  Parts of this code is based on code of at.terai@gmail.com from
 *     http://java-swing-tips.blogspot.com/2008/06/mouse-drag-auto-scrolling.html
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

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;


/**
 *
 *
 * @author Thomas Welsch
 *
 */
class DragMoverListener implements ActionListener, HierarchyListener, MouseMotionListener, MouseListener,
MouseWheelListener {

    private static final int                  SPEED   = 2;

    private final Cursor                      dc;

    private final Cursor                      hc      = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

    private final Rectangle                   rect    = new Rectangle();

    private final javax.swing.Timer           scroller;

    private final JComponent                  label;

    private final JViewport                   vport;

    private Point                             startPt = new Point();

    private Point                             move    = new Point();

    private DragMoveEventPassThroughInterface passThrough;

    public DragMoverListener(JViewport vport, JComponent comp, DragMoveEventPassThroughInterface passThrough) {
        this.vport = vport;
        this.label = comp;
        this.dc = label.getCursor();
        this.scroller = new javax.swing.Timer(5, this);
        vport.addMouseMotionListener(this);
        vport.addMouseListener(this);
        vport.addHierarchyListener(this);
        vport.addMouseWheelListener(this);
        this.passThrough = passThrough;
    }

    @Override
    public void hierarchyChanged(HierarchyEvent e) {
        if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0
                && !vport.isDisplayable()) {
            scroller.stop();
        }
    }

    //boolean mouseDown = false;

    @Override
    public void actionPerformed(ActionEvent e) {
        //if (mouseDown) return;
        Rectangle vr = vport.getViewRect();
        int w = vr.width;
        int h = vr.height;
        Point ptZero = SwingUtilities.convertPoint(vport, 0, 0, label);
        rect.setRect(ptZero.x - move.x, ptZero.y - move.y, w, h);
        label.scrollRectToVisible(rect);
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        scroller.stop();
        Point pt = e.getPoint();
        move.setLocation(SPEED * (pt.x - startPt.x), SPEED * (pt.y - startPt.y));
        startPt.setLocation(pt);
        //mouseDown = false;
        scroller.start();
        checkPassThrough(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        label.setCursor(hc);
        startPt.setLocation(e.getPoint());
        scroller.stop();
        checkPassThrough(e);
        //mouseDown = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        label.setCursor(dc);
        checkPassThrough(e);
        // mouseDown = false;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        label.setCursor(dc);
        move.setLocation(0, 0);
        scroller.stop();
        checkPassThrough(e);
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        checkPassThrough(e);
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        checkPassThrough(e);
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        checkPassThrough(e);
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        checkPassThrough(e);
    }

    private void checkPassThrough(MouseEvent e) {
        if (passThrough != null) passThrough.mouseEvent(adjustedMouseEvent(e), displacement);
    }

    private Point adjustedPoint = new Point();

    private Point displacement  = new Point();

    private MouseEvent adjustedMouseEvent(MouseEvent e) {
        displacement = SwingUtilities.convertPoint(vport, 0, 0, label);

        adjustedPoint.x = e.getX() + displacement.x;
        adjustedPoint.y = e.getY() + displacement.y;

        if (e instanceof MouseWheelEvent) {
            MouseWheelEvent mwe = (MouseWheelEvent) e;
            e = new MouseWheelEvent(
                    (Component) mwe.getSource(),
                    mwe.getID(),
                    mwe.getWhen(),
                    mwe.getModifiers(),
                    adjustedPoint.x,
                    adjustedPoint.y,
                    mwe.getXOnScreen(),
                    mwe.getYOnScreen(),
                    mwe.getClickCount(),
                    mwe.isPopupTrigger(),
                    mwe.getScrollType(),
                    mwe.getScrollAmount(),
                    mwe.getWheelRotation()
            );
        } else {
            e = new MouseEvent(
                    (Component) e.getSource(),
                    e.getID(),
                    e.getWhen(),
                    e.getModifiers(),
                    adjustedPoint.x,
                    adjustedPoint.y,
                    e.getXOnScreen(),
                    e.getYOnScreen(),
                    e.getClickCount(),
                    e.isPopupTrigger(),
                    e.getButton()
            );
        }

        return e;
    }
}
