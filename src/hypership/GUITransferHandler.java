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

import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;

/**
 * 
 * This class is the real transfer handler for drag-and-drop functionalities.
 *
 */
public class 						GUITransferHandler extends TransferHandler
{
	private static final long 		serialVersionUID = -4252567977463705363L;
	
	/**
	 * 
	 * Main constructor for the GUITransferHandler class
	 * 
	 */
	public 							GUITransferHandler()
	{
		super();
	}
	
	/**
	 * 
	 * Creates a new transferable object
	 * 
	 */
	protected Transferable 			createTransferable(JComponent c)
	{
		Transferable 				t = null;
		Object [] 					path;
		String 						send = new String();
		
		if(c instanceof JTree)
		{
			path = ((JTree) c).getSelectionPath().getPath();
			send = ((FileNode)path[path.length - 1]).getFile().getAbsolutePath();
			t = new GUITransferable(send);
			if (((FileNode)path[path.length - 1]).getFile().isDirectory())
				t = null;
		}
		return t;
	}
	
	/**
	 * 
	 * Action triggered when the file has been dropped
	 * 
	 */
	protected void 					exportDone(JComponent source, Transferable data, int action)
	{
		if(source instanceof JTree)
		{
			JTree tree = (JTree) source;
			TreePath currentPath = tree.getSelectionPath();
			if(currentPath != null)
				currentPath.getLastPathComponent();
			super.exportDone(source, data, action);
		}
	}
	public int getSourceActions(JComponent c) {
		return TransferHandler.MOVE;
	}
}