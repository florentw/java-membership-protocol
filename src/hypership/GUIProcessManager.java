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

package hypership;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TreeMap;

import membership.MSMember;
import dprocess.DPMessage;
import dprocess.RemoteCallback;
import dprocess.RemoteProcess;
import dprocess.dpclient.DPClientInfo;
import dprocess.dpclient.RemoteCallbackIpl;

/**
 *
 * Main class for the local client for the remote process library.
 *
 * @version										1.0
 *
 */
public class 									GUIProcessManager
{
	private static int 							distantServerPort = 3030;
	private RemoteCallback 						callBack;
	private Remote								callBackStub;
	private TreeMap<String, Integer> 			exitQueue;
	private Hashtable<Integer, RemoteProcess> 	processList;
	private Hashtable<Integer, DPClientInfo>	processInfos;
	private int 								processCount;

	/**
	 *
	 * Main constructor for the client, allocates all memory resources
	 *
	 */
	public 										GUIProcessManager ()
	{
		processCount = 0 ;
		processList = new Hashtable<Integer, RemoteProcess>();
		processInfos = new Hashtable<Integer, DPClientInfo>();
		/*try {
			processList.put(1, new RemoteProcessIpl(null));
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		processInfos.put(1, new DPClientInfo("bitch",5,"toto.exe"));
		processInfos.put(2, new DPClientInfo("localhost",3,"plop.exe"));
		processInfos.put(3, new DPClientInfo("localhost",3,"boom.exe"));*/
		exitQueue = new TreeMap<String, Integer>();
		callBack = new RemoteCallbackIpl(exitQueue, processList, processInfos);
		try
		{
			UnicastRemoteObject.exportObject (callBack, 0);
			callBackStub = RemoteObject.toStub(callBack);
		}
		catch (RemoteException e)
		{
			System.err.println("DPClient::Can't export remote callback");
			e.printStackTrace();
		}
	}

	/**
	 *
	 * Sending a request for the creation of a remote process to a distant server.
	 *
	 * @param distantServer : Distant server name
	 * @param message : DPMessage filled with the binary data
	 * @return A remote reference to the newly created process
	 *
	 */
	private RemoteProcess 						sendServerRequest(String distantServer, DPMessage message)
	{
		ObjectInputStream 						ois;
		ObjectOutputStream 						oos;
		InetAddress 							server;
		Socket									clientSock;

		try
		{
			clientSock = new Socket();
			server = InetAddress.getByName(distantServer);
			clientSock.connect(new InetSocketAddress(server, distantServerPort)) ;
			ois = new ObjectInputStream (clientSock.getInputStream());
			oos = new ObjectOutputStream (clientSock.getOutputStream());
			System.out.println("Sending request to server ...");
			oos.writeObject(message);
			System.out.println("Receiving remote reference ...");
			RemoteProcess rp = (RemoteProcess) ois.readObject ();
			return rp;
		}
		catch (ClassNotFoundException e)
		{
			System.err.println("SendServerRequest::Can't read object");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			System.err.println("SendServerRequest::Can't initialize connection with server");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 *
	 * Initiates an remote execution request to a specified member with a given executable file
	 *
	 * @param member : member to send the request
	 * @param path : path to the executable file
	 * @return PID if successful / -1 otherwise
	 */
	public int 									rexec(MSMember member, String path)
	{
		DPMessage 								DPm = null;
		DPClientInfo							DPi = null;
		RemoteProcess 							rp;

		++this.processCount ;
		File fichier = new File(path);
		byte[] exec = new byte[(int) fichier.length()];
		FileInputStream in = null;
		try {
			in = new FileInputStream(fichier);
		} catch (FileNotFoundException e) {
			System.err.println("GUIProcessManager::File not found " + fichier.getAbsolutePath());
			e.printStackTrace();
			return -1;
		}
		try {
			in.read(exec);
		} catch (IOException e) {
			System.err.println("GUIProcessManager::File not readable " + fichier.getAbsolutePath());
			e.printStackTrace();
			return -1;
		}
		DPm = new DPMessage(exec, callBackStub);
		DPi = new DPClientInfo (member.getAddress(), this.processCount, path.substring(path.lastIndexOf("/") + 1));
		//member.getAddress() a la place de localhost
		rp = sendServerRequest(member.getAddress(), DPm);
		synchronized (this.processList)
		{
			processList.put(new Integer(this.processCount), rp);
			processInfos.put(new Integer(this.processCount), DPi);
		}

		return this.processCount;
	}


	/*
	 * A VERIFIER : le kill est envoy� au bon process ???????
	 * 	correspondance des ID dans les differentes tables de hash
	 *
	 */
	public int 				rkill(RemoteProcess rp, int sig)
	{
		int 				sigNum = sig;
		int 				ret=0;

		try { ret=rp.rkill(sigNum); }
			catch (NumberFormatException e) { e.printStackTrace(); }
			catch (RemoteException e) {e.printStackTrace(); }
			return ret;
		}


	public int 				rexit(int status)
	{
		int 				result = 0;

		/* Queries the server */

		Enumeration<Integer> k = processList.keys();
		for (Enumeration<RemoteProcess> e = processList.elements(); e.hasMoreElements() ;)
		{
			e.nextElement() ;
			int pid = k.nextElement() ;
			try
			{
				result = processList.get(pid).rexit(status);
				System.out.println ("Process with local PID: "+pid+" and status : "+status) ;
			}
			catch (NumberFormatException e1)
			{
				e1.printStackTrace();
				return -1;
			}
			catch (RemoteException e1)
			{
				e1.printStackTrace();
				return -1;
			}
		}
		/* Free ressources on local hashtables */
		processList.clear() ;
		processInfos.clear() ;
		exitQueue.clear();
		this.processCount = 0 ;

		//Return result of last rexit command
		return result;
	}

	/**
	 *
	 * Sends an RWait request
	 *
	 * @return The return status of the process
	 */
	public RwaitReturn 		rwait()
	{
		String 				ret = new String();
		int 				status;
		RemoteProcess 		rp;

		/* First check if any process already exited */
		if (exitQueue.isEmpty())
		{
			/* Wait for a notify */
			try { synchronized(this.exitQueue) { this.exitQueue.wait(); } }
			catch (InterruptedException e1)
			{
				e1.printStackTrace() ;
				ret = String.valueOf("-1") ;
			}
		}
		String uidExited = exitQueue.firstKey();
		status = exitQueue.remove(uidExited);
		Enumeration<Integer> k = processList.keys();
		for (Enumeration<RemoteProcess> e = processList.elements(); e.hasMoreElements() ;)
		{
			rp = e.nextElement() ;
			int pid = k.nextElement() ;
			try
			{
				if (rp.getUid().equals(uidExited))
				{
					/* Free ressources on local hashtables */
					processList.remove(pid) ;
					processInfos.remove(pid) ;
					System.out.println ("Process with local PID: "+pid+" and status : "+status) ;
					ret = String.valueOf(pid);
					break;
				}
			}
			catch (RemoteException e1)
			{
				e1.printStackTrace();
				ret = String.valueOf("-1") ;
			}
		}

		 return new RwaitReturn(ret, status);
	}

	/**
	 *
	 * This function is the GUI equivalent to the RPS function
	 *
	 * @return The concatenated list of remote processes
	 */
	public String 				rlist()
	{
		String 					answer = new String();
		Enumeration<Integer> 	k = processList.keys();
		RemoteProcess 			rp;

		answer = "Process ID :\t\t| Hostname :\t\t| UID :\n" ;
		for (Enumeration<RemoteProcess> e = processList.elements(); e.hasMoreElements() ;)
		{
			rp = e.nextElement() ;
			int pid = k.nextElement() ;
			try { answer += pid+"\t\t| "+processInfos.get(pid).getServerName()+"\t\t| "+rp.getUid().hashCode()+"\n" ; }
			catch (RemoteException e1) {
				e1.printStackTrace();
				return "-1" ;
			}
		}
		answer += "-----------------------------------------------------------------------------------------------------------------------------";
		return answer;
	}

	public Hashtable<Integer, DPClientInfo>	getProcessInfos(){
		return processInfos;
	}
	public Hashtable<Integer, RemoteProcess> 	getProcessList(){
		return processList;
	}
}

class 				RwaitReturn{
	private String 	returnCode;
	private int 	status;

	public RwaitReturn(String returnCode, int status){
		this.returnCode = returnCode;
		this.status = status;
	}
	//Getters
	public String 	getReturnCode(){
		return returnCode;
	}
	public int 		getStatus(){
		return status;
	}
	//Setters
	public void		setReturnCode(String code){
		returnCode = code;
	}
	public void		setStatus(int status){
		this.status = status;
	}

}
