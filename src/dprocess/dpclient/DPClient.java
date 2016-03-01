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
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TreeMap;

import dprocess.DPMessage;
import dprocess.RemoteCallback;
import dprocess.RemoteProcess;

/**
 *
 * Main class for the local client for the remote process library.
 * This class contains all the treatment for local API conversation through a socket.
 * Once the API call is decoded, the connection is established with the specified server.
 * This class encapsulates the local hashing table of all processes launched through this client.
 *
 * @version										2.1
 * @see											DPClientInfo
 * @see											APIMessage
 * @see											dprocess.DPMessage
 *
 */
public class 									DPClient
{
	private static int 							distantServerPort = 3030;
	private static int 							localServerPort = 1818;
	private RemoteCallback 						callBack;
	private Remote								callBackStub;
	private TreeMap<String, Integer> 			exitQueue;
	private Hashtable<Integer, RemoteProcess> 	processList;
	private Hashtable<Integer, DPClientInfo>	processInfos;
	private int 								processCount;
	private static Thread						rwaitThread;

	/**
	 *
	 * Main function for the local client
	 *
	 * @param args : command line arguments
	 *
	 */
	public static void 							main(String[] args)
	{
		ServerSocket 							serverAPISock;
		OutputStream 							clientOs;
		DPClient								DPC;
		Socket									clientSock;

		try
		{
			System.out.println("Starting DPClient Listener Thread ...");
			serverAPISock = new ServerSocket(localServerPort);
			serverAPISock.setReuseAddress(true) ;
		}
		catch (IOException e)
		{
			System.err.println("DPClient::Can't initialize socket");
			e.printStackTrace();
			return;
		}

		DPC = new DPClient();

		while (true)
		{
			try
			{
				clientSock = serverAPISock.accept();
				System.out.println("New client accepted...");

				APIMessage msg = DPC.getAPIMessage(clientSock);

				if (msg == null)
				{
					System.err.println("DPClient::Error while reading API message");
					clientSock.close();
					continue;
				}

				/** Response code to send to the client */
				String answer = DPC.treatAPIRequest(msg);
				//System.out.println("Response : " + answer);

				/** Answers the client */
				if(answer.equals("rwait"))
				{
					System.out.println("DPClient::Rwait answer will be sent by thread");
					DPC.assignThread(new Thread(new DPClientThread(DPC, clientSock)));
					rwaitThread.start();

				}
				else
				{
					try
					{
						clientOs = clientSock.getOutputStream();
						String localPid = answer;
						clientOs.write(localPid.getBytes());
					}
					catch (IOException e)
					{
						System.err.println("DPClient::Error while writing answer on API client socket");
						e.printStackTrace();
					}
					try
					{
						clientSock.close();
					}
					catch (IOException e)
					{
						System.err.println("DPClient::Error while closing API client socket");
					}

				}
			}
			catch (IOException e)
			{
				System.err.println("DPClient::Error while accepting client from API");
				e.printStackTrace();
			}

		}

	}

	/**
	 *
	 * Main constructor for the client, allocates all memory resources
	 *
	 */
	public 										DPClient ()
	{
		processCount = 0 ;
		processList = new Hashtable<Integer, RemoteProcess>();
		processInfos = new Hashtable<Integer, DPClientInfo>();
		exitQueue = new TreeMap<String, Integer>();
		callBack = new RemoteCallbackIpl(exitQueue, processList, processInfos);
		rwaitThread = null;
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
		catch (NullPointerException e)
		{
			System.err.println("DPClient::Callback initialisation failed");
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
			RemoteProcess rp = (RemoteProcess) ois.readObject();
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
	 * This function handles a received API Request
	 *
	 * @param message : a received API message
	 * @return the answer to send to the API
	 *
	 */
	@SuppressWarnings("deprecation")
	private String 								treatAPIRequest(APIMessage message)
	{
		String 									answer = "-1";
		DPMessage 								DPm = null;
		DPClientInfo							DPi = null;
		RemoteProcess 							rp;

		/* Execute a remote process */
		if (message.commandName.equals("rexec"))
		{
			if (message.numArgs != 2)
			{
				System.err.println("TreatAPIRequest::Wrong argument count for rexec") ;
				return "-1" ;
			}

			/* New pid */
			++this.processCount ;

			DPm = new DPMessage(message.execute, callBackStub);
			DPi = new DPClientInfo (message.args[0], this.processCount);
			rp = sendServerRequest(message.args[0], DPm);
			if (rp == null)
				return "-1";
			processList.put(new Integer(this.processCount), rp);
			processInfos.put(new Integer(this.processCount), DPi);


			answer = String.valueOf(this.processCount) ;
		}
		/* Signal a remote process */
		if (message.commandName.equals("rkill"))
		{
			if (message.numArgs != 2)
			{
				System.err.println("TreatAPIRequest::Wrong argument count for rkill") ;
				return "-1" ;
			}
			//System.out.println("Info APIMessage :::: " + message.commandName + " / " + message.numArgs + " / " + message.args[0] + " / " + message.args[1]);

			/* Queries the server */
			int pid = Integer.parseInt(message.args[0]) ;
			int sigNum = Integer.parseInt(message.args[1]) ;

			if (processList.containsKey(pid))
			{
				try {answer = String.valueOf(processList.get(pid).rkill(sigNum)); }
				catch (NumberFormatException e) { e.printStackTrace(); }
				catch (RemoteException e) {e.printStackTrace(); }
			}
			else
			{
				System.err.println("TreatAPIRequest::This pid doesn't exists : "+pid) ;
				return "-1" ;
			}

			return String.valueOf(pid) ;
		}
		/* Exit all remote processes */
		if (message.commandName.equals("rexit"))
		{
			if (message.numArgs != 1)
			{
				System.err.println("TreatAPIRequest::Wrong argument count for rexit") ;
				return "-1" ;
			}
			/* Queries the server */
			int status = Integer.parseInt(message.args[0]) ;

			synchronized(this.processList)
			{
				Enumeration<Integer> k = processList.keys();
				for (Enumeration<RemoteProcess> e = processList.elements(); e.hasMoreElements() ;)
				{
					rp = e.nextElement() ;
					int pid = k.nextElement() ;
					try
					{
						if( processList.get(pid).rexit(status) != -1)
						{
							System.out.println ("Rexit process with local PID: "+pid+" and status : "+status) ;
							answer =  ""+status;
						}
					}
					catch (NumberFormatException e1) {
						e1.printStackTrace();
						return "-1" ;
					}
					catch (RemoteException e1) {
						e1.printStackTrace();
						return "-1" ;
					}
				}
			}

			/* Free ressources on local hashtables */

			processList.clear() ;
			processInfos.clear() ;
			this.processCount = 0 ;

			return answer ;
		}
		/* Wait for any remote process */
		if (message.commandName.equals("rwait"))
		{
			if (message.numArgs > 0)
			{
				System.err.println("TreatAPIRequest::Too many arguments for rwait command") ;
				return "-1" ;
			}



			return "rwait";
		}
		/* Lists all remote processes */
		if (message.commandName.equals("rlist"))
		{
			if (message.numArgs > 0)
			{
				System.err.println("TreatAPIRequest::Too many arguments for rlist") ;
				return "-1" ;
			}


			synchronized (this.processList)
			{
				Enumeration<Integer> k = processList.keys();
				answer = "Process ID :\t| Hostname :\t\t| UID :\n" ;

				for (Enumeration<RemoteProcess> e = processList.elements(); e.hasMoreElements() ;)
				{
					rp = e.nextElement() ;
					int pid = k.nextElement() ;
					try { answer += pid+"\t\t| "+processInfos.get(pid).getServerName()+"\t\t| "+rp.getUid().hashCode()+"\n" ;}
					catch (RemoteException e1) {
						e1.printStackTrace();
						return "-1" ;
					}
				}

				System.out.println(answer);
			}
		}
		return answer ;
	}

	/**
	 *
	 * Receiving a message from the API
	 *
	 * @param cSocket : the socket to read from
	 * @return a processed API Message
	 *
	 */
	private APIMessage 							getAPIMessage(Socket cSocket)
	{
		int										i, numArgs, msgLen = 0 ;
		String									message = "", type = "", content = "" ;
		String []								args = new String[2];
		byte []									data = new byte[1000000];
		byte []									rawMsg;
		byte [] 								exeData = new byte[1];
		byte []									size = new byte[8];
		InputStream								is;

		try
		{
			is = cSocket.getInputStream ();
			if (is.read(data) == -1)
			{
				System.err.println("GetAPIMessage::Can't read on socket");
				return null;
			}
			for (i = 0; i < 8; i++)
				size[i] = data[i];
			msgLen = Integer.parseInt(new String(size));
			rawMsg = new byte[msgLen];
			System.out.println("GetAPIMessage::Size : " + msgLen);
			//message = "";
			size = new byte[5];
			for (i = 0; i < 5; i++)
				size[i] = data[i + 8];
			type = new String(size);
			for (i = 0; i < msgLen; i++)
				rawMsg[i] = data[i + 8];

			message = new String(rawMsg);

			//System.out.println("Sizezzzzzzzz : "+message.length()+" - Type : "+type) ;


			//System.out.println("GetAPIMessage::Type : " + type);
			if (msgLen == 0)
			{
				System.err.println("GetAPIMessage::Message is empty");
				return null;
			}

			if (type.length() == 0)
			{
				System.err.println("GetAPIMessage::Type is empty");
				return null;
			}

			/* No parameters commands (rwait, rlist) */
			if (msgLen == 5)
			{
				System.out.println("GetAPIMessage::Type (1) : "+type+" - Args : 0") ;
				return new APIMessage(type, 0, args, exeData);
			}

			content	= message.substring(6);
			int pos	= content.indexOf(":");

			if (pos == -1) /* A single argument */
			{
				args[0]		= content;
				numArgs		= 1;
			}
			else /* Two arguments */
			{
				args[0]		= content.substring(0, pos);
				args[1]		= content.substring(pos+1);
				numArgs		= 2;

				if (type.equals("rexec"))
				{
					int l = msgLen - 7 - args[0].length() ;
					if (l <= 0)
					{
						System.err.println("GetAPIMessage::Wrong length for message");
						return null;
					}

					exeData = new byte[l] ;
					//System.out.println("Re size of data"+ l);
					for (i = 0; i < l; i++)
						exeData[i] = data[i + (15 + args[0].length())];
				}
			}

			System.out.println("GetAPIMessage::Type (2) : "+type+" - Args : "+numArgs) ;

			return new APIMessage(type, numArgs, args, exeData);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public TreeMap<String, Integer> 			getExitQueue(){
		return exitQueue;
	}
	public Hashtable<Integer, DPClientInfo>		getProcessInfos(){
		return processInfos;
	}
	public Hashtable<Integer, RemoteProcess> 	getProcessList(){
		return processList;
	}
	/*public Socket								getClientSock(){
		return clientSock;
	}*/

	public void assignThread(Thread t){
		rwaitThread = t;
	}
}
