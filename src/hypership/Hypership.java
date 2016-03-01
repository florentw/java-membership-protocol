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

import hyperbolize.animation.AnimatedCube;
import hyperbolize.animation.AnimatedTranslation;
import hyperbolize.espace.Espace;
import hyperbolize.espace.EspaceHyperbolique;
import hyperbolize.objet.Cube;
import hyperbolize.rendu.Rendu;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.opengl.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.vecmath.Point3d;
import javax.vecmath.Point4d;

import membership.MSMain;
import membership.MSMember;
import membership.MSView;
import membership.MSViewLocal;

import com.sun.opengl.util.Animator;
import com.vlsolutions.swing.docking.DockingConstants;
import com.vlsolutions.swing.docking.DockingDesktop;
import com.vlsolutions.swing.docking.DockingPreferences;

/**
 *
 * This is the main frame class for the GUI which will contain all widgets.
 *
 *
 */
public class 							Hypership extends JFrame implements GLEventListener, MouseListener, MouseMotionListener
{
	private static final long 			serialVersionUID = 1L;

	private final Logger 				LOG = Logger.getLogger(Hypership.class.getName());
	private static Animator 			animator = null;
	private static Rendu 				rendu;
	private static GrapheMembership 	graphe;
	private static GLCanvas 			canvas;
	private int							prevMouseX;
	private int							prevMouseY;
	private int							lastPicked;
	private boolean						mouseRDown;
	private boolean						mouseLDown;
	private MSMain						membership;
 	private	GUIFileExplorer 			fileExplorePanel;
 	private GUIInfos 					infoPanel;
 	private GUIRps						rpsPanel;
 	private GUIOpenGL 					gl;
 	private GUIToolbar 					toolBar;
 	private GUIMenu						menuBar;
 	private DockingDesktop 				desk;
 	private GUIProcessManager			processManager;
 	private MSMember					pickedMember;

 	/**
 	 *
 	 * Main constructor for the Hypership GUI
 	 * Initialize all the GUI widgets.
 	 *
 	 * @param s : the title of the frame
 	 * @param glCanv : the contained OpenGL canvas
 	 *
 	 */
	public 								Hypership(String s, GLCanvas glCanv)
	{
		super(s);
		desk = new DockingDesktop();
		graphe = new GrapheMembership();
		rendu = new Rendu();
		init_graphe();
		lastPicked = 0;
		rendu.addObjet(graphe);
		membership = new MSMain(this);
		processManager = new GUIProcessManager();
		pickedMember = new MSMember("", "", 0);
		fileExplorePanel = new GUIFileExplorer();
		infoPanel = new GUIInfos(this);
		gl = new GUIOpenGL(glCanv, this);
		rpsPanel = new GUIRps();
		rpsPanel.sendInfo("Welcome to the membership.\n");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	    toolBar = new GUIToolbar(desk, getContentPane(), this);
	    desk.addDockable(gl);
	    desk.split(gl, fileExplorePanel, DockingConstants.SPLIT_LEFT);
	    desk.split(gl, infoPanel, DockingConstants.SPLIT_RIGHT);
	    desk.split(infoPanel, rpsPanel, DockingConstants.SPLIT_BOTTOM);
	    desk.setDockableWidth(fileExplorePanel, 0.2);
	    desk.setDockableWidth(infoPanel, 0.2);
	    menuBar = new GUIMenu(toolBar);
	    this.setJMenuBar(menuBar);
	}

	/**
	 *
	 * Connects to the membership service
	 *
	 */
	public void							initMembership()
	{
		rpsPanel.sendInfo("Starting DPServer...\n");
		membership.launch();
		rpsPanel.sendInfo("Connecting...\n");
	}

	/**
	 *
	 * Stops to the membership service
	 *
	 */
	public void							stopMembership()
	{
		membership.stop();
		rpsPanel.sendInfo("Disconnected...\n");
	}

	/**
	 *
	 * Raise a pop up window
	 *
	 */
	public void							launchPopUp(String desc, JPanel pane)
	{
        new GUIPopUp(desc, pane);
	}

	/**
	 *
	 * Raise a warning pop up
	 *
	 * @param msg : message inside the pop up
	 * @param poptitle : title of the pop up
	 *
	 */
	public void							popupWarn(String msg, String poptitle)
	{
		JOptionPane.showMessageDialog(this, msg, poptitle, JOptionPane.WARNING_MESSAGE);
	}

	/**
	 *
	 * Raise an error pop up
	 *
	 * @param msg : message inside the pop up
	 * @param poptitle : title of the pop up
	 *
	 */
	public void							popupError(String msg, String poptitle)
	{
		JOptionPane.showMessageDialog(this, msg, poptitle, JOptionPane.ERROR_MESSAGE);
	}

	/**
	 *
	 * Raise an information pop up
	 *
	 * @param msg : message inside the pop up
	 * @param poptitle : title of the pop up
	 *
	 */
	public void							popupInfo(String msg, String poptitle)
	{
		JOptionPane.showMessageDialog(this, msg, poptitle, JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 *
	 * Updates the current view of the membership on the OpenGL canvas
	 *
	 * @param view : the new membership view
	 *
	 */
	public void							updateView(MSView view)
	{
		synchronized (this)
		{
			if (graphe.getFocus() != null)
                rendu.applyTransform(EspaceHyperbolique.translate(Espace.ORIGIN4, graphe.getNode(lastPicked).getValue().getCoord4d()));
            graphe.resetCoords();
            graphe.updateMembershipView(membership.getMemberID(), view);
            graphe.setCenterNode(0);
			lastPicked = 0;
			canvas.repaint();
		}
	}

	/**
	 *
	 * Filled graph initialization
	 *
	 */
	private void 						init_graphe()
	{
		MSView							view;

		view = new MSView(0, new MSMember("yo", "moi", 0));
		for (int i = 0; i < 10; i++)
		{
			MSViewLocal tmp = new MSViewLocal(i);
			for (int j = 0; j < (Math.random() * 100); j++)
				tmp.addMember(new MSMember("yo" + Math.random() + " " + j, "moi." + Math.random()*100000, j));
			view.addGlobalMember(tmp);
		}
		graphe.updateMembershipView(new MSMember("Phony", "Phony", 0), view);
		graphe.setCenterNode(0);
	}

	/**
	 * @param args
	 */
	public static void 					main(String[] args)
	{
	    DockingPreferences.initHeavyWeightUsage();
	    DockingPreferences.setSingleHeavyWeightComponent(true);
		GLCapabilities capabs = new GLCapabilities();
		capabs.setHardwareAccelerated(true);
		capabs.setDoubleBuffered(true);
		capabs.setSampleBuffers(true);
		canvas = new GLCanvas(capabs);
		canvas.setMinimumSize(new Dimension(800,600));
		canvas.setMaximumSize(new Dimension(800,600));
		Hypership h = new Hypership("Hyperbolizer", canvas);
	    h.setSize(1280, 800);
		canvas.addGLEventListener(h);
		canvas.addMouseListener(h);
		canvas.addMouseMotionListener(h);
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
	    //canvas.requestFocus();
	}

	public GUIProcessManager			getprocessManager(){
		return processManager;
	}

	public GrapheMembership 			getgraphe()
	{
		return graphe;
	}

	public int							getlastPicked()
	{
		return lastPicked;
	}

	public MSMember						getPickedMember()
	{
		return pickedMember;
	}

	public void 						setLastPicked(int value)
	{
		lastPicked = value;
	}

	public GUIFileExplorer 				getfileExplorePanel()
	{
		return fileExplorePanel;
	}

	public GUIInfos 					getInfoPanel()
	{
		return infoPanel;
	}

	public GUIRps						getRpsPanel()
	{
		return rpsPanel;
	}

	public void 						display(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		synchronized (this)
		{
			rendu.draw(drawable);
		}
	}

	public void 						displayChanged(GLAutoDrawable drawable, boolean arg1, boolean arg2)
	{
	}

	public void 						init(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		if (LOG.isLoggable(Level.FINE))
			LOG.fine("Init GL is " + gl.getClass().getName());
		gl.glEnable(GL.GL_DEPTH_TEST);
		//canvas.setGL(gl);
	}

	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4)
	{
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

	public void	droppedFile(String filename, double x, double y)
	{
		double w = canvas.getWidth();
		double h = canvas.getHeight();
		double x2 = (x - (w / 2)) / (w / 2);
		double y2 =  -(y - (h / 2)) / (h / 2);
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

		pickedMember = ((NodeMembership)graphe.getNode(picked).getValue()).getMember();
		infoPanel.refresh(pickedMember);

		new GUIPopUpRexec(this);
	}

	public void	draggedFile(String filename, double x, double y)
	{
		double w = canvas.getWidth();
		double h = canvas.getHeight();
		double x2 = (x - (w / 2)) / (w / 2);
		double y2 =  -(y - (h / 2)) / (h / 2);
		int picked = graphe.getPickingNode(x2, y2, 0.0);
		if (lastPicked != picked)
		{
			graphe.getNode(lastPicked).getValue().getRepresentation().setAlpha(0.2);
			graphe.getNode(picked).getValue().getRepresentation().setAlpha(50.0);
		}
		lastPicked = picked;
		canvas.repaint();
	}

	public void mousePressed(MouseEvent e)
	{
		prevMouseX = e.getX();
		prevMouseY = e.getY();
		double w = canvas.getWidth();
		double h = canvas.getHeight();
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			double x2 = (prevMouseX - (w / 2)) / (w / 2);
			double y2 =  -(prevMouseY - (h / 2)) / (h / 2);
			int picked = graphe.getPickingNode(x2, y2, 0.0);
			lastPicked = picked;
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

			pickedMember = ((NodeMembership)graphe.getNode(picked).getValue()).getMember();
			infoPanel.refresh(pickedMember);
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

	public GUIToolbar		getToolbar()
	{
		return toolBar;
	}
}