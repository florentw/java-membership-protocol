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
import java.util.Stack;

/**
* 
* For a depth run of the graph given in argument, this iterator is 
* in reality a class that holds a stack in order to run through
* the graph.
* 
* @author 						Philippe ESLING
* @version 						1.0
* @see							Graphe
* 
*/
public class 					DIterator
{
	private Node<?> 			current = null;
	private Stack<Iterator<?>> 	stack;
	private ListGraphe<?> 		graff;
	int 						color[];
	
	/**
	 * The main constructor for the DIterator class
	 * 
	 * @param nbr : Number of nodes in the graph
	 * @param g : Reference to the graph to be run
	 */
	public 						DIterator(int nbr, ListGraphe<?> g)
	{
		int 					i;
		
		graff = g;
		color = new int[nbr];
		for (i = 0; i < nbr; i++)
			color[i] = 0;
		stack = new Stack<Iterator<?>>();
		stack.push(g.nodesIter());
	}
	
	/**
	 * Checks if the depth run is over, this function (on the Iterator basis)
	 * also fill the stack to prepare the continuation of the depth run.
	 * 
	 * @return True if there are some nodes left
	 */
	public boolean 				hasNext()
	{
		Iterator<?> 			adj;
		Node<?> 				w;
		
		if (current != null)
			return true;
		while (!stack.empty())
		{
			adj = (Iterator<?>) stack.peek();
			while (adj.hasNext())
			{
				w = (Node<?>) adj.next();
				if (color[w.getIndex()] == 0)
				{
					color[w.getIndex()] = 1;
					stack.push(graff.adjNodes(w));
					current = w;
					return true;
				}
			}
			stack.pop();
		}
		return false;
	}
	
	/**
	 * To continue the depth run, this function returns a reference
	 * to the next node of the depth run
	 * 
	 * @return DIterator on the next node
	 */	
	public Node<?> 				next()
	{
		Node<?> 				ret;
		
		if (hasNext())
		{
			ret = current;
			current = null;
			return ret;
		}
		return null;
	}
}
