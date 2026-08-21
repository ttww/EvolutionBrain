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

package tw.master.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import tw.master.GlobalsClientGui;
import tw.master.brain.Brain;
import tw.master.gl3d.Panel3dFactory;
import tw.master.gl3d.View;
import tw.master.gl3d.World3dDrawInterface;
import tw.master.gl3d.World3dInterface;
import tw.master.utils.GuiStartStopStepThread;
import tw.master.utils.Utils;



@SuppressWarnings("serial")
public class Brain3dDisplay extends JPanel implements World3dDrawInterface {

    /**
     * The current brain to display.
     */
    private Brain   brain;

    private long    mouseClickedMS;

    private final Point startPressPos = new Point();

    private long    lastDisplayed = 0;

    private float   rx;

    private float   ry;

    private float   rz;

    private boolean autoRot       = true;

    private boolean toLeft;

    private boolean toRight;

    private boolean toUp;

    private boolean toDown;

    private boolean reset;

    private boolean showPos;

    private boolean plus;

    private boolean minus;

    private boolean restorePos1;

    // ---------------------------------------------------------------------------------------------

    /**
     * Building a 3D panel for displaying a brain.<p>
     * The OpenGL driver is used with this constructor.
     *
     * @param globals   The globals
     */
    public Brain3dDisplay(final GlobalsClientGui globals) {
        this(globals, Panel3dFactory.TYPE_3D.OPENGL);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Building a 3D panel for displaying a brain.
     *
     * @param globals   The globals
     * @param type3d    Type of 3D driver: Panel3dFactory.TYPE_3D.OPENGL or Panel3dFactory.TYPE_3D.JAVA
     */
    public Brain3dDisplay(final GlobalsClientGui globals, final Panel3dFactory.TYPE_3D type3d) {

        setName("Brain3dDisplay");

        final World3dInterface w3d = Panel3dFactory.get3dPanel(type3d);

        final JPanel watchPanel = w3d.get3DPanel(this);

        watchPanel.setBorder(new TitledBorder("WatchedBrain"));
        watchPanel.setPreferredSize(new Dimension(600, 500));
        watchPanel.setMinimumSize(new Dimension(600, 500));

        setLayout(new BorderLayout());
        add(watchPanel, BorderLayout.CENTER);

        // Screen update thread --------------------------------------------------------------------
        GuiStartStopStepThread up3d = new GuiStartStopStepThread("Update3DThread", globals) {

            @Override
            public void doStep(GlobalsClientGui lglobals) {
//				if (!lglobals.disableDraw) ((OpenGL3dImplementation)watchPanel).canvas.display();
//                ((OpenGL3dImplementation) watchPanel).canvas.display();
                ((World3dInterface) watchPanel).refresh();
            }
        };

        watchPanel.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                //System.err.println(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                switch (key) {
                    case KeyEvent.VK_LEFT:
                        toLeft = false;
                        break;
                    case KeyEvent.VK_RIGHT:
                        toRight = false;
                        break;
                    case KeyEvent.VK_UP:
                        toUp = false;
                        break;
                    case KeyEvent.VK_DOWN:
                        toDown = false;
                        break;
                    default:
                        //System.err.println("Key = "+key);
                        break;
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

                int key;

                // Put it together, as long as it works ;-)
                if (e.isActionKey()) key = e.getKeyCode();
                else
                    key = e.getKeyChar();

                switch (key) {
                    case KeyEvent.VK_LEFT:
                        toLeft = true;
                        autoRot = false;
                        break;
                    case KeyEvent.VK_RIGHT:
                        toRight = true;
                        autoRot = false;
                        break;
                    case KeyEvent.VK_UP:
                        toUp = true;
                        autoRot = false;
                        break;
                    case KeyEvent.VK_DOWN:
                        toDown = true;
                        autoRot = false;
                        break;
                    case '1':
                        restorePos1 = true;
                        autoRot = false;
                        break;
                    case 'r':
                    case 'R':
                        reset = true;
                        break;
                    case 'p':
                    case 'P':
                        showPos = true;
                        break;
                    case '+':
                        plus = true;
                        break;
                    case '-':
                        minus = true;
                        break;
                    default:
                        //System.err.println("Key = "+key+"  ("+(char)key+")  "+e.isActionKey()+"  char = "+e.getKeyChar());
                }
            }
        });

        watchPanel.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                // System.err.println("Click");
                long now = System.currentTimeMillis();

                if (now - mouseClickedMS < 300) autoRot = !autoRot;
                mouseClickedMS = now;
            }

            @Override
            public void mousePressed(MouseEvent e) {
                //System.err.println("Press");
                synchronized (startPressPos) {
                    startPressPos.setLocation(e.getPoint());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                //System.err.println("Release");
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // System.err.println("Enter");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // System.err.println("Exit");
            }

        });


        watchPanel.addMouseWheelListener(new MouseWheelListener() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                //System.err.println("Wheel");

                View view = w3d.getView();

                if (view != null) {
                    view.moveFwd(e.getWheelRotation() * 10);
                    //view.printDebug();
                }
            }

        });

        watchPanel.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {
                // System.err.println("Drag");

                View view = w3d.getView();
                Point current = e.getPoint();

                synchronized (startPressPos) {
                    int diffX = current.x - startPressPos.x;
                    int diffY = current.y - startPressPos.y;

                    if ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0) {
                        if (view != null) {
                            // Pan speed is scaled down to avoid jumpy translation.
                            float panScale = 0.5f;
                            view.moveUp(diffY * panScale);
                            view.moveLeft(diffX * panScale);
                        }
                    }
                    else {
                        autoRot = false;

                        if (view != null) {
                            // Use pixel delta directly to make drag direction and speed feel natural.
                            view.lookRight(diffX);
                            view.lookDown(diffY);
                        }
                    }
                    startPressPos.setLocation(current);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                // System.err.println("Move");
            }

        });

        watchPanel.requestFocusInWindow();
        up3d.setSleepTime(40);

        if (Utils.skipPaint(this)) return;

        up3d.setAutostep(true);
        up3d.start();

    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Set the brain to display.
     *
     * @param newBrain     The brain to display
     */
    public void setWatchedBrain(Brain newBrain) {
        this.brain = newBrain;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Sets the autorotation mode.
     *
     * @param  newAutoRot   New mode
     */
    public void setAutoRot(boolean newAutoRot) {
        this.autoRot = newAutoRot;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void draw(World3dInterface g3d) {
        if (brain != null) {

            long now = System.currentTimeMillis();
            if (lastDisplayed == 0) lastDisplayed = now;

//			g3d.reset();
//			g3d.scale(25);


            synchronized (startPressPos) {

                if (showPos) {
                    System.err.println();
                    System.err.println("rx    = " + rx);
                    System.err.println("ry    = " + ry);
                    System.err.println("rz    = " + rz);
                    View view = g3d.getView();
                    if (view != null) {
                        System.err.println("camX = " + view.camX);
                        System.err.println("camY = " + view.camY);
                        System.err.println("camZ = " + view.camZ);
                        view.printDebug();
                    }
                    System.err.println("line  = " + Brain.getSynapseLineWidth());
                    showPos = false;
                }
                if (restorePos1) {
                    rx = 15;
                    ry = 22;
                    rz = 33;
                    View view = g3d.getView();
                    if (view != null) {
                        view.reset();
                        view.setCamera(0, 0, 95);
                    }
                    Brain.setSynapseLineWidth(3.4f);

                    restorePos1 = false;
                }
                if (reset) {
                    System.err.println("RESET !");
                    rx = 0;
                    ry = 0;
                    rz = 0;

                    View view = g3d.getView();
                    if (view != null) {
                        view.reset();
                        view.setCamera(0, 0, 250);
                    }
                    reset = false;
                }

                if (plus && brain != null) {
                    Brain.setSynapseLineWidth(Brain.getSynapseLineWidth() + 0.3f);
                    plus = false;
                }
                if (minus && brain != null) {
                    Brain.setSynapseLineWidth(Brain.getSynapseLineWidth() - 0.3f);
                    minus = false;
                }
                if (autoRot) {
                    float f = 0.01f * (now - lastDisplayed);

                    rx += 0.2f * f;
                    ry += 0.3f * f;
                    rz += 0.4f * f;
                    if (rx > 360) rx -= 360;
                    if (ry > 360) ry -= 360;
                    if (rz > 360) rz -= 360;
                }
                if (toLeft || toRight || toUp || toDown) {
                    float f = 0.1f * (now - lastDisplayed);

                    if (toLeft) ry -= 0.2f * f;
                    if (toRight) ry += 0.2f * f;
                    if (toUp) rx -= 0.2f * f;
                    if (toDown) rx += 0.2f * f;

                    if (rx > 360) rx -= 360;
                    if (ry > 360) ry -= 360;
                    //if (rz > 360) rz -= 360;
                    if (rx < 0) rx += 360;
                    if (ry < 0) ry += 360;
                    //if (rz < 0)   rz += 360;
                }
                //System.err.println(toLeft);
                lastDisplayed = now;

//                View view = g3d.getView();
//                if (view != null) {
//                    view.lookDown(rx);
//                    view.lookLeft(ry);
//
//                    rx = ry = rz = 0;
//                }

                g3d.rotX(rx);
                g3d.rotY(ry);
                g3d.rotZ(rz);
            }

            World3dDrawInterface w3dd = brain;
            w3dd.draw(g3d);
        }
    }

    @Override
    public void drawBackgound(World3dInterface g3d, float w, float h) {
//		if (globals.watchedCrawler != null && globals.watchedCrawler instanceof BrainCrawler) {
//			BrainCrawler bc = (BrainCrawler) globals.watchedCrawler;
//			bc.updateVisionField(globals);
//			globals.ap.setData(bc.lfp.sf);
//
//			World3dDrawInterface w3dd = bc.getBrain();
//			w3dd.drawBackgound(g3d,w,h);
//		}
    }

}
