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

import hyperbolize.math.Maths;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Stack;

/**
 * 
 * This class encapsulates the computing of the positions in the hyperbolic space for a spanning tree structured graph.
 * These static functions follow the H3 algorithm two-pass scheme defined by Tamara MUNZER.
 * 
 */
public class 				GrapheHyperboliqueH3
{
	private static double 	AIRE_FEUILLE = 0.25;
    private static double 	ESPACEMENT_HEMISPHERE = 2.0;

    public static void		premierePasse (GrapheHyperbolique g)
    {
    	// On initialise tout les radius a une valeur invalide
    	initalizeAllRadiiToInvalidValue(g);

    	// On cree une pile d'index en commencant par la racine
    	Stack<Integer> s = new Stack<Integer>();
    	s.push(new Integer(g.getRoot()));

    	// On parcourt la pile pour effectuer le calcul de radius
    	while (!s.empty())
    	{
    		int node = (s.pop()).intValue();

    		// On parcourt tous les noeuds adjacents au noeud courant
    		Iterator<?> children = g.adjNodes(g.getNode(node));
    		if (children.hasNext())
    		{
    			if (checkAllChildrenRadiiAvailable(children, g))
    			{
    				children = g.adjNodes(g.getNode(node));
    				double totalChildrenSphericalCapArea = sumTotalChildrenSphericalCapArea(children, g);
    				double hemisphereArea = ESPACEMENT_HEMISPHERE * totalChildrenSphericalCapArea;
    				double radius = calculateRadiusFromHemisphereArea (hemisphereArea);
    				g.getNode(node).getValue().getCoordPolaire().setX(radius);
    			}
    			else
    			{
    				s.push(new Integer(node));
    				children = g.adjNodes(g.getNode(node));
    				for (int i = 0; children.hasNext(); i++)
    				{
    					s.push(new Integer(((Node<?>)children.next()).getIndex()));
    				}
    			}
    		}
    		else
    		{
    			// Cas d'une feuille, le radius est calcule a partir d'une valeur fixe
    			double radius = calculateRadiusFromHemisphereArea(AIRE_FEUILLE);
    			g.getNode(node).getValue().getCoordPolaire().setX(radius);
    		}
    	}
    }

    private static double 	sumTotalChildrenSphericalCapArea (Iterator<?> children, GrapheHyperbolique g)
    {
    	double 				area = 0.0;
    	int					child;

    	for (int i = 0; children.hasNext(); i++)
    	{
    		child = ((Node<?>)children.next()).getIndex();
    		double childradius = g.getNode(child).getValue().getCoordPolaire().x;
    		double childarea = calculateCircleAreaFromRadius (childradius);
    		area += childarea;
    	}
    	return area;
    }

    private static boolean 	checkAllChildrenRadiiAvailable(Iterator<?> children, GrapheHyperbolique g)
    {
    	for (int i = 0; children.hasNext(); i++)
    	{
    		int child = ((Node<?>)children.next()).getIndex();
    		if (g.getNode(child).getValue().getCoordPolaire().x < 0.0)
    			return false;
    	}
    	return true;
    }

    public static void 		secondePasse(GrapheHyperbolique g)
    {
    	Stack<Integer> s = new Stack<Integer>();

    	g.getNode(g.getRoot()).getValue().getCoordPolaire().setY(0.0);
    	g.getNode(g.getRoot()).getValue().getCoordPolaire().setZ(0.0);

    	s.push(new Integer(g.getRoot()));

    	while (!s.empty()) {
    		int parent = (s.pop()).intValue();
    		double parentRadius = g.getNode(parent).getValue().getCoordPolaire().x;
    		Iterator<?> unsortedChildren = g.adjNodes(g.getNode(parent));

    		if (unsortedChildren.hasNext())
    		{
    			int[] sortedChildren = sortChildren (unsortedChildren, g.nbAdjacent(g.getNode(parent)), g);
    			double phiAccum = calculateDeltaPhi(g.getNode(sortedChildren[0]).getValue().getCoordPolaire().x, parentRadius);
    			double thetaAccum = 0.0;
    			// Garde une trace du deltaphi precedent
    			double previousDeltaPhi = 0.0;

    			for (int i = 1; i < sortedChildren.length; i++) {
    				// Mets phi pour la premiere bande
    				if (i == 1) {
    					previousDeltaPhi = calculateDeltaPhi(g.getNode(sortedChildren[i]).getValue().getCoordPolaire().x, parentRadius);
    					phiAccum += previousDeltaPhi;
    				}
    				// Deplacement le long de la bande
    				double deltaTheta = calculateDeltaTheta(g.getNode(sortedChildren[i]).getValue().getCoordPolaire().x, parentRadius, phiAccum);

    				// Arrivee a la fin d'une bande : changement de bande
    				if (thetaAccum + 2.0 * deltaTheta > 2.0 * Math.PI) {
    					phiAccum += previousDeltaPhi;
    					previousDeltaPhi = calculateDeltaPhi(g.getNode(sortedChildren[i]).getValue().getCoordPolaire().x, parentRadius);
    					phiAccum += previousDeltaPhi;

    					thetaAccum = 0.0;

    					// on recalcule ce fils
    					i--;
    					continue;
    				}
    				else {
    					// augmente le theta
    					thetaAccum += deltaTheta;
    					double theta = thetaAccum;
    					double phi = phiAccum;
    					g.getNode(sortedChildren[i]).getValue().getCoordPolaire().setY(phi);
    					g.getNode(sortedChildren[i]).getValue().getCoordPolaire().setZ(theta);

    					if (!((0.0 <= phi) && (phi <= 2.0 * Math.PI))) {
    						System.err.println ("ERROR: phi was set out of bounds.");
    					}
    					if (!((0.0 <= theta) && (theta <= 2.0 * Math.PI))) {
    						System.err.println ("ERROR: theta was set out of bounds.");
    					}


    					thetaAccum += deltaTheta;
    				}
    			}
    		}
    		unsortedChildren = g.adjNodes(g.getNode(parent));

    		for (int i = 0; unsortedChildren.hasNext(); i++) {
    			int child = ((Node<?>)unsortedChildren.next()).getIndex();
    			s.push(new Integer(child));
    		}

    	}

    }

    private static int[] 	sortChildren (Iterator<?> unsortedChildren, int nb, GrapheHyperbolique g)
    {
    	RadiusSortNode[] 	rsnarray = new RadiusSortNode[nb];
    	
    	for (int i = 0; i < rsnarray.length; i++)
    	{
    		int id = ((Node<?>)unsortedChildren.next()).getIndex();
    		double radius = g.getNode(id).getValue().getCoordPolaire().x;
    		rsnarray[i] = new RadiusSortNode(id, radius);
    	}
    	Arrays.sort(rsnarray);
    	int[] result = new int[rsnarray.length];
    	for (int i = 0; i < rsnarray.length; i++) {
    		result[i] = rsnarray[i].id;
    	}
    	return result;
    }

    private static void 	initalizeAllRadiiToInvalidValue(GrapheHyperbolique g)
    {
    	for (int i = 0; i < g.nodesNumber(); i++)
    	{
    		g.getNode(i).getValue().getCoordPolaire().setX(-1.0);
    	}
    }

    private static double 	calculateCircleAreaFromRadius (double radius)
    {
    	return (2.0 * Math.PI * (Maths.cosh(radius) - 1.0));
    }

    private static double 	calculateRadiusFromHemisphereArea (double area)
    {
    	return (Maths.asinh(Math.sqrt(area / (2.0 * Math.PI))));
    }

    private static double 	calculateDeltaPhi (double radius, double radiusParent)
    {
    	return (Math.atan((Maths.tanh(radius)) / (Maths.sinh(radiusParent))));
    }

    private static double 	calculateDeltaTheta (double radius, double radiusParent, double phiCurrentBand)
    {
    	return (Math.atan((Maths.tanh(radius)) / (Maths.sinh(radiusParent * Math.sin(phiCurrentBand)))));
    }
}


class 				RadiusSortNode implements Comparable<Object>
{
	public int 		id;
	public double 	radius;

	public 			RadiusSortNode(int nodeid, double noderadius)
	{
		this.id = nodeid;
		this.radius = noderadius;
	}

	public int 		compareTo(Object o) throws ClassCastException
	{
		if (!(o instanceof RadiusSortNode))
			throw (new ClassCastException ("Uncomparable object to RadiusSortNode."));
		RadiusSortNode rsn = (RadiusSortNode) o;
		if (this.radius < rsn.radius)
			return 1;
		if (this.radius == rsn.radius)
			return 0;
		else
			return -1;
	}

	public String 	toString()
	{
		return id + ": " + radius;
	}
}
