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
* This controller handles the leader watching for faulty messages in order to broadcast the information to the ring.
* When a faulty message is received, the faulty member is removed from the ring and this information is broadcasted.
*
* @version				1.0
*
*/
public class 			MSControllerLeader extends MSControllerUnicastUDP implements Runnable
{
	private static int	leaderServerPort = 6868 ;

	/**
	 *
	 * Main constructor for the MSControllerLeader class
	 *
	 * @param i : the MSMain instance
	 *
	 */
	public				MSControllerLeader(MSMain i)
	{
		super(i, leaderServerPort, false);
	}

	/**
	 *
	 * Treat a faulty message when received
	 *
	 * @param msg : the received UDP message
	 *
	 */
	public void			treatMessage(String msg)
	{
		int 			lpos;
		double 			mID;

		if (msg.substring(0, 6).equals("FAULTY"))
		{
			lpos = msg.indexOf(":");
			mID = Double.parseDouble(msg.substring(6,lpos));
			System.out.println("MSControllerLeader::FAULTY - Removing node #"+mID);
			infos.getView().getLocalView().removeMemberByID(mID);
			infos.updateView(infos.getView());
			infos.updateGUIView();
			infos.getView().printGlobalWiew();
			System.out.println("MSControllerLeader:Broadcasting modified membership view to 2nd level");
			infos.leader2nd.sendMessage("MEMBERSHIP");
			infos.startWatcher();
		}
	}

	/**
	 *
	 * Main listening function for the MSControllerLeader thread.
	 * Infinite loop waiting for a faulty message.
	 *
	 */
	public void			listen()
	{
		String			msg;
		try
		{
			while (true)
			{
				msg = receiveMessage();
				treatMessage(msg) ;
			}
		}
		catch (IOException e)
		{
			System.err.println("MSControllerLeader::Fatal error while watching 2nd-level ring.");
			e.printStackTrace();
			return;
		}
	}

	/**
	 *
	 * Main running function for the MSControllerLeader thread
	 *
	 */
	public void 		run()
	{
		listen() ;
		shutdown() ;
	}

	public int			treatMessage(MSMessage msg)
	{
		return 0;
	}
}
