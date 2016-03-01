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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * 
 * This class implements the transferable functionalities for drag-and-drop.
 * It represents the file being dragged from the file explorer to the OpenGL interface.
 * 
 *
 */
public class 				GUITransferable implements Transferable
{
	private Object 			data;
	private static final 	DataFlavor[] flavors = new DataFlavor[1];
	static { flavors[0] = DataFlavor.stringFlavor; }
	
	/**
	 * 
	 * Main constructor for the GUITransferable class
	 * 
	 * @param data : the selected file
	 * 
	 */
	public 					GUITransferable(Object data)
	{
		super();
		this.data = data;
	}
	
	public DataFlavor[] 	getTransferDataFlavors()
	{
		return flavors;
	}
	
	public boolean 			isDataFlavorSupported(DataFlavor flavor)
	{
		return true;
	}
	
	public Object 			getTransferData(DataFlavor flavor)
	throws UnsupportedFlavorException, IOException
	{
		return data;
	}

}
