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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 
 * Main class for all the 'rexit' related pop-ups used in the GUI.
 *
 */
public class 					GUIPopUpRexit extends JFrame implements ActionListener
{
	private static final long 	serialVersionUID = 5239485140285531681L;
	private JButton				butOK;
	private JButton				butCancel;
	Hypership					hyper;
	JTextField					field;
	
	/**
	 * 
	 * Main constructor for the GUIPopUpRexit class
	 * 
	 * @param hyper : the Hypership instance
	 * 
	 */
	public 						GUIPopUpRexit(Hypership hyper)
	{
		super("Rexit");
		this.hyper = hyper;
		
		setBounds(10, 10, 150, 150);
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
		content = createRexitPopUpContent();
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
	private JPanel 				createRexitPopUpContent()
	{
		JPanel 					pane = new JPanel();
		JLabel					status = new JLabel("Exit status  :  ");
		field = new JTextField("-1");
		
		pane.setLayout(new GridBagLayout());
		GridBagConstraints cStatus = new GridBagConstraints();
		GridBagConstraints cField = new GridBagConstraints();
		
		cStatus.gridx = 0;
		cStatus.gridy = 0;
		cStatus.gridwidth = 2;

		pane.add(status, cStatus);

		cField.gridx = 2;
		cField.gridy = 0;
		cField.ipadx = 30;
		pane.add(field, cField);

		return pane;
	}
		
	/**
	 * 
	 * RExit has been confirmed by user
	 * 
	 */
	public void 				confirm()
	{
		int 					res=0;
		
		res = hyper.getprocessManager().rexit(Integer.parseInt(field.getText()));
		hyper.popupInfo("Rexit returned : " + res, "Rexit return");
		this.dispose();
	}
	
	public void 				cancel()
	{
		this.dispose();
	}

	public void actionPerformed(ActionEvent e)
	{
		
	}
}
