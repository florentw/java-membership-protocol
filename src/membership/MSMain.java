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

import hypership.Hypership;

/**
 *
 * This is the main membership class, it handles all the information about the current membership.
 * This class mainly acts as a threader center which allows to launch all activities related to the membership.
 *
 * @version							1.5
 *
 */
public class 						MSMain
{
	private static byte[]			multicast2ndLevel		= {(byte)239,23,23,0};
	private static int				multicast2ndLevelPort	= 5353 ;
	private static byte[]			multicast1stLevel		= {(byte)232,23,0,0};
	private static int				multicast1stLevelPort	= 5454 ;

	private MSView					view;
	private MSMember				memberID;
	private MSControllerJoin		join;
	private MSControllerElection	election;
	public  MSControllerGlobal		leader1st;
	public  MSControllerLocal		leader2nd;
	private MSControllerLeader		leaderVisor;
	public  MSControllerMember		member;
	private MSWatcherActiveListener	watcherReply;
	private MSWatcherActive			watch;

	private Hypership				hyperGUI;

	/**
	 *
	 * Main constructor for the membership
	 *
	 */
	public							MSMain(Hypership h)
	{
		SysInfo si = new SysInfo () ;
		si.buildMemberID(1) ;
		hyperGUI = h;
		multicast2ndLevel = si.getLocalMulticastAddress() ;
		memberID = new MSMember(si.getLocalHostName(), si.getLocalIP(), si.buildMemberID(1));
		System.out.println("MSMain: Starting new member: ID: "+si.buildMemberID(1)+" - IP: "+si.getLocalIP()) ;

		int gID =     ((multicast2ndLevel[0] & 0xFF) << 24)
					| ((multicast2ndLevel[1] & 0xFF) << 16)
					| ((multicast2ndLevel[2] & 0xFF) << 8)
					|  (multicast2ndLevel[3] & 0xFF);

		view = new MSView(gID, memberID);

		join = null;
		election = null;
		leader1st = null;
		leader2nd = null;
		leaderVisor = null;
		member = null;
		watcherReply = null;
		watch = null;
	}

	/**
	 *
	 * Start the membership activity by running a join thread.
	 *
	 */
	public void				launch()
	{
		updateGUIView();
		join = new MSControllerJoin(this, multicast2ndLevel, multicast2ndLevelPort);
		join.start();
		watch = null;
		watcherReply = new MSWatcherActiveListener(this, memberID);
		watcherReply.start();
	}

	/**
	 *
	 * Stop all membership related activities and properly close all sockets.
	 *
	 */
	public void				stop()
	{
		System.out.println("Cleaning join / election ...");
		stopJoinAndElection();
		System.out.println("Cleaning leader ...");
		stopLeader();
		System.out.println("Cleaning member ...");
		stopMember();
	}

	/**
	 *
	 * Start an election for this member.
	 *
	 */
	public void			startElection()
	{
		if (election == null || (election != null && election.isAlive()))
		{
			election = new MSControllerElection(this, multicast2ndLevel, multicast2ndLevelPort);
			election.start();
		}
	}

	/**
	 *
	 * Start all leader related activities :
	 * 	- 2nd-level controller for join and views
	 * 	- 1st-level controller for groups activities
	 *
	 */
	public void				startLeader()
	{
		leader2nd = new MSControllerLocal(this, multicast2ndLevel, multicast2ndLevelPort);
		leader1st = new MSControllerGlobal(this, multicast1stLevel, multicast1stLevelPort);
		leaderVisor = new MSControllerLeader(this);

		if (view.getLocalView().getMembersList().size() > 1)
			startWatcher();
		leader2nd.start();
		leader1st.start();
		leaderVisor.start();
	}

	/**
	 *
	 * Stop all leader related activities.
	 *
	 */
	@SuppressWarnings("deprecation")
	public void				stopLeader()
	{
		if (leader1st != null)
		{
			leader1st.interrupt();
			leader1st.shutdown();
			leader1st.stop();
		}
		if (leader2nd != null)
		{
			leader2nd.interrupt();
			leader2nd.shutdown();
			leader2nd.stop();
		}
		if (leaderVisor != null)
		{
			leaderVisor.interrupt();
			leaderVisor.shutdown();
			leaderVisor.stop();
		}
		leader1st = null;
		leader2nd = null;
		leaderVisor = null;
	}

	/**
	 *
	 * Start a member activity
	 *
	 */
	public void				startMember()
	{
		if (member != null && member.isAlive())
			return;
		member = new MSControllerMember(this, multicast2ndLevel, multicast2ndLevelPort);
		member.start();
		startWatcher();
	}

	/**
	 *
	 * Stop a member activity
	 *
	 */
	@SuppressWarnings("deprecation")
	public void				stopMember()
	{
		if (member != null)
		{
			member.interrupt();
			member.shutdown();
			member.stop();
		}
		member = null;
	}

	/**
	 *
	 * Start watchers for machine fault
	 *
	 */
	@SuppressWarnings("deprecation")
	public void				startWatcher()
	{
		MSMember			newWatch;

		if ((newWatch = view.getLocalView().getWatched(memberID)) == null)
		{
			if (watch != null)
			{
				watch.shutdown();
				watch.interrupt();
				watch.stop();
				watch = null ;
			}
			return ;
		}
		if (watch == null || (watch != null && !(watch.isAlive())))
		{
			watch = new MSWatcherActive(this, newWatch);
			watch.start();
			return;
		}
		if (watch.getWatched().getID() != newWatch.getID())
			watch.changeWatched(newWatch);
	}

	/**
	 *
	 * Stop watchers
	 *
	 */
	@SuppressWarnings("deprecation")
	public void				stopWatchers()
	{
		if (watch != null || watch.isAlive())
		{
			watch.interrupt();
			watch.shutdown();
			watch.stop();
		}
		if (watcherReply != null || watcherReply.isAlive())
		{
			watcherReply.interrupt();
			watcherReply.shutdown();
			watcherReply.stop();
		}
		watch = null;
		watcherReply = null;
	}

	@SuppressWarnings("deprecation")
	public void				stopJoinAndElection()
	{
		if (join != null || join.isAlive())
		{
			join.interrupt();
			join.shutdown();
			join.stop();
		}
		if (election != null || join.isAlive())
		{
			election.interrupt();
			election.shutdown();
			election.stop();
		}
		join = null;
		election = null;
	}

	/**
	 *
	 * This function returns the current active watcher state
	 *
	 * @return true if a watcher is currently running, false otherwise
	 *
	 */
	public boolean			watcherEnabled()
	{
		return (watch != null);
	}

	/**
	 *
	 * Sets the current membership view
	 *
	 * @param v : the new view
	 *
	 */
	public void				setView(MSView v)
	{
		view = v;
		if (hyperGUI != null)
			hyperGUI.updateView(view);
	}

	/**
	 *
	 * Updates the membership view according to a received global view
	 *
	 * @param v : the received view
	 * @return true if a modification has been made, false otherwise
	 *
	 */
    public int                updateView(MSView v)
    {
        if (view.updateGlobalView(v) == 0)
            return 0;
        updateGUIView();
        return 1;
    }

    public void                updateGUIView()
    {
        if (hyperGUI != null)
            hyperGUI.updateView(view);
    }

	/**
	 *
	 * Returns the current view of the membership
	 *
	 * @return The view of the membership
	 *
	 */
	public MSView			getView()
	{
		return view;
	}

	/**
	 *
	 * Gets the ID of the local member
	 *
	 * @return ID of the local member
	 *
	 */
	public MSMember			getMemberID()
	{
		return memberID;
	}

	public static void 		main(String args[])
	{
		MSMain 				main = new MSMain(null);

		main.launch();
		synchronized (main)
		{
			try { main.wait(); }
			catch (InterruptedException e) { e.printStackTrace(); }
		}
	}
}