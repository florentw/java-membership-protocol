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

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * 
 * This interface define the standard behavior of a call back for the DProcess application
 * The only implementing class is used on termination of a remote process.
 * 
 * @see				dprocess.dpclient.RemoteCallbackIpl
 * @version			1.0
 *
 */
public interface 	RemoteCallback extends Remote
{
	/**
	 * 
	 * Remote call for triggering desired operations 
	 * 
	 * @param exitStatus		: The exit value of the remote process.
	 * @param uid				: Unique identifier of the process.
	 * @throws RemoteException
	 * 
	 */
	public void		call(int exitStatus, String uid) throws RemoteException;
}
