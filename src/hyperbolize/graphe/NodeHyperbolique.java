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

package hyperbolize.graphe;

import hyperbolize.objet.Objet;

import javax.vecmath.*;

public class 					NodeHyperbolique
{
	private Point3d				coord3d;
	private Point4d				coord4d;
	private Point3d				coordPolaire;
	private String				label;
	private Objet				representation;
	
	public 						NodeHyperbolique()
	{
		coord3d = new Point3d(0.0, 0.0, 0.0);
		coord4d = new Point4d(0.0, 0.0, 0.0, 0.0);
		coordPolaire = new Point3d(-1.0, 0.0, 0.0);
	}
	
	public						NodeHyperbolique(String l)
	{
		label = l;
		coord3d = new Point3d(0.0, 0.0, 0.0);
		coord4d = new Point4d(0.0, 0.0, 0.0, 0.0);
		coordPolaire = new Point3d(-1.0, 0.0, 0.0);
	}
	
	public						NodeHyperbolique(String l, Objet o)
	{
		label = l;
		coord3d = new Point3d(0.0, 0.0, 0.0);
		coord4d = new Point4d(0.0, 0.0, 0.0, 0.0);
		coordPolaire = new Point3d(-1.0, 0.0, 0.0);
		representation = o;
	}
	
	public Point3d				getCoord3d()
	{
		return coord3d;
	}
	
	public Point4d				getCoord4d()
	{
		return coord4d;
	}
	
	public Point3d				getCoordPolaire()
	{
		return coordPolaire;
	}
	
	public String				getLabel()
	{
		return label;
	}
	
	public Objet				getRepresentation()
	{
		return representation;
	}
	
	public void					setRepresentation(Objet o)
	{
		representation = o;
		representation.setCoord4d(coord4d);
	}
	
	public void					setCoord3d(Point3d p)
	{
		coord3d = p;
	}
	
	public void					setCoord4d(Point4d p)
	{
		coord4d = p;
		representation.setCoord4d(p);
	}
	
	public void					setCoordPolaire(Point3d p)
	{
		coordPolaire = p;
		representation.setCoordPolaire(p);
	}
}
