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

package hyperbolize.animation;

import javax.vecmath.*;

/**
 * 
 * Main class for animation purposes.
 * It contains the static methods for interpolation formulas using bezier curves
 *
 */
public class 				Animation 
{
	private double			startTime;
	private double			duration;
	protected double		time;
	
	public					Animation(double d)
	{
		duration = d;
		startTime = System.currentTimeMillis();
	}
	
	public void				setTime()
	{
		time = (System.currentTimeMillis() - startTime) / duration;
	}
	
	public double			getTime()
	{
		return time;
	}


	public boolean 			isDead() 
	{
		return time > 1.0;
	}
	
	public static double	interpolation(double a, double b, double t)
	{
		return (t <= 1.0) ? ((1 - t) * a) + (t * b) : b;
	}
	
	public static Point2d	interpolation(Point2d a, Point2d b, double t)
	{
		return new Point2d(interpolation(a.x, b.x, t),
					interpolation(a.y, b.y, t)); 
	}
	
	public static Point3d	interpolation(Point3d a, Point3d b, double t)
	{
		return new Point3d(interpolation(a.x, b.x, t),
					interpolation(a.y, b.y, t),
					interpolation(a.z, b.z, t)); 
	}
	
	public static Point4d	interpolation(Point4d a, Point4d b, double t)
	{
		return new Point4d(interpolation(a.x, b.x, t),
					interpolation(a.y, b.y, t),
					interpolation(a.z, b.z, t),
					interpolation(a.w, b.w, t)); 
	}
	
	public static double	interpolationCubique(double a, double b, double c, double t)
	{
		return (t <= 1.0) ? (Math.pow((1 - t), 2) * a) + (2 * (1 - t) * t * b) + (Math.pow(t, 2) * c) : c;
	}
	
	public static Point2d	interpolationCubique(Point2d a, Point2d b, Point2d c, double t)
	{
		return new Point2d(interpolationCubique(a.x, b.x, c.x, t),
					interpolationCubique(a.y, b.y, c.y, t)); 
	}
	
	public static Point3d	interpolationCubique(Point3d a, Point3d b, Point3d c, double t)
	{
		return new Point3d(interpolationCubique(a.x, b.x, c.x, t),
					interpolationCubique(a.y, b.y, c.y, t),
					interpolationCubique(a.z, b.z, c.z, t)); 
	}
	
	public static Point4d	interpolationCubique(Point4d a, Point4d b, Point4d c, double t)
	{
		return new Point4d(interpolationCubique(a.x, b.x, c.x, t),
					interpolationCubique(a.y, b.y, c.y, t),
					interpolationCubique(a.z, b.z, c.z, t),
					interpolationCubique(a.w, b.w, c.w, t)); 
	}
}
