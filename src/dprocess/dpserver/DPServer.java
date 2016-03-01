/*
	dprocess - a distributed process library in java and C

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

package dprocess.dpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * Main class for the server of remote processes.
 * This is the main entry point for the dynamic pool of threads.
 * An infinite loop is launched for waiting new clients,
 * when new clients arrive, the work is then deferred to the thread supervisor.
 *
 * @version					1.0
 * @see						DPSupervisor
 * @see						DPServerThread
 *
 */
public class 				DPServer
{
	private static int		serverPort = 3030;

	/**
	 *
	 * Main function for the server.
	 * Infinite loop of accept with a dynamic pool of threads.
	 * A supervisor handles the creation and destruction of threads.
	 * The clients are then affected to a thread.
	 *
	 * @param args : command line arguments
	 */
	public static void		main(String args[])
	{
		DPSupervisor		pool;
		ServerSocket		serverSock;
		Socket				clientSock;
		
		System.out.println("Starting DPServer ...");
		try
		{
			pool = new DPSupervisor(10);
			serverSock = new ServerSocket(serverPort);
			serverSock.setReuseAddress(true) ;
		}
		catch (IOException e)
		{
			System.err.println("DPServer::Can't initialize socket");
			e.printStackTrace();
			return;
		}
		while (true)
		{
			try
			{
				clientSock = serverSock.accept();
				System.out.println("New client accepted.");
				pool.startClient(clientSock);
			}
			catch (IOException e)
			{
				System.err.println("DPServer::Error while accepting client");
				e.printStackTrace();
			}
		}
	}

}
