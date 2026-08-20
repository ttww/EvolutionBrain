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

import java.awt.Color;

import javax.swing.JPanel;



public interface World3dInterface {

    public JPanel get3DPanel(World3dDrawInterface drawer);

    public void reset();

    public void refresh();

    public void rotX(float r);

    public void rotY(float r);

    public void rotZ(float r);

    public void scale(float f);

    public float setLineWidth(float f);

    public void drawText(float x, float y, float z, String txt);

    public void drawLine(float x1, float y1, float z1, float x2, float y2, float z2);

    public void drawSphere(float x, float y, float z, float r);

    public void drawBox(float minX, float maxX, float minY, float maxY, float minZ, float maxZ);

    public void setColor(Color col);

    public View getView();


}
