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

import dprocess.RemoteCallback;
import dprocess.RemoteProcess;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TreeMap;

/**
 *
 * This class implements the RemoteCallback functionalities.
 * The instance will be called on termination of a process.
 *
 * @see							RemoteCallback
 * @version						1.0
 *
 */
public class 					RemoteCallbackIpl implements RemoteCallback
{
	TreeMap<String, Integer> 	exitQueue;
	Hashtable<Integer, RemoteProcess> 	processList;
	Hashtable<Integer, DPClientInfo>	processInfos;

	/**
	 *
	 * Main constructor for the RemoteCallbackIpl
	 *
	 * @param exitQ : exit queue used for notifies.
	 */
	public 						RemoteCallbackIpl(TreeMap<String, Integer> exitQ, Hashtable<Integer, RemoteProcess> pList, Hashtable<Integer, DPClientInfo> pInfos)
	{
		this.exitQueue = exitQ;
		this.processList = pList;
		this.processInfos = pInfos;

	}

	/**
	 *
	 * Remote call signaling the termination of a remote process
	 * Puts the exit status at the end of the exitQueue.
	 * Notifies the eventually waiting process.
	 *
	 * @param exitStatus		: The exit value of the remote process.
	 * @param uid				: Unique identifier of the process.
	 * @throws RemoteException
	 *
	 */
	public void					call(int exitStatus, String uid) throws RemoteException
	{
		System.out.println("RemoteCallbackIpl::Waking rwait thread up.");
		/** Wakes a thread up on callback event */
		synchronized (this.exitQueue)
		{
			exitQueue.put(uid, exitStatus);
			this.exitQueue.notify();
		}

		synchronized (this.processList)
		{
			Enumeration<Integer> k = processList.keys();
			for (Enumeration<RemoteProcess> e = processList.elements(); e.hasMoreElements() ;)
			{
				RemoteProcess rp = e.nextElement() ;
				int pid = k.nextElement() ;
				if(rp.getUid().equals(uid))
				{
					processList.remove(pid);
					processInfos.remove(pid);
				}
			}
		}
	}
}