/*
	hyperbolize - a java implementation of the H3 algorithm

	Copyright (C) 2008 Philippe Esling

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package hyperbolize.math;

import javax.vecmath.Point3d;
import javax.vecmath.Point4d;
import javax.vecmath.Tuple4d;

public class 					Maths 
{
    public static final double 	EPSILON = 1e-10;
    
    public static boolean 		epsilonZero(double x)
    {
    	return Math.abs(x) < EPSILON;
    }

    public static double 		sinh(double x)
    {
    	return (Math.exp(x) - Math.exp(-x)) / 2.0;
    }

    public static double 		cosh(double x)
    {
    	return (Math.exp(x) + Math.exp(-x)) / 2.0;
    }

    public static double 		tanh(double x)
    {
    	double expX = Math.exp(x);
    	double exp_X = Math.exp(-x);

    	return (expX - exp_X) / (expX + exp_X);
    }

    public static double 		asinh(double x)
    {
    	return Math.log(x + Math.sqrt(x * x + 1));
    }

    public static double 		acosh(double x)
    {
    	return Math.log(x + Math.sqrt(x * x - 1));
    }

    public static double 		atanh(double x)
    {
    	return Math.log((1.0 + x) / (1.0 - x)) / 2.0;
    }
    
    public static double 		dotProduct(Point3d x, Point3d y)
    {
    	return x.x*y.x + x.y*y.y + x.z*y.z;
    }

    public static double 		dotProduct(Point4d x, Point4d y)
    {
    	return (x.x*y.x + x.y*y.y + x.z*y.z) / (x.w * y.w);
    }

    public static double 		minkowski(Point4d x, Point4d y)
    {
    	return x.x*y.x + x.y*y.y + x.z*y.z - x.w*y.w;
    }

    public static double 		vectorLength(Tuple4d p)
    {
    	double w2 = p.w * p.w;
    	
    	return Math.sqrt((p.x*p.x + p.y*p.y + p.z*p.z) / w2);
    }

    public static void 			makeUnitVector(Tuple4d p)
    {
    	double s = p.w * vectorLength(p);
    	
    	p.x /= s;
    	p.y /= s;
    	p.z /= s;
    	p.w = 1.0;
    }
}
