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

import java.util.LinkedList;

public class 							Node<T> 
{
	private double 						_d_val;
	private int 						_d_color;
	private Node<T> 					_d_pred;
	private int 						_index;
	private T 							_value;
	private LinkedList<Node<T>> 		next;
	
	/**
	 * Main constructor for the node class.
	 * 
	 * @param i : the index of the node
	 * @param v : a reference to the station he represents
	 */
	public 								Node(int i, T v)
	{
		_index = i;
		_value = v;
		next = new LinkedList<Node<T>>();
	}
	
	public 								Node(int i, Node<T> v)
	{
		_index = i;
		_value = v.getValue();
		next = new LinkedList<Node<T>>();
	}

	/**
	 * Returns the index of the node
     * Each node have an index between 0 and nbr_nodes - 1
     *  
	 */
    public int 							getIndex()
    {
    	return _index;
    }

    /**
     * For further inspection of the station.
     * 
     * @return the value of the node
     */
    public T 							getValue()
    {
    	return _value;
    }
    
    /**
     * For the dijkstra algorithm purposes only
     * 
     * @param p : the new dijkstra value
     */
    public void 						setDijkstraVal(double p)
    {
    	_d_val = p;
    }

    /**
     * For the dijkstra algorithm purposes only
     * 
     * @return the dijkstra value for this node
     */
    public double 						getDijkstraVal()
    {
    	return _d_val;
    }
    
    /**
     * For the dijkstra algorithm purposes only
     * 
     * @param i : the new dijkstra color
     */
    public void 						setDijkstraColor(int i)
    {
    	_d_color = i;
    }
    
    /**
     * For the dijkstra algorithm purposes only
     * 
     * @return the dijkstra color for this node
     */
    public int 							getDijkstraColor()
    {
    	return _d_color;
    }
    
    /**
     * For the dijkstra algorithm purposes only
     * 
     * @param i : the new dijkstra predecessor
     */
    public void 						setDijkstraPred(Node<T> i)
    {
    	_d_pred = i;
    }
    
    /**
     * For the dijkstra algorithm purposes only
     * 
     * @return the dijkstra predecessor
     */
    public Node<T> 						getDijkstraPred()
    {
    	return _d_pred;
    }
    
    public LinkedList<Node<T>>			getNext()
    {
    	return next;
    }
}

