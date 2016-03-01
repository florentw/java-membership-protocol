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
 * This class is a standard message of the membership which contains :
 * 	- The type of the message
 * 	- The sender of the message
 *  - The current membership view of the sender
 *
 *  @version					1.1
 *  @see						Serializable
 *
 */
public class 					MSMessage implements Serializable
{
	private static final long 	serialVersionUID = 1L;
	private MSMember			sender;
	private MSView				view;
	private String				type;

	/**
	 *
	 * Main constructor for a membership message
	 *
	 * @param t : type of the message
	 * @param s : sender of the message
	 * @param v : current membership view of the sender
	 *
	 */
	public						MSMessage(String t, MSMember s, MSView v)
	{
		sender = s;
		view = v;
		type = t;
	}
	
	/**
	 * 
	 * Gets the sender of the message
	 * 
	 * @return Sender of the message
	 * 
	 */
	public MSMember				getSender()
	{
		return sender;
	}

	/**
	 * 
	 * Gets the view of the message
	 * 
	 * @return View of the message
	 * 
	 */
	public MSView				getView()
	{
		return view;
	}
	
	/**
	 * 
	 * Gets the type of the message
	 * 
	 * @return Type of the message
	 * 
	 */
	public String				getType()
	{
		return type;
	}

	public String				toString()
	{
		return sender + " : [" + type + "] " + view;
	}
}
