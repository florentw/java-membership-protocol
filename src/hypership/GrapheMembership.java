/*
	jmmp - a java implementation of a multicast membership protocol

	Copyright (C) 2008 Philippe Esling, Julien Clement and Florent Weber

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

package hypership;

import java.util.Iterator;
import java.util.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.vecmath.*;

import membership.MSMember;
import membership.MSView;
import membership.MSViewLocal;

import hyperbolize.animation.Animation;
import hyperbolize.espace.Espace;
import hyperbolize.graphe.GrapheHyperbolique;
import hyperbolize.graphe.Node;
import hyperbolize.graphe.NodeHyperbolique;
import hyperbolize.objet.Cube;
import hyperbolize.objet.Objet;

/**
*
* This class is the connection required for implementing a membership hyperbolic graph representation
*
* @version 									1.5
* @see 										GrapheHyperbolique
*
*/
public class 								GrapheMembership extends GrapheHyperbolique
{

	/**
	 *
	 * Main constructor for the membership graph
	 *
	 */
	public 									GrapheMembership()
	{
		super();
	}

	/**
	 *
	 * To add an hyperbolic node with a specific representation and member in the graph
	 *
	 * @param label : Label of the node
	 * @param o : Representation of the node
	 * @return A reference to the created node
	 *
	 */
	public Node<NodeHyperbolique>			addNode(String label, Objet o, MSMember m)
	{
		return super.addNode(new NodeMembership(label, o, m));
	}

	/**
	 *
	 * Updates the hyperbolic graph from a membership view
	 *
	 * @param v : the new view to represent
	 *
	 */
	public void								updateMembershipView(MSMember member, MSView v)
	{
		Vector<MSViewLocal>					globalView;
		Iterator<MSViewLocal>				globalIter;
		Iterator<MSMember>					localIter;
		MSViewLocal							localView;
		int									curParent;
		int									curNode;
		Matrix4d 							rot;

		clear();
		addNode("Phony Root", new Cube(0.2), new MSMember("Phony", "Phony", 0));
		setRoot(0);
		globalView = v.getGlobalView();
		globalIter = globalView.iterator();
		for (curParent = 1, curNode = 1; globalIter.hasNext(); curParent = curNode)
		{
			localView = globalIter.next();
			localIter = localView.getMembersList().iterator();
			if (!localIter.hasNext())
				continue;
			addNode("Leader - Group : " + localView.getGroupID(), new Cube(0.12, new Point3d(0.23, 0.6, 0.7), 0.2), localIter.next());
			if (((NodeMembership)getNode(curParent).getValue()).getMember().getID() == member.getID())
				getNode(curParent).getValue().setRepresentation(new Cube(0.2, new Point3d(0.0, 1.0, 0.0), 0.5));
			putEdge(0, curParent);
			for (curNode = curParent + 1; localIter.hasNext(); curNode++)
			{
				addNode("Member - Group : " + localView.getGroupID(), new Cube(0.1, new Point3d(1.0, 0.0, 0.0), 0.2), localIter.next());
				if (((NodeMembership)getNode(curNode).getValue()).getMember().getID() == member.getID())
					getNode(curNode).getValue().setRepresentation(new Cube(0.2, new Point3d(0.0, 1.0, 0.0), 0.5));
				putEdge(curParent, curNode);
			}
		}
		prepareGraphe();
		projectHyperbolicToEuclidean();
		getNode(0).getValue().getRepresentation().setVisible(false);
		rot = new Matrix4d(Espace.IDENTITY4);
		rot.rotY(90);
		applyTransform(rot);
	}

    public void                                resetCoords()
    {
        for (int i = 0; i < nodesNumber(); i++)
        {
            getNode(i).getValue().setCoord4d(new Point4d(0.0, 0.0, 0.0, 0.0));
            getNode(i).getValue().setCoord3d(new Point3d(0.0, 0.0, 0.0));
            getNode(i).getValue().setCoordPolaire(new Point3d(0.0, 0.0, 0.0));
        }
        actualFocusIndex = 0;
        setRoot(0);
    }

	/**
	 *
	 * Sets the actual center node (placed at the 4-dimensional origin) of the graph
	 *
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
	 * Main drawing function, called by the OpenGL rendering loop
	 * This function handles all the graph related drawing.
	 *
	 */
	public void								draw(GLAutoDrawable d)
	{
		int									i, j;
		Iterator<?>							iterL;
		Point3d								p, previous;
		Point3d								ol_previous;
		Point4d								sC, mC, eC;
		NodeHyperbolique 					nextL;
		Objet 								aff;
		GL									gl = d.getGL();

		for (i = 0; i < nodesNumber(); i++)
		{
			aff = getNode(i).getValue().getRepresentation();
			p = getNode(i).getValue().getCoord3d();
			aff.draw(d);
			previous = p;
			ol_previous = p;
			for (j = 0, iterL = adjNodes(getNode(i)); iterL.hasNext(); j++, ol_previous = previous, previous = nextL.getCoord3d())
			{
				int nextLink = ((Node<?>)iterL.next()).getIndex();
				nextL = getNode(nextLink).getValue();
				if (i == 0)
					gl.glLineWidth(4.0f);
				else
					gl.glLineWidth(1.0f);
				if (nextL.getRepresentation().isVisible())
				{
					gl.glDisable(GL.GL_DEPTH_TEST);
					gl.glEnable(GL.GL_BLEND);
					if (i == 0)
					{
						sC = new Point4d(0.0, 0.5, 1.0, 0.01);
						mC = new Point4d(0.9, 0.9, 1.0, 3.0);
						eC = sC;
						drawNiceLine(gl, p, nextL.getCoord3d(), 10, sC, mC, eC);
					}
					else
					{
						sC = new Point4d(0.5, 1.0, 0.0, 0.01);
						mC = new Point4d(0.5, 1.0, 0.5, 2.0);
						eC = sC;
						drawNiceArc(gl, ol_previous, previous, nextL.getCoord3d(), 10, sC, mC, eC);
					}
					gl.glEnable(GL.GL_DEPTH_TEST);
					gl.glDisable(GL.GL_BLEND);
				}
			}
			gl.glLineWidth(1.0f);
		}
	}

	/**
	 *
	 * Function for drawing a bezier curve between different member of a same local ring
	 *
	 * @param gl : the openGL context
	 * @param a : starting point
	 * @param b : middle point
	 * @param c : ending point
	 * @param segments : number of segments in the curve
	 * @param start_c : starting color
	 * @param mid_c : middle color
	 * @param end_c : ending color
	 *
	 */
	public void							drawNiceArc(GL gl, Point3d a, Point3d b, Point3d c, int segments,
										Point4d start_c, Point4d mid_c, Point4d end_c)
	{
		    Point4d						curColor;
		    Point3d						curPoint;
		    double						time;
		    int							i;

		    gl.glBegin(GL.GL_LINE_STRIP);
		    for(i = 0; i <= segments; i++)
		    {
		    	time = (double)i / (double)segments;
		    	curColor = Animation.interpolationCubique(start_c, mid_c, end_c, time);
		    	curPoint = Animation.interpolationCubique(a, b, c, time);
		    	gl.glColor4d(curColor.x, curColor.y, curColor.z, curColor.w);
		        gl.glVertex3d(curPoint.x, curPoint.y, curPoint.z);

		    }
		    gl.glEnd();
		    gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	/**
	 *
	 * Function for drawing a nice colored line between 2 points
	 *
	 * @param gl : the openGL context
	 * @param a : starting point
	 * @param b : ending point
	 * @param segments : number of segments in the line
	 * @param start_c : starting color
	 * @param mid_c : middle color
	 * @param end_c : ending color
	 *
	 */
	public void							drawNiceLine(GL gl, Point3d a, Point3d b, int segments,
										Point4d start_c, Point4d mid_c, Point4d end_c)
	{
		Point4d							curColor;
		Point3d							curPoint;
	    double							time;
	    int								i;

		gl.glBegin(GL.GL_LINE_STRIP);
		for(i = 0; i <= segments; i++)
		{
			time = (double)i / (double)segments;
			curColor = Animation.interpolationCubique(start_c, mid_c, end_c, time);
			curPoint = Animation.interpolation(a, b, time);
			gl.glColor4d(curColor.x, curColor.y, curColor.z, curColor.w);
			gl.glVertex3d(curPoint.x, curPoint.y, curPoint.z);
		}
		gl.glEnd();
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}
}
