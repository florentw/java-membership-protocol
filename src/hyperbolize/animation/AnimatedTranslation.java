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

import hyperbolize.espace.EspaceHyperbolique;
import hyperbolize.objet.Objet;
import hyperbolize.rendu.Rendu;

public class 					AnimatedTranslation extends Animation implements Objet
{
	private Point4d				pStart;
	private Point4d				pEnd;
	private Point4d				pCurrent;
	private Point4d				oCurrent;
	private Rendu				rendu;
	
	public 						AnimatedTranslation(double d, Rendu r)
	{
		super(d);
		rendu = r;
	}
	
	public 						AnimatedTranslation(double d, Point4d pS, Point4d pE, Rendu r)
	{
		super(d);
		pStart = new Point4d(pS);
		pEnd = new Point4d(pE);
		pCurrent = new Point4d(pS);
		oCurrent = new Point4d();
		rendu = r;
	}

	public void 				applyTransform(Matrix4d t) 
	{	
	}

	public void 				draw(GLAutoDrawable d)
	{
		setTime();
		oCurrent.set(pCurrent);
		pCurrent.set(interpolation(pStart, pEnd, time));
		rendu.applyTransform(EspaceHyperbolique.translate(pCurrent, oCurrent));
	}


	public void 				setAlpha(double alpha) {}

	public void 				setCoord4d(Point4d p) {}

	public void 				setCoordPolaire(Point3d p) {}

	public void 				setRotation(double x, double y, double z) {}

	public void 				setVisible(boolean v) {}

	public boolean 				isVisible() {return false;}


}
