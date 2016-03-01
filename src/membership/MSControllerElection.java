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
 * This controller handles the election procedure.
 * The election works as follows :
 * 	- Send a membership message on the 2nd level ring
 * 	- Sets a timeout on the socket and wait for a message
 * 		* a message with inferior leader ID 	=> member
 * 		* a message with superior leader ID 	=> leader
 * 		* timeout expired (no receive)			=> leader
 *
 * @version				1.5
 *
 */
public class 			MSControllerElection extends MSControllerMulticast
{
	private static int	electionTimeout = 5000;

	/**
	 *
	 * Main constructor for the election thread
	 *
	 * @param i : the information structure
	 * @param adr : the 2nd level group address
	 *
	 */
	public				MSControllerElection(MSMain i, byte [] adr, int port)
	{
		super(i, adr, port);
	}

	/**
	 *
	 * Running function for the election thread :
	 * 	- Connects to the address
	 * 	- Launches the election procedure
	 *
	 */
	public void 		run()
	{
		connect();
		listen();
	}

	/**
	 *
	 * Main function for the election procedure.
	 * 	- Send a membership message on the 2nd level ring
	 * 	- Sets a timeout on the socket and wait for a message
	 * 		* a message with inferior leader ID 	=> member
	 * 		* a message with superior leader ID 	=> leader
     * 		* timeout expired (no receive)			=> leader
	 *
	 */
	public void 		listen()
	{
		MSMessage		message;

		System.out.println("MSControllerElection::Election started");
		timeout.setTimeOut(electionTimeout);
		timeout.start();
		sendMessage("MEMBERSHIP");
		try
		{
			while (true)
			{
				message = receiveMessage();
				if (message.getSender().getID() == infos.getMemberID().getID())
					continue;
				if (message.getView().getLocalView().getLeader().getID() < infos.getMemberID().getID())
				{
					synchronized(timeout){ timeout.notify(); }
					infos.setView(message.getView());
					shutdown();
					infos.startMember();
					System.out.println("MSControllerElection::Election lost / Starting member...");
					return;
				}
			}
		}
		catch (java.net.SocketException e)
		{
			infos.startLeader();
			System.out.println("MSControllerElection::Election winned / Starting leader...");
			return;
		}
		catch (IOException e)
		{
			System.err.println("MSControllerElection::Fatal error in election period.");
			e.printStackTrace();
			return;
		}
	}

	public int			treatMessage(MSMessage msg)
	{
		return 0;
	}
}
