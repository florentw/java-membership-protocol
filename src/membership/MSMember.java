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

import java.io.Serializable;

/**
 *
 * This class encapsulates every information on a member :
 * 	- Name of the member
 * 	- State of the member
 * 	- Address of the computer
 *  - Member Identifier
 *
 * @version						1.0
 *
 */
public class 					MSMember implements Serializable
{
	private static final long 	serialVersionUID = 1L;
	private String				name;
	private String				state;
	private String				address;
	private double				memberID;

	/**
	 *
	 * Main constructor for the member class.
	 * Automatically sets the member's state to 'join'.
	 *
	 * @param n : name of the member
	 * @param a : address of the computer
	 * @param d : identifier of the member
	 */
	public						MSMember(String n, String a, double d)
	{
		name = n;
		address = a;
		state = "JOIN";
		memberID = d;
	}

	/**
	 * 
	 * Gets the ID of the member
	 * 
	 * @return ID of the member
	 * 
	 */
	public double				getID()
	{
		return memberID;
	}

	/**
	 * 
	 * Gets the name of the member
	 * 
	 * @return Name of the member
	 * 
	 */
	public String				getName()
	{
		return name;
	}

	/**
	 * 
	 * Gets the state of the member
	 * 
	 * @return State of the member
	 * 
	 */
	public String				getState()
	{
		return state;
	}
	
	/**
	 * 
	 * Gets the address of the member
	 * 
	 * @return Address of the member
	 * 
	 */
	public String				getAddress()
	{
		return address;
	}

	/**
	 * 
	 * Gets the byte array representation of the member address
	 * 
	 * @return Address of the member
	 * 
	 */
	public byte []				getByteAddress()
	{
		byte [] addr = new byte[4] ;
		String [] sadr = address.split("\\.") ;

		addr[0] = (byte)Integer.parseInt(sadr[0]) ;
		addr[1] = (byte)Integer.parseInt(sadr[1]) ;
		addr[2] = (byte)Integer.parseInt(sadr[2]) ;
		addr[3] = (byte)Integer.parseInt(sadr[3]) ;

		return addr;
	}

	public String				toString()
	{
		return "[" + memberID + "] " + name + " " + address;
	}
}
