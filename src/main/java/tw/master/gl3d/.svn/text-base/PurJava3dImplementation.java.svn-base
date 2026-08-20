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

package tw.master.gl3d;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

import tw.master.math.Matrix3D;
import tw.master.utils.Utils;



/**
 * New Pure java 3D panel. Not fully functional !
 *
 * @author Thomas Welsch
 */
public class PurJava3dImplementation extends JPanel implements World3dInterface {

    private static final long serialVersionUID = 1L;

    private static final boolean DEBUG_MISSING = false;


    private World3dDrawInterface draw;

    private Graphics2D           g2;

    private InternalJPanel       ip;

    /**
     * New Pure java 3D panel. Not fully functional !
     */
    public PurJava3dImplementation() {
        super();
        setLayout(new BorderLayout());

        ip = new InternalJPanel();
        add(ip, BorderLayout.CENTER);
    }

    /* (non-Javadoc)
     * @see tw.master.gl3d.World3dInterface#refresh()
     */
    @Override
    public void refresh() {
        ip.repaint();
    }


    @Override
    public JPanel get3DPanel(World3dDrawInterface drawer) {
        this.draw = drawer;
        return this;
    }

    /**
     * Helper class for doing the drawing.
     *
     * @author Thomas Welsch
     */
    class InternalJPanel extends JPanel {

        private static final long serialVersionUID = 1L;

//		public InternalJPanel() {
//			super();
//			setLayout(new BorderLayout());
//		}
        /* (non-Javadoc)
         * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
         */
        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(new Color(0, 0, 0));

            g.fillRect(0, 0, getWidth(), getHeight());


            g2 = (Graphics2D) g;
            Utils.setQuality(g2);

            g2.translate(this.getWidth() / 2f, this.getHeight() / 2f);

            draw.draw(PurJava3dImplementation.this);


        }

    }

    private Matrix3D        displayMat = new Matrix3D();

    private Line2D.Float    l          = new Line2D.Float();

    private Ellipse2D.Float e          = new Ellipse2D.Float();

    private float[]         f3         = new float[3];

    private float[]         f6         = new float[6];

    private float[]         r3         = new float[3];

    private float[]         r6         = new float[6];

    @Override
    public void reset() {
        displayMat.unit();
    }

    @Override
    public void drawLine(float x1, float y1, float z1, float x2, float y2, float z2) {
//		System.err.println("lll");

        f6[0] = x1;
        f6[1] = y1;
        f6[2] = z1;
        f6[3] = x2;
        f6[4] = y2;
        f6[5] = z2;

        displayMat.transform(f6, r6);

        l.x1 = r6[0];
        l.y1 = r6[1];
        l.x2 = r6[3];
        l.y2 = r6[4];
        g2.draw(l);
    }

    @Override
    public void drawSphere(float x, float y, float z, float r) {
        f3[0] = x;
        f3[1] = y;
        f3[2] = z;

        displayMat.transform(f3, r3);

        e.x = r3[0] - r / 2f;
        e.y = r3[1] - r / 2f;

        e.height = r;
        e.width = r;

        g2.fill(e);
    }

    @Override
    public void rotX(float r) {
        displayMat.xrot(r);
    }

    @Override
    public void rotY(float r) {
        displayMat.yrot(r);
    }

    @Override
    public void rotZ(float r) {
        displayMat.zrot(r);
    }

    @Override
    public void scale(float f) {
        displayMat.scale(f);
    }

    @Override
    public void setColor(Color col) {
        g2.setColor(col);
    }

    @Override
    public View getView() {
        if (DEBUG_MISSING) System.err.println("No view controll for PurJava....");
        return null;
    }

    @Override
    public void drawBox(float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
        if (DEBUG_MISSING) System.err.println("No drawBox for PurJava....");
    }

    @Override
    public float setLineWidth(float f) {
        return 1;
    }

    @Override
    public void drawText(float x, float y, float z, String txt) {
        if (DEBUG_MISSING) System.err.println("No drawText for PurJava....");
    }

}
