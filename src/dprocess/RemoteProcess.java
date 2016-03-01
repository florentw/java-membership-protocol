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
 * This interface defines the functionalities available on a remote process.
 * 
 * @see					dprocess.dpclient.RemoteProcessIpl
 * @version 			1.0
 *
 */
public interface 		RemoteProcess extends Remote
{
	/**
	 * 
	 * Waits for the end of the process
	 * 
	 * @return Exit status of the process
	 * @throws RemoteException
	 * 
	 */
	public int			rwait()				throws RemoteException;
	
	/**
	 * 
	 * Sends a POSIX signal to the process
	 * 
	 * @param signal : integer value of the signal
	 * @return Exit status of the process
	 * @throws RemoteException
	 * 
	 */
	public int			rkill(int signal)	throws RemoteException;
	
	/**
	 * 
	 * Exits the process
	 * 
	 * @param status : status of termination
	 * @return Exit status of the process
	 * @throws RemoteException
	 * 
	 */
	public int			rexit(int status)	throws RemoteException;
	
	/**
	 * 
	 * Checks if the process is finished
	 * 
	 * @return true if finished / false otherwise
	 * @throws RemoteException
	 * 
	 */
	public boolean		isFinished()		throws RemoteException;
	
	/**
	 * 
	 * Gets the unique identifier of the process
	 * 
	 * @return String value of the UID.
	 * @throws RemoteException
	 * 
	 */
	public String		getUid()			throws RemoteException;
}
