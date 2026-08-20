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

package tw.master.visionfield;

import java.awt.geom.Point2D;
import java.io.Serializable;

public class VisionFieldParams implements Serializable {

    private static final long	serialVersionUID	= 1L;

    public	int		width;
    public	int		height;

    /**
     * Position of view.
     */
    public Point2D.Float p;

    /**
     * Angle of view (direction).
     */
    public float a;

    /**
     * Array of seen value.
     */
    public float[][]	sf;

    /**
     * Maximal found value.
     */
    public float		w;

    public	boolean		hasEverSeen;

    public long			changeCounter;

    /**
     * Found overlay value if overlay image is found (this is the RED value from ((100...200) - 100) / 100f).<p>
     * This is used for coding some energy values into the area (range 0.0...1.0)
     */
    public float		overlayValue;

    public boolean		update;

    // Caches for positions:
    public int					lastXp;
    public int					lastYp;
    public float				lastA				= -123456.789f; // Not
    // init....

    public VisionFieldParams(int width, int height) {
        this.width  = width;
        this.height = height;
    }
}
