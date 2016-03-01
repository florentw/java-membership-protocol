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

package hyperbolize.espace;

import javax.vecmath.*;

import hyperbolize.math.Maths;

public class 						EspaceHyperbolique extends Espace
{
	public static double			distance(Point3d a, Point3d b)
	{
		double t1 = Maths.dotProduct(a, b) - 1.0;
		double t2 = Maths.dotProduct(a, a) - 1.0;
		double t3 = Maths.dotProduct(b, b) - 1.0;	

		return 2.0 * Maths.acosh(Math.sqrt((t1 * t1) / (t2 * t3)));
	}
	
	public static double			distance(Point4d a, Point4d b)
	{
		double t1 = Maths.minkowski(a, b);
		double t2 = Maths.minkowski(a, a);
		double t3 = Maths.minkowski(b, b);

		return 2.0 * Maths.acosh(Math.sqrt((t1 * t1) / (t2 * t3)));
    }
	    
    public static Matrix4d 			reflect(Point4d p)
    {
    	double xx = p.x * p.x;
    	double xy = p.x * p.y;
    	double xz = p.x * p.z;
    	double xw = p.x * p.w;

    	double yy = p.y * p.y;
    	double yz = p.y * p.z;
    	double yw = p.y * p.w;

    	double zz = p.z * p.z;
    	double zw = p.z * p.w;

    	double ww = p.w * p.w;

    	Matrix4d result = new Matrix4d(xx, xy, xz, -xw,
    				       xy, yy, yz, -yw,
    				       xz, yz, zz, -zw,
    				       xw, yw, zw, -ww);

    	result.mul( - ((2.0) / (Maths.minkowski(p, p))));
    	result.add(Espace.IDENTITY4);

    	return result;
    }
    
	public static Matrix4d	 		translate(Point4d a, Point4d b)
    {
		double aa_h = Maths.minkowski(a, a);
		double bb_h = Maths.minkowski(b, b);
		double ab_h = Maths.minkowski(a, b);
		double lScale = Math.sqrt(bb_h * ab_h);
		double rScale = Math.sqrt(aa_h * ab_h);
		Point4d midpoint = new Point4d();
		midpoint.x = lScale * a.x + rScale * b.x;
		midpoint.y = lScale * a.y + rScale * b.y;
		midpoint.z = lScale * a.z + rScale * b.z;
		midpoint.w = lScale * a.w + rScale * b.w;

		Matrix4d r_a = reflect(a);
		Matrix4d r_m = reflect(midpoint);
		r_m.mul(r_a);
		return r_m;
    }
		

    
	public static Point4d 			calculatePivot(Point4d a, Point4d b)
	{
		Point3d 					ae = new Point3d();
		Point3d 					be = new Point3d();
		Point3d 					ab = new Point3d();
		double 						p, q, r;
		
		ae.project(a);
		be.project(b);
		ab.sub(ae, be);
		p = Maths.dotProduct(ae, ab);
		q = Maths.dotProduct(be, ab);
		r = Maths.dotProduct(ab, ab);
		return new Point4d(	p * be.getX() - q * ae.getX(),
				   			p * be.getY() - q * ae.getY(),
				   			p * be.getZ() - q * ae.getZ(),
				   			r);
	}
}
