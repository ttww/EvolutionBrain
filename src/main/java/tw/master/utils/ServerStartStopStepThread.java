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

package tw.master.utils;

import tw.master.engine.Engine;



public abstract class ServerStartStopStepThread extends AbstractStartStopStepThread {

    private final Engine engine;

    public ServerStartStopStepThread(String name, Engine globals) {
        this(name, globals, 0, false, false);
    }

    public ServerStartStopStepThread(String name, Engine engine, int sleepTime, boolean autostep) {
        this(name, engine, sleepTime, autostep, false);
    }

    public ServerStartStopStepThread(String name, int sleepTime, boolean autostep, boolean autostart) {
        this(name, null, sleepTime, autostep, autostart);
    }

    public ServerStartStopStepThread(String name, Engine engine, int sleepTime, boolean autostep, boolean autostart) {
        super(name, sleepTime, autostep, autostart);
        setDaemon(true);
        this.engine = engine;
        if (autostart) start();
    }

    public abstract void doStep(@SuppressWarnings("hiding") Engine engine);

    @Override
    public void doStep() {
        doStep(engine);
    }

}
