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

import javax.media.opengl.GLAutoDrawable;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Point4d;

import hyperbolize.objet.*;

/**
 * 
 * This class extends the animation scheme for cubes.
 * Given a starting cube and an ending cube with respective coordinates and colors,
 * it allows an animated cube to be drawn by interpolation.
 *
 */
public class 			AnimatedCube extends Animation implements Objet
{
	private Cube		cStart;
	private Cube		cEnd;
	private Cube		cCurrent;

	
	public				AnimatedCube(double d)
	{
		super(d);
		cStart = new Cube(1.0);
		cEnd = new Cube(1.0);
		cCurrent = new Cube(1.0);
	}
	
	public				AnimatedCube(double d, Cube s, Cube e)
	{
		super(d);
		cStart = new Cube(s);
		cEnd = new Cube(e);
		cCurrent = new Cube(cStart);
		cCurrent.setVisible(true);
	}
	
	public void 		applyTransform(Matrix4d t)
	{
		cStart.applyTransform(t);
		cEnd.applyTransform(t);
		cCurrent.applyTransform(t);
	}

	public void 		draw(GLAutoDrawable d)
	{
		setTime();
		cCurrent.setAlpha(interpolation(cStart.getAlpha(), cEnd.getAlpha(), time));
		cCurrent.setSize(interpolation(cStart.getSize(), cEnd.getSize(), time));
		cCurrent.setCoord4d(interpolation(cStart.getCoord4d(), cEnd.getCoord4d(), time));
		cCurrent.setColor(interpolation(cStart.getColor(), cEnd.getColor(), time));
		cCurrent.draw(d);
	}

	public void 		setAlpha(double alpha)
	{
		
	}

	public void 		setCoord4d(Point4d p) 
	{

	}

	public void 		setCoordPolaire(Point3d p)
	{
		
	}

	public void 		setRotation(double x, double y, double z)
	{
		
	}

	public boolean 		isVisible()
	{
		return isDead();
	}

	public void 		setVisible(boolean v)
	{
		
	}
}
