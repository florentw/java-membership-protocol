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

/**
*
* This controller handles the member watching of membership messages on the 2nd level ring.
* If the member detects that it has a better ID than the current leader, it starts an election period.
* If the leader has a better ID, it continues to assure its functionality.
*
* @version				1.0
*
*/
public class 			MSControllerMember extends MSControllerMulticast
{
	/**
	 *
	 * Main constructor for the MSControllerLocal class
	 *
	 * @param i : the MSMain instance
	 * @param adr : the local multicast address
	 * @param port : the local port
	 *
	 */
	public				MSControllerMember(MSMain i, byte [] adr, int port)
	{
		super(i, adr, port);
	}

	/**
	 *
	 * Treat a membership message when received.
	 *
	 * @param msg : the received message
	 *
	 */
	public int			treatMessage(MSMessage msg)
	{
		if (msg.getType().equals("MEMBERSHIP") && (msg.getSender().getID() != infos.getMemberID().getID()))
		{
			/* Better than me ? */
			if (msg.getView().getLocalView().getLeader().getID() < infos.getMemberID().getID())
			{
				System.out.println("MSControllerMember::Updating view");
				infos.setView(msg.getView());
				infos.getView().printGlobalWiew();
				infos.startWatcher() ;
			}
			else
			{
				shutdown();
				infos.startElection();
				System.out.println("MSControllerMember::Starting election...");
				return 1;
			}
		}

		if (msg.getType().equals("FAILCHECK") && (msg.getSender().getID() == infos.getMemberID().getID()))
		{
			/* Restart our watchers */
			System.out.println("MSControllerMember::FAILCHECK msg received");
			infos.updateGUIView();
			infos.startWatcher() ;
		}

		return 0;
	}

	/**
	 *
	 * Main listening function for the MSControllerMember thread.
	 * Infinite loop waiting for membership messages.
	 *
	 */
	public void 		listen()
	{
		MSMessage		message ;

		try
		{
			while (true)
			{
				message = receiveMessage();
				if (treatMessage (message) == 1)
					return;
			}
		}
		catch (IOException e) {
			System.err.println("MSControllerMember::Fatal error while listenning.");
			e.printStackTrace();
		}
	}

	/**
	 *
	 * Main running function for the MSControllerMember thread
	 *
	 */
	public void 		run()
	{
		connect();
		listen();
		shutdown() ;
	}
}
