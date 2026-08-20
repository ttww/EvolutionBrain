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

package tw.master.crawler;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;

import tw.master.engine.Engine;
import tw.master.math.FastTrigonomic;
import tw.master.tree.TreeNodeInfoInterface;
import tw.master.utils.UpdateListenerInterface;
import tw.master.visionfield.VisionFieldHandler;



/**
 *
 * @author Thomas Welsch
 */
public abstract class Crawler implements Serializable, TreeNodeInfoInterface {

    private static final long serialVersionUID = 1L;

    /**
     * @author    Thomas Welsch
     */
    public enum LiveState {
        Living,
        Hold,
        Dead_LostConnection,
        Dead_NoEnergy,
        Dead_NoActivity,
        Dead_StepLimit,
        Dead_Circling,
        Dead_LostLine,
        Dead_ByDog,
    }

    public LiveState           liveState         = LiveState.Living;

//	protected	boolean				isDead;
    protected Point.Float      pos;

    protected Point.Float      step;

    protected String           name;

    protected int              generation;

    protected long             lastMutation;

    private boolean            stepDirty;

    private float              limitXMin;

    private float              limitXMax;

    private float              limitYMin;

    private float              limitYMax;

    protected float            direction;

    protected float            speed;

    protected long             stepCount;

    protected long             maxSteps;

    protected float            energy            = 5000;

    private float              visitEnergy;

    private float              lastEnergyChanges = 0;

    protected int              lastX;

    protected int              lastY;

    public int                 visitWidth;

    public int                 visitHeight;

    /**
     * Array with all positions, that has been visited.
     */
    public byte[]              visit;

    /**
     * stepCount of last <code>visit</code> update.
     */
    public long                visitUpdate;

    //public boolean             isWatched;

    public boolean             isControled;

    private int                numberOfSetPixels;


    public transient boolean   removeFromRunListForTransfer;

    protected transient Engine engine;

//	private GlobalsServer globalsServer;


    // ---------------------------------------------------------------------------------------------

    public Crawler(Engine engine) {
        this(engine, engine.startPos, 0, 1, engine.limits);
    }

    // ---------------------------------------------------------------------------------------------

    public Crawler(Engine engine, Point.Float startPos, float angle, float speed, Rectangle2D.Float limits) {
        this.engine = engine;
        this.pos = new Point.Float(startPos.x, startPos.y);

        this.limitXMin = limits.x;
        this.limitYMin = limits.y;
        this.limitXMax = limits.x + limits.width;
        this.limitYMax = limits.y + limits.height;
        this.speed = speed;

        this.step = new Point.Float();

//		this.maxSteps		= Rnd.rnd(100,10000);
        this.maxSteps = 0;
        setAngle(angle);

        visitHeight = (int) (limits.height + 0.5) + 1;
        visitWidth = (int) (limits.width + 0.5) + 1;

        visit = new byte[visitHeight * visitWidth];

        numberOfSetPixels = engine.visionFieldHandler.getNumberOfSetPixels();

        //System.err.println(numberOfSetPixels);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Used after deserialisation for setting the engine.
     * 
     * @param  engine
     */
    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    // ---------------------------------------------------------------------------------------------

    public void setAngle(float angle) {

//		if (isDead) throw new IllegalStateException("operation on dead crawler");
        if (liveState != LiveState.Living) throw new IllegalStateException("operation on dead crawler");

        while (angle < 0)
            angle += 360;
        while (angle > 360)
            angle -= 360;

        this.direction = angle;

        stepDirty = true;

//		System.err.println(this + "  Angel = "+direction+"   Step = "+step);
    }

    // ---------------------------------------------------------------------------------------------

    public void adjustSpeed(float adjustSpeed) {

        if (adjustSpeed == 0) return;

        speed += adjustSpeed;
        if (speed < 0) speed = 0;
        else
            if (speed > 1) speed = 1;

        stepDirty = true;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * @param  speed
     */
    public void setSpeed(float speed) {

        if (speed < 0) speed = 0;
        else
            if (speed > 1) speed = 1;

        if (Math.abs(this.speed - speed) < 0.001f) return;

        this.speed = speed;
        stepDirty = true;
    }

    // ---------------------------------------------------------------------------------------------

    private void updateStep() {
        if (!stepDirty) return;

        step.x = 0 * speed;
        step.y = 1 * speed;

        rotatePoint(step, direction);
        stepDirty = false;
    }

    // ---------------------------------------------------------------------------------------------

    public void adjustAngle(float adjustAngel) {
        if (adjustAngel == 0) return;

        setAngle(this.direction - adjustAngel);
    }

    // ---------------------------------------------------------------------------------------------

    public float getAngle() {
        return direction;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * @return
     */
    public long getStepCount() {
        return stepCount;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * @param  name
     */
    public void setName(String name) {
        this.name = name;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    // ---------------------------------------------------------------------------------------------

    public void getPos(Point.Float retPos) {
        retPos.x = pos.x;
        retPos.y = pos.y;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * @return
     */
    public float getVisitEnergy() {
        return visitEnergy;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * @return
     */
    public float getEnergy() {
        return energy;
    }

    public void changeEnergy(float change) {
        final float SUM_COUNT = 100f;

        energy += change;
        lastEnergyChanges = (lastEnergyChanges * SUM_COUNT + change) / (SUM_COUNT + 1);
    }

    /**
     * @return
     */
    public float getLastEnergyChanges() {
        return lastEnergyChanges;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Do one step in the current direction.
     */
    public void step() {
        stepCount++;
        if (maxSteps != 0 && stepCount > maxSteps) {
//			isDead = true;
            liveState = LiveState.Dead_StepLimit;
            return;
        }
//		if (isDead) throw new IllegalStateException("operation on dead crawler");
        if (liveState != LiveState.Living) throw new IllegalStateException("operation on dead crawler");

        updateStep();

        float x = pos.x + step.x;
        if (x < limitXMin) x += limitXMax - limitXMin;
        if (x > limitXMax) x -= limitXMax - limitXMin;
        //if (x < limitXMin || x >= limitXMax) return false;

        float y = pos.y + step.y;
        if (y < limitYMin) y += limitYMax - limitYMin;
        if (y > limitYMax) y -= limitYMax - limitYMin;
//		if (y < limitYMin || y >= limitYMax) return false;

        synchronized (this) {
            pos.x = x;
            pos.y = y;

//			System.err.println(this + "  new = "+direction+"  Step = "+step+"  pos = "+pos);
        }


        // -----------------------------------------------------------------------------------------
        // Slowly fade out the visit field
        // -----------------------------------------------------------------------------------------
        if (stepCount % 200 == 0) { // Remark: Find this routine in our brain
            for (int i = 0; i < visit.length; i++) {
                byte bb = visit[i];
                if (bb != 0) {
                    bb--;
                    visit[i] = bb;
                }
            }
        }

        // -----------------------------------------------------------------------------------------
        // Update the "visit" array and
        // calculate the energy which represents the "correctness" of being on the "line"
        // -----------------------------------------------------------------------------------------
        int ix = (int) (x + 0.5f - limitXMin);
        int iy = (int) (y + 0.5f - limitYMin);
        if (ix != lastX || iy != lastY) {
            lastX = ix;
            lastY = iy;

            //System.err.println(name+" Set "+lastX+","+lastY+"  --> "+(lastY * visitWidth + lastX));
//			visit[lastY * visitWidth + lastX]++;

            // -------------------------------------------------------------------------------------
            // Get the "number of visits" from the visit array for the actual position:
            // If we are under 200 we refresh this to the minimum of 200, else we increase by 10
            // This is mainly for the display of the visit trail
            // -------------------------------------------------------------------------------------
            int actVisitIdx = lastY * visitWidth + lastX;

            short b = (short) (visit[actVisitIdx] & 0xff);
            if (b < 200) {
                b = 200;
            } else {
                b += 10;
                if (b > 255) b = 255;
            }

            visit[actVisitIdx] = (byte) b;

            // -------------------------------------------------------------------------------------
            // If the brain moves and the last calculation is older than 10 steps, we are recalculate.
            // Doing it only 1/10 the time, we save CPU for other things and it's enough :-)
            // -------------------------------------------------------------------------------------
            if (stepCount - visitUpdate > 10) {


                // ---------------------------------------------------------------------------------
                // Calculate ratio set number if pixel against number of visits
                // ---------------------------------------------------------------------------------
                if (false) {
                    int notZero = 0; //	;-)
                    for (int i = 0; i < visit.length; i++) {

                        if (visit[i] != 0) notZero++;
//						short v = (short) (visit[i] & 0xff);
//						if (/*v < 210 &&*/ v > 0) {
//							v--;
//							if (v != 0) v--;
//							if (v != 0) notZero++;
//							visit[i] = (byte) v;
//						}
                    }
                    visitEnergy = (float) notZero / numberOfSetPixels;
                }

                // ---------------------------------------------------------------------------------
                // Calculate ratio of visits, that have a pixel in the image:
                // ---------------------------------------------------------------------------------
                if (true) {
                    VisionFieldHandler lfh = engine.visionFieldHandler;
                    int hitCount = 0;
                    boolean haveOverlay = engine.imgOverlay != null ? true : false;
                    for (int i = 0; i < visit.length; i++) {
                        if (visit[i] != 0) {
                            int visitY = i / visitWidth;
                            int visitX = i % visitWidth;

                            float iv;
                            if (haveOverlay) iv = lfh.getCachedOverlayValue(visitX, visitY);
                            else
                                iv = lfh.getCachedImageValue(visitX, visitY);
                            if (iv > 0) hitCount++;
                        }
                    }
                    visitEnergy = (float) hitCount / numberOfSetPixels;
                    //if (hitCount > 100) System.err.println("Hits = "+hitCount+" --> "+visitEnergy+"   of "+numberOfSetPixels);
                }

                visitUpdate = stepCount;
            }
        }

    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Inform listeners about updates (for GUI Updates).
     */
    public void callUpdateListeners() {
        if (updateListener.size() != 0) {
            LinkedList<UpdateListenerInterface> copyUL = new LinkedList<UpdateListenerInterface>();

            synchronized (updateListener) {
                copyUL.addAll(updateListener);
            }

            for (UpdateListenerInterface as : copyUL)
                as.doUpdate();
        }
    }

    // ---------------------------------------------------------------------------------------------

    private transient LinkedList<UpdateListenerInterface> updateListener = new LinkedList<UpdateListenerInterface>();

    // ---------------------------------------------------------------------------------------------

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        deSerialisationInit();
    }

    private void deSerialisationInit() {
        updateListener = new LinkedList<UpdateListenerInterface>();
    }


    public void addUpdateListener(UpdateListenerInterface as) {
        synchronized (updateListener) {
            if (!updateListener.contains(as)) updateListener.add(as);
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void removeUpdateListener(UpdateListenerInterface as) {
        synchronized (updateListener) {
            updateListener.remove(as);
        }
    }

    // ---------------------------------------------------------------------------------------------

    private static float grad2rad(float angle) {
        return angle * (float) (Math.PI / 180);
    }

    // ---------------------------------------------------------------------------------------------

    private static void rotatePoint(Point.Float p, float angle) {
        float rad = grad2rad(angle);
        float x = FastTrigonomic.cos(rad) * p.x - FastTrigonomic.sin(rad) * p.y;
        float y = FastTrigonomic.sin(rad) * p.x + FastTrigonomic.cos(rad) * p.y;
//		x = (float) (Math.cos(rad) * p.x - Math.sin(rad) * p.y);
//		y = (float) (Math.sin(rad) * p.x + Math.cos(rad) * p.y);

        p.x = x;
        p.y = y;
    }


    /**
     * Do a simulation step. This function should call callUpdateListeners() for update the gui
     *
     */
    public abstract void doSimulationStep();

    // ---------------------------------------------------------------------------------------------

    public boolean isDead() {
        return liveState != LiveState.Living && liveState != LiveState.Hold;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the fitness, for simple crawlers this is the lastEnergyChanges mapped to 0..1.
     *
     * @return 0...1
     */
    public float getFitness() {
        if (lastEnergyChanges < 0) return 0;

        return lastEnergyChanges / 2;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public String getText() {
        return name;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public String getHierarchy() {
        return name;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public String getTooltipText() {
        return "Tooltip for " + name + " !\nLive state = " + liveState;
    }

    @Override
    public Color getColor() {
        if (engine.watchedCrawler == this) return Color.RED;
//        if (isWatched) return Color.RED;
        return null;
    }

    @Override
    public float getBarValue() {
        if (rank == -1) return -1;

        return 100 - rank / (float) engine.numberOfCrawlers * 100f;
    }

    public int rank = -1; // Ranking

    @Override
    public void treeNodeClicked() {
        engine.setWatchedCrawler(this);
    }

//	public static void main(final String[] args) {
//	}
}
