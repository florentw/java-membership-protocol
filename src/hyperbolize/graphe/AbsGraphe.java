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

/**
* 
* This class is the superclass for implementing a graph representation
* It only contains the number of nodes in order flexibility in representation
* 
* @author 						Philippe ESLING
* @version 						1.0
* @see 							Graphe
* @see							ListGraphe
* 
*/
public class 			AbsGraphe
{
    protected int 		_nbr_nodes;

    /**
     * Creation of an empty graph
     *
     */
    public 				AbsGraphe()
    {
    	_nbr_nodes = 0;
    }

    /**
     * Returns the number of nodes in the graph
     * 
     * @return Number of nodes
     */
    public int 			nodesNumber()
    {
    	return _nbr_nodes;
    }
}
