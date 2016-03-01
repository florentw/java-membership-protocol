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
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
*
* This class is the second-level active watcher.
* It is mostly an infinite loop following this procedure :
* 	- Waits for a waitForPingTimeout time.
* 	- Send a "ping" message to the watched member.
* 	- Waits for a "pong" answer.
* If no answer is received, the member is considered faulty.
*
* @version						2.0
*
*/
public class 					MSWatcherActive extends MSControllerUnicastUDP implements Runnable
{
	public static int			pingTimeout = 3000;
	public static int			waitForPingTimeout = 10000;
	public static int			leaderServerPort = 6868 ;
	private static int			watcherServerPort = 6969 ;
	private MSMember			watched ;
	private InetAddress			watchedAddr ;


	/**
	 *
	 * Main constructor for the MSWatcherActive class
	 *
	 * @param i : the MSMain instance
	 * @param watch : the member to watch
	 *
	 */
	public						MSWatcherActive(MSMain i, MSMember watch)
	{
		super(i, watcherServerPort, true);
		watched = watch;
		try {
			watchedAddr = InetAddress.getByAddress(watched.getByteAddress()) ;
		} catch (UnknownHostException e) {
			System.err.println("MSWatcherActive::Can't find the host to watch.");
			e.printStackTrace();
		}

		System.out.println("MSWatcherActive:: "+infos.getMemberID().getAddress()+" is starting to watch: "+watch.getAddress()) ;
	}

	/**
	 *
	 * Gets the current watched member
	 *
	 * @return The MSMember reference
	 *
	 */
	public MSMember				getWatched()
	{
		return watched;
	}

	/**
	 *
	 * Change the watched member
	 *
	 * @param wName : the MSMember to watch
	 *
	 */
	public void					changeWatched(MSMember wName)
	{
		watched = wName;
		System.out.println("MSWatcherActive:: "+infos.getMemberID().getAddress()+" is changing its watched address: "+watched.getAddress()) ;

		try { watchedAddr = InetAddress.getByAddress(watched.getByteAddress()) ; }
		catch (UnknownHostException e) {
			System.err.println("MSWatcherActive::Can't find the host to watch.");
			e.printStackTrace();
		}
	}

	/**
	 *
	 * Ping procedure :
	 * Send a message to the watched and wait for rgroupActivityeply.
	 * If no reply is received after the timeout, consider it faulty.
	 *
	 * @return
	 */
	private int					ping()
	{
		String 					msg;

		try
		{
			sendMessage("PING", watchedAddr, watcherServerPort);
			try
			{
				msg = receiveMessage();
				if (msg.substring(0, 4).equals("PONG"))
				{
//					System.out.println("MSWatcherActive::PONG received") ;
					return 0;
				}
			}
			catch (java.net.SocketTimeoutException e)
			{
				MSMember leader = infos.getView().getLocalView().getLeader() ;

				if (watched.getID() == leader.getID())
				{
					System.out.println("MSWatcherActive::FAULT DETECTION -> LEADER FAILURE") ;
					infos.getView().getLocalView().removeMember(leader) ;
					infos.getView().printGlobalWiew() ;
					infos.startElection();
					return 1;
				}
				else
				{
					msg = "FAULTY"+watched.getID()+":" ;
					System.out.println("MSWatcherActive::FAULT DETECTION -> MEMBER FAILURE #"+watched.getAddress()) ;
					try
					{
						/* If the current node is not in leader state and the MSControllerMember is alive then
						 * send a FAILCHECK message to myself */
						if (infos.getMemberID().getID() != infos.getView().getLocalView().getLeader().getID()
							&& infos.member != null)
						{
							infos.getView().getLocalView().removeMemberByID(watched.getID()) ;
							infos.member.sendMessage("FAILCHECK") ;
						}

						sendMessage (msg, InetAddress.getByAddress(leader.getByteAddress()), leaderServerPort);
					}
					catch (UnknownHostException e1)
					{
						System.err.println("MSWatcherActive::Can't find leader host.");
						e1.printStackTrace();
					}
					return 1;
				}
			}
		}
		catch (SocketException e)
		{
			System.out.println("MSWatcherActive::Switching to new watcher.");
			return 1;
		}
		catch (IOException e) {
			System.err.println("MSWatcherActive::Fatal error while receiving message.");
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 *
	 * Main listening function for the MSWatcherActive thread.
	 * Infinite loop waiting for a timeout before starting the ping procedure.
	 *
	 */
	public void 				listen()
	{
		try
		{
			datagramSock.setSoTimeout(pingTimeout);
			while(true)
			{
				synchronized (this)
				{
					try { this.wait(waitForPingTimeout); }
					catch (InterruptedException e)
					{
						System.out.println("MSWatcherActive::Notification received / Closing watcher service.");
						return;
					}
					if (ping() == 1)
						return;
				}
			}
		} catch (IOException e) {
			System.err.println("MSWatcherActive::Fatal error while receiving message.");
			e.printStackTrace();
		}
	}

	/**
	 *
	 * Main running function for the MSWatcherActive thread
	 *
	 */
	public void 				run()
	{
		listen();
		shutdown();
	}

	public int	 				treatMessage(MSMessage msg)
	{
		return 0;
	}

	public String				toString()
	{
		return "Watched : " + watched.getName();
	}
}
