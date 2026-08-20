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

package tw.master.math;

/**
 * Class with fast and optimized trigonomic functions.<p>
 * Because we are using mostly the same values, this class calculates only the really used
 * sin() (...) values and store it in a cache. This way we can have a high precision
 * and are fast....
 *
 * @author Thomas Welsch
 */
public class FastTrigonomic {

    private static final float TWO_PI	=  (float) Math.PI * 2;

    private static final int	SIZE 	=  0x1000;

    private static final float RAD_SLICE = TWO_PI / SIZE;

    private	static float[] sinCache;
    private	static float[] cosCache;

    public static float sin(float a) {
        if (sinCache == null) {
            sinCache = new float[SIZE];
            for ( int i = 0; i < SIZE; i++ )    {
                sinCache[ i ] = (float) Math.sin( i * RAD_SLICE );
            }
        }

        int i = (int) (a / TWO_PI * SIZE ) & SIZE - 1;
        return sinCache[i];
    }

    public static float cos(float a) {
        if (cosCache == null) {
            cosCache = new float[SIZE];
            for ( int i = 0; i < SIZE; i++ )    {
                cosCache[ i ] = (float) Math.cos( i * RAD_SLICE );
            }
        }

        int i = (int) (a / TWO_PI * SIZE ) & SIZE - 1;
        return cosCache[i];
    }


}
