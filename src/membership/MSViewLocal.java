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

package membership;

import java.io.Serializable;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

/**
*
* This class implements the local view of a membership 2nd level ring.
*
* @version 	1.0
*
*/
public class 								MSViewLocal implements Serializable
{
	private static final long 				serialVersionUID = 1L;
	private SortedMap<Double, MSMember>		localView;
	private int								groupID;

	/**
	 *
	 * Main constructor for a local view
	 *
	 */
	public									MSViewLocal(int gID)
	{
		groupID = gID;
		localView = new TreeMap<Double, MSMember>();
	}

	/**
	 *
	 * Add a new member to the local view
	 *
	 * @param member : New member to add
	 *
	 */
	synchronized public void				addMember(MSMember member)
	{
		MSMember							mTmp;
		int									mIt;
		Object []							mArray ;

		/* Check if the member address exists */
		mArray = localView.keySet().toArray() ;
		for (mIt = 0; mIt < mArray.length; mIt++)
		{
			mTmp = localView.get(mArray[mIt]) ;
			if (mTmp.getAddress().equals(member.getAddress()))
			{
				localView.remove(mTmp.getID()) ;
				break ;
			}
		}

		localView.put(new Double(member.getID()), member) ;
	}

	/**
	 *
	 * Remove a member from the local view
	 *
	 * @param member : Member to remove
	 *
	 */
	synchronized public void				removeMember(MSMember member)
	{
		localView.remove(member.getID());
	}

	/**
	 *
	 * Remove a member from the local view by its ID
	 *
	 * @param memberID : ID of the member to remove
	 *
	 */
	synchronized public void				removeMemberByID(Double memberID)
	{
		localView.remove(memberID) ;
	}

	/**
	 *
	 * Gets a member from the local view by its ID
	 *
	 * @return the MSMember reference
	 *
	 */
	public MSMember							getMemberByID(Double memberID)
	{
		return localView.get(memberID) ;
	}

	/**
	 *
	 * Gets the leader of the local view
	 *
	 * @return the MSMember reference to the leader
	 *
	 */
	public MSMember							getLeader()
	{
		return localView.get(localView.firstKey());
	}

	/**
	 *
	 * Gets the last member of the local view
	 *
	 * @return the MSMember reference to the last
	 *
	 */
	public MSMember							getLast()
	{
		if (!localView.isEmpty())
			return localView.get(localView.lastKey());
		return null;
	}

	/**
	 *
	 * Gets the group ID associated with this local view
	 *
	 * @return The group identifier
	 *
	 */
	public int								getGroupID()
	{
		return groupID;
	}

	/**
	 *
	 * Gets the watched member related to a specific watcher.
	 *
	 * @param watcher : the member that will watch
	 * @return The MSMember reference to the watched member
	 *
	 */
	synchronized public MSMember		getWatched(MSMember watcher)
	{
		SortedMap<Double,MSMember>		wView;

		if (localView.size() < 2)
			return null;
		if (watcher.getID() == getLeader().getID())
			return getLast();
		wView = localView.headMap(watcher.getID());
		if (!wView.isEmpty())
			return (MSMember) wView.get(wView.lastKey());
		return null;
	}

	public Collection<MSMember>				getMembersList()
	{
		return localView.values();
	}

	/**
	 *
	 * Nice printing of the local view in the console
	 *
	 */
	public void 							printView ()
	{
		MSMember							mTmp;
		int									mIt;
		Object []							mArray ;

		mArray = localView.keySet().toArray() ;

		for (mIt = 0; mIt < mArray.length; mIt++)
		{
			mTmp = localView.get(mArray[mIt]) ;
			System.out.println("|-> Node : #"+mIt
	   							+ " - Name: "+mTmp.getName()
	   							+ " - ID: "+mTmp.getID()
	   							+ " - IP: "+mTmp.getAddress());
		}
	}

}