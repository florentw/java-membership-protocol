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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.vlsolutions.swing.docking.DockingDesktop;
import com.vlsolutions.swing.toolbars.ToolBarConstraints;
import com.vlsolutions.swing.toolbars.ToolBarContainer;
import com.vlsolutions.swing.toolbars.ToolBarPanel;
import com.vlsolutions.swing.toolbars.VLToolBar;

/**
 *
 * This class is the main definition of the upper tool bar.
 * It contains icon representations of the functionalities.
 *
 *
 */
public class 				GUIToolbar
{
	DockingDesktop 			desktop;
	ToolBarContainer 		container;
	Hypership				hyper;
	VLToolBar 				toolBarMS;
	VLToolBar				toolBarRP;
	VLToolBar				toolBarRPS;
	VLToolBar				toolBarWork;
	VLToolBar				toolBarExit;
	JButton					connectButton;
	JButton					stopButton;
	JButton					rexecButton;
	JButton					rkillButton;
	JButton					rexitButton;
	JButton					rpsButton;
	JButton					saveWButton;
	JButton					loadWButton;
	JButton					exitButton;

	/**
	 *
	 * Main constructor for the GUIToolbar class
	 *
	 * @param desk : the docking context
	 * @param frame : the containing frame
	 * @param h : the instance of the hypership
	 *
	 */
	public 					GUIToolbar(Component desk, Container frame, Hypership h)
	{
		hyper = h;
		desktop = (DockingDesktop)desk;
		container = ToolBarContainer.createDefaultContainer(true, true, true, true);
		container.add(desktop, BorderLayout.CENTER);
	    frame.add(container, BorderLayout.CENTER);
		ToolBarPanel topPanel = container.getToolBarPanelAt(BorderLayout.NORTH);
		toolBarMS = new VLToolBar("Membership");
		toolBarRP = new VLToolBar("Remote process control");
		toolBarRPS = new VLToolBar("Remote process listing");
		toolBarWork = new VLToolBar("Workspace handling");
		toolBarExit = new VLToolBar("Exit");

		initToolBarMS();
		initToolBarRP();
		initToolBarRPS();
		initToolBarWork();
		initToolBarExit();
		topPanel.add(toolBarMS , new ToolBarConstraints(0,0));
		topPanel.add(toolBarRP , new ToolBarConstraints(0,1));
		topPanel.add(toolBarRPS , new ToolBarConstraints(0,2));
		topPanel.add(toolBarWork , new ToolBarConstraints(0,3));
		topPanel.add(toolBarExit , new ToolBarConstraints(0,4));
	}

	/**
	 *
	 * Initialize membership related tool bar
	 *
	 */
	private void 			initToolBarMS()
	{
		connectButton = new JButton("", new ImageIcon("icons/connect.png"));
		connectButton.setToolTipText("Connect to the membership.");
		stopButton = new JButton("", new ImageIcon("icons/disconnect.png"));
		stopButton.setToolTipText("Disconnect to the membership.");
		connectButton.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e){ connect(); }});
		stopButton.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e) { disconnect(); }});
		connectButton.setEnabled(true);
		stopButton.setEnabled(false);
		toolBarMS.add(connectButton);
	    toolBarMS.add(stopButton);
	}

	/**
	 *
	 * Initialize remote process related tool bar
	 *
	 */
	private void 			initToolBarRP()
	{
		rexecButton = new JButton("", new ImageIcon("icons/rexec.png"));
		rexecButton.setToolTipText("Execute a remote process.");
		rkillButton = new JButton("", new ImageIcon("icons/rkill2.png"));
		rkillButton.setToolTipText("Send a signal to a remote process.");
		rexitButton = new JButton("", new ImageIcon("icons/rexit.png"));
		rexitButton.setToolTipText("Exit a remote process.");
		rexecButton.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e) { rexec(); }});
		rkillButton.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e) { rkill(); }});
		rexitButton.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e) { rexit(); }});
		rexecButton.setEnabled(false);
		rkillButton.setEnabled(false);
		rexitButton.setEnabled(false);
		toolBarRP.add(rexecButton);
		toolBarRP.add(rkillButton);
		toolBarRP.add(rexitButton);
	}

	/**
	 *
	 * Initialize RPS related tool bar
	 *
	 */
	private void 			initToolBarRPS()
	{
		rpsButton = new JButton("", new ImageIcon("icons/rps.png"));
		rpsButton.setToolTipText("List all processes.");
		rpsButton.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e) { rps(); }});
		rpsButton.setEnabled(false);
		toolBarRPS.add(rpsButton);
	}

	/**
	 *
	 * Initialize workspace related tool bar
	 *
	 */
	private void 			initToolBarWork()
	{
		saveWButton = new JButton("", new ImageIcon("icons/work_save.png"));
		saveWButton.setToolTipText("Save your workspace configuration.");
		loadWButton = new JButton("", new ImageIcon("icons/work_load.png"));
		loadWButton.setToolTipText("Load a workspace configuration.");
		saveWButton.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e){ saveWorkspace(); }});
		loadWButton.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e) { loadWorkspace(); }});
		toolBarWork.add(saveWButton);
	    toolBarWork.add(loadWButton);
	}

	/**
	 *
	 * Initialize exit related tool bar
	 *
	 */
	private void 			initToolBarExit()
	{
		exitButton = new JButton("", new ImageIcon("icons/rkill.png"));
		exitButton.setToolTipText("Shutdown application.");
		exitButton.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e) { exitApp(); }});
		toolBarExit.add(exitButton);
	}

	/**
	 *
	 * Launch the connection to the membership
	 *
	 */
	public void				connect()
	{
		connectButton.setEnabled(false);
		stopButton.setEnabled(true);
		rexecButton.setEnabled(true);
		rkillButton.setEnabled(true);
		rexitButton.setEnabled(true);
		rpsButton.setEnabled(true);
		hyper.initMembership();
	}

	/**
	 *
	 * Stop the connection to the membership
	 *
	 */
	public void				disconnect()
	{
		connectButton.setEnabled(true);
		stopButton.setEnabled(false);
		rexecButton.setEnabled(false);
		rkillButton.setEnabled(false);
		rexitButton.setEnabled(false);
		rpsButton.setEnabled(false);
		hyper.stopMembership();
	}

	public void 			rexec()
	{
        System.out.println("Rexec !");
        //GUIProcessManager pm = hyper.getprocessManager();

        //JPanel content = createRexecPopUpContent();
        //hyper.launchPopUp("Rexec", content);
        new GUIPopUpRexec(hyper);
        //hyper.popupError("Process id is :" + pm.rexec(member, path), "Remote execution");
	}



	public void 			rkill()
	{
        System.out.println("Rkill !");
        new GUIPopUpRkill(hyper);
	}

	public void 			rexit()
	{
        System.out.println("Rexit !");
        new GUIPopUpRexit(hyper);
        //hyper.popupInfo("Rexit returned with status : " + hyper.getprocessManager().rexit(status), "Rexit status");
	}

	public void 			rps()
	{
        System.out.println("RPS !");
        hyper.getRpsPanel().sendInfo(hyper.getprocessManager().rlist());
	}

	public void				saveWorkspace()
	{
		JFileChooser		fileChoose = new JFileChooser();
		fileChoose.setFileFilter(new XMLFileFilter());
		int returnVal = fileChoose.showSaveDialog(fileChoose);
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File file = fileChoose.getSelectedFile();
    		GUIWorkspaces.saveWorkspace(desktop, file.getAbsolutePath());
        }
	}

	public void				loadWorkspace(String file)
	{
		GUIWorkspaces.loadWorkspace(desktop, file);
	}

	public void				loadWorkspace()
	{
		JFileChooser		fileChoose = new JFileChooser();
		fileChoose.setFileFilter(new XMLFileFilter());
		int returnVal = fileChoose.showOpenDialog(fileChoose);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			File file = fileChoose.getSelectedFile();
			GUIWorkspaces.loadWorkspace(desktop, file.getAbsolutePath());
		}
	}

	public void 			exitApp()
	{
        System.out.println("Exit application !");
        hyper.getprocessManager().rexit(0);
        System.exit(0);
	}

	public void 			actionPerformed(ActionEvent event)
	{
	}
}

class						XMLFileFilter extends FileFilter
{
	public					XMLFileFilter()
	{

	}

	public boolean 			accept(File f)
	{
	    if (f.isDirectory())
	    	return true;
	    String name = f.getName();
	    if (name == null || name.lastIndexOf('.') == -1)
	    	return false;
	    String extension = name.substring(name.lastIndexOf('.'));
	    if (extension != null & extension.equals(".msw"))
	    	return true;
		return false;
	}

	public String getDescription()
	{
		return "Membership Workspaces (*.msw)";
	}
}
