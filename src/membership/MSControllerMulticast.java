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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 * This abstract class encapsulates the basic functionality for a multicast controller.
 * This class implements the MSController interface for a membership service.
 *
 * @version							1.8
 *
 */
public abstract class 				MSControllerMulticast extends Thread implements MSController
{
	protected MSMain				infos;
	protected MulticastSocket		multicastSock;
	protected InetAddress			multicastAddr;
	protected int					multicastPort;
	protected static int			maxDPacket = 50000;
	protected MSTimeout				timeout;

	/**
	 *
	 * Constructor for the controller which will be called by subclasses.
	 * Initialize the MulticastSocket for communication
	 *
	 * @param i 	: The information structure of the membership
	 * @param adr	: The multicast address to connect
	 *
	 */
	public							MSControllerMulticast(MSMain i, byte[] adr, int port)
	{
		try
		{
			infos = i;
			multicastPort = port ;
			multicastSock = new MulticastSocket(multicastPort);
			multicastSock.setTimeToLive(5) ;
			multicastAddr = InetAddress.getByAddress(adr);
			timeout = new MSTimeout(0, multicastSock, multicastAddr);
		}
		catch (UnknownHostException e)
		{
			System.err.println("MSControllerMulticast::Can't find host : " + adr);
			e.printStackTrace();
			return;
		}
		catch (SocketException e)
		{
			System.err.println("MSControllerMulticast::Can't set socket options.");
			e.printStackTrace();
			return;
		}
		catch (IOException e)
		{
			System.err.println("MSControllerMulticast::Can't open socket.");
			e.printStackTrace();
			return;
		}
	}

	/**
	 *
	 * Main connection function for joining a multicast group.
	 *
	 */
	public void						connect()
	{
		try { multicastSock.joinGroup(multicastAddr); }
		catch (IOException e)
		{
			System.err.println("MSControllerMulticast::Can't join multicast group.");
			e.printStackTrace();
		}
	}

	/**
	 *
	 * Reconnects to the multicast service by recreating the socket.
	 *
	 */
	public void						reconnect()
	{
		try
		{
			multicastSock = new MulticastSocket(multicastPort);
			multicastSock.joinGroup(multicastAddr);
		}
		catch (IOException e)
		{
			System.err.println("MSControllerMulticast::Can't reconnect to multicast group.");
			e.printStackTrace();
		}
	}

	/**
	 *
	 * Function called on service termination, closes the multicast socket.
	 *
	 */
	public void						shutdown()
	{
		try { multicastSock.leaveGroup(multicastAddr) ; }
		catch (IOException e)
		{
			//System.err.println("MSControllerMulticast::Problem while leaving multicast group.");
		}
		multicastSock.close();
	}

	/**
	 *
	 * Sends an MSMessage through the socket.
	 * First serialize the message and then send it.
	 *
	 * @param type : type of the message
	 *
	 */
	public void						sendMessage(String type)
	{
		DatagramPacket				packet;
		MSMessage					message;

		try
	 	{
			message = new MSMessage(type, infos.getMemberID(), infos.getView());
			packet = serialize(message);
	 		multicastSock.send(packet);
		}
	 	catch (IOException e)
	 	{
	 		//System.err.println("MSControllerMulticast::Can't send multicast packet.");
	 	}
	}

	/**
	 *
	 * Sends an MSMessage through the socket.
	 * First serialize the message and then send it.
	 *
	 * @param type : type of the message
	 *
	 */
	public void						sendHeartBeat(String type)
	{
		DatagramPacket				packet;
		MSMessage					message;
		MSView						sender;

		try
	 	{
			sender = new MSView(infos.getView().getLocalView().getGroupID(), infos.getMemberID());
			sender.setLocalView(infos.getView().getLocalView());
			message = new MSMessage(type, infos.getMemberID(), sender);
			packet = serialize(message);
	 		multicastSock.send(packet);
		}
	 	catch (IOException e)
	 	{
	 		System.err.println("MSControllerMulticast::Can't send multicast packet.");
	 		e.printStackTrace();
	 	}
	}

	/**
	 *
	 * Receives an MSMessage through the socket.
	 * First de-serialize the message and then returns it.
	 *
	 * @return A decoded MSMessage
	 *
	 */
	public MSMessage				receiveMessage() throws java.net.SocketTimeoutException, IOException
	{
		DatagramPacket				packet;
		MSMessage					message = null ;
		byte []						data = new byte[maxDPacket];

		packet = new DatagramPacket(data, maxDPacket);

		while (message == null)
		{
			multicastSock.receive(packet);
			message = deserialize(packet);
		}
		return message;
	}

	/**
	 *
	 * Encapsulation of the java serialization for a multicast service.
	 * Transforms a MSMessage to a DatagramPacket for sending.
	 *
	 * @param msm : The filled MSMessage.
	 * @return The DatagramPacket to send over a MulticastSocket.
	 *
	 */
	public DatagramPacket			serialize(MSMessage msm)
	{
		ByteArrayOutputStream		bos;
		ObjectOutputStream 			os;
		DatagramPacket				ret;
		byte []						msg;
		int 						i, s;

		ret = null;
		try
		{
			bos = new ByteArrayOutputStream();
			os = new ObjectOutputStream(bos);
			os.writeObject(msm);
			msg = bos.toByteArray();
			s = msg.length;
			byte [] newp = new byte[s + 4] ;
			newp[0] = (byte)((s & 0xff000000) >>> 24);
			newp[1] = (byte)((s & 0x00ff0000) >>> 16);
			newp[2] = (byte)((s & 0x0000ff00) >>> 8);
			newp[3] = (byte)((s & 0x000000ff));
			for (i = 0; i < s;i++)
				newp[i+4] = msg[i] ;
			ret = new DatagramPacket(newp, s+4, multicastAddr, multicastPort);
		}
		catch (IOException e)
		{
			System.err.println("MSControllerMulticast::Fatal problem in serialization");
			e.printStackTrace();
			return null;
		}
		return ret;
	}

	/**
	 *
	 * Encapsulation of the java de-serialization for a multicast service.
	 * Transforms a received DatagramPacket to a MSMessage.
	 *
	 * @param msg : The received DatagramPacket
	 * @return The freshly created MSMessage
	 *
	 */
	public MSMessage				deserialize(DatagramPacket msg)
	{
		ObjectInputStream 			ois;
		ByteArrayInputStream		bais;
		MSMessage					ret;

		ret = null;
		try
		{
			byte [] b = msg.getData() ;
			int psize =   ((b[0] & 0xFF) << 24)
						| ((b[1] & 0xFF) << 16)
						| ((b[2] & 0xFF) << 8)
						|  (b[3] & 0xFF);
			bais = new ByteArrayInputStream(msg.getData(),4, psize);
			ois = new ObjectInputStream(bais);
			ret = (MSMessage) ois.readObject();
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("MSControllerMulticast::Can't resolve MSMessage");
			e.printStackTrace();
			return null;
		}
		catch (StreamCorruptedException e)
		{
			//System.out.println("MSControllerMulticast::Received corrupted message. (not from membership)");
			return null ;
		}
		catch (EOFException e)
		{
			//System.out.println("MSControllerMulticast::Received corrupted message. (not from membership)");
			return null ;
		}
		catch (IOException e) { e.printStackTrace(); }
		return ret;
	}
}
