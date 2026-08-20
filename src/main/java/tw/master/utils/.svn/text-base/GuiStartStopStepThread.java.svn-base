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

import tw.master.GlobalsClientGui;



public abstract class GuiStartStopStepThread extends AbstractStartStopStepThread {

    private final GlobalsClientGui globals;

    /**
     * Create but not start the Thread.
     * 
     * @param name
     * @param globals
     */
    public GuiStartStopStepThread(String name, GlobalsClientGui globals) {
        this(name, globals, 0, false, false);
    }

    public GuiStartStopStepThread(String name, GlobalsClientGui globals, int sleepTime, boolean autostep) {
        this(name, globals, sleepTime, autostep, false);
    }

    public GuiStartStopStepThread(String name, int sleepTime, boolean autostep, boolean autostart) {
        this(name, null, sleepTime, autostep, autostart);
    }

    public GuiStartStopStepThread(String name, GlobalsClientGui globals, int sleepTime, boolean autostep,
            boolean autostart) {
        super(name, sleepTime, autostep, autostart);
        this.globals = globals;
        if (autostart) start();
    }


    public abstract void doStep(GlobalsClientGui lglobals);

    @Override
    public void doStep() {
        doStep(globals);
    }

}
