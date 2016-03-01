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

import java.io.*;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import com.vlsolutions.swing.docking.DockingDesktop;

/**
 * 
 * This class only contains the static method for saving or loading a workspace.
 *
 */
public class 					GUIWorkspaces
{
	/**
	 * 
	 * Saves the current workspace to a .xml file
	 * 
	 * @param desk : the workspace container
	 * @param filename : the target file
	 * 
	 */
	  public static void 		saveWorkspace(DockingDesktop desk, String filename)
	  {

		  File 					saveFile = new File(filename);
		  try
		  {
			  BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(saveFile));
			  desk.writeXML(out);
			  out.close();
		  }
		  catch (IOException e)
		  {
			  e.printStackTrace();
			  return;
		  }
	  }

		/**
		 * 
		 * Loads a workspace from a .xml file
		 * 
		 * @param desk : the workspace container
		 * @param filename : the target file
		 * 
		 */
	  public static void 		loadWorkspace(DockingDesktop desk, String filename)
	  {
		  File 					loadFile = new File(filename);
		  BufferedInputStream 	in;
		  
		  try
		  {
			in = new BufferedInputStream(new FileInputStream(loadFile));
			desk.readXML(in);
		  }
		  catch (FileNotFoundException e)
		  {
			  System.err.println("GUIWorkspaces::Can't load workspace::File doesn't exist");
			  e.printStackTrace();
		  }
		  catch (ParserConfigurationException e)
		  {
			  System.err.println("GUIWorkspaces::Can't load workspace::Parser init failed");
			  e.printStackTrace();
		  }
		  catch (SAXException e)
		  {
			  System.err.println("GUIWorkspaces::Can't load workspace::File is not a workspace file");
			  e.printStackTrace();
		  }
		  catch (IOException e)
		  {
			  e.printStackTrace();
		  } 
	  }
}
