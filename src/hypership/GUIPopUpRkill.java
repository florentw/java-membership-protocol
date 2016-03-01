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
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dprocess.RemoteProcess;
import dprocess.dpclient.DPClientInfo;

/**
 *
 * Main class for all the 'rkill' related pop-ups used in the GUI.
 *
 */
public class GUIPopUpRkill extends JFrame implements ActionListener
{
	private static final long 	serialVersionUID = 5239485140285531681L;
	private JButton				butOK;
	private JButton				butCancel;
	Hypership					hyper;
	JComboBox					signalList;

	/**
	 *
	 * Main constructor for the GUIPopUpRkill class
	 *
	 * @param hyper : the Hypership instance
	 *
	 */
	public 						GUIPopUpRkill(Hypership hyper)
	{
		super("Rkill");
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
		content = createRkillPopUpContent();
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
	private JPanel 				createRkillPopUpContent()
	{
		JPanel 					pane = new JPanel();
		JButton					refresh = new JButton("Refresh");
		final JTextField		member = new JTextField();
		final JTextField		proc = new JTextField();

		signalList = new JComboBox();

		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		//Texte "signal"
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.LINE_START;
		pane.add(new JLabel("Signal : "), c);

		c.gridx = 2;
		c.gridy = 0;
		c.gridwidth = 1;
		c.ipadx = 30;
		for(int i=1 ; i<33 ; i++)
			signalList.addItem("" + i);
		pane.add(signalList, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.ipadx = 0;
		pane.add(new JLabel("Host node : "), c);

		c.gridx = 0;
		c.gridy = 2;
		c.ipadx = 200;
		pane.add(member,c);

		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		c.ipadx = 0;
		pane.add(new JLabel("Process : "), c);

		c.gridx = 0;
		c.gridy = 4;
		c.ipadx = 200;
		pane.add(proc,c);

		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 1;
		c.ipadx = 0;
		refresh.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e){
	        	member.setText(hyper.getInfoPanel().getName());
	        	proc.setText(hyper.getInfoPanel().getSelectedProcess());
	        }});
		pane.add(refresh,c);


		return pane;
	}

	/**
	 *
	 * RKill has been confirmed by user
	 *
	 */
	public void 							confirm()
	{
		DPClientInfo  						info;
		Hashtable<Integer, DPClientInfo> 	RPInfo = hyper.getprocessManager().getProcessInfos();
		RemoteProcess						rp = null;
		int 								index = 0;
		int 								result;
		String								tmpCompare = new String();

		hyper.getInfoPanel().getSelectedProcess();
		for (Enumeration<DPClientInfo> e = RPInfo.elements(); e.hasMoreElements() ; )
		{
			index++;
			info = e.nextElement() ;
			tmpCompare = info.getProcessName() + " | " + info.getLocalPID();

			if(tmpCompare.equals(hyper.getInfoPanel().getSelectedProcess()))
			{
				System.out.println("\"" + tmpCompare + "\" || \"" + hyper.getInfoPanel().getSelectedProcess() + "\"  ID : " + index);
				rp = hyper.getprocessManager().getProcessList().get(info.getLocalPID());

			}


		}
		if(rp == null)
			hyper.popupError("Wrong process", "Process name error");
		else
		{
			result = hyper.getprocessManager().rkill(rp,signalList.getSelectedIndex());
			hyper.getInfoPanel().refresh(hyper.getPickedMember());
			hyper.popupInfo("Return value of rkill : " + result, "Rkill return code");
		}
		this.dispose();
	}

	public void 							cancel()
	{
		this.dispose();
	}

	public void actionPerformed(ActionEvent e)
	{

	}
}
