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
import java.util.LinkedList;
import java.util.List;

public class 						ListGraphe<T> extends AbsGraphe
{
	   private List<Node<T>>		_nodes;
	   private int					root;

	    /**
	     * Main constructor for the ListGraph class
	     * Creation of a new empty graph
	     *
	     */
	    public 						ListGraphe()
	    {
	    	_nodes = new LinkedList<Node<T>>();
	    	root = 0;
	    }

	    /**
	     * Uses this function to iterate through the nodes
	     *
	     * @return an iterator on the nodes
	     */
	    public Iterator<?>		 	nodesIter()
	    {
	    	return _nodes.iterator();
	    }

	    /**
	     * To add a node in the graph
	     *
	     * @param valeur : the node to add
	     * @return A reference to the freshly created node
	     *
	     */
	    public Node<T> 				addNode(T valeur)
	    {
	    	Node<T> a = new Node<T>(_nbr_nodes, valeur);
	    	_nbr_nodes++;
	    	_nodes.add(a);
	    	return a;
	    }

	    /**
	     * To get a reference on a Node by his index
	     *
	     * @param i : the index of a node
	     * @return the reference to this node
	     *
	     */
	    public Node<T> 				getNode(int i)
	    {
	    	return (Node<T>) _nodes.get(i);
	    }

	    /**
	     * To put an edge between two nodes
	     *
	     * @param a : the first node
	     * @param b : the second node
	     *
	     */
	    public void 				putEdge(Node<T> a, Node<T> b)
	    {
	    	a.getNext().add(b);
	    }

	    /**
	     * To get if an edge exists between two nodes
	     *
	     * @param a : the first node
	     * @param b : the second node
	     * @return true if the edge exists, false otherwise
	     *
	     */
	    public boolean 				getEdge(Node<?> a, Node<?> b)
	    {
	    	return a.getNext().contains(b);
	    }

	    /**
	     * Adds an edge between two given nodes (with their index)
	     *
	     * @param a : index of the first node
	     * @param b : index of the second
	     *
	     */
	    public void 				putEdge(int a, int b)
	    {
	    	getNode(a).getNext().add(getNode(b));
	    }

	    /**
	     * If an edge exists between two given nodes (with their index)
	     *
	     * @param a : index of the first node
	     * @param b : index of the second
	     * @return true if the edge exists, false otherwise
	     *
	     */
	    public boolean 				getEdge(int a, int b)
	    {
	    	if (getNode(a).getNext().contains(getNode(b))) {
	    		return true;
	    	} else {
	    		return false;
	    	}
	    }

	    /**
	     * This function is very useful during search through the nodes,
	     * it returns an iterator on the adjacent nodes of the specified one.
	     *
	     * @param a : the specified node
	     * @return an iterator on the adjacent nodes
	     *
	     */
	    public Iterator<?>			adjNodes(Node<?> a)
	    {
	    	return a.getNext().iterator();
	    }

		/**
		 * For debug use only : console printing of the
		 * graph
		 */
		public void 				consolePrint()
		{
			DIterator d = new DIterator(_nbr_nodes, this);
			Node<?> current;

			for (; d.hasNext(); ) {
				current = (Node<?>) d.next();
				System.out.println(current.getValue().toString());
			}
		}

		public int					getRoot()
		{
			return root;
		}

		public void					setRoot(int r)
		{
			root = r;
		}

		public int					getSize()
		{
			return _nodes.size();
		}

		public int					nbAdjacent(Node<?> a)
		{
			return a.getNext().size();
		}

		public void					clear()
		{
			_nodes.clear();
			_nbr_nodes = 0;
		}
}
