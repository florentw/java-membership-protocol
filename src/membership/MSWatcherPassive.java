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

import java.util.Vector;

/**
 *
 * This class is the first-level passive watcher.
 * Even if all the socket related operations are done in the MSControllerGlobal class
 * This class encapsulates the vector group fault checking over periodic times.
 *
 * @version								1.2
 * @see									MSControllerGlobal
 *
 */
public class 							MSWatcherPassive
{
	private Vector<WatchingStruct>		groups;
	private MSMain						infos;
	private long						lastActivity;

	/**
	 *
	 * Main constructor for the passive watcher.
	 * Initializes the groups vector.
	 *
	 */
	public								MSWatcherPassive(MSMain i)
	{
		groups = new Vector<WatchingStruct>();
		infos = i;
	}

	/**
	 *
	 * This function is called only when a group fault has been detected.
	 * It updates the global view consequently.
	 *
	 * @param d
	 */
	public void							signalFaultyGroup(int d)
	{
		System.out.println("MSWatcherPassive::Group " + d + " is faulty !");
		infos.getView().removeGlobalMemberByID(d);
	  	infos.updateGUIView() ;

	  	if (infos.getView().getLocalView().getMembersList().size() > 1)
	  	{
			System.out.println("MSWatcherPassive::Broadcast modified membership view to 2nd level");
		  	infos.leader2nd.sendMessage("MEMBERSHIP");
	  	}
	}

	/**
	 *
	 * Periodic check of the groups vector.
	 * Assures fault watching even without activity on the first-level network
	 *
	 */
	synchronized public void			checkForFaulty()
	{
		WatchingStruct					tmpGrp;
		long							elapsed;
		int								itGroups;

		System.out.println("MSWatcherPassive::Checking for faulty");
		if (groups.size() == 0)
			return;
		elapsed = System.currentTimeMillis() - lastActivity;
		for (itGroups = 0; itGroups < groups.size(); itGroups++)
		{
			tmpGrp = groups.elementAt(itGroups);
			tmpGrp.remaining -= elapsed;
			if (tmpGrp.remaining > 0)
				break;
			signalFaultyGroup(tmpGrp.groupID);
			elapsed = -tmpGrp.remaining;
			groups.remove(tmpGrp);
			itGroups--;
		}
		lastActivity = System.currentTimeMillis();
	}

	synchronized public void			initGroupWatching()
	{
		int								i, grp, nb_add;

		for (i = 0, nb_add = 0; i < infos.getView().getGlobalView().size(); i++)
		{
			grp = infos.getView().getGlobalView().get(i).getGroupID();
			if (grp == infos.getView().getLocalView().getGroupID())
				continue;
			if (nb_add == 0)
				groups.add(new WatchingStruct(grp, MSControllerGlobal.leaderTimeout));
			else
				groups.add(new WatchingStruct(grp, 1));
			nb_add++;
		}
		lastActivity = System.currentTimeMillis();
	}

	/**
	 *
	 * This function handles the monitoring of group activities.
	 * It is based on the kernel 'timeout' function architecture.
	 * The groups vector is sorted so that each case contains the differential time remaining
	 * compared to its preceding case before this group is being declared 'faulty'.
	 *
	 * @param d : group that sent a message (activity detected)
	 *
	 */
	synchronized public void			groupActivity(int d)
	{
		WatchingStruct					tmpGrp;
		boolean							existing;
		int								itGroups;
		long							grpStamp;
		long							elapsed;

		synchronized (this)
		{
			existing = false;
			if (d == infos.getView().getLocalView().getGroupID())
				return;
			if (groups.size() == 0)
			{
				groups.add(new WatchingStruct(d, MSControllerGlobal.leaderTimeout));
				lastActivity = System.currentTimeMillis();
				return;
			}
			grpStamp = MSControllerGlobal.leaderTimeout;
			elapsed = System.currentTimeMillis() - lastActivity;
			for (itGroups = 0; itGroups < groups.size(); itGroups++)
			{
				tmpGrp = groups.elementAt(itGroups);
				tmpGrp.remaining -= elapsed;
				if (tmpGrp.groupID == d)
				{
					elapsed = -tmpGrp.remaining;
					groups.remove(tmpGrp);
					itGroups--;
					existing = true;
					continue;
				}
				if (tmpGrp.remaining <= 0)
				{
					signalFaultyGroup(tmpGrp.groupID);
					elapsed = -tmpGrp.remaining;
					groups.remove(tmpGrp);
					itGroups--;
					continue;
				}
				elapsed = 0;
				grpStamp -= tmpGrp.remaining;
			}

			if (!existing)
				System.out.println("MSWatcherPassive::Adding new group : "+d);
			groups.add(new WatchingStruct(d, grpStamp));
			lastActivity = System.currentTimeMillis();
		}
	}
}

/**
 *
 * A simple class for storing the remaining time before a group fault.
 *
 */
class									WatchingStruct
{
	public int							groupID;
	public long							remaining;

	public								WatchingStruct(int gID, long rem)
	{
		groupID = gID;
		remaining = rem;
	}
}