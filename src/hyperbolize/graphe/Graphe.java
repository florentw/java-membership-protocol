/*
	hyperbolize - a java implementation of the H3 algorithm

	Copyright (C) 2008 Philippe Esling

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

package hyperbolize.graphe;

import java.util.Iterator;

/**
* 
* This interface is the basic functionality required for implementing a graph representation
* 
* @author 						Philippe ESLING
* @version 						1.0
* @see 							AbsGraphe
* @see							ListGraphe
* 
*/
public interface 	Graphe 
{
	/**
	 * @return Returns the number of nodes
	 */
	int 			nodesNumber();

	/**
	 * For a simple run through the nodes (without any logical order)
	 * 
	 * @return Returns an iterator on the nodes
	 */
	Iterator<?> 	nodesIter();

	/**
	 * To add a node in the graph (without any edges for the moment)
	 * 
	 * @param value : The station to add
	 * @return A reference to the created node
	 */
	Node<?>			addNode(Node<?> value);

	/**
	 * Returns a reference to a node which have the specified index
	 * 
	 * @param i : the integer value of the index
	 * @return A reference to the node
	 */
	Node<?>			getNode(int i);
	
	/**
	 * Adds an edge between two given nodes
	 * 
	 * @param a : First node
	 * @param b : Second node
	 */
	void 			putEdge(Node<?> a, Node<?> b);

	/**
	 * If an edge exists between two given nodes
	 * 
	 * @param a : First node
	 * @param b : Second node
	 * @return true if the edge exists, false otherwise
	 */
	boolean 		getEdge(Node<?> a, Node<?> b);

	/**
	 * Adds an edge between two given nodes (with their index)
	 * 
	 * @param a : index of the first node
	 * @param b : index of the second
	 */
	void 			putEdge(int a, int b);

	/**
	 * If an edge exists between two given nodes (with their index)
	 * 
	 * @param a : index of the first node
	 * @param b : index of the second
	 * @return true if the edge exists, false otherwise
	 */
	boolean 		getEdge(int a, int b);

	/**
	 * Returns an iterator on the nodes which have an edge
	 * with the given node
	 * 
	 * @param a : selected node
	 */
	Iterator<?> 	adjNodes(Node<?> a);

	/**
	 *  For debug use only : print of a width run .
	 */
	void 			consolePrint();

}
