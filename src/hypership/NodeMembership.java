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

import hyperbolize.graphe.NodeHyperbolique;
import hyperbolize.objet.Objet;

import membership.MSMember;

/**
 * 
 * This class encapsulates the supplementary information about a member for membership purposes. 
 *
 */
public class 					NodeMembership extends NodeHyperbolique
{

	private MSMember			member;

	public 						NodeMembership(MSMember m)
	{
		super();
		member = m;
	}
	
	public						NodeMembership(String l, MSMember m)
	{
		super(l);
		member = m;
	}
	
	public						NodeMembership(String l, Objet o, MSMember m)
	{
		super(l, o);
		member = m;
	}
	
	public MSMember				getMember()
	{
		return member;
	}
}
