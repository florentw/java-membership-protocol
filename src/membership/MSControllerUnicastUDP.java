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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;

/**
*
* This abstract class encapsulates the basic functionality for a UDP based controller.
* This class implements the MSController interface for a membership service.
*
* @version							1.2
*
*/
public abstract class 				MSControllerUnicastUDP extends Thread implements MSController
{
	protected MSMain				infos;
	protected DatagramSocket		datagramSock;
	protected SocketAddress			lastMessageSock;
	protected int					datagramPort;
	protected static int			maxDPacket = 50000;
	protected boolean				isClient ;

	/**
	 *
	 * Main constructor for the MSControllerUnicastUDP
	 *
	 * @param i : the MSMain instance
	 * @param port : the port to use
	 * @param client : true if this controller implements a client service
	 *
	 */
	public 							MSControllerUnicastUDP(MSMain i, int port, boolean client)
	{
		try
		{
			infos = i;
			datagramPort = port;
			isClient = client ;

			if (isClient)
				datagramSock = new DatagramSocket();
			else
				datagramSock = new DatagramSocket(datagramPort);
		}
		catch (SocketException e) {
			System.err.println("MSControllerUnicastUDP::Error while creating UDP socket");
			e.printStackTrace();
		}
	}

	public void						connect()
	{
	}

	/**
	 *
	 * Free all resources (disconnect and close socket).
	 *
	 */
	public void						shutdown()
	{
		datagramSock.close();
	}

	/**
	 *
	 * Send a message over the datagram socket to the specified address and port
	 *
	 * @param msg : the type of the message
	 * @param addr : the datagram destination
	 * @param port : the port to use
	 */
	public void						sendMessage(String msg, InetAddress addr, int port)
	{
		DatagramPacket				dpacket;
		byte []						message;

		try
		{
			message = msg.getBytes();
			dpacket = new DatagramPacket(message, 0, message.length, addr, port);
			datagramSock.send(dpacket);
		}
		catch (IOException e)
		{
			System.err.println("MSControllerUnicastUDP::Can't send datagram packet");
			e.printStackTrace();
		}
	}

	/**
	 *
	 * Receives a message through the socket.
	 *
	 * @return A decoded string
	 *
	 */
	public String					receiveMessage() throws java.net.SocketTimeoutException, IOException
	{
		byte []						msg;

		msg = new byte[maxDPacket];
		DatagramPacket in = new DatagramPacket(msg, maxDPacket);
		datagramSock.receive(in);
		lastMessageSock = in.getSocketAddress();
		return new String(msg);
	}

	/**
	 *
	 * Replies to the last received message
	 *
	 * @param msg : the message to send
	 *
	 */
	public void						replyToLastMessage(String msg)
	{
		DatagramPacket				dpacket;
		byte []						message;

		try
		{
			message = msg.getBytes();
			dpacket = new DatagramPacket(message, 0, message.length, lastMessageSock);
			datagramSock.send(dpacket);
		}
		catch (IOException e)
		{
			System.err.println("MSControllerUnicastUDP::Can't send datagram packet");
			e.printStackTrace();
		}
	}

	public String					toString()
	{
		return infos.toString();
	}
}
