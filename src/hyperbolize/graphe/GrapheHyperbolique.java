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

import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.vecmath.*;

import hyperbolize.espace.Espace;
import hyperbolize.espace.EspaceHyperbolique;
import hyperbolize.math.Maths;
import hyperbolize.objet.Objet;

/**
* 
* This class contains the representation of an hyperbolic graph.
* It is based on a list graph and implements a drawable interface in order to be rendered in OpenGL.
* 
* @version 									3.1
* 
*/
public class 								GrapheHyperbolique extends ListGraphe<NodeHyperbolique> implements Objet
{
    protected Point4d						actualFocus;
    protected int							actualFocusIndex;
    protected boolean						visible;

	public 									GrapheHyperbolique()
	{
		super();
		actualFocusIndex = getRoot();
		visible = true;
	}

	/**
	 * 
	 * To add an empty hyperbolic node in the graph 
	 * 
	 * @return A reference to the created node
	 * 
	 */
	public Node<NodeHyperbolique>			addNode()
	{
		return super.addNode(new NodeHyperbolique());
	}

	/**
	 * 
	 * To add an hyperbolic node with a specific label in the graph 
	 * 
	 * @param label : Label of the node
	 * @return A reference to the created node
	 * 
	 */
	public Node<NodeHyperbolique>			addNode(String label)
	{
		return super.addNode(new NodeHyperbolique(label));
	}

	/**
	 * 
	 * To add an hyperbolic node with a specific representation in the graph 
	 * 
	 * @param label : Label of the node
	 * @param o : Representation of the node
	 * @return A reference to the created node
	 * 
	 */
	public Node<NodeHyperbolique>			addNode(String label, Objet o)
	{
		return super.addNode(new NodeHyperbolique(label, o));
	}

	/**
	 * 
	 * This function starts the computing of the positions in the hyperbolic space.
	 * Following the H3 two-pass scheme defined by Tamara MUNZER.
	 * 
	 */
	public void								prepareGraphe()
	{
		GrapheHyperboliqueH3.premierePasse(this);
		GrapheHyperboliqueH3.secondePasse(this);
		for (int i = 0; i < nodesNumber(); i++)
			getNode(i).getValue().getRepresentation().setCoordPolaire(getNode(i).getValue().getCoordPolaire());
	}

	/**
	 * 
	 * Just for debugging purposes, prints all the polar coordinates of the nodes
	 * 
	 */
	public void								printValue()
	{
		for (int i = 0; i < nodesNumber(); i++)
			System.out.println(getNode(i).getValue().getCoordPolaire().toString());
	}

	/**
	 * 
	 * Gets the closest node in the graph to a picking value on the screen
	 * 
	 * @param x : x axis position
	 * @param y : y axis position
	 * @param z : z axis position
	 * @return The index of the closest node
	 * 
	 */
	public int								getPickingNode(double x, double y, double z)
	{
		Point3d 							tmp = new Point3d(x, y, z);
		double								min_dist = Double.MAX_VALUE;
		int									min_index = 1;

		actualFocus = getNode(actualFocusIndex).getValue().getCoord4d();
		getNode(actualFocusIndex).getValue().getRepresentation().setAlpha(0.2);
		for (int i = 0; i < nodesNumber(); i++)
		{
			Point3d tmpP =  new Point3d(getNode(i).getValue().getCoord3d());
			tmpP.setZ(0.0);
			double dist = EspaceHyperbolique.distance(tmpP, tmp);
			if (dist < min_dist && getNode(i).getValue().getRepresentation().isVisible())
			{
				min_dist = dist;
				min_index = i;
			}
		}
		getNode(min_index).getValue().getRepresentation().setVisible(true);
		getNode(min_index).getValue().getRepresentation().setAlpha(1.0);
		Iterator<?> it = adjNodes(getNode(min_index));
		for (; it.hasNext(); )
			((NodeHyperbolique)((Node<?>)it.next()).getValue()).getRepresentation().setVisible(true);
		return min_index;
	}

	/**
	 * 
	 * Sets the current center node (focus) of the hyperbolic graph 
	 * 
	 * @param pick : index of the picked node.
	 */
	public void								setCenterNode(int pick)
	{
		Point4d newFocus = new Point4d(getNode(pick).getValue().getCoord4d());
		Point4d destination = new Point4d();

		destination.set(Espace.ORIGIN4);
		setRoot(pick);
		actualFocus = newFocus;
		actualFocusIndex = pick;
	}

	/**
	 * 
	 * Gets the actual focus (center node) of the graph
	 * 
	 * @return The 4-dimensional coordinates of the focus
	 * 
	 */
	public Point4d							getFocus()
	{
		return actualFocus;
	}

	/**
	 * 
	 * Draw the current hyperbolic graph in a specific GL context.
	 * 
	 */
	public void								draw(GLAutoDrawable d)
	{
		int									i;
		Iterator<?>							iterL;
		GL									gl = d.getGL();

		for (i = 0; i < nodesNumber(); i++)
		{
			Objet aff = getNode(i).getValue().getRepresentation();
			Point3d p = getNode(i).getValue().getCoord3d();
			aff.draw(d);
			for (iterL = adjNodes(getNode(i)); iterL.hasNext(); )
			{
				int nextLink = ((Node<?>)iterL.next()).getIndex();
				NodeHyperbolique nextL = getNode(nextLink).getValue();
				if (nextL.getRepresentation().isVisible())
				{
					gl.glDisable(GL.GL_DEPTH_TEST);
					gl.glEnable(GL.GL_BLEND);
					gl.glBegin(GL.GL_LINES);
					gl.glColor4d(0.5, 1.0, 0.0, 0.0);
					gl.glVertex3d(p.getX(), p.getY(), p.getZ());
					gl.glColor4d(0.5, 1.0, 0.5, 0.5);
					gl.glVertex3d(nextL.getCoord3d().getX(), nextL.getCoord3d().getY(), nextL.getCoord3d().getZ());
					gl.glEnd();
					gl.glEnable(GL.GL_DEPTH_TEST);
					gl.glDisable(GL.GL_BLEND);
				}
			}
		}
	}

	/**
	 * 
	 * Applies a space transformation (translation / rotation / ...) to the whole graph.
	 * 
	 * @param t : The 4x4 matrix transformation to apply
	 * 
	 */
	public void								applyTransform(Matrix4d t)
	{
		int									i;

		for (i = 0; i < nodesNumber(); i++)
		{
			NodeHyperbolique next = getNode(i).getValue();
			t.transform(next.getCoord4d());
			Point4d tmp = next.getCoord4d();
			next.getRepresentation().setCoord4d(tmp);
			next.setCoord3d(new Point3d(tmp.x / tmp.w, tmp.y / tmp.w, tmp.z / tmp.w));
		}
	}

	public void								setCoord4d(Point4d p)
	{

	}

	/**
	 * 
	 * Transforms an hyperbolic to an euclidean distance
	 * 
	 * @param x : the hyperbolic distance
	 * @return the euclidean distance
	 */
	private double 							toEuclideanDistance(double x)
    {
		double 								y = Maths.cosh(x / 2.0);
		return Math.sqrt(1.0 - 1.0 / (y * y));
    }

	/**
	 * 
	 * Starts the projection operation from the hyperbolic space to the unit Klein sphere.
	 * 
	 */
    public void 							projectHyperbolicToEuclidean()
    {
    	int 								root = getRoot();

    	getNode(root).getValue().setCoord4d(Espace.ORIGIN4);
    	getNode(root).getValue().setCoord3d(new Point3d(0.0, 0.0, 0.0));
    	projectHyperbolicToEuclideanHelper(new Matrix4d(Espace.IDENTITY4), root);
    }

    /**
     *
     * Complete projection operation from the hyperbolic space to the unit Klein sphere.
     *
     * @param parentTransform : transformation inherited from the parent
     * @param parent : node id of the parent
     * 
     */
    private void 							projectHyperbolicToEuclideanHelper(Matrix4d parentTransform,
							   				int parent)
    {
    	Iterator<?>							children = adjNodes(getNode(parent));
    	double 								cRadiusE, pRadiusE, cPhi, lastPhi, cPoleE;
    	int									child;

    	if (children.hasNext())
    	{
    		pRadiusE = toEuclideanDistance(getNode(parent).getValue().getCoordPolaire().x);
    		Matrix4d rotPhi = new Matrix4d(Espace.IDENTITY4);
    		lastPhi = 0.0;
    		Matrix4d rot = new Matrix4d(Espace.IDENTITY4);
    		Point3d position;

    		Point4d childCenterAbsolute;
    		Point4d childPoleAbsolute;

    		for (; children.hasNext();)
    		{
    			child = ((Node<?>)children.next()).getIndex();
    			cRadiusE = toEuclideanDistance(getNode(child).getValue().getCoordPolaire().x);
    			cPhi = getNode(child).getValue().getCoordPolaire().y;
    			if (cPhi != lastPhi)
    			{
    				lastPhi = cPhi;
    				rotPhi.rotZ(cPhi);
    			}
    			rot.rotX(getNode(child).getValue().getCoordPolaire().z);
    			rot.mul(rotPhi);

    			childCenterAbsolute = new Point4d(pRadiusE, 0.0, 0.0, 1.0);
    			rot.transform(childCenterAbsolute);

    			cPoleE = toEuclideanDistance(getNode(child).getValue().getCoordPolaire().x
						+ getNode(parent).getValue().getCoordPolaire().x);
    			childPoleAbsolute = new Point4d(cPoleE, 0.0, 0.0, 1.0);
    			rot.transform(childPoleAbsolute);

    			parentTransform.transform(childCenterAbsolute);
    			parentTransform.transform(childPoleAbsolute);
    			Point4d positionH = new Point4d(childCenterAbsolute);
    			getNode(child).getValue().setCoord4d(childCenterAbsolute);
    			if (positionH.w != 0.0)
    			{
    				position = new Point3d(positionH.x / positionH.w,
    						positionH.y / positionH.w,
    						positionH.z / positionH.w);
        			getNode(child).getValue().setCoord3d(position);
    			}
    			else
    			{
    				System.err.println ("Division par zero dans la projection en coordonnees 3D");
    				System.out.println(cRadiusE);
    			}

    			Matrix4d childTransform = calculateCanonicalOrientation(childCenterAbsolute, childPoleAbsolute);

    			projectHyperbolicToEuclideanHelper(childTransform, child);
	    }
	}
    }

    private static Matrix4d 				calculateCanonicalOrientation(Point4d a, Point4d b)
    {
    	Point4d pa = new Point4d(a);
    	Point4d pb = new Point4d(b);
    	Point4d pivot = EspaceHyperbolique.calculatePivot(pa, pb);
    	Matrix4d retval = EspaceHyperbolique.translate(Espace.ORIGIN4, pivot);

    	Matrix4d t1 = EspaceHyperbolique.translate(pivot, Espace.ORIGIN4);
    	t1.transform(pa);
    	t1.transform(pb);

    	retval.mul(EspaceHyperbolique.translate(Espace.ORIGIN4, pa));

    	Matrix4d t2 = EspaceHyperbolique.translate(pa, EspaceHyperbolique.ORIGIN4);
    	t2.transform(pa);
    	t2.transform(pb);

    	pb.project(pb);
    	double rho = Maths.vectorLength(pb);
    	double phi = Math.acos(pb.x / rho);
    	double theta = Math.atan2(pb.z, pb.y);

    	if (!( phi < Maths.EPSILON))
    	{
    		Matrix4d rotT = new Matrix4d(Espace.IDENTITY4);
    		Matrix4d rotP = new Matrix4d(Espace.IDENTITY4);
    		rotT.rotX(theta);
    		rotP.rotZ(phi);
    	    retval.mul(rotT);
    	    retval.mul(rotP);
    	}
    	else
	    {
	    }

    	return retval;
   }

	public void 					setRotation(double x, double y, double z)
	{
		for (int i = 0; i < nodesNumber(); i++)
			getNode(i).getValue().getRepresentation().setRotation(x, y, z);
	}

	public void 					setCoordPolaire(Point3d p)
	{

	}

	public void 					setAlpha(double alpha)
	{

	}

	public boolean 					isDead()
	{
		return false;
	}

	public boolean 					isVisible()
	{
		return visible;
	}

	public void 					setVisible(boolean v)
	{
		visible = v;
	}
}
