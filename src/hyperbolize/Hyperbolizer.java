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

package hyperbolize;

import hyperbolize.animation.AnimatedCube;
import hyperbolize.animation.AnimatedTranslation;
import hyperbolize.espace.Espace;
import hyperbolize.graphe.GrapheHyperbolique;
import hyperbolize.objet.Cube;
import hyperbolize.rendu.Rendu;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.opengl.*;
import javax.swing.JFrame;
import javax.vecmath.Point3d;
import javax.vecmath.Point4d;

import com.sun.opengl.util.Animator;

public class 				Hyperbolizer extends JFrame implements GLEventListener, MouseListener, MouseMotionListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final 			Logger LOG = Logger.getLogger(Hyperbolizer.class.getName());
	private static 			Animator animator = null;
	private static 			Rendu rendu;
	private static 			GrapheHyperbolique graphe;
	private static			GLCanvas canvas;
	private int				prevMouseX;
	private int				prevMouseY;
	private boolean			mouseRDown;
	private boolean			mouseLDown;
	
	public 					Hyperbolizer(String s)
	{
		super(s);
		graphe = new GrapheHyperbolique();
		rendu = new Rendu();
		init_graphe();
		rendu.addObjet(graphe);
	}
	
	private void init_graphe()
	{
		graphe.addNode("Root", new Cube(0.2));
		for (int i = 1; i < 10; i++)
			graphe.addNode("Noeud n�" + String.valueOf(i), new Cube(0.1));
		for (int i = 1; i < 10; i++)
			graphe.putEdge(0, i);
		for (int i = 10; i < 5000; i++)
		{
			graphe.addNode("Noeud n�" + String.valueOf(i), new Cube(0.1));
			graphe.putEdge(i / 10, i);
		}
		graphe.setRoot(0);
		graphe.prepareGraphe();
		graphe.projectHyperbolicToEuclidean();
		graphe.getNode(0).getValue().getRepresentation().setVisible(true);
		graphe.getPickingNode(0.0, 0.0, 0.0);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		Hyperbolizer h = new Hyperbolizer("Hyperbolizer");
	    canvas = new GLCanvas();
	    canvas.addGLEventListener(h);
	    canvas.addMouseListener(h);
	    canvas.addMouseMotionListener(h);
	    h.add(canvas);
	    h.setSize(1280, 1024);
	    animator = new Animator(canvas);
	    h.addWindowListener(new WindowAdapter()
	    {
	      public void windowClosing(WindowEvent e)
	      {
	        animator.stop();
	        System.exit(0);
	      }
	    });
	    h.setVisible(true);
	    animator.start();
	    canvas.requestFocus();
	}
	
	public void display(GLAutoDrawable drawable) 
	{
		GL gl = drawable.getGL();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		rendu.draw(drawable);
	}
	public void displayChanged(GLAutoDrawable drawable, boolean arg1, boolean arg2) {
		// TODO Auto-generated method stub
		
	}
	public void init(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		if (LOG.isLoggable(Level.FINE))
			LOG.fine("Init GL is " + gl.getClass().getName());	
		gl.glEnable(GL.GL_DEPTH_TEST);
		//gl.glDepthRange(100.0, -100.0);
	}
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
	}

	
	public void mouseClicked(MouseEvent e) {

	}
	
	public void mouseDragged(MouseEvent e)
	{
		int x = e.getX();
		int y = e.getY();
		float diffX = (float)(x - prevMouseX);
		float diffY = (float)(y - prevMouseY);		
		if (mouseRDown)
		{
			rendu.rotateY(diffX / 100);
			rendu.rotateX(diffY / 100);
		}
		if (mouseLDown)
		{
			rendu.translate(Espace.ORIGIN4, new Point4d((x - 620)*0.002, 0.0, 0.0, 1.0));
		}
		prevMouseX = x;
		prevMouseY = y;
	}
	
	public void mouseMoved( MouseEvent e ) {

	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e) {
		prevMouseX = e.getX();
		prevMouseY = e.getY();	
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			double x2 = (prevMouseX - 620.0) / 620.0;
			double y2 =  -(prevMouseY - 512.0) / 512.0;
			int picked = graphe.getPickingNode(x2, y2, 0.0);
			rendu.addObjet(new AnimatedTranslation(500.0, graphe.getFocus(), graphe.getNode(picked).getValue().getCoord4d(), rendu));
			graphe.setCenterNode(picked);
			Cube staCube = (Cube)graphe.getNode(picked).getValue().getRepresentation();
			Cube endCube = new Cube(staCube);
			endCube.setColor(new Point3d(0.2,0.8,0.2));
			endCube.setRotation(staCube.getRotX(), staCube.getRotY(), staCube.getRotZ());
			endCube.setAlpha(0.0);
			endCube.setSize(1.0);
			rendu.addObjet(new AnimatedCube(500.0, (Cube)graphe.getNode(picked).getValue().getRepresentation(), endCube));
			staCube.setAlpha(0.5);
			canvas.repaint();
		}
		if (e.getButton() == MouseEvent.BUTTON3)
			mouseRDown = true;
	}

	public void mouseReleased(MouseEvent e) {	
		if (e.getButton() == MouseEvent.BUTTON1)
			mouseLDown = false;
		if (e.getButton() == MouseEvent.BUTTON3)
			mouseRDown = false;
	}

}