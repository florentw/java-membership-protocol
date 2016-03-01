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
import java.util.Vector;

/**
 *
 * This class implements the global view of the membership.
 *
 * @version 	1.0
 *
 */
public class 								MSView implements Serializable
{
	private static final long 				serialVersionUID = 1L;
	private MSViewLocal						localView;
	private Vector<MSViewLocal>				globalView;

	/**
	 *
	 * Constructor for the view :
	 * 	- Initialize global view (vector of local views)
	 * 	- Initialize local view
	 *
	 */
	public									MSView(int groupID, MSMember member)
	{
		localView = new MSViewLocal(groupID);
		globalView = new Vector<MSViewLocal>();
		addLocalMember(member);
		addGlobalMember(localView);
	}

	/**
	 *
	 * Get the local view of the membership
	 *
	 * @return Current local view of the caller
	 *
	 */
	synchronized public MSViewLocal			getLocalView()
	{
		return localView;
	}

	/**
	 *
	 * Get the local view of the membership
	 *
	 * @return Current local view of the caller
	 *
	 */
	synchronized public void				setLocalView(MSViewLocal v)
	{
		localView = v;
	}

	/**
	 *
	 * Get the global view of the membership
	 *
	 * @return Current global view
	 *
	 */
	synchronized public Vector<MSViewLocal>	getGlobalView()
	{
		return globalView;
	}

	/**
	 *
	 * Add a new member to the local view
	 *
	 * @param member : New member to add
	 *
	 */
	synchronized public void				addLocalMember(MSMember member)
	{
		localView.addMember(member);
	}

	/**
	 *
	 * Add a new member to the global view
	 * The member is a 2nd-level group (MSLocalView)
	 *
	 * @param group : Local view of the group to add
	 *
	 */
	synchronized public void				addGlobalMember(MSViewLocal group)
	{
		globalView.add(group);
	}

	/**
	 *
	 * Remove a member from the local view
	 *
	 * @param member : Member to remove
	 *
	 */
	synchronized public void				removeLocalMember(MSMember member)
	{
		localView.removeMember(member);
	}

	/**
	 *
	 * Remove a member from the local view by its ID
	 *
	 * @param id : ID of the member to remove
	 *
	 */
	synchronized public void				removeLocalMemberByID(double id)
	{
		localView.removeMemberByID(id);
	}

	/**
	 *
	 * Remove a group from the global view
	 *
	 * @param group : Group to remove
	 *
	 */
	synchronized public void				removeGlobalMember(MSViewLocal group)
	{
		globalView.remove(group);
	}

	/**
	 *
	 * Remove a group from the global view by its ID
	 *
	 * @param gID : ID of the group to remove
	 *
	 */
	synchronized public void				removeGlobalMemberByID(int gID)
	{
		MSViewLocal							groupTmp;
		int									groupIt;

		for (groupIt = 0; groupIt < globalView.size(); groupIt++)
		{
			groupTmp = globalView.elementAt(groupIt);
			if (groupTmp.getGroupID() == gID)
				globalView.removeElementAt(groupIt);
		}
	}

	/**
	 *
	 * Prints the global view
	 *
	 */
	synchronized public void				printGlobalWiew()
	{
		MSViewLocal							groupTmp;
		int									groupIt;

		System.out.println("MSView::printGlobalWiew :") ;
		for (groupIt = 0; groupIt < globalView.size(); groupIt++)
		{
			groupTmp = globalView.elementAt(groupIt);

			String ip ;
			int gid = groupTmp.getGroupID() ;
			ip = ""+((gid & 0xff000000) >>> 24) ;
			ip += "."+((gid & 0x00ff0000) >>> 16);
			ip += "."+((gid & 0x0000ff00) >>> 8);
			ip += "."+((gid & 0x000000ff));

			System.out.println("+ Group #"+groupIt+" - MIP: "+ip+" - GID: "+groupTmp.getGroupID()) ;
			groupTmp.printView() ;
		}
	}

	/**
	 *
	 * Prints our local view
	 *
	 */
	synchronized public void				printLocalWiew(MSViewLocal group)
	{
		group.printView() ;
	}

	/**
	 *
	 * Update the local view from the parameter view, given the following rules :
	 * 		- The local view is not modified as the current information is considered more accurate
	 * 		- If a group doesn't exist inside the global view, add it.
	 * 		- If an existing group has been modified, replace it.
	 *
	 * @param view : The received view
	 *
	 */
	synchronized public int					updateGlobalView(MSView view)
	{
		MSViewLocal							groupIncoming, groupTmp;
		int									groupIt, inIt;
		Object []							tmpArray, incomeArray;

		groupIncoming = view.getLocalView();
		for (groupIt = 0; groupIt < globalView.size(); groupIt++)
		{
			groupTmp = globalView.elementAt(groupIt);
			if (groupTmp.getGroupID() == groupIncoming.getGroupID())
			{
				tmpArray = groupTmp.getMembersList().toArray();
				incomeArray = groupIncoming.getMembersList().toArray();
				for (inIt = 0; inIt < incomeArray.length && inIt < tmpArray.length; inIt++)
					if (((MSMember)tmpArray[inIt]).getID() != ((MSMember)incomeArray[inIt]).getID())
					{
						globalView.removeElementAt(groupIt);
						addGlobalMember(groupIncoming);
						return 1;
					}
				if (inIt != incomeArray.length || inIt != tmpArray.length)
				{
					globalView.removeElementAt(groupIt);
					addGlobalMember(groupIncoming);
					return 1;
				}
				return 0;
			}
		}
		addGlobalMember(groupIncoming);
		return 1;
	}
}