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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import membership.MSMember;

/**
 *
 * Main class for all the 'rexec' related pop-ups used in the GUI.
 *
 */
public class 					GUIPopUpRexec extends JFrame implements ActionListener
{
	private static final long 	serialVersionUID = 5239485140285531681L;
	private JButton				butOK;
	private JButton				butCancel;
	Hypership					hyper;
	JTextArea					rexecInfos;
	JComboBox 					processBox;
	JTextField					file;

	/**
	 *
	 * Main constructor for the GUIPopUpRexec class
	 *
	 * @param hyper : the Hypership instance
	 *
	 */
	public 						GUIPopUpRexec(Hypership hyper)
	{
		super("Rexec");
		this.hyper = hyper;
		setBounds(10, 10, 300, 300);
		setVisible(true);
		setAlwaysOnTop(true);
		setLayout(new GridBagLayout());
		JPanel boots = new JPanel();
		boots.setLayout(new GridLayout(1,2));
		GridBagConstraints c = new GridBagConstraints();
		butOK = new JButton(new ImageIcon("icons/rps.png"));
		butOK.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e){ confirm(); }});
		butCancel = new JButton(new ImageIcon("icons/quit.png"));
		butCancel.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e){ cancel(); }});
		boots.add(butOK);
		boots.add(butCancel);
		JPanel content = new JPanel();
		content = createRexecPopUpContent();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.CENTER;
		add(content,c);
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,0,0,0);

		c.anchor = GridBagConstraints.PAGE_END;
		c.weightx = 1;
		c.weighty = 1;
		add(boots,c);
	}

	/**
	 *
	 * Creates the content inside the PopUp
	 *
	 * @return A JPanel filled with the content
	 *
	 */
	private JPanel 				createRexecPopUpContent()
	{
		JPanel 					pane = new JPanel();
		JButton 				browse = new JButton("Browse");
		JButton					refresh = new JButton("Refresh");
		String					selectedFile = new String();
		MSMember 				tmpNode;
		int 					i;

		processBox = new JComboBox();
		rexecInfos = new JTextArea();

		selectedFile = hyper.getfileExplorePanel().getSelectedFile();
		System.out.println("Fichier selectionn� :");
		System.out.println(selectedFile);
		if(selectedFile.equals("Aucun fichier s�lectionn�"))
			file = new JTextField("Aucun fichier s�lectionn�");
		else
			file = new JTextField(selectedFile);

		rexecInfos.setEditable(false);

		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		//pane.setPreferredSize(new Dimension(200,200));

		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10,10,10,10);
		c.weightx = 1;
		c.weighty = 0;

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		pane.add(new JLabel("Program to launch :"),c);
		c.gridx = 0;
		c.gridy = 1;
		pane.add(file,c);

		browse.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e) { file.setText(loadExe()); }});
		c.gridx = 2;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 0;
		pane.add(browse,c);
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		pane.add(new JLabel("Host node :"),c);
		c.gridx = 0;
		c.gridy = 3;
		pane.add(processBox,c);
		c.gridx = 2;
		c.gridy = 3;
		c.gridwidth = 1;
		c.ipadx = 0;
		refresh.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e){
	        	updateInfos(rexecInfos, hyper.getPickedMember());
	        }});
		pane.add(refresh,c);
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 3;
		c.weighty = 1;
		pane.add(rexecInfos,c);

		for(i = 0; i < hyper.getgraphe().nodesNumber() ; i++)
		{
			tmpNode = ((NodeMembership)hyper.getgraphe().getNode(i).getValue()).getMember();
			processBox.addItem(tmpNode.getName());
		}
		//Highlight the picked node
		MSMember member = ((NodeMembership)hyper.getgraphe().getNode(hyper.getlastPicked()).getValue()).getMember();
		System.out.println("LastPicked "+hyper.getlastPicked());
		processBox.setSelectedIndex(hyper.getlastPicked());
		if(hyper.getlastPicked() != 0)
			updateInfos(rexecInfos, member);
		processBox.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e){
	        	//Highlight selected node in view
	        	int index = ((JComboBox)e.getSource()).getSelectedIndex();
	        	MSMember selectedMember = ((NodeMembership)hyper.getgraphe().getNode(index).getValue()).getMember();
	        	updateInfos(rexecInfos, selectedMember);
	        	hyper.getgraphe().getNode(hyper.getlastPicked()).getValue().getRepresentation().setAlpha(0.2);
	        	hyper.getgraphe().getNode(index).getValue().getRepresentation().setAlpha(50.0);
	        	hyper.setLastPicked(index);

	        }});




		return pane;
	}

	/**
	 *
	 * Loads an executable file from a file chooser
	 *
	 * @return The absolute path of the file
	 *
	 */
	public String				loadExe()
	{
		JFileChooser			fileChoose = new JFileChooser();

		int returnVal = fileChoose.showOpenDialog(fileChoose);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			File file = fileChoose.getSelectedFile();
			return file.getAbsolutePath();
		}
		return "";
	}

	/**
	 *
	 * Updates the information inside the contents according to a specified member
	 *
	 * @param area : the information area
	 * @param member : the selected member
	 *
	 */
	private void 				updateInfos(JTextArea area, MSMember member)
	{
		processBox.setSelectedItem(member.getName());
		area.removeAll();
		area.setText("");
		area.append("Adress : " + member.getAddress() + "\n");
		area.append("ID : " + member.getID() + "\n");
	}

	/**
	 *
	 * RExec has been confirmed by user
	 *
	 */
	public void 					confirm()
	{
		int 						index = processBox.getSelectedIndex();
		MSMember 					member = ((NodeMembership)hyper.getgraphe().getNode(index).getValue()).getMember();
		int 						res = 0;

		res = hyper.getprocessManager().rexec(member,file.getText());
		hyper.popupInfo("Rexec returned : " + res, "Rexec return");
		hyper.getInfoPanel().refresh(member);
		this.dispose();
	}

	public void 					cancel()
	{
		this.dispose();
	}

	public void 					actionPerformed(ActionEvent e)
	{

	}
}
