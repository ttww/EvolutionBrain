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

package tw.master.brain.activation;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.io.Serializable;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import tw.gui.annotiations.AnnotationGuiGenerator;
import tw.gui.annotiations.GuiBooleanAnnotation;
import tw.master.utils.Utils;


/**
 * @author Thomas Welsch
 *
 */
public class AbstractActivationFunction implements ActivationFunctionInterface, Serializable {

    private static final long    serialVersionUID = 1L;

    protected static final float MIN_VALUE        = -1;

    protected static final float MAX_VALUE        = 1;

    protected static final int   STEPS            = 2000;

    protected static boolean     scaleInputs      = true;


    private float                minValue;

    private float                maxValue;

    private float                minResult;

    private float                maxResult;

    private float[]              fa;

    private float                stepDivisor;

    private float                maxStep;

    protected AbstractActivationFunction() { }


    /**
     * @param  t
     */
    @GuiBooleanAnnotation(
            label = "Divide by number of inputs",
            text = "",
            tooltip = "If set, all input signals are diveded by the number of inputs after adding it."
    )
    public static void setScaleInputs(boolean t) {
        scaleInputs = t;
    }

    /**
     * @return
     */
    public static boolean isScaleInputs() {
        return scaleInputs;
    }



    protected void setMappingArray(float minValue, float maxValue, float[] fa) {
        for (int i = 0; i < fa.length; i++) {
            float f = fa[i];
            if (f < 0) fa[i] = 0;
            else
                if (f > 1) fa[i] = 1;
        }

        this.fa = fa;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.minResult = fa[0];
        this.maxResult = fa[fa.length - 1];
        this.stepDivisor = (maxValue - minValue) / fa.length;


        // Calculate the maximum step inside the array:
        maxStep = 0;
        float f = fa[0];
        for (int i = 1; i < fa.length; i++) {
            float d = fa[i] - f;
            if (d > maxStep) maxStep = d;
            f = fa[i];
        }

        for (JPanel p : visualPanels)
            p.repaint();
    }

    @Override
    public float getActivation(final float actualActivity, float effInput, final int numberOfInputs) {

        effInput += actualActivity; // Adding old activity, not switchable by GUI yet.

        if (scaleInputs) effInput /= numberOfInputs;

        if (effInput <= minValue) return minResult;
        if (effInput >= maxValue) return maxResult;

        float f = effInput - minValue;

        int i = (int) (0.5f + f / stepDivisor);
        if (i == fa.length) return maxResult; // against rounding problems

        //System.err.println("idx="+i);
        return fa[i];
    }

    @Override
    public int getSteps() {
        return fa.length;
    }

    @Override
    public float getStepDivisor() {
        return stepDivisor;
    }

    @Override
    public float getMaxStep() {
        return maxStep;
    }

    @Override
    public float getMinValue() {
        return minValue;
    }

    @Override
    public float getMaxValue() {
        return maxValue;
    }

    @Override
    public float getMinResult() {
        return minResult;
    }

    @Override
    public float getMaxResult() {
        return maxResult;
    }

    static class AktivationFunctionDrawer extends JPanel {

        private static final long           serialVersionUID = 1L;

        private ActivationFunctionInterface afi;

        private Font                        font             = new Font("Courier", Font.BOLD, 12);

        //private Font						miniFont = new Font("Courier",Font.BOLD,10);

        public AktivationFunctionDrawer(ActivationFunctionInterface afi) {
            super();
            //setLayout(new GridBagLayout());
            setName(afi.getClass().getSimpleName());
            this.afi = afi;
        }

        /* (non-Javadoc)
         * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
         */
        @Override
        protected void paintComponent(Graphics g) {
            int w = this.getWidth();
            int h = this.getHeight();

            final int RIM = 10;

            Graphics2D g2 = (Graphics2D) g;

            Utils.setQuality(g2);

            // -------------------------------------------------------------------------------------
            // Draw a nice frame :-)
            // -------------------------------------------------------------------------------------
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(0, 0, w, h);

            g2.setColor(Color.DARK_GRAY.darker());
            g2.fillRect(RIM, RIM, w - 2 * RIM, h - 2 * RIM);

            g2.setColor(Color.GRAY.brighter());
            int borderX = RIM - 1;
            int borderY = RIM - 1;
            int borderW = w - 2 * RIM + 1;
            int borderH = h - 2 * RIM + 2;

            g2.drawRect(borderX, borderY, borderW, borderH);


            // -------------------------------------------------------------------------------------
            // Draw curve
            // -------------------------------------------------------------------------------------
            Path2D.Float poly = new Path2D.Float();

            // Limit the output steps to max. width:
            int steps = afi.getSteps();
            if (steps > w - 2 * RIM) steps = w - 2 * RIM;

            float min = afi.getMinValue();
            float max = afi.getMaxValue();
            float step = (max - min) / steps;

            int hm1 = h - 2 * RIM - 2;
            if (hm1 < 0) return; // Area to small;



            float xf = w - 2 * RIM - 2;
            float xs = xf / steps;

            poly.reset();
            g2.setColor(Color.GRAY);
            float ipx = borderX + borderW / 2;
            int ipy = borderY + 1;
            int ipb = borderY + borderH - 2;


            g2.setFont(font);
            g2.drawString("0", ipx, ipb - 4);


            for (float x = -0.1f; x > min; x -= 0.1) {
                poly.moveTo(ipx + x * xf, ipy);
                poly.lineTo(ipx + x * xf, ipb);
            }
            for (float x = 0.0f; x < max; x += 0.1) {
                poly.moveTo(ipx + x * xf, ipy);
                poly.lineTo(ipx + x * xf, ipb);
            }
            g2.setColor(Color.GRAY.darker().darker());
            g2.draw(poly);
            poly.reset();

            float firstX = 0;
            float firstY = 0;
            float lastX = 0;
            float lastY = 0;

            boolean first = true;
            float f = min;
            float x = 0;
            while (f <= max) {

                float a = afi.getActivation(0, f, 1);
                float y = hm1 - a * hm1;

                //System.err.println(f+"  -->  "+String.format("%6.3f",new Float(a))+"  --> "+y);

                if (first) {
                    firstX = 1 + RIM + x;
                    firstY = RIM + y;
                    poly.moveTo(firstX, firstY);
                    first = false;
                } else {
                    lastX = 1 + RIM + x;
                    lastY = RIM + y;
                    poly.lineTo(lastX, lastY);
                }

                f += step;
                x += xs;
            } // while

            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(2));
            g2.draw(poly);

            firstX += 2;
            firstY -= 3;
            //if (firstY > h / 2)	firstY += 10; else firstY -= 10;
            lastX -= 30;
            lastY += 12;
            //if (lastY > h / 2)	lastY += 10; else lastY -= 10;
            g2.setColor(Color.GRAY.brighter());
            g2.drawString(String.format("%+2.1f", new Float(afi.getMinResult())), firstX, firstY);
            g2.drawString(String.format("%+2.1f", new Float(afi.getMaxResult())), lastX, lastY);


            // -------------------------------------------------------------------------------------
            // Output information about the curve:
            // -------------------------------------------------------------------------------------
            int yi = 20 + RIM;
            int ys = 15;
            int xi = 20;

            g2.setColor(Color.YELLOW);
            g2.setFont(font);
            g2.drawString(String.format("Name........: %s", afi.getClass().getSimpleName()), xi, yi);
            yi += ys;
            g2.drawString(String.format("Input.......: %+2.1f...%+2.1f", new Float(min), new Float(max)), xi, yi);
            yi += ys;
            g2.drawString(
                    String.format("Result Min..: %+2.1f...%+2.1f", new Float(afi.getMinResult()),
                            new Float(afi.getMaxResult())), xi, yi);
            yi += ys;
            g2.drawString(String.format("# steps.....: %d", new Integer(afi.getSteps())), xi, yi);
            yi += ys;
            g2.drawString(String.format("Step........: %3.4f", new Float(afi.getStepDivisor())), xi, yi);
            yi += ys;
            g2.drawString(String.format("Max step....: %3.4f", new Float(afi.getMaxStep())), xi, yi);
            yi += ys;
        }
    } // class AktivationFunctionDrawer

    // ---------------------------------------------------------------------------------------------

    private LinkedList<JPanel> visualPanels = new LinkedList<JPanel>();

    // ---------------------------------------------------------------------------------------------
    @Override
    public void freeVisualPanel(JPanel oldVisualPanel) {
        synchronized (visualPanels) {
            visualPanels.remove(oldVisualPanel);
        }
    }


    @Override
    public JPanel getVisualPanel() {
        final JPanel ret = new JPanel(new BorderLayout());

        ret.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 30, 10, 30),
                BorderFactory.createLoweredBevelBorder()));


        synchronized (visualPanels) {
            visualPanels.add(ret);
        }

        JPanel curve = new AktivationFunctionDrawer(this);

        JPanel control = null;
        try {
            control = AnnotationGuiGenerator.generateComponent(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ret.add(curve, BorderLayout.CENTER);

        if (control != null) ret.add(control, BorderLayout.SOUTH);

        return ret;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    // ---------------------------------------------------------------------------------------------

    public static void main(String[] args) {

        ActivationFunctionInterface afi1 = LinearActivation.getActivationFunction();
        Utils.showBean(afi1.getVisualPanel(), afi1.getName());

        ActivationFunctionInterface afi2 = SignumActivation.getActivationFunction();
        Utils.showBean(afi2.getVisualPanel(), afi2.getName());

    }

}
