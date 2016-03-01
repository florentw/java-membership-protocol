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
* This controller handles the leader watching of join and membership messages on the 2nd level ring.
* When a join message is received, the newly coming computer is added to the membership view.
* This new view is broadcasted to the entire ring.
* 	- If the member has a better ID than the current leader, the leader ends itself and start member functionality.
* 	- If the leader has a better ID, it continues to assure its functionality.
*
* @version				1.0
*
*/
public class 			MSControllerLocal extends MSControllerMulticast
{

	/**
	 *
	 * Main constructor for the MSControllerLocal class
	 *
	 * @param i : the MSMain instance
	 * @param adr : the local multicast address
	 * @param port : the local port
	 */
	public				MSControllerLocal(MSMain i, byte [] adr, int port)
	{
		super(i, adr, port);
	}

	public boolean		isClosed()
	{
		return multicastSock.isClosed();
	}

	/**
	 *
	 * Treat a join or membership message when received
	 *
	 * @param msg : the received message
	 *
	 */
	public int			treatMessage(MSMessage msg)
	{
		/* onJoin */
		if (msg.getType().equals("JOIN"))
		{
			/* We add the new member */
			infos.getView().addLocalMember(msg.getSender()) ;
			infos.updateView(infos.getView()) ;
			infos.updateGUIView();

			System.out.println("MSControllerLocal:: (JOIN) Adding new member #"+msg.getSender().getID());
			infos.getView().printGlobalWiew() ;

			/* Then we multicast the new membership view */
			sendMessage("MEMBERSHIP") ;

			/* Better than me ? */
			if (msg.getView().getLocalView().getLeader().getID() < infos.getMemberID().getID())
			{
				System.out.println("MSControllerLocal:: (JOIN) Election lost / Starting member...");
				shutdown();
				infos.leader1st.interrupt();
				infos.leader1st.shutdown();
				infos.startMember();
				return 1;
			}
			infos.startWatcher();

		} /* onMembership */
		else if (msg.getType().equals("MEMBERSHIP")
				&& msg.getSender().getID() != infos.getMemberID().getID())
		{
			/* Better than me ? */
			if (msg.getView().getLocalView().getLeader().getID() < infos.getMemberID().getID())
			{
				System.out.println("MSControllerLocal:: (MS) Election lost / Starting member...");
				shutdown();
				infos.leader1st.interrupt();
				infos.leader1st.shutdown();
				infos.startMember();
				return 1;
			}

			infos.startWatcher() ;
		}
		return 0;
	}

	/**
	 *
	 * Main listening function for the MSControllerLocal thread.
	 * Infinite loop waiting for join or membership messages.
	 *
	 */
	public void 		listen()
	{
		MSMessage message;

		try
		{
			while (true)
			{
				message = receiveMessage();
				if (treatMessage(message) == 1)
					return;
			}
		} catch (IOException e) {
			System.err.println("MSControllerLocal::Fatal error while listenning.");
			e.printStackTrace();
		}
	}

	/**
	 *
	 * Main running function for the MSControllerLocal thread
	 *
	 */
	public void 		run()
	{
		connect();
		listen();
		shutdown() ;
	}
}
