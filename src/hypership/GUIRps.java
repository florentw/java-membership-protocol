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

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.Dockable;
import com.vlsolutions.swing.docking.DockingConstants;

/**
 *
 * This class is the RPS information widget on processes.
 *
 *
 */
public class GUIRps extends JPanel implements Dockable
{

	private static final long 	serialVersionUID = -2754056062135964932L;
	DockKey 					key = new DockKey("Rps");
	JTextArea 					textArea = new JTextArea();

	/**
	 *
	 * Main constructor for the GUIRps class.
	 *
	 */
	public 						GUIRps()
	{
		key.setName("Rps");
        key.setTooltip("List of remote process");
        setLayout(new BorderLayout());

        textArea.setEditable(false);

        add(new JScrollPane(textArea),BorderLayout.CENTER);
        //add(textArea, BorderLayout.CENTER);

        key.setMaximizeEnabled(true);
        key.setCloseEnabled(false);
        key.setAutoHideBorder(DockingConstants.HIDE_RIGHT);

	}

	public Component 			getComponent()
	{
    	return this;
    }

    public DockKey 				getDockKey()
    {
    	return this.key;
    }

    public void 				sendInfo(String info)
    {
    	textArea.append(info);
    }
}
