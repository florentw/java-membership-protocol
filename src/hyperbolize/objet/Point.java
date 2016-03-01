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

package hyperbolize.objet;

import javax.media.opengl.GLAutoDrawable;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Point4d;

public class Point implements hyperbolize.objet.Objet
{

	public void setVisible(boolean v) {
	}

	public void setRotation(double x, double y, double z) {
	}

	public void setCoordPolaire(Point3d p) {
	}

	public void setCoord4d(Point4d p) {
	}

	public void setAlpha(double alpha) {
	}

	public boolean isVisible() {
		return false;
	}

	public boolean isDead() {
		return false;
	}

	public void draw(GLAutoDrawable d) {
	}

	public void applyTransform(Matrix4d t) {
	}

}
