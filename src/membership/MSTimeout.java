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
import java.net.MulticastSocket;

/**
 *
 * This class implements the timeout functionality needed for the membership services.
 * It is associated with a socket that will be closed if the timeout has reached termination.
 * This timeout must be notified in order to prevent the socket closing. (disarming the timeout)
 *
 * @version					1.2
 *
 */
public class 				MSTimeout extends Thread
{
	private	long 			timeOut;
	private MulticastSocket	watchedThread;
	private InetAddress		watchedAddress ;

	/**
	 *
	 * Main constructor for the MSTimeout class
	 *
	 * @param time : time before closing socket when launched
	 * @param watched : socket watched and closed in case of time out reached
	 * @param watchedAddr : address to which the socket is connected
	 */
	public					MSTimeout(long time, MulticastSocket watched, InetAddress watchedAddr)
	{
		timeOut = time;
		watchedThread = watched;
		watchedAddress = watchedAddr;
	}

	/**
	 *
	 * Sets the time to wait before closing the socket
	 *
	 * @param time : time to wait
	 */
	public void		setTimeOut(long time)
	{
		timeOut = time;
	}

	/**
	 *
	 * Main running function for the MSTimeout thread
	 *
	 */
	public void		run()
	{
		while(true)
		{
			synchronized (this)
			{
				try { this.wait(timeOut); }
				catch (InterruptedException e)
				{
					//System.out.println("MSTimeout::Interrupt");
					return;
				}
				//System.out.println("MSTimeout::Timeout reached");
				try { watchedThread.leaveGroup(watchedAddress) ; }
				catch (IOException e)
				{
					System.err.println("MSTimeout::Can't close watched socket");
					return;
				}
				watchedThread.close();
				return;
			}
		}
	}
}
