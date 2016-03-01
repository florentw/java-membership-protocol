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

package dprocess.dpclient;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * This class is the thread used by the DPClient in the case of a RWAIT call.
 * If the local client is asked to wait for the end of a remote process, it launches a DPClientThread accordingly.
 * The use of a thread in such a context allows the client to continue assuming its functionalities even if a
 * local user asks to wait for a process
 *
 * @version				1.1
 * @see					DPClient
 *
 */
public class 			DPClientThread implements Runnable
{
	private DPClient 	client;
	private Socket		clientSock;

	/**
	 *
	 * Main constructor for the DPClientThread class
	 *
	 * @param client : the local running client
	 *
	 */
	public			DPClientThread(DPClient client, Socket clientSock)
	{
		this.client = client;
		this.clientSock = clientSock;
	}

	/**
	 *
	 * This function is called when the thread is run.
	 * It handles all the waiting behavior by :
	 * 		- first checking if a process has already exited.
	 * 		- if it is not the case, wait for a call back to be received.
	 *
	 */
	public void 		run()
	{
		//RemoteProcess   rp;
		String			answer="";
		OutputStream 	clientOs =null;
		if (client.getExitQueue().isEmpty())
		{
			/* Wait for a notify */
			try { synchronized(client.getExitQueue()) { client.getExitQueue().wait(); } }
			catch (InterruptedException e1)
			{
				e1.printStackTrace() ;
				answer = "-1" ;
			}
		}
		String uidExited = client.getExitQueue().firstKey();
		Integer statusExited = client.getExitQueue().remove(uidExited);
		/*Enumeration<Integer> k = client.getProcessList().keys();
		for (Enumeration<RemoteProcess> e = client.getProcessList().elements(); e.hasMoreElements() ;)
		{
			rp = e.nextElement() ;
			int pid = k.nextElement() ;
			try
			{
				if (rp.getUid().equals(uidExited))
				{
					client.getProcessList().remove(pid) ;
					client.getProcessInfos().remove(pid) ;

					System.out.println ("Rwait process with local PID: "+pid+" // Exited with status : "+statusExited) ;
					answer = pid + ":" + 1;
					break;
				}
			}
			catch (RemoteException e1)
			{
				e1.printStackTrace();
				answer =  "-1" ;
			}
		}*/
		answer = uidExited.hashCode() + ":" + statusExited;

		try {
			clientOs = clientSock.getOutputStream();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		String localPid = answer;
		try {
			clientOs.write(localPid.getBytes());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			clientSock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
