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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * 
 * This class is the upper menu bar widget.
 * 
 * 
 */
public class 					GUIMenu extends JMenuBar
{	
	private static final long 	serialVersionUID = 3925140484409702116L;
	JMenu 						menuFile;
	JMenu						workSample;
	JMenu 						menuMS;
	JMenu 						menuRP;
	JMenu						menuRps;
	JMenu						dedicasse;
	JMenuItem 					loadWork;
	JMenuItem 					saveWork;
	JMenuItem					sample1;
	JMenuItem					sample2;
	JMenuItem					sample3;
	JMenuItem					quit;
	JMenuItem 					connect;
	JMenuItem 					disconnect;
	JMenuItem 					rexec;
	JMenuItem 					rkill;
	JMenuItem 					rexit;
	JMenuItem					rps;
	JMenuItem					about;
	GUIToolbar					toolBar;

	/**
	 * 
	 * Main constructor for the GUIMenu class
	 * 
	 * @param tBar : the associated toolbar
	 * 
	 */
	public 						GUIMenu(GUIToolbar tBar)
	{
		super();
		toolBar = tBar;
		//Menu File
		menuFile = new JMenu("File");
		menuFile.setMnemonic(KeyEvent.VK_F);
		menuFile.getAccessibleContext().setAccessibleDescription("Manage application.");
		
		loadWork = new JMenuItem("Load workspace", new ImageIcon("icons/menu_work_load.png"));
		loadWork.getAccessibleContext().setAccessibleDescription("Load a workspace.");
		loadWork.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.ALT_MASK));
		menuFile.add(loadWork);
		
		saveWork = new JMenuItem("Save workspace", new ImageIcon("icons/menu_work_save.png"));
		saveWork.getAccessibleContext().setAccessibleDescription("Save current workspace.");
		saveWork.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
		menuFile.add(saveWork);
		
		workSample = new JMenu("Workspace samples");
		workSample.setMnemonic(KeyEvent.VK_W);

		sample1 = new JMenuItem("Vectorized");
		sample1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		workSample.add(sample1);
		
		sample2 = new JMenuItem("Hyperbooo");
		sample2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.ALT_MASK));
		workSample.add(sample2);
		
		sample3 = new JMenuItem("Sample 3");
		sample3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, ActionEvent.ALT_MASK));
		workSample.add(sample3);

		menuFile.add(workSample);
		
		menuFile.addSeparator();
		quit = new JMenuItem("Quit", new ImageIcon("icons/menu_quit.png"));
		quit.getAccessibleContext().setAccessibleDescription("Quit the application.");
		quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.ALT_MASK));
		menuFile.add(quit);
		this.add(menuFile);
		
		//Menu Membership
		menuMS = new JMenu("Membership");
		menuMS.setMnemonic(KeyEvent.VK_M);
		menuMS.getAccessibleContext().setAccessibleDescription("Access the membership.");
		this.add(menuMS);
		
		connect = new JMenuItem("Connect", new ImageIcon("icons/menu_connect.png"));
		connect.getAccessibleContext().setAccessibleDescription("Connect to the membership.");
		connect.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
		menuMS.add(connect);
		
		disconnect = new JMenuItem("Disconnect", new ImageIcon("icons/menu_disconnect.png"));
		disconnect.getAccessibleContext().setAccessibleDescription("Disconnect from the membership.");
		disconnect.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.ALT_MASK));
		menuMS.add(disconnect);
		
		//Menu Remote Process
		menuRP = new JMenu("DProcess");
		menuRP.setMnemonic(KeyEvent.VK_D);
		menuRP.getAccessibleContext().setAccessibleDescription("Control Process.");
		this.add(menuRP);
		
		rexec = new JMenuItem("Rexec", new ImageIcon("icons/menu_rexec.png"));
		rexec.getAccessibleContext().setAccessibleDescription("Execute a process on a remote machine.");
		rexec.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.ALT_MASK));
		menuRP.add(rexec);
		
		rkill = new JMenuItem("Rkill", new ImageIcon("icons/menu_rkill.png"));
		rkill.getAccessibleContext().setAccessibleDescription("Send a signal to a remote process.");
		rkill.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.ALT_MASK));
		menuRP.add(rkill);
		
		rexit = new JMenuItem("Rexit", new ImageIcon("icons/menu_rexit.png"));
		rexit.getAccessibleContext().setAccessibleDescription("End a remote process.");
		rexit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.ALT_MASK));
		menuRP.add(rexit);
		
		//Menu RPS
		menuRps = new JMenu("RPS");
		menuRps.setMnemonic(KeyEvent.VK_R);
		menuRps.getAccessibleContext().setAccessibleDescription("Displays distributed processes.");
		this.add(menuRps);
		
		rps = new JMenuItem("List distributed processes", new ImageIcon("icons/menu_rps.png"));
		rps.getAccessibleContext().setAccessibleDescription("Displays distributed processes.");
		rps.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.ALT_MASK));
		menuRps.add(rps);
		
		//Menu dedicasse
		dedicasse = new JMenu("?");
		dedicasse.getAccessibleContext().setAccessibleDescription("About.");
		this.add(dedicasse);
		
		about = new JMenuItem("About", KeyEvent.VK_A);
		about.getAccessibleContext().setAccessibleDescription("About.");
		dedicasse.add(about);
		
		//All listeners for menu selection
		loadWork.addActionListener(new ActionListener(){
	        public  void    actionPerformed(ActionEvent e)
	        {
	            toolBar.loadWorkspace();
	        }
	    });
		
		saveWork.addActionListener(new ActionListener(){
	        public  void    actionPerformed(ActionEvent e)
	        {
	            toolBar.saveWorkspace();
	        }
	    });
		
		sample1.addActionListener(new ActionListener(){
	        public  void    actionPerformed(ActionEvent e)
	        {
	           	toolBar.loadWorkspace("./msw/Vectorized.msw");
	        }
	    });
		
		sample2.addActionListener(new ActionListener(){
	        public  void    actionPerformed(ActionEvent e)
	        {
	           	toolBar.loadWorkspace("./msw/Hyperbolover.msw");
	        }
	    });
		
		sample3.addActionListener(new ActionListener(){
	        public  void    actionPerformed(ActionEvent e)
	        {
	            System.out.println("Load workspace sample 3!");
	        }
	    });
		
		quit.addActionListener(new ActionListener(){
	        public  void    actionPerformed(ActionEvent e)
	        {
	            toolBar.exitApp();
	        }
	    });
		
		connect.addActionListener(new ActionListener(){
	        public  void    actionPerformed(ActionEvent e)
	        {
	            toolBar.connect();
	        }
	    });
		
		disconnect.addActionListener(new ActionListener(){
	        public  void    actionPerformed(ActionEvent e)
	        {
	            toolBar.disconnect();
	        }
	    });
		
		rexec.addActionListener(new ActionListener(){
	        public  void    actionPerformed(ActionEvent e)
	        {
	        	toolBar.rexec();
	        }
	    });
		
		rkill.addActionListener(new ActionListener(){
	        public  void    actionPerformed(ActionEvent e)
	        {
	        	toolBar.rkill();
	        }
	    });
		
		rexit.addActionListener(new ActionListener(){
	        public  void    actionPerformed(ActionEvent e)
	        {
	        	toolBar.rexit();
	        }
	    });
		
		rps.addActionListener(new ActionListener(){
	        public  void    actionPerformed(ActionEvent e)
	        {
	            toolBar.rps();
	        }
	    });
		
		about.addActionListener(new ActionListener(){
	        public  void    actionPerformed(ActionEvent e)
	        {
	            System.out.println("About!");
	        }
	    });
	}
}
