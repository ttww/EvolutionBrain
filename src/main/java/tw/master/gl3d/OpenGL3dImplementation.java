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
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.util.gl2.GLUT;

import tw.master.utils.Utils;


@SuppressWarnings("serial")
public class OpenGL3dImplementation extends JPanel implements World3dInterface, GLEventListener {

    private static GLCapabilities caps;

    private World3dDrawInterface  draw;

    public GLCanvas               canvas;

    private GL2                   gl;

    private GLUT                  glut;

    //private GLU						glu;

    private int                   sphereList;

    static {
        // Not working anymore, if the first class access from the EDT !
//		caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));
//		caps.setAlphaBits(8);
//
//		// ATI  !!!
//		caps.setSampleBuffers(true);
//	    caps.setNumSamples(8);
    }

    private static boolean        atiMode = false;

    public static void initOpenGL() {
        caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));
        caps.setAlphaBits(8);

//		System.err.println("GL_VENDOR: " +  GLProfile.get(GLProfile.GL2).getImplName());
//		System.err.println("GL_RENDERER: " + gl.glGetString(GL2.GL_RENDERER));
//		System.err.println("GL_VERSION: " + gl.glGetString(GL2.GL_VERSION));

        // ATI  !!!
        if (atiMode) {
            caps.setSampleBuffers(true);
            caps.setNumSamples(8);
        }
    }

    public OpenGL3dImplementation() {
        super();

        setName("OpenGL3dImplementation");

        setLayout(new BorderLayout());

        canvas = new GLCanvas(caps);
        canvas.addGLEventListener(this);

        add(canvas, BorderLayout.CENTER);

    }

    /* (non-Javadoc)
     * @see tw.master.gl3d.World3dInterface#refresh()
     */
    @Override
    public void refresh() {
        canvas.display();
    }

    /* (non-Javadoc)
     * @see java.awt.Component#addKeyListener(java.awt.event.KeyListener)
     */
    @Override
    public synchronized void addKeyListener(KeyListener l) {
        canvas.addKeyListener(l);
        //System.err.println("Set focus missing ?! :-)");
    }

    /* (non-Javadoc)
     * @see java.awt.Component#addMouseListener(java.awt.event.MouseListener)
     */
    @Override
    public synchronized void addMouseListener(MouseListener l) {
        canvas.addMouseListener(l);
    }

    /* (non-Javadoc)
     * @see java.awt.Component#addMouseMotionListener(java.awt.event.MouseMotionListener)
     */
    @Override
    public synchronized void addMouseMotionListener(MouseMotionListener l) {
        canvas.addMouseMotionListener(l);
    }

    /* (non-Javadoc)
     * @see java.awt.Component#addMouseWheelListener(java.awt.event.MouseWheelListener)
     */
    @Override
    public synchronized void addMouseWheelListener(MouseWheelListener l) {
        canvas.addMouseWheelListener(l);
    }

    @Override
    public JPanel get3DPanel(World3dDrawInterface drawer) {
        this.draw = drawer;
        return this;
    }

//	@Override
//	protected void paintComponent(Graphics g) {
//		super.paint(g);
//
//	}


    @Override
    public void reset() {
//		gl.glLoadIdentity();
    }


    @Override
    public void drawText(float x, float y, float z, String txt) {
        gl.glRasterPos3f(x, y, z); // set position
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, txt);
    }

    private float lineWidth = 0.11f;

    /**
     * @param f
     * @return
     */
    @Override
    public float setLineWidth(float f) {
        float old = lineWidth;
        lineWidth = f;
        return old;
    }

    @Override
    public void drawLine(float x1, float y1, float z1, float x2, float y2, float z2) {
        gl.glLineWidth(lineWidth);
        gl.glBegin(GL.GL_LINES);
        gl.glVertex3f(x1, y1, z1);
        gl.glVertex3f(x2, y2, z2);
        gl.glEnd();
    }

    @Override
    public void drawSphere(float x, float y, float z, float r) {
        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);
        float sc = 0.6f + r * 3;
        gl.glScalef(sc, sc, sc);
        gl.glCallList(sphereList);
        gl.glPopMatrix();
    }

    @Override
    public void drawBox(float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {

        gl.glPushMatrix();

        gl.glLineWidth(lineWidth);
        gl.glBegin(GL2.GL_QUADS);


//		System.err.println("x = "+minX+" .. "+maxX);
//		System.err.println("y = "+minY+" .. "+maxY);
//		System.err.println("z = "+minZ+" .. "+maxZ);

        //gl.glColor4f(0.4f,0.4f,0.4f,0.2f);			// Set The Color To Green

        // gl.glColor3f(0.0f,1.0f,0.0f);			// Set The Color To Green

        gl.glVertex3f(maxX, maxY, minZ); // Top Right Of The Quad (Top)
        gl.glVertex3f(minX, maxY, minZ); // Top Left Of The Quad (Top)
        gl.glVertex3f(minX, maxY, maxZ); // Bottom Left Of The Quad (Top)
        gl.glVertex3f(maxX, maxY, maxZ); // Bottom Right Of The Quad (Top)

        // gl.glColor3f(1.0f,0.5f,0.0f);			// Set The Color To Orange

        gl.glVertex3f(maxX, minY, maxZ); // Top Right Of The Quad (Bottom)
        gl.glVertex3f(minX, minY, maxZ); // Top Left Of The Quad (Bottom)
        gl.glVertex3f(minX, minY, minZ); // Bottom Left Of The Quad (Bottom)
        gl.glVertex3f(maxX, minY, minZ); // Bottom Right Of The Quad (Bottom)

        // gl.glColor3f(1.0f,0.0f,0.0f);			// Set The Color To Red

        gl.glVertex3f(maxX, maxY, maxZ); // Top Right Of The Quad (Front)
        gl.glVertex3f(minX, maxY, maxZ); // Top Left Of The Quad (Front)
        gl.glVertex3f(minX, minY, maxZ); // Bottom Left Of The Quad (Front)
        gl.glVertex3f(maxX, minY, maxZ); // Bottom Right Of The Quad (Front)

        // gl.glColor3f(1.0f,1.0f,0.0f);			// Set The Color To Yellow

        gl.glVertex3f(maxX, minY, minZ); // Bottom Left Of The Quad (Back)
        gl.glVertex3f(minX, minY, minZ); // Bottom Right Of The Quad (Back)
        gl.glVertex3f(minX, maxY, minZ); // Top Right Of The Quad (Back)
        gl.glVertex3f(maxX, maxY, minZ); // Top Left Of The Quad (Back)

        // gl.glColor3f(0.0f,0.0f,1.0f);			// Set The Color To Blue

        gl.glVertex3f(minX, maxY, maxZ); // Top Right Of The Quad (Left)
        gl.glVertex3f(minX, maxY, minZ); // Top Left Of The Quad (Left)
        gl.glVertex3f(minX, minY, minZ); // Bottom Left Of The Quad (Left)
        gl.glVertex3f(minX, minY, maxZ); // Bottom Right Of The Quad (Left)

        // gl.glColor3f(1.0f,0.0f,1.0f);				// Set The Color To Violet

        gl.glVertex3f(maxX, maxY, minZ); // Top Right Of The Quad (Right)
        gl.glVertex3f(maxX, maxY, maxZ); // Top Left Of The Quad (Right)
        gl.glVertex3f(maxX, minY, maxZ); // Bottom Left Of The Quad (Right)
        gl.glVertex3f(maxX, minY, minZ); // Bottom Right Of The Quad (Right)

        gl.glEnd();

        gl.glPopMatrix();

    }


//	private void drawSphere(GL2 gl,int numMajor, int numMinor, float radius)
//	{
//		float majorStep = (float) (Math.PI / numMajor);
//		float minorStep = (float) (2.0 * Math.PI / numMinor);
//		int i, j;
//
//		for (i = 0; i < numMajor; ++i) {
//			float a = i * majorStep;
//			float b = a + majorStep;
//			float r0 = (float) (radius * Math.sin(a));
//			float r1 = (float) (radius * Math.sin(b));
//			float z0 = (float) (radius * Math.cos(a));
//			float z1 = (float) (radius * Math.cos(b));
//
//			gl.glBegin(GL2.GL_TRIANGLE_STRIP);
//			for (j = 0; j <= numMinor; ++j) {
//				double c = j * minorStep;
//				float x = (float) Math.cos(c);
//				float y = (float) Math.sin(c);
//
//				gl.glNormal3f(((x * r0) / radius), ((y * r0) / radius), (z0 / radius));
//				gl.glTexCoord2f(j / (float) numMinor, i / (float) numMajor);
//				gl.glVertex3f((x * r0), (y * r0), z0);
//
//				gl.glNormal3f((x * r1) / radius, (y * r1) / radius, z1 / radius);
//				gl.glTexCoord2f(j / (float) numMinor, (i + 1) / (float) numMajor);
//				gl.glVertex3f(x * r1, y * r1, z1);
//			}
//
//			gl.glEnd();
//		}
//	}

    @Override
    public void rotX(float r) {
        gl.glRotatef(r, 1.0f, 0.0f, 0.0f);
    }

    @Override
    public void rotY(float r) {
        gl.glRotatef(r, 0.0f, 1.0f, 0.0f);
    }

    @Override
    public void rotZ(float r) {
        gl.glRotatef(r, 0.0f, 0.0f, 1.0f);
    }

    @Override
    public void scale(float f) {
//		gl.glScalef(f,f,f);
    }

    @Override
    public void setColor(Color col) {
        gl.glColor4f(col.getRed() / 255f, col.getGreen() / 255f, col.getBlue() / 255f, col.getAlpha() / 255f);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        if (Utils.skipPaint(this)) return;

        gl = drawable.getGL().getGL2();

        // Special handling for the case where the GLJPanel is translucent
//		// and wants to be composited with other Java 2D content
        if (drawable instanceof GLJPanel &&
                !((GLJPanel) drawable).isOpaque() &&
                ((GLJPanel) drawable).shouldPreserveColorBufferIfTranslucent()) {
            gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
        } else {
            //gl.glClearColor(20,20,20,255);   // RGBA
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        }


        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();

        v.setCamera(gl);
//		v.setCameraLookAt(glu);



//		gl.glPushMatrix();

        float w = 16;
        /*
        gl.glBegin(GL2.GL_QUADS);

        float z = 0;
        gl.glColor3f(0.2f,0.2f,0.2f);
        gl.glVertex3f(-w,-w,z);
        gl.glVertex3f(w,-w,z);
        gl.glVertex3f(w,w,z);
        gl.glVertex3f(-w,w,z);
        gl.glEnd();
        //		gl.glPopMatrix();
         */
        draw.drawBackgound(this, w, w);

        gl.glPushMatrix();

        gl.glColor3f(0.5f, 0.5f, 0.5f);
        //	gl.glLoadIdentity(); /* clear the matrix */
        /* viewing transformation */
        //gl.glScalef(1.0f, 2.0f, 1.0f); /* modeling transformation */
        draw.draw(this);
        glut.glutWireCube(1.0f);
        gl.glPopMatrix();

        gl.glFlush();
    }

    @Override
    public void dispose(GLAutoDrawable drawable) { }

    @Override
    public void init(GLAutoDrawable drawable) {
        gl = drawable.getGL().getGL2();
//
//		glu = new GLU();
        glut = new GLUT();
//
        gl.setSwapInterval(1);

//		float pos[] = { 5.0f, 5.0f, 10.0f, 0.0f };
//
//		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, pos, 0);
//		gl.glEnable(GL2.GL_CULL_FACE);
//		gl.glEnable(GL2.GL_LIGHTING);
//		gl.glEnable(GL2.GL_LIGHT0);
//		gl.glEnable(GL2.GL_DEPTH_TEST);
//
//		gl.glEnable(GL2.GL_NORMALIZE);
//
//		gl.glClearColor(0.0f, 0f, 0f, 1f);



//
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
//        gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        gl.glEnable(GL.GL_CULL_FACE);
        gl.glDisable(GL.GL_DEPTH_TEST); // Tiefentest aktivieren

        gl.glShadeModel(GLLightingFunc.GL_FLAT);

        gl.glEnable(GL2ES1.GL_POINT_SMOOTH);

        gl.glEnable(GL.GL_LINE_SMOOTH);
        gl.glEnable(GL2GL3.GL_POLYGON_SMOOTH);

        gl.glEnable(GL.GL_BLEND);

        if (atiMode) gl.glEnable(GL.GL_MULTISAMPLE); // ATI ?

        // ATI TEST !!!!:   gl.glEnablei(GL2.GL_SRC_ALPHA,GL2.GL_ONE_MINUS_SRC_ALPHA);

        gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
        gl.glHint(GL.GL_LINE_SMOOTH, GL.GL_NICEST);

        // Important for LineTransparent and AntiAliasing !
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
//		gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_DONT_CARE);

        // ATI ??
        //gl.glSampleCoverage(GL.GL_SAMPLE_ALPHA_TO_COVERAGE, false);


        int[] buf  = new int[1];
        int[] sbuf = new int[1];

        gl.glGetIntegerv(GL.GL_SAMPLE_BUFFERS, buf, 0);
        //System.out.println("number of sample buffers is " + buf[0]);
        gl.glGetIntegerv(GL.GL_SAMPLES, sbuf, 0);
        //System.out.println("number of samples is " + sbuf[0]);



        v = new View();
        v.camX = 0;
        v.camY = 0;
        v.camZ = 250;

        v.setCamera(gl);

        //glu.gluLookAt(0.0,0.0, 25.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);


        sphereList = gl.glGenLists(1);
        gl.glNewList(sphereList, GL2.GL_COMPILE);
//		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, red, 0);
        glut.glutSolidSphere(0.1f, 14, 14);
        gl.glEndList();

    }

    private View v;

    @Override
    public View getView() {
        return v;
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        gl = drawable.getGL().getGL2();

//		float h = (float)height / (float)width;

//		gl.glMatrixMode(GL2.GL_PROJECTION);
//
//		System.err.println("GL_VENDOR: " + gl.glGetString(GL2.GL_VENDOR));
//		System.err.println("GL_RENDERER: " + gl.glGetString(GL2.GL_RENDERER));
//		System.err.println("GL_VERSION: " + gl.glGetString(GL2.GL_VERSION));
//
//		gl.glLoadIdentity();
//		gl.glFrustum(-1.0f, 1.0f, -h, h, 5.0f, 60.0f);
//		gl.glMatrixMode(GL2.GL_MODELVIEW);
//		gl.glLoadIdentity();
//
////		glu.gluLookAt(10,10,10,0,0,0,0,0,0);
//		gl.glTranslatef(0.0f, 0.0f, -40.0f);
//


        //
        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION); /* prepare for and then */
        gl.glLoadIdentity(); /* define the projection */
        float ww = 1.0f;
        gl.glFrustum(-ww, ww, -ww, ww, 15, 2000.0); /* transformation */

        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW); /* back to modelview matrix */
        gl.glViewport(0, 0, w, h); /* define the viewport */

        v.setCamera(gl);
    }



}
