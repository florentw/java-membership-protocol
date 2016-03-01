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

package dprocess;

import java.io.Serializable;
import java.rmi.Remote;

/**
 * 
 * This class defines a message format for the communication between the client and the server
 * Used only in the context of a remote process creation.
 * 
 * @version						1.0
 *
 */
public class 					DPMessage implements Serializable
{
	private static final long	serialVersionUID = -6568267489466729754L;
	private byte[]				data;
	private Remote				callbackStub;

	/**
	 * 
	 * Main constructor for a message instantiation
	 * 
	 * @param data		: The process to launch (binary executable format)
	 * @param remote	: The call back stub for use upon process termination
	 * 
	 */
	public 						DPMessage(byte[] data, Remote remote)
	{
		this.data			= data ;
		this.callbackStub	= remote ;
	}

	/**
	 * 
	 * Gets the binary data associated with the message
	 * 
	 * @return A string containing the binary code.
	 * 
	 */
	public byte[]				getData()
	{
		return data;
	}

	/**
	 * 
	 * Gets the call back associated with the process
	 * 
	 * @return A remote reference
	 * 
	 */
	public Remote				getCallback()
	{
		return callbackStub;
	}

	/**
	 * 
	 * Returns the concatenated data of the class
	 * 
	 */
	public String 				toString()
	{
		return "Data : " + data + " / Callback : " + callbackStub;
	}
}
