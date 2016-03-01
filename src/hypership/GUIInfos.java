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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import membership.MSMember;

import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.Dockable;
import com.vlsolutions.swing.docking.DockingConstants;

import dprocess.dpclient.DPClientInfo;

/**
 *
 * This class is the information widget on members.
 * When a member is selected in the membership, this panel will contain all related information :
 * [ID, Name, IP, State]
 *
 *
 */
public class 					GUIInfos extends JPanel implements Dockable
{
	private static final long 	serialVersionUID = -4347898768268019602L;
    private DockKey				key;
  	private JLabel				memberID;
	private JLabel				memberName;
	private JLabel				memberAdress;
	private JLabel				memberState;
	private JComboBox			processList;
	private double				id;
	private String				name;
	Hypership					hyper;

	/**
	 *
	 * Main constructor for the GUIInfos class
	 *
	 * @param hyper : the Hypership instance
	 *
	 */
    public 						GUIInfos(Hypership hyper)
    {
    	key = new DockKey("Infos");
    	memberID = new JLabel();
    	memberName = new JLabel();
    	memberAdress = new JLabel();
    	memberState = new JLabel();
    	processList = new JComboBox();
    	id = 0;
    	name = new String();
    	this.hyper = hyper;

    	key.setName("Informations");
        key.setTooltip("Informations on members");

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(5,0,5,0);
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		add(new JLabel(new ImageIcon("icons/member_id.gif")),c);

		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(new JLabel("Member ID"),c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.CENTER;
		memberID.setText("");
        add(memberID, c);

        c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		add(new JLabel(new ImageIcon("icons/member_id.gif")),c);

		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(new JLabel("Name : "),c);

		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.CENTER;
		memberName.setText("");
        add(memberName, c);


        c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		add(new JLabel(new ImageIcon("icons/member_id.gif")),c);

        c.gridx = 1;
		c.gridy = 4;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(new JLabel("Adress : "),c);

		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.CENTER;
		memberAdress.setText("");
        add(memberAdress, c);

        c.gridx = 0;
		c.gridy = 7;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		add(new JLabel(new ImageIcon("icons/list.png")),c);

		c.gridx = 1;
		c.gridy = 7;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.LINE_START;
		add(new JLabel("Remote processes"),c);

		c.gridx = 0;
		c.gridy = 8;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.CENTER;
		add(processList, c);

        refreshProcessList(hyper.getprocessManager().getProcessInfos());
        key.setMaximizeEnabled(false);
        key.setCloseEnabled(false);
        key.setAutoHideBorder(DockingConstants.HIDE_RIGHT);
    }

    /**
     *
     * Sets the ID of the newly selected member.
     *
     * @param id : the new member identifier
     *
     */
    public void 				setID(double id)
    {
    	this.id = id;
    	memberID.setText(String.valueOf(this.id));
    }

    /**
     *
     * Sets the name of the newly selected member.
     *
     * @param name : the new member name
     *
     */
    public void 				setName(String name)
    {
    	this.name = name;
    	memberName.setText(this.name);
    }

    /**
     *
     * Refresh all the information labels in the widget according to a member
     *
     * @param member : newly selected member
     *
     */
    public void 				refresh(MSMember member)
    {
    	setID(member.getID());
		setName(member.getName());
		memberAdress.setText(member.getAddress());
		memberState.setText(member.getState());
		refreshProcessList(hyper.getprocessManager().getProcessInfos());
		repaint();
    }

    /**
     *
     * Refresh the list of running processes according to a specified one
     *
     * @param RPInfo : the list of remote processes.
     *
     */
    public void 				refreshProcessList(Hashtable<Integer, DPClientInfo> RPInfo)
    {
    	DPClientInfo 			info;

    	processList.removeAllItems();
		MSMember member = ((NodeMembership)hyper.getgraphe().getNode(hyper.getlastPicked()).getValue()).getMember();

    	if(RPInfo.isEmpty())
    		processList.addItem("Aucun processus");
    	else
    		for (Enumeration<DPClientInfo> e = RPInfo.elements(); e.hasMoreElements() ;)
    		{
    			info = e.nextElement() ;
    			if(info.getServerName().equals(member.getAddress()))
    			{
    				System.out.println("Process trouvé");
    				processList.addItem(info.getProcessName() + " | " + info.getLocalPID());
    			}

    		}
    	repaint();
    }


    public Component 			getComponent()
    {
    	return this;
    }

    public DockKey 				getDockKey()
    {
    	return this.key;
    }

    public double 				getID()
    {
    	return id;
    }

    public String 				getName()
    {
    	return name;
    }

    public String 				getSelectedProcess()
    {
    	return (String)processList.getSelectedItem();
    }
}
