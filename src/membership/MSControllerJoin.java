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
 * This controller handles the join procedure for a newly arriving computer.
 * The procedure is as follows :
 * 	- Send a join message on the 2nd level ring
 * 	- Sets a timeout on the socket and wait for a message
 * 		* a message with inferior leader ID 	=> member
 * 		* a message with superior leader ID 	=> election
 * 		* timeout expired (no receive)			=> election
 *
 * @version				1.0
 *
 */
public class 			MSControllerJoin extends MSControllerMulticast
{
	private static int	joinTimeout = 5000;

	/**
	 *
	 * Main constructor for the join thread
	 *
	 * @param i : the information structure
	 * @param adr : the 2nd level group address
	 *
	 */
	public				MSControllerJoin(MSMain i, byte[] adr, int port)
	{
		super(i, adr, port);
	}

	/**
	 *
	 * Running function for the join thread :
	 * 	- Connects to the address
	 * 	- Launches the join procedure
	 *
	 */
	public void 		run()
	{
		connect();
		listen();
	}

	/**
	 *
	 * Main function for the join procedure which handles all the operations
	 * 	- Send a join message on the 2nd level ring
	 * 	- Sets a timeout on the socket and wait for a message
	 * 		* a message with inferior leader ID 	=> member
	 * 		* a message with superior leader ID 	=> election
     * 		* timeout expired (no receive)			=> election
	 *
	 */
	public void 		listen()
	{
		MSMessage		message;

		timeout.setTimeOut(joinTimeout);
		timeout.start();
		sendMessage("JOIN");
		try
		{
			System.out.println("MSControllerJoin::Waiting for response.");
			while (true)
			{
				message = receiveMessage();
				if (message.getType().equals("JOIN"))
				{
                    if (message.getSender().getID() > infos.getMemberID().getID())
                        infos.getView().addLocalMember(message.getSender());

                    if (message.getView().getLocalView().getMembersList().size() > 1)
                    	sendMessage("MEMBERSHIP");
                    continue;
				}
				if (message.getType().equals("MEMBERSHIP") && (message.getSender().getID() != infos.getMemberID().getID()))
				{
					infos.getView().printGlobalWiew() ;

					synchronized(timeout){ timeout.interrupt(); }
					infos.setView(message.getView());

					infos.getView().printGlobalWiew() ;

					if (message.getView().getLocalView().getLeader().getID() < infos.getMemberID().getID())
					{
						shutdown();
						infos.startMember();
						System.out.println("MSControllerJoin::Leader found / Starting membership...");
						return;
					}
					shutdown();
					infos.startElection();
					System.out.println("MSControllerJoin::Starting election...");
					return;
				}
			}
		}
		catch (java.net.SocketException e)
		{
			shutdown() ;
			infos.startElection();
			System.out.println("MSControllerJoin::Join timeout expired / Starting election...");
			return;
		}
		catch (IOException e)
		{
			System.err.println("MSControllerJoin::Fatal error in join period.");
			e.printStackTrace();
			return;
		}
	}

	public int			treatMessage(MSMessage msg)
	{
		return 0;
	}
}
