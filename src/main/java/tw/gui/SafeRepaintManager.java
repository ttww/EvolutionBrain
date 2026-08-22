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

package tw.gui;

import javax.swing.JComponent;
import javax.swing.RepaintManager;

/**
 * Works around a long-standing JDK/Aqua-LookAndFeel bug where a component that has already been
 * removed from its container (or is otherwise not currently showing) can still have a stale dirty
 * region queued in the RepaintManager. When that region is later processed, the component's UI
 * delegate gets asked to paint even though its internal fields were already cleared by
 * uninstallUI(), causing sporadic NullPointerExceptions such as:
 * <pre>
 *   BasicSliderUI.calculateFocusRect / recalculateIfInsetsChanged - "this.focusRect"/"this.slider" is null
 * </pre>
 * Skipping dirty-region requests for components that are not currently showing avoids scheduling
 * those stale repaints in the first place.
 *
 * @author Thomas Welsch
 */
public class SafeRepaintManager extends RepaintManager {

    /**
     * Installs this RepaintManager as the current one for the calling thread's AppContext, unless
     * one is already installed.
     */
    public static void install() {
        if (!(RepaintManager.currentManager((JComponent) null) instanceof SafeRepaintManager)) {
            RepaintManager.setCurrentManager(new SafeRepaintManager());
        }
    }

    @Override
    public void addDirtyRegion(JComponent c, int x, int y, int w, int h) {
        if (c == null || !c.isShowing()) return;
        super.addDirtyRegion(c, x, y, w, h);
    }

}
