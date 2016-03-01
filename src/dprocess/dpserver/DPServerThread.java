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

import java.net.Socket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;

import dprocess.DPMessage;
import dprocess.RemoteCallback;
import dprocess.RemoteProcess;

/**
 *
 * A server thread that will handle all communications with a client.
 * This thread will live for all the time being of the process life.
 * When the process ends, the thread then calls the remote client and restarts waiting.
 *
 * @version						1.0
 * @see							DPServer
 * @see							DPSupervisor
 *
 */
public class 					DPServerThread implements Runnable
{
	private DPSupervisor		supervisor;
	private RemoteProcess		rProcess;
	private RemoteCallback		rCallback;
	private DPMessage			message;
	private Socket				clientSock;
	private int					rExitStatus;

	/**
	 *
	 * Main constructor for a server thread
	 *
	 * @param sVisor : the specific thread supervisor
	 */
	public						DPServerThread(DPSupervisor sVisor)
	{
		supervisor = sVisor;
	}

	/**
	 *
	 * Main running function for the thread.
	 * As clients are assumed to connect only for execution, the thread acts as follow :
	 * 	- Get the DPMessage on the ObjectInputStream.
	 *  - Create a process and send the reference to the asker.
	 *  - Launch the process and immediately wait for it.
	 *  - Use the supplied call back to notify the end.
	 *
	 */
	public void 				run()
	{
		ObjectOutputStream 		oos;
		ObjectInputStream		ois;

		while(true)
		{
			synchronized(supervisor)
			{
				try
				{
					while (supervisor.nextClient == null)
						supervisor.wait();
					if (supervisor.delete)
					{
						supervisor.delete = false;
						this.finalize();
						return;
					}
					clientSock = supervisor.nextClient;
					supervisor.nextClient = null;
				}
				catch (InterruptedException e)
				{
					/* Interruption will only happens if the dynamic pool is too big */
					System.out.println("DPServerThread::This thread has been interrupted.");
					return;
				}
				catch (Throwable e)
				{
					System.out.println("DPServerThread::Fatal thread error / Shutting down.");
					e.printStackTrace();
					return;
				}
			}
			try
			{
				System.out.println("Client : " + clientSock);
				oos = new ObjectOutputStream(clientSock.getOutputStream());
				ois = new ObjectInputStream(clientSock.getInputStream());

				System.out.println("Receiving message ...");
				message = (DPMessage) ois.readObject();

				System.out.println("Saving callback ...");
				rCallback = (RemoteCallback) message.getCallback();

				System.out.println("Creating remote reference ...");
				rProcess = new RemoteProcessIpl(message.getData());
				UnicastRemoteObject.exportObject(rProcess, 0);
				oos.writeObject(RemoteObject.toStub(rProcess));
			}
			catch (ClassNotFoundException e)
			{
				System.err.println("DPServerThread::Can't resolve DPMessage");
				e.printStackTrace();
				return;
			}
			catch (RemoteException e)
			{
				System.err.println("DPServerThread::Can't create remote reference");
				e.printStackTrace();
				return;
			}
			catch (IOException e)
			{
				System.err.println("DPServerThread::Fatal error while opening streams");
				e.printStackTrace();
				return;
			}

			System.out.println("Launching process ...");
			try { ((RemoteProcessIpl)rProcess).launchProcess(); }
			catch (RemoteException e)
			{
				System.err.println("DPServerThread::Can't launch process");
				e.printStackTrace();
				return;
			}

			System.out.println("Waiting for end ...");
			try { rExitStatus = rProcess.rwait(); }
			catch (RemoteException e)
			{
				System.err.println("DPServerThread::Can't wait for process");
				e.printStackTrace();
				return;
			}

			System.out.println("Calling back creator ...");
			try { rCallback.call(rExitStatus, rProcess.getUid()); }
			catch (RemoteException e)
			{
				System.err.println("DPServerThread::Can't call back client for process termination");
				e.printStackTrace();
				return;
			}

			System.out.println("Client connection ended.");
			rProcess = null;
			supervisor.endClient();
			try {
				clientSock.close();
			} catch (IOException e) {
				System.out.println("DPServer::Can't close client socket");
				e.printStackTrace();
			}
		}
	}

}
