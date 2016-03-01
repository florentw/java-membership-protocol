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
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;

import javax.media.opengl.GLCanvas;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.Dockable;

/**
 *
 *	This class is the OpenGL representation widget.
 *
 *
 */
public class 					GUIOpenGL extends JPanel implements Dockable, DropTargetListener
{
	private static final long 	serialVersionUID = -4347898768268019602L;
    DockKey 					key;
    Hypership					hyper;

    /**
     *
     * Main constructor for the GUIOpenGL class
     *
     * @param glCanv : the OpenGL canvas to paint on
     * @param h : the Hypership instance
     */
    public 						GUIOpenGL(GLCanvas glCanv, Hypership h)
    {
    	hyper = h;
    	key = new DockKey("OpenGL");
    	key.setName(" Membership View");
    	key.setTooltip("Explore the hyperbolic membership.");
        key.setIcon(new ImageIcon("icons/member_view.gif"));
    	key.setCloseEnabled(false);
    	key.setAutoHideEnabled(false);
    	key.setFloatEnabled(false);
    	key.setMaximizeEnabled(true);
    	key.setResizeWeight(1f);
    	setLayout(new BorderLayout());
    	this.setDropTarget(new DropTarget(this, this));
    	add(glCanv, BorderLayout.CENTER);
    }

    /**
     *
     * Action triggered when a file is dragged over the OpenGL widget :
     *  highlight the node by closest location.
     *
     */
	public void 				dragOver(DropTargetDragEvent arg0)
	{
		hyper.draggedFile("", arg0.getLocation().x, arg0.getLocation().y);
	}

	/**
	 *
	 * The dragged file has been dropped :
	 *  get the file data and then launch DProcess execution.
	 *
	 */
	public void 				drop(DropTargetDropEvent arg0)
	{
		try
		{
			String transfer = (String) arg0.getTransferable().getTransferData(arg0.getCurrentDataFlavors()[0]);
			System.out.println("Dropped : " + (String)transfer);
			hyper.droppedFile(transfer, arg0.getLocation().x, arg0.getLocation().y);
		}
		catch (UnsupportedFlavorException e)
		{
			System.err.println("GUIOpenGL::Dropped file doesn't match any known format");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public Component 			getComponent()
    {
    	return this;
    }

    public DockKey 				getDockKey()
    {
    	return this.key;
    }

	public void 				dragEnter(DropTargetDragEvent arg0) { }

	public void 				dragExit(DropTargetEvent arg0) { }

	public void 				dropActionChanged(DropTargetDragEvent arg0) { }
}