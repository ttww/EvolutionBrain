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



public class Panel3dFactory {

    /**
     * @author    Thomas Welsch

     */
    public enum TYPE_3D {
        JAVA,
        OPENGL
    };

    public static World3dInterface get3dPanel(TYPE_3D which) {
        switch (which) {
            case JAVA:
                return new PurJava3dImplementation();
            case OPENGL:
                return new OpenGL3dImplementation();
            default:
                break;
        }
        return null;
    }
}
