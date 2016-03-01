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

package hyperbolize.rendu;

import java.util.Iterator;
import java.util.LinkedList;

import javax.media.opengl.GLAutoDrawable;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point4d;

import hyperbolize.espace.Espace;
import hyperbolize.espace.EspaceHyperbolique;
import hyperbolize.objet.Objet;


public class 					Rendu
{
	private LinkedList<Objet>	listeObjets;

	public						Rendu()
	{
		listeObjets = new LinkedList<Objet>();
	}

	public void					flush()
	{
		listeObjets.clear();
	}

	public void					addObjet(Objet o)
	{
		listeObjets.add(o);
	}

	public LinkedList<Objet>	getListeObjets()
	{
		return listeObjets;
	}

	public void					draw(GLAutoDrawable d)
	{
		for (Iterator<Objet> i = listeObjets.iterator(); i.hasNext(); )
		{
			Objet o = (Objet) i.next();
			o.draw(d);
			if (o.isDead())
				i.remove();
		}
	}

	public void					applyTransform(Matrix4d t)
	{
		for (Iterator<Objet> i = listeObjets.iterator(); i.hasNext(); )
		{
			Objet o = (Objet) i.next();
			o.applyTransform(t);
		}
	}

	public void					rotateX(double alpha)
	{
		Matrix4d				rot = new Matrix4d(Espace.IDENTITY4);

		rot.rotX(alpha);
		for (Objet o : listeObjets)
		{
			o.applyTransform(rot);
			o.setRotation(alpha, 0.0, 0.0);
		}
	}

	public void					rotateY(double alpha)
	{
		Matrix4d				rot = new Matrix4d(Espace.IDENTITY4);

		rot.rotY(alpha);
		for (Objet o : listeObjets)
		{
			o.applyTransform(rot);
			o.setRotation(0.0, alpha, 0.0);
		}
	}

	public void					rotateZ(double alpha)
	{
		Matrix4d				rot = new Matrix4d(Espace.IDENTITY4);

		rot.rotZ(alpha);
		for (Objet o : listeObjets)
		{
			o.applyTransform(rot);
			o.setRotation(0.0, 0.0, alpha);
		}
	}

	public void					translate(Point4d a, Point4d b)
	{
		Matrix4d				t = EspaceHyperbolique.translate(a, b);

		for (Objet o : listeObjets)
			o.applyTransform(t);
	}
}
