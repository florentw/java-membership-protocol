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

import java.io.IOException;
import java.net.SocketException;

/**
 *
 * This class implements the 1st-level service, used only by the ring leader
 * MSControllerGlobal extends the MSControllerMulticast service by handling :
 * 		- Reception of views from other group leaders on the 1st-level ring.
 * 		- Group fault watching with a global timeout.
 * 		- Keep-alive messages sent to other leaders.
 *
 * @version						2.1
 *
 */
public class 					MSControllerGlobal extends MSControllerMulticast
{
	public static int			leaderTimeout = 20000;
	private MSWatcherPassive	groupsWatcher;

	/**
	 *
	 * Main constructor for the leader's first-level service.
	 * Initializes the multicast connection and the passive watcher.
	 *
	 * @param i : the information structure relative to the membership
	 * @param adr : the multicast address to connect to
	 */
	public						MSControllerGlobal(MSMain i, byte [] adr, int port)
	{
		super(i, adr, port);
		groupsWatcher = new MSWatcherPassive(i);

	}

	/**
	 *
	 * Function for message handling.
	 *
	 * @param msg : the received message
	 *
	 */
	public int					treatMessage(MSMessage msg)
	{
	  	if (infos.updateView(msg.getView()) == 1)
	  	{
			System.out.println("MSControllerGlobal::Broadcast modified membership view to 2nd level");
	  		infos.leader2nd.sendMessage("MEMBERSHIP");
	  	}
		groupsWatcher.groupActivity(msg.getView().getLocalView().getGroupID());
		return 0;
	}

	/**
	 *
	 * Main listening loop for the global ring watching
	 *
	 */
	public void 				listen()
	{
		MSMessage				message;
		boolean					check;

		check = true;
		timeout.setTimeOut(leaderTimeout / 2);
		timeout.start();
		System.out.println("MSControllerGlobal::Start 1st-level watching");
		sendMessage("MEMBERSHIP");
		while (true)
		{
			try
			{
				message = receiveMessage();
				if (message.getSender().getID() == infos.getMemberID().getID())
					continue;
				treatMessage(message);
			}
			catch (SocketException e)
			{
				reconnect();
				sendHeartBeat("MEMBERSHIP");
				check = !check;
				if (check)
					groupsWatcher.checkForFaulty();
				timeout = new MSTimeout(leaderTimeout / 2, multicastSock, multicastAddr);
				timeout.start();
			}
			catch (IOException e)
			{
				System.err.println("MSControllerGlobal::Fatal error while watching 1st-level ring.");
				e.printStackTrace();
				return;
			}
		}
	}

	/**
	 *
	 * Main running function for the global controller
	 *
	 */
	public void 				run()
	{
		groupsWatcher.initGroupWatching();
		connect();
		listen();
		shutdown();
	}

}
