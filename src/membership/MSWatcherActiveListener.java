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
* This class is the second-level active watcher answerer.
* Our implementation allows watched members to automatically reply to any sender.
* It is mostly an infinite loop following this procedure :
* 	- Waits for a "ping" message from the watching member.
* 	- Automatically reply to the watcher by getting is address from the received "ping" message.
* 
* @version						2.0
*
*/
public class 				MSWatcherActiveListener extends MSControllerUnicastUDP implements Runnable
{
	private static int		watcherServerPort = 6969 ;

	/**
	 * 
	 * Main constructor for the MSWatcherActiveListener class
	 * 
	 * @param i : the MSMain instance
	 * @param watch : the initial watching member
	 * 
	 */
	public					MSWatcherActiveListener(MSMain i, MSMember watch)
	{
		super(i, watcherServerPort, false);
		System.out.println("MSWatcherActiveListener::Starting") ;
	}

	/**
	 * 
	 * Main listening function for the MSWatcherActive thread.
	 * Infinite loop waiting for ping message and automatically replies to the sender.
	 * 
	 */
	public void listen()
	{
		String				msg;

		try
		{
			while (true)
			{
				msg = receiveMessage();

				if (msg.substring(0, 4).equals("PING"))
				{
//					System.out.println("MSWatcherActiveListener::PING Received") ;
					replyToLastMessage("PONG");
				}
			}
		}
		catch (IOException e) {
			System.err.println("MSWatcherActiveListenner::Fatal error while receiving message.");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Main running function for the MSWatcherActiveListener thread
	 * 
	 */
	public void 			run()
	{
		listen();
		shutdown();
	}
	
	public int treatMessage(MSMessage msg)
	{
		return 0;
	}
}