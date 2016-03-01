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

/**
 *
 * Interface for the controllers of membership capacities
 *
 * @version				1.0
 *
 */
public interface 		MSController
{
	/**
	 *
	 * First function to call, initialize and connect
	 *
	 */
	public void			connect();

	/**
	 *
	 * Infinite loop to listen and receive messages
	 *
	 */
	public void			listen();

	/**
	 *
	 * Shutdowns service, cleaning all used resources
	 *
	 */
	public void			shutdown();

	/**
	 *
	 * After receiving a message, this function operates the desired functionality
	 *
	 * @param msg : Received message to handle
	 *
	 */
	public int			treatMessage(MSMessage msg);
}
