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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

/**
 * System Information Retriever
 * Provides simple methods to get the system informations :
 * 		- local ring multicast address
 * 		- local IP address
 * 		- local host name
 *
 */

public class 							SysInfo
{
	private InetAddress					localAddress ;

	/**
	 *
	 * Main constructor for the SysInfo class
	 *
	 */
	public 								SysInfo()
	{
		Enumeration<NetworkInterface> 	interfaces;
		Enumeration<InetAddress> 		addresses;
		NetworkInterface 				card;
		try
		{
			localAddress = null;
			interfaces = NetworkInterface.getNetworkInterfaces();
			if (interfaces == null)
				return;
			while (interfaces.hasMoreElements())
			{
				card = (NetworkInterface) interfaces.nextElement();
				addresses = card.getInetAddresses();
				if (addresses == null)
					continue;
				while (addresses.hasMoreElements())
				{
					InetAddress address = (InetAddress) addresses.nextElement();
					if (!address.isLinkLocalAddress() && !address.isAnyLocalAddress() && !address.isLoopbackAddress())
					{
						localAddress = address ;
						return ;
					}
				}
			}
		}
		catch (SocketException e)
		{
			System.out.println("SysInfo::Can't retrieve system information");
			e.printStackTrace();
			localAddress = null ;
		}
	}

	/**
	 *
	 * Gets the local IP of this computer
	 *
	 * @return The local IP of the computer
	 *
	 */
	public String 						getLocalIP ()
	{
		if (localAddress != null)
			return localAddress.getHostAddress() ;
		else
			return "" ;
	}

	/**
	 *
	 * Gets the local ring multicast address for this computer.
	 * It is simply based on the ethernet sub-mask that is mapped to a valid multicast address
	 *
	 * @return Local ring multicast address
	 *
	 */
	public byte [] 						getLocalMulticastAddress()
	{
		byte []							multicastAddr = new byte[4];
		String []						tmpAddr;

		multicastAddr[0] = (byte)235;
		tmpAddr = localAddress.getHostAddress().split("\\.");
		multicastAddr[1] = (byte)Integer.parseInt(tmpAddr[1]);
		multicastAddr[2] = (byte)Integer.parseInt(tmpAddr[2]);
		multicastAddr[3] = (byte)((Integer.parseInt(tmpAddr[3]) / 20) * 10);
		return multicastAddr;
	}

	/**
	 *
	 * Gets the local host name of this computer
	 *
	 * @return The local host name of the computer
	 *
	 */
	public String 						getLocalHostName()
	{
		if (localAddress != null)
			return localAddress.getHostName() ;
		else
			return "" ;
	}

	/**
	 *
	 * Builds the member identifier in the membership, based on the following scheme :
	 * CurrentTime + IP Address + Port
	 *
	 * @return The local host name of the computer
	 *
	 */
	public double 						buildMemberID(int port)
	{
		Calendar 						c = Calendar.getInstance();
		Date 							d = c.getTime() ;

		if (localAddress != null)
			return Double.parseDouble(String.valueOf(d.getTime())+Math.abs(localAddress.hashCode()+port)) ;
		else
			return -1 ;
	}
}
