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

import tw.master.engine.Engine;
import tw.master.utils.Rnd;
import tw.master.visionfield.VisionFieldParams;


public class WalkingCrawler extends Crawler {

    private static final long serialVersionUID = 1L;

    public WalkingCrawler(Engine engine) {
        super(engine);
    }

    private long lastMs;

    private tw.master.visionfield.VisionFieldParams lfp;

    @Override
    public void doSimulationStep() {
        try {
            if (this == engine.controlledCrawler)
                return;

            long nowMs = System.currentTimeMillis();

            boolean doRot;
            if (nowMs - lastMs > 1000) {
                doRot = true;
                lastMs = nowMs;
            }
            else
                doRot = false;
            doRot = false;

            if (doRot) {
                float a = Rnd.rnd(-2f, 2f);
                adjustAngle(a);
            }

            step();
            if (liveState != LiveState.Living) return;

            if (lfp == null) {
                lfp = new VisionFieldParams(6, 8);
                lfp.p = pos;
            }
            lfp.a = direction;

            engine.visionFieldHandler.updateVisionFieldData(lfp);
            float w = lfp.w;

            // float[][] lf = globals.getVisionField(pos,direction);
            // float w = findFirst(lf);
            // float w = globals.getImageValue(pos);
            // float w = globals.get9ImageValue(runP);
            if (w > 0.001) {
                this.adjustAngle(Rnd.rnd(150f, 210f));
                // setAngle(Rnd.rnd(0f,90f));
                // step();
            }
        }
        finally {
            this.callUpdateListeners();
        }
    }

}
